import exception.JuciException;
import exception.JuciRuntimeException;
import input.GoCommand;
import lombok.extern.slf4j.Slf4j;
import output.*;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.*;
import java.util.function.BiConsumer;

@Slf4j
public class UCIEngine implements Runnable, IUCIEngine {
    private static final int START_TIMEOUT = 5; //seconds

    private String author;
    private String name;
    private Map<String, Option> options;

    private String executablePath;
    private Process process;
    private PrintWriter processOutput;
    private BufferedReader processInput;
    private State state;
    private CountDownLatch startLatch;
    private CountDownLatch isReadyLatch;
    private BlockingQueue<CalculationResult> calculationResultQueue;
    private ExecutorService goExecutor;
    private ExecutorService isReadyExecutor;

    private List<BiConsumer<UCIEngine, Info>> infoCallbacks;

    private enum State {
        INITIAL, STARTING, IDLE, CALCULATING;
    }


    public UCIEngine(String executablePath) {
        this(executablePath, null);
    }

    public UCIEngine(String executablePath, BiConsumer<UCIEngine, Info> consumer) {
        this.executablePath = executablePath;
        this.state = State.INITIAL;
        this.options = new HashMap<>();
        this.startLatch = new CountDownLatch(1);
        this.isReadyLatch = new CountDownLatch(1);
        this.calculationResultQueue = new LinkedBlockingQueue<>();
        this.infoCallbacks = new ArrayList<>();
        addInfoListener(consumer);
    }

    public void addInfoListener(BiConsumer<UCIEngine, Info> consumer) {
        if (consumer != null) {
            infoCallbacks.add(consumer);
        }
    }

    public void removeInfoListener(BiConsumer<UCIEngine, Info> consumer) {
        infoCallbacks.remove(consumer);
    }

    @Override
    public void run() {
        String read;
        CalculationResult calculationResult = new CalculationResult();
        try {
            while (state != State.INITIAL && (read = processInput.readLine()) != null) {
                if ("uciok".equals(read)) {
                    startLatch.countDown();
                    continue;
                }

                if ("readyok".equals(read)) {
                    isReadyLatch.countDown();
                    continue;
                }

                UCIOutput uciOutput = OutputFactory.generateUCIOutput(read);

                if (uciOutput instanceof Id) {
                    Id id = (Id) uciOutput;

                    if (id.getAuthor() != null) {
                        this.author = id.getAuthor();
                    }

                    if (id.getName() != null) {
                        this.name = id.getName();
                    }
                    continue;
                }

                if (uciOutput instanceof Option) {
                    Option option = (Option) uciOutput;
                    options.put(option.getName(), option);
                    continue;
                }

                if (uciOutput instanceof Info) {
                    infoCallbacks.forEach(consumer -> consumer.accept(this, (Info) uciOutput));
                    calculationResult.processInfo((Info) uciOutput);
                    continue;
                }

                if (uciOutput instanceof Bestmove) {
                    Bestmove bestmove = (Bestmove) uciOutput;
                    calculationResult.setBestmove(bestmove.getBestmove());
                    calculationResult.setPonder(bestmove.getPonder());
                    calculationResultQueue.put(calculationResult);
                    calculationResult = new CalculationResult();
                    continue;
                }

                log.debug("could not make use of engine output '{}'", read);
            }
        } catch (Exception e) {
            log.error("Error while reading engine input", e);
        }
    }

    public void start() throws JuciException, IOException {
        try {
            startAsync().get();
        } catch (ExecutionException e) {
            if (e.getCause() instanceof JuciException) {
                throw (JuciException) e.getCause();
            }
            if (e.getCause() instanceof RuntimeException) {
                throw (RuntimeException) e.getCause();
            }
            if (e.getCause() instanceof InterruptedException) {
                throw new JuciException(e.getCause());
            }
            if (e.getCause() instanceof IOException) {
                throw (IOException) e.getCause();
            }
            throw new JuciException(e.getCause());
        } catch (InterruptedException e) {
            throw new JuciException(e);
        }
    }

    public synchronized CompletableFuture<Void> startAsync() {
        if (state != State.INITIAL) {
            throw new JuciRuntimeException("Engine is already starting/started");
        }

        state = State.STARTING;
        startLatch = new CountDownLatch(1);
        CompletableFuture<Void> future = new CompletableFuture<>();

        Thread t = new Thread(() -> {
            try {
                process = Runtime.getRuntime().exec(executablePath);
                processInput = new BufferedReader(new InputStreamReader(process.getInputStream()));
                processOutput = new PrintWriter(process.getOutputStream(), true);
                ThreadFactory threadFactory = r -> {
                    Thread t1 = Executors.defaultThreadFactory().newThread(r);
                    t1.setDaemon(true);
                    return t1;
                };
                isReadyExecutor = Executors.newFixedThreadPool(1, threadFactory);
                goExecutor = Executors.newFixedThreadPool(1, threadFactory);

                sendCommand("uci");

                Thread readerThread = new Thread(UCIEngine.this);
                readerThread.setDaemon(true);
                readerThread.start();

                boolean success = startLatch.await(START_TIMEOUT, TimeUnit.SECONDS);

                if (!success) {
                    //no uciok received within timeout
                    shutdownInternal();
                    future.completeExceptionally(new JuciException("Engine did not respond to uci communication"));
                    return;
                }

                state = State.IDLE;
                future.complete(null);
            } catch (Exception e) {
                state = State.INITIAL;
                future.completeExceptionally(e);
            }
        });
        t.setDaemon(true);
        t.start();

        return future;
    }

