# Java Chess UCI API [![Build Status](https://travis-ci.org/TheTrueRandom/JavaChessUCI.svg?branch=master)](https://travis-ci.org/TheTrueRandom/JavaChessUCI) [![](https://jitpack.io/v/TheTrueRandom/JavaChessUCI.svg)](https://jitpack.io/#TheTrueRandom/JavaChessUCI)


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
    <version>master-SNAPSHOT</version>
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
    implementation 'com.github.TheTrueRandom:JavaChessUCI:master-SNAPSHOT'
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
