import exception.JuciException;
import exception.JuciRuntimeException;
import input.GoCommand;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import output.CalculationResult;
import output.Info;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.*;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.fail;


class EngineTest {
    private static String EXECUTABLE;
    private UCIEngine engine;

    @BeforeAll
    static void init() {
        EXECUTABLE = EngineTest.class.getResource("stockfish_8_x64").getFile();
        new File(EXECUTABLE).setExecutable(true);
    }

    @BeforeEach
    void setUp() throws JuciException, IOException {
        engine = new UCIEngine(EXECUTABLE);
        engine.start();
    }

    @AfterEach
    void tearDown() {
        engine.shutdown();
    }

    @Test
    void wrongExecutableThrowsIOException() throws JuciException {
        try {
            new UCIEngine("wrongExecutable").start();
            fail("expected IOException");
        } catch (IOException e) {
            //expected
        }
    }

    @Test
    void startEngineWithNonUCIExecutableThenException() throws IOException {
        UCIEngine engine = new UCIEngine("python");
        try {
            engine.start();
            fail("Expected JuciException");
        } catch (JuciException e) {
            assertThat(e.getMessage(), is("Engine did not respond to uci communication"));
        }
    }

    @Test
    void startEngineAsync() throws ExecutionException, InterruptedException {
        UCIEngine engine = new UCIEngine(EXECUTABLE);
        CompletableFuture<Void> startFuture = engine.startAsync();
        try {
            engine.getAuthor();
            fail("Expected JuciRuntimeException");
        } catch (JuciRuntimeException e) {
            assertThat(e.getMessage(), is("Engine still starting, wait until the future is completed when starting the engine async"));
        }

        startFuture.get();

        assertThat(engine.getAuthor(), is(notNullValue()));
        engine.shutdown();
    }

    @Test
    void startTheEngineTwiceThrowsJuciRuntime() {
        UCIEngine engine = new UCIEngine(EXECUTABLE);
        engine.startAsync();

        try {
            engine.startAsync();
            fail("expected JuciRuntime");
        } catch (JuciRuntimeException e) {
            assertThat(e.getMessage(), is("Engine is already starting/started"));
        }
    }

    @Test
    void shutdownEngine() throws Exception {
        engine.shutdown();
        engine.start();
        engine.isReady();
    }

    @Test
    void initialPropertiesSetWhenStartingEngine() {
        assertThat(engine.getAuthor(), is(notNullValue()));
        assertThat(engine.getName(), is(notNullValue()));
        assertThat(engine.getOptions(), hasKey("Threads"));
    }

    @Test
    void isReadyShouldWorkConcurrently() throws InterruptedException {
        int threads = 10;
        int count = 10;
        //test would block if there are deadlock/racing conditions
        BlockingQueue<Object> queue = new LinkedBlockingQueue<>();
        CountDownLatch countDownLatch = new CountDownLatch(threads);
        for (int i = 0; i < threads; i++) {
            new Thread(() -> {
                for (int j = 0; j < count; j++) {
                    engine.isReady();
                    queue.add(new Object());
                }
                countDownLatch.countDown();
            }).start();
        }

        countDownLatch.await();
        assertThat(queue, hasSize(threads * count));
    }

    @Test
    void engineCalculationFuture() throws Exception {
        CompletableFuture<CalculationResult> result = engine.goAsync(GoCommand.builder()
                .searchmoves(Arrays.asList("e2e4", "d2d4"))
                .movetime(1000)
                .build());
        CalculationResult calculationResult = result.get();
        assertThat(calculationResult.getBestmove(), isOneOf("e2e4", "d2d4"));
    }

    @Test
    void positionFen() throws Exception {
        List<Info> infos = new ArrayList<>();
        engine.addInfoListener((uciEngine, info) -> infos.add(info));
        engine.fen("8/8/8/8/8/6k1/r7/7K b - - 0 1");

        CalculationResult calculationResult = engine.goMovetimeAsync(100).get();

        assertThat(calculationResult.getBestmove(), is("a2a1"));
        assertThat(calculationResult.getLastScoreInfo().getScore().getMate(), is(1));
        assertThat(infos.get(infos.size() - 1).getScore().getMate(), is(1));
    }

