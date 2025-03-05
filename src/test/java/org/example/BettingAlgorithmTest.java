package org.example;

public class BettingAlgorithmTest {
    public static void main(String[] args) {
        // Sample messages with different odds
        String message1 = "{\"odds\": 1.5}";
        String message2 = "{\"odds\": 2.5}";

        // Process the messages
        BettingAlgorithm.processMessage(message1);
        BettingAlgorithm.processMessage(message2);
    }
}
