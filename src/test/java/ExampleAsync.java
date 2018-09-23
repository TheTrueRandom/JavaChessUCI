public class ExampleAsync {
    public static void main(String[] args) throws Exception {
        UCIEngine engine = new UCIEngine("/usr/bin/stockfish");

        engine.startAsync()
                .thenRun(() -> engine.setOption("Threads", 8))
                .thenRun(() -> engine.setOption("MultiPV", 2))
                .thenRun(engine::uciNewGame)
                .thenCompose(aVoid -> engine.isReadyAsync())
                .thenRun(() -> engine.startPos("e2e4", "e7e5"))
                .thenCompose(aVoid -> engine.goMovetimeAsync(100))
                .thenAccept(System.out::println); //CalculationResult(bestmove=g1f3, ponder=b8c6)

        Thread.sleep(1000);
    }
}