    @Test
    void positionFenWithMoves() {
        engine.fen("8/8/8/6r1/8/6k1/8/7K w - - 5 4", "h1g1", "g5f5", "g1h1");

        CalculationResult calculationResult = engine.goMovetime(100);

        assertThat(calculationResult.getBestmove(), is("f5f1"));
        assertThat(calculationResult.getLastScoreInfo().getScore().getMate(), is(1));
    }

    @Test
    void positionStartpos() throws IOException, JuciException {
        UCIEngine engine = new UCIEngine(EXECUTABLE);
        engine.start();

        engine.startPos("e2e4", "e7e5", "f1c4", "a7a5", "d1f3", "a5a4");
        CalculationResult calculationResult = engine.goMovetime(100);

        assertThat(calculationResult.getBestmove(), is("f3f7"));
        assertThat(calculationResult.getLastScoreInfo().getScore().getMate(), is(1));
    }

    @Test
    void uciNewGame() {
        engine.fen("8/8/8/6r1/8/6k1/8/7K w - - 5 4");
        engine.goMovetime(100);
        engine.startPos();
        engine.uciNewGame();
        engine.isReady();

        CalculationResult result = engine.go(GoCommand.builder()
                .searchmoves(Arrays.asList("e2e4", "d2d4"))
                .movetime(100)
                .build());
        assertThat(result.getBestmove(), isOneOf("e2e4", "d2d4"));
    }


    @Test
    void sendStop() throws Exception {
        for (int i = 0; i < 10; i++) {
            List<CalculationResult> result = new ArrayList<>();
            engine.goInfiniteAsync().thenAccept(result::add);
            Thread.sleep(100);

            engine.stop();
            engine.isReady();
            Thread.sleep(10); //call to list.add() may take longer than receiving readyok

            assertThat(result, hasSize(1));
        }
    }

    @Test
    void shutdownWhileCalculatingThenCompletedWithException() throws InterruptedException, IOException, JuciException, ExecutionException {
        UCIEngine engine = new UCIEngine(EXECUTABLE);
        engine.start();
        CompletableFuture<CalculationResult> future = engine.goInfiniteAsync();
        engine.shutdown();

        try {
            future.get();
            fail("Expected exception");
        } catch (ExecutionException e) {
            assertThat(e.getCause(), instanceOf(InterruptedException.class));
        }

        engine.start();

        ExecutorService executor = Executors.newSingleThreadExecutor();
        Future<Exception> expectedException = executor.submit(() -> {
            try {
                engine.goInfinite();
                fail("Expected exception");
            } catch (Exception e) {
                return e;
            }

            return null;
        });

        Thread.sleep(100);
        engine.shutdown();
        executor.shutdown();

        assertThat(expectedException.get().getCause(), instanceOf(InterruptedException.class));
    }

    @Test
    void shutdownDuringStartThrowsException() throws InterruptedException {
        UCIEngine engine = new UCIEngine("python");
        engine.startAsync();

        //"race condition"
        JuciRuntimeException e = assertThrows(JuciRuntimeException.class, engine::shutdown);
        assertThat(e.getMessage(), is("Engine is not started"));

        //not yet started
        Thread.sleep(100);
        assertThrows(JuciRuntimeException.class, engine::shutdown);
    }

    @Test
    void onlyOneCalculation() {
        engine.goInfiniteAsync();
        assertThrows(JuciRuntimeException.class, engine::goInfinite);
    }

    @Test
    void ponderhit() throws InterruptedException, ExecutionException {
        engine.fen("4r3/8/8/8/8/6k1/8/5K2 b - - 4 3", "e8e3", "f1g1");
        CompletableFuture<CalculationResult> future = engine.goAsync(GoCommand.builder().ponder(true).build());
        Thread.sleep(100);
        engine.ponderhit();
        assertThat(future.get().getBestmove(), is("e3e1"));
    }
}
