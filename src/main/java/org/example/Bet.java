package org.example;

public class Bet {
    String name;
    String event;
    String oddsA;
    String oddsDraw;
    String oddsB;
    double maxSum;

    public Bet(String name, String event, String oddsA, String oddsDraw, String oddsB, double maxSum) {
        this.name = name;
        this.event = event;
        this.oddsA = oddsA;
        this.oddsDraw = oddsDraw;
        this.oddsB = oddsB;
        this.maxSum = maxSum;
    }
}
