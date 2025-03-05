package org.example;

import java.io.PrintWriter;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.util.*;

public class BetManager {
    // Map to store offered bets with a unique ID
    private static Map<String, Bet> offeredBets = new HashMap<>();

    // Map to store placed bets categorized by bettor's name
    private static Map<String, List<String>> placedBets = new HashMap<>();

    // Getter method to access the offered bets
    public static Map<String, Bet> getOfferedBets() {
        return offeredBets;
    }

    // Method to handle bookmaker's POST requests
    public static void handleBookmakerPost(String requestBody, PrintWriter out) {
        // Parse the request body to extract parameters
        Map<String, String> params = parseRequestBody(requestBody);

        // Extract parameters from the parsed map
        String name = params.get("name");
        String event = params.get("event");
        String oddsA = params.get("oddsA");
        String oddsDraw = params.get("oddsDraw");
        String oddsB = params.get("oddsB");
        String maxSum = params.get("maxSum");

        // Check if all required parameters are present
        if (name != null && event != null && oddsA != null && oddsDraw != null && oddsB != null && maxSum != null) {
            // Create a unique ID for the bet and create a new Bet object
            String betId = UUID.randomUUID().toString();
            Bet bet = new Bet(name, event, oddsA, oddsDraw, oddsB, Double.parseDouble(maxSum));

            // Store the bet in the offeredBets map
            offeredBets.put(betId, bet);
            out.println("HTTP/1.1 200 OK\r\nContent-Type: text/plain\r\n\r\nBet received successfully!");
        } else {
            out.println("HTTP/1.1 400 Bad Request\r\nContent-Type: text/plain\r\n\r\nCeva nu a mers!");
        }
    }

    // Method to handle bettor's POST requests
    public static void handleBettorPost(String requestBody, PrintWriter out) {
        // Parse the request body to extract parameters
        Map<String, String> params = parseRequestBody(requestBody);

        // Extract parameters from the parsed map
        String bettorName = params.get("bettorName");
        String event = params.get("event");
        String outcome = params.get("outcome");
        String amount = params.get("amount");

        // Check if all required parameters are present
        if (bettorName != null && event != null && outcome != null && amount != null) {
            // Find a matching bet based on the event and outcome
            String betKey = getMatchingBetKey(event, outcome);
            if (betKey != null) {
                Bet bet = offeredBets.get(betKey);
                // Check if the amount is within the maximum sum allowed for the bet
                if (Double.parseDouble(amount) <= bet.getMaxSum()) {
                    // Store the bet details in the placedBets map
                    String betDetails = String.format("Bettor: %s, Event: %s, Outcome: %s, Amount: %s", bettorName, event, outcome, amount);
                    placedBets.computeIfAbsent(bettorName, k -> new ArrayList<>()).add(betDetails);
                    out.println("HTTP/1.1 200 OK\r\nContent-Type: text/plain\r\n\r\nBet placed successfully!");
                } else {
                    out.println("HTTP/1.1 400 Bad Request\r\nContent-Type: text/plain\r\n\r\nAmount exceeds maximum allotted sum!");
                }
            } else {
                out.println("HTTP/1.1 400 Bad Request\r\nContent-Type: text/plain\r\n\r\nNo matching bet found!");
            }
        } else {
            out.println("HTTP/1.1 400 Bad Request\r\nContent-Type: text/plain\r\n\r\nCeva nu a mers!");
        }
    }

    // Method to handle checking the outcome of a bet
    public static void handleCheckOutcomePost(String requestBody, PrintWriter out) {
        // Parse the request body to extract parameters
        Map<String, String> params = parseRequestBody(requestBody);

        // Extract parameters from the parsed map
        String event = params.get("event");
        String outcome = params.get("outcome");
        String amountStr = params.get("amount");

        // Check if all required parameters are present
        if (event != null && outcome != null && amountStr != null) {
            // Find a matching bet based on the event and outcome
            String betKey = getMatchingBetKey(event, outcome);
            if (betKey != null) {
                Bet bet = offeredBets.get(betKey);
                double amount = Double.parseDouble(amountStr);
                double odds = getOddsByOutcome(bet, Integer.parseInt(outcome));
                double totalOutcome = amount * odds;
                double taxedOutcome = totalOutcome - 2.5;

                // Send the calculated outcome back to the client
                out.println("HTTP/1.1 200 OK\r\nContent-Type: text/plain\r\n\r\nTotal Outcome: " + totalOutcome + "\nOutcome after Tax: " + taxedOutcome);
            } else {
                out.println("HTTP/1.1 400 Bad Request\r\nContent-Type: text/plain\r\n\r\nNo matching bet found!");
            }
        } else {
            out.println("HTTP/1.1 400 Bad Request\r\nContent-Type: text/plain\r\n\r\nCeva nu a mers!");
        }
    }

    // Helper method to find a matching bet key based on the event and outcome
    private static String getMatchingBetKey(String event, String outcome) {
        for (Map.Entry<String, Bet> entry : offeredBets.entrySet()) {
            Bet bet = entry.getValue();
            if (bet.getEvent().equals(event)) {
                return entry.getKey();
            }
        }
        return null;
    }

    // Helper method to get the odds based on the outcome
    private static double getOddsByOutcome(Bet bet, int outcome) {
        switch (outcome) {
            case 0:
                return Double.parseDouble(bet.getOddsDraw());
            case 1:
                return Double.parseDouble(bet.getOddsA());
            case 2:
                return Double.parseDouble(bet.getOddsB());
            default:
                return 0;
        }
    }

    // Helper method to parse the request body into a map of parameters
    private static Map<String, String> parseRequestBody(String requestBody) {
        String[] pairs = requestBody.split("&");
        Map<String, String> params = new HashMap<>();

        for (String pair : pairs) {
            String[] keyValue = pair.split("=");
            if (keyValue.length == 2) {
                params.put(URLDecoder.decode(keyValue[0], StandardCharsets.UTF_8),
                        URLDecoder.decode(keyValue[1], StandardCharsets.UTF_8));
            }
        }
        return params;
    }
}
