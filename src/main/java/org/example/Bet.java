package org.example;

public class Bet {
    public String name;
    public String event;
    public String oddsA;
    public String oddsDraw;
    public String oddsB;
    public double maxSum;

    public Bet(String name, String event, String oddsA, String oddsDraw, String oddsB, double maxSum) {
        this.name = name;
        this.event = event;
        this.oddsA = oddsA;
        this.oddsDraw = oddsDraw;
        this.oddsB = oddsB;
        this.maxSum = maxSum;
    }

    public String getName() {
        return name;
    }

    public String getEvent() {
        return event;
    }

    public String getOddsA() {
        return oddsA;
    }

    public String getOddsDraw() {
        return oddsDraw;
    }

    public String getOddsB() {
        return oddsB;
    }

    public double getMaxSum() {
        return maxSum;
    }
}
