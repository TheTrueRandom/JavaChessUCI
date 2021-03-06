# Java Chess UCI API [![Build Status](https://travis-ci.org/TheTrueRandom/JavaChessUCI.svg?branch=master)](https://travis-ci.org/TheTrueRandom/JavaChessUCI) [![](https://jitpack.io/v/TheTrueRandom/JavaChessUCI.svg)](https://jitpack.io/#TheTrueRandom/JavaChessUCI/master) [![Codacy Badge](https://api.codacy.com/project/badge/Grade/f76fb8175fd04e89960fbcf54352f7ee)](https://app.codacy.com/app/TheTrueRandom/JavaChessUCI?utm_source=github.com&utm_medium=referral&utm_content=TheTrueRandom/JavaChessUCI&utm_campaign=Badge_Grade_Dashboard) [![codecov](https://codecov.io/gh/TheTrueRandom/JavaChessUCI/branch/master/graph/badge.svg)](https://codecov.io/gh/TheTrueRandom/JavaChessUCI)



Synchronous and asynchronous Java API for the chess UCI interface.
For full functionality overview have a look at [the interface](src/main/java/IUCIEngine.java).


Every method which is waiting for a result from the engine can be executed
synchronously (e.g. `goMovetime(int movetime)`) - blocking the current thread until the operation is finished -
or asynchronously (e.g. `goMovetimeAsync(int movetime)`) - returning a [Future](https://docs.oracle.com/javase/8/docs/api/java/util/concurrent/CompletableFuture.html) which will complete once the operation is finished.

## Installation
### Maven
```xml
<repository>
    <id>jitpack.io</id>
    <url>https://jitpack.io</url>
</repository>
```

```xml
<dependency>
    <groupId>com.github.TheTrueRandom</groupId>
    <artifactId>JavaChessUCI</artifactId>
    <version>master</version>
</dependency>
```
### Gradle
```
repositories {
    maven { url 'https://jitpack.io' }
}
```

```
dependencies {
    implementation 'com.github.TheTrueRandom:JavaChessUCI:master'
}
```
## Usage

```java
UCIEngine engine = new UCIEngine("/usr/bin/stockfish");
engine.start();

System.out.println("Name: " + engine.getName());
System.out.println("Author: " + engine.getAuthor());
engine.getOptions().entrySet().forEach(System.out::println);

engine.setOption("Threads", 8);
engine.setOption("MultiPV", 2);

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
```

### Asynchronous
```java
UCIEngine engine = new UCIEngine("/usr/bin/stockfish");

engine.startAsync()
    .thenRun(() -> engine.setOption("Threads", 8))
    .thenRun(() -> engine.setOption("MultiPV", 2))
    .thenRun(engine::uciNewGame)
    .thenCompose(aVoid -> engine.isReadyAsync())
    .thenRun(() -> engine.startPos("e2e4", "e7e5"))
    .thenCompose(aVoid -> engine.goMovetimeAsync(100))
    .thenAccept(System.out::println); //CalculationResult(bestmove=g1f3, ponder=b8c6)
```
