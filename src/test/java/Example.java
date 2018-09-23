import output.CalculationResult;

public class Example {
    public static void main(String[] args) throws Exception {
        UCIEngine engine = new UCIEngine("/usr/bin/stockfish");
        engine.start();

        System.out.println("Name: " + engine.getName());
        System.out.println("Author: " + engine.getAuthor());
        engine.getOptions().entrySet().forEach(System.out::println);

        engine.setOption("Threads", 8);
        engine.setOption("MultiPV", 2);

        engine.addInfoListener((uciEngine, info) -> {
            System.out.println(uciEngine.getName() + " information during calculation: " + info);
        });

        engine.uciNewGame();
        engine.isReady();
        engine.startPos("e2e4", "e7e5");
        CalculationResult calculationResult = engine.goMovetime(100);

        System.out.println("Bestmove: " + calculationResult.getBestmove());
        System.out.println("Ponder: " + calculationResult.getPonder());

        System.out.println("Bestmove multipv 1: " + calculationResult.getBestmovePv(1));
        System.out.println("Bestmove multipv 2: " + calculationResult.getBestmovePv(2));

        System.out.println("Score Information for multipv 1: " + calculationResult.getLastScoreInfo(1));
        System.out.println("Score Information for multipv 2: " + calculationResult.getLastScoreInfo(2));
    }
}
