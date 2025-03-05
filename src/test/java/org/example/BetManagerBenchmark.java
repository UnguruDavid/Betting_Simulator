package org.example;

import org.openjdk.jmh.annotations.*;
import java.util.concurrent.TimeUnit;
import java.io.PrintWriter;
import java.io.StringWriter;

@State(Scope.Benchmark)
public class BetManagerBenchmark {

    private String requestBodyBookmaker;
    private String requestBodyBettor;

    // Setup method to initialize test data
    @Setup
    public void setUp() {
        // Test data for bookmaker's POST request
        requestBodyBookmaker = "name=Bookmaker1&event=TeamA-TeamB&oddsA=2.0&oddsDraw=3.0&oddsB=4.0&maxSum=1000";
        BetManager.handleBookmakerPost(requestBodyBookmaker, new PrintWriter(new StringWriter()));

        // Test data for bettor's POST request
        requestBodyBettor = "bettorName=Bettor1&event=TeamA-TeamB&outcome=1&amount=500";
    }

    // Benchmark method to measure throughput of handling bettor POST requests
    @Benchmark
    @BenchmarkMode(Mode.Throughput)
    @OutputTimeUnit(TimeUnit.MILLISECONDS)
    public void testHandleBettorPost() {
        BetManager.handleBettorPost(requestBodyBettor, new PrintWriter(new StringWriter()));
    }
}