    public void isReady() {
        try {
            isReadyAsync().get();
        } catch (ExecutionException e) {
            if (e.getCause() instanceof RuntimeException) {
                throw (RuntimeException) e.getCause();
            }
            throw new JuciRuntimeException(e.getCause());
        } catch (InterruptedException e) {
            throw new JuciRuntimeException(e);
        }
    }

    public synchronized CompletableFuture<Void> isReadyAsync() {
        if (state == State.INITIAL) {
            throw new JuciRuntimeException("Engine is not started");
        }

        if (isReadyLatch == null || isReadyLatch.getCount() == 0) {
            isReadyLatch = new CountDownLatch(1);
        }

        CompletableFuture<Void> future = new CompletableFuture<>();

        sendCommand("isready");

        isReadyExecutor.submit(() -> {
            try {
                isReadyLatch.await();
                future.complete(null);
            } catch (Exception e) {
                future.completeExceptionally(e);
            }
        });

        return future;
    }

    @Override
    public CalculationResult go(GoCommand goCommand) {
        try {
            return goAsync(goCommand).get();
        } catch (ExecutionException e) {
            if (e.getCause() instanceof RuntimeException) {
                throw (RuntimeException) e.getCause();
            }
            throw new JuciRuntimeException(e.getCause());
        } catch (InterruptedException e) {
            throw new JuciRuntimeException(e);
        }
    }

    @Override
    public CalculationResult goInfinite() {
        return go(GoCommand.builder().infinite(true).build());
    }

    @Override
    public CalculationResult goMovetime(int movetime) {
        return go(GoCommand.builder().movetime(movetime).build());
    }

    public synchronized CompletableFuture<CalculationResult> goAsync(GoCommand goCommand) {
        if (goCommand == null) {
            throw new JuciRuntimeException("GoCommand cannot be null");
        }

        if (state != State.IDLE) {
            log.error("Cannot start calculating as current state is " + state);
            throw new JuciRuntimeException("Engine is not idle");
        }

        state = State.CALCULATING;
        calculationResultQueue.clear();
        CompletableFuture<CalculationResult> future = new CompletableFuture<>();

        sendCommand(goCommand.getCommand());

        goExecutor.submit(() -> {
            try {
                CalculationResult calculationResult = calculationResultQueue.take();
                state = State.IDLE;
                future.complete(calculationResult);
            } catch (Exception e) {
                future.completeExceptionally(e);
            }
        });

        return future;
    }

    @Override
    public CompletableFuture<CalculationResult> goInfiniteAsync() {
        return goAsync(GoCommand.builder().infinite(true).build());
    }

    @Override
    public CompletableFuture<CalculationResult> goMovetimeAsync(int movetime) {
        return goAsync(GoCommand.builder().movetime(movetime).build());
    }

    public synchronized void shutdown() {
        if (state == State.INITIAL || state == State.STARTING) {
            throw new JuciRuntimeException("Engine is not started");
        }

        shutdownInternal();
    }

    private synchronized void shutdownInternal() {
        isReadyExecutor.shutdownNow();
        goExecutor.shutdownNow();

        isReadyExecutor = null;
        goExecutor = null;

        process.destroy();
        process = null;
        processInput = null;
        processOutput = null;
        state = State.INITIAL;
    }

    @Override
    public void uciNewGame() {
        try {
            Thread.sleep(100);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        sendCommand("ucinewgame");
    }

    @Override
    public void stop() {
        sendCommand("stop");
    }

    @Override
    public void ponderhit() {
        sendCommand("ponderhit");
    }

    public void setOption(String name, Object value) {
        sendCommand("setoption name " + name + " value " + value);
    }

    public void fen(String fen, String... moves) {
        String command = "position fen " + fen;
        if (moves.length > 0) {
            command += " moves " + String.join(" ", moves);
        }
        sendCommand(command);
    }

    public void startPos(String... moves) {
        String command = "position startpos";
        if (moves.length > 0) {
            command += " moves " + String.join(" ", moves);
        }
        sendCommand(command);
    }

    private synchronized void sendCommand(String command) {
        if (processOutput == null) {
            log.warn("attempt to send command on destroyed process (" + command + ")");
            return;
        }

        log.info("executing command '{}'", command);
        processOutput.println(command);
    }

    private void checkStarted() {
        if (state == State.INITIAL) {
            throw new JuciRuntimeException("Engine not started, did you forget to call engine.start() ?");
        }
        if (state == State.STARTING) {
            throw new JuciRuntimeException("Engine still starting, wait until the future is completed when starting the engine async");
        }
    }

    public String getAuthor() {
        checkStarted();
        return author;
    }

    public String getName() {
        checkStarted();
        return name;
    }

    public Map<String, Option> getOptions() {
        checkStarted();
        return options;
    }
}
