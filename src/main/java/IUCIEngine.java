import exception.JuciException;
import input.GoCommand;
import output.CalculationResult;
import output.Option;

import java.io.IOException;
import java.util.Map;
import java.util.concurrent.CompletableFuture;

public interface IUCIEngine {
    /**
     * Start the engine and block the current thread until the engine is started
     * (for a correct executable this method usually only blocks for a few ms).
     * When the method returns successfully the engine is successfully set to UCI mode
     * and properties can be accessed (e.g. {@link #getName()} {@link #getAuthor()}, {@link #getOptions()}
     *
     * @throws JuciException If the engine (executable) is not able to speak the UCI protocol
     * @throws IOException   If an I/O error occurs
     */
    void start() throws JuciException, IOException;

    /**
     * Start the engine asynchronously.
     *
     * @return a future which will complete once the engine is started or
     * complete exceptionally when the engine failed to start (see {@link #start()})
     * @see #start()
     */
    CompletableFuture<Void> startAsync();

    /**
     * Send 'setoption' command to the engine.
     *
     * @param name  Option name (e.g. Threads)
     * @param value Option value (e.g. 8)
     */
    void setOption(String name, Object value);

    /**
     * Send 'stop' to the engine which indicates ongoing calculation should be stopped.
     * Results in the engine to respond with 'bestmove'
     * and calls to {@link #go(GoCommand)} or {@link #goAsync(GoCommand)} to complete.
     */
    void stop();

    /**
     * Send 'position fen' to the engine.
     *
     * @param fen   the fen position
     * @param moves optional moves
     */
    void fen(String fen, String... moves);

    /**
     * Send 'position startpos' to the engine.
     *
     * @param moves optional moves
     */
    void startPos(String... moves);

    /**
     * Send 'ucinewgame' to the engine.
     * Usually {@link #isReady()} should be called afterwards.
     */
    void uciNewGame();

    /**
     * Send UCI 'isready' to the engine.
     * Blocks the current thread until the engine responds with 'readyok'
     */
    void isReady();

    /**
     * Send UCI 'isready' to the engine asynchronously.
     *
     * @return a future which will complete once the engine responds with 'readyok'
     * @see #isReady()
     */
    CompletableFuture<Void> isReadyAsync();

    /**
     * Send UCI 'go' command to the engine
     *
     * @param goCommand allows to set every possible UCI go parameter
     * @return {@link output.CalculationResult}
     */
    CalculationResult go(GoCommand goCommand);

    /**
     * Convenience method for UCI 'go infinite'
     *
     * @see #go(GoCommand)
     */
    CalculationResult goInfinite();

    /**
     * Convenience method for UCI 'go movetime X'
     *
     * @param movetime movetime in ms
     * @see #go(GoCommand)
     */
    CalculationResult goMovetime(int movetime);

    /**
     * Send UCI 'go' command to the engine asynchronously
     *
     * @return a future which will complete when the engine finishes calculating
     * @see #go(GoCommand)
     */
    CompletableFuture<CalculationResult> goAsync(GoCommand goCommand);

    /**
     * Convenience method for UCI 'go infinite' asynchronously
     *
     * @see #goAsync(GoCommand)
     */
    CompletableFuture<CalculationResult> goInfiniteAsync();

    /**
     * Convenience method for UCI 'go movetime X' asynchronously
     *
     * @see #goAsync(GoCommand)
     */
    CompletableFuture<CalculationResult> goMovetimeAsync(int movetime);

    /**
     * Send 'ponderhit' to the engine
     */
    void ponderhit();

    /**
     * Shutdown the engine by destroying the process.
     * {@link #start()} could be called again afterwards
     */
    void shutdown();

    /**
     * Get the name of the engine.
     * Note: this method can only be called after {@link #start()}
     *
     * @return name
     */
    String getName();

    /**
     * Get the author of the engine.
     * Note: this method can only be called after {@link #start()}
     *
     * @return author
     */
    String getAuthor();

    /**
     * Get the options of the engine
     * Note: this method can only be called after {@link #start()}
     *
     * @return a map with optionName: {@link output.Option}
     */
    Map<String, Option> getOptions();
}
