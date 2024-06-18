package org.example;

import java.io.PrintWriter;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.util.*;

public class BetManager {
    private static Map<String, Bet> offeredBets = new HashMap<>();
    private static Map<String, List<String>> placedBets = new HashMap<>();

    public static Map<String, Bet> getOfferedBets() {
        return offeredBets;
    }

    public static void handleBookmakerPost(String requestBody, PrintWriter out) {
        Map<String, String> params = parseRequestBody(requestBody);

        String name = params.get("name");
        String event = params.get("event");
        String oddsA = params.get("oddsA");
        String oddsDraw = params.get("oddsDraw");
        String oddsB = params.get("oddsB");
        String maxSum = params.get("maxSum");

        if (name != null && event != null && oddsA != null && oddsDraw != null && oddsB != null && maxSum != null) {
            String betId = UUID.randomUUID().toString();
            Bet bet = new Bet(name, event, oddsA, oddsDraw, oddsB, Double.parseDouble(maxSum));
            offeredBets.put(betId, bet);
            out.println("HTTP/1.1 200 OK\r\nContent-Type: text/plain\r\n\r\nBet received successfully!");
        } else {
            out.println("HTTP/1.1 400 Bad Request\r\nContent-Type: text/plain\r\n\r\nCeva nu a mers!");
        }
    }

    public static void handleBettorPost(String requestBody, PrintWriter out) {
        Map<String, String> params = parseRequestBody(requestBody);

        String bettorName = params.get("bettorName");
        String event = params.get("event");
        String outcome = params.get("outcome");
        String amount = params.get("amount");

        if (bettorName != null && event != null && outcome != null && amount != null) {
            String betKey = getMatchingBetKey(event, outcome);
            if (betKey != null) {
                Bet bet = offeredBets.get(betKey);
                if (Double.parseDouble(amount) <= bet.getMaxSum()) {
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

    public static void handleCheckOutcomePost(String requestBody, PrintWriter out) {
        Map<String, String> params = parseRequestBody(requestBody);

        String event = params.get("event");
        String outcome = params.get("outcome");
        String amountStr = params.get("amount");

        if (event != null && outcome != null && amountStr != null) {
            String betKey = getMatchingBetKey(event, outcome);
            if (betKey != null) {
                Bet bet = offeredBets.get(betKey);
                double amount = Double.parseDouble(amountStr);
                double odds = getOddsByOutcome(bet, Integer.parseInt(outcome));
                double totalOutcome = amount * odds;
                double taxedOutcome = totalOutcome - 2.5;

                out.println("HTTP/1.1 200 OK\r\nContent-Type: text/plain\r\n\r\nTotal Outcome: " + totalOutcome + "\nOutcome after Tax: " + taxedOutcome);
            } else {
                out.println("HTTP/1.1 400 Bad Request\r\nContent-Type: text/plain\r\n\r\nNo matching bet found!");
            }
        } else {
            out.println("HTTP/1.1 400 Bad Request\r\nContent-Type: text/plain\r\n\r\nCeva nu a mers!");
        }
    }

    private static String getMatchingBetKey(String event, String outcome) {
        for (Map.Entry<String, Bet> entry : offeredBets.entrySet()) {
            Bet bet = entry.getValue();
            if (bet.getEvent().equals(event)) {
                return entry.getKey();
            }
        }
        return null;
    }

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
