package org.example;

import java.io.*;
import java.net.*;
import java.nio.charset.StandardCharsets;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

public class Main {

    // Store offered bets in memory
    private static Map<String, Bet> offeredBets = new ConcurrentHashMap<>();
    private static Map<String, List<String>> placedBets = new ConcurrentHashMap<>();

    public static void main(String[] args) {
        int port = 8081; // Port number for HTTP server

        try (ServerSocket serverSocket = new ServerSocket(port)) {
            System.out.println("Server is listening on port " + port);

            while (true) {
                Socket clientSocket = serverSocket.accept();
                System.out.println("New connection: " + clientSocket.getInetAddress());

                // Handle client request in a new thread
                Thread thread = new Thread(() -> {
                    try (
                            BufferedReader in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
                            PrintWriter out = new PrintWriter(clientSocket.getOutputStream(), true)
                    ) {
                        String requestLine = in.readLine();
                        System.out.println("Request: " + requestLine);

                        if (requestLine != null) {
                            // GET request
                            if (requestLine.startsWith("GET")) {
                                handleGetRequest(requestLine, out);
                            }
                            // POST request
                            else if (requestLine.startsWith("POST")) {
                                handlePostRequest(requestLine, in, out);
                            } else {
                                String response = "HTTP/1.1 400 Bad Request\r\n\r\n";
                                out.println(response);
                            }
                        }
                    } catch (IOException e) {
                        System.err.println("Error handling client request: " + e.getMessage());
                    } finally {
                        try {
                            clientSocket.close();
                        } catch (IOException e) {
                            System.err.println("Error closing client socket: " + e.getMessage());
                        }
                    }
                });

                thread.start();
            }
        } catch (IOException e) {
            System.err.println("Error starting the server: " + e.getMessage());
        }
    }

    public static void handleGetRequest(String requestLine, PrintWriter out) {
        // Determine the type of user from the URL
        String response;
        if (requestLine.contains("/bettor")) {
            response = generateBettorPage();
        } else if (requestLine.contains("/simulation-user")) {
            response = generateSimulationUserPage();
        } else if (requestLine.contains("/bookmaker")) {
            response = generateBookmakerPage();
        } else if (requestLine.contains("/view-offered-bets")) {
            response = viewOfferedBets();
        } else {
            response = generateHomePage();
        }
        out.println(response);
    }

    private static String generateHomePage() {
        return "HTTP/1.1 200 OK\r\n"
                + "Content-Type: text/html\r\n"
                + "\r\n"
                + "<html><body>"
                + "<h1>Welcome to the Sports Betting Simulation</h1>"
                + "<ul>"
                + "<li><a href='/bettor'>Bettor</a></li>"
                + "<li><a href='/simulation-user'>Simulation User</a></li>"
                + "<li><a href='/bookmaker'>Bookmaker</a></li>"
                + "</ul>"
                + "</body></html>";
    }

    private static String generateBettorPage() {
        return "HTTP/1.1 200 OK\r\n"
                + "Content-Type: text/html\r\n"
                + "\r\n"
                + "<html><body>"
                + "<h1>Bettor Page</h1>"
                + "<form method='POST' action='/bettor'>"
                + "<label for='bettorName'>Bettor Name:</label><br>"
                + "<input type='text' id='bettorName' name='bettorName'><br><br>"
                + "<label for='event'>Event (format: TeamA-TeamB):</label><br>"
                + "<input type='text' id='event' name='event'><br><br>"
                + "<label for='outcome'>Outcome (0=Draw, 1=TeamA wins, 2=TeamB wins):</label><br>"
                + "<input type='text' id='outcome' name='outcome'><br><br>"
                + "<label for='amount'>Amount:</label><br>"
                + "<input type='text' id='amount' name='amount'><br><br>"
                + "<input type='submit' value='Place Bet'>"
                + "</form>"
                + "<h2>Check Bet Outcome</h2>"
                + "<form method='POST' action='/check-outcome'>"
                + "<label for='event'>Event (format: TeamA-TeamB):</label><br>"
                + "<input type='text' id='event' name='event'><br><br>"
                + "<label for='outcome'>Outcome (0=Draw, 1=TeamA wins, 2=TeamB wins):</label><br>"
                + "<input type='text' id='outcome' name='outcome'><br><br>"
                + "<label for='amount'>Amount:</label><br>"
                + "<input type='text' id='amount' name='amount'><br><br>"
                + "<input type='submit' value='Check Outcome'>"
                + "</form>"
                + "<h2>Available Bets</h2>"
                + "<a href='/view-offered-bets'>View all offered bets</a>"
                + "</body></html>";
    }

    private static String generateSimulationUserPage() {
        return "HTTP/1.1 200 OK\r\n"
                + "Content-Type: text/html\r\n"
                + "\r\n"
                + "<html><body>"
                + "<h1>Simulation User Page</h1>"
                + "<p>This section is for administrators who observe or modify the simulation.</p>"
                + "</body></html>";
    }

    private static String generateBookmakerPage() {
        return "HTTP/1.1 200 OK\r\n"
                + "Content-Type: text/html\r\n"
                + "\r\n"
                + "<html><body>"
                + "<h1>Bookmaker Page</h1>"
                + "<form method='POST' action='/bookmaker'>"
                + "<label for='name'>Bookmaker Name:</label><br>"
                + "<input type='text' id='name' name='name'><br><br>"
                + "<label for='event'>Event (format: TeamA-TeamB):</label><br>"
                + "<input type='text' id='event' name='event'><br><br>"
                + "<label for='oddsA'>Odds for TeamA:</label><br>"
                + "<input type='text' id='oddsA' name='oddsA'><br><br>"
                + "<label for='oddsDraw'>Odds for Draw:</label><br>"
                + "<input type='text' id='oddsDraw' name='oddsDraw'><br><br>"
                + "<label for='oddsB'>Odds for TeamB:</label><br>"
                + "<input type='text' id='oddsB' name='oddsB'><br><br>"
                + "<label for='maxSum'>Maximum Allotted Sum:</label><br>"
                + "<input type='text' id='maxSum' name='maxSum'><br><br>"
                + "<input type='submit' value='Submit'>"
                + "</form>"
                + "</body></html>";
    }

    private static String viewOfferedBets() {
        StringBuilder response = new StringBuilder("HTTP/1.1 200 OK\r\n"
                + "Content-Type: text/html\r\n"
                + "\r\n"
                + "<html><body>"
                + "<h1>Offered Bets</h1>"
                + "<ul>");
        synchronized (offeredBets) {
            for (Bet bet : offeredBets.values()) {
                response.append("<li>").append("Event: ").append(bet.event)
                        .append(", Odds: [")
                        .append(bet.oddsA).append(", ")
                        .append(bet.oddsDraw).append(", ")
                        .append(bet.oddsB).append("], Max Sum: ")
                        .append(bet.maxSum).append("</li>");
            }
        }
        response.append("</ul>")
                .append("</body></html>");
        return response.toString();
    }

    public static void handlePostRequest(String requestLine, BufferedReader in, PrintWriter out) throws IOException {
        StringBuilder requestBody = new StringBuilder();
        String line;
        int contentLength = 0;
        while (!(line = in.readLine()).isEmpty()) {
            if (line.startsWith("Content-Length:")) {
                contentLength = Integer.parseInt(line.split(":")[1].trim());
            }
        }

        char[] body = new char[contentLength];
        in.read(body, 0, contentLength);
        requestBody.append(body);

        System.out.println("Full Request Body: " + requestBody.toString());

        if (requestLine.contains("/bookmaker")) {
            handleBookmakerPost(requestBody.toString(), out);
        } else if (requestLine.contains("/bettor")) {
            handleBettorPost(requestBody.toString(), out);
        } else if (requestLine.contains("/check-outcome")) {
            handleCheckOutcomePost(requestBody.toString(), out);
        } else {
            String response = "HTTP/1.1 400 Bad Request\r\n"
                    + "Content-Type: text/plain\r\n"
                    + "\r\n"
                    + "Unknown action!";
            out.println(response);
        }
    }

    private static void handleBookmakerPost(String requestBody, PrintWriter out) {
        // Extract the bet data from the request
        String[] pairs = requestBody.split("&");
        Map<String, String> params = new HashMap<>();

        for (String pair : pairs) {
            String[] keyValue = pair.split("=");
            if (keyValue.length == 2) {
                params.put(URLDecoder.decode(keyValue[0], StandardCharsets.UTF_8),
                        URLDecoder.decode(keyValue[1], StandardCharsets.UTF_8));
            }
        }

        System.out.println("Parsed Params: " + params);

        String name = params.get("name");
        String event = params.get("event");
        String oddsA = params.get("oddsA");
        String oddsDraw = params.get("oddsDraw");
        String oddsB = params.get("oddsB");
        String maxSum = params.get("maxSum");

        System.out.println("Name: " + name);
        System.out.println("Event: " + event);
        System.out.println("OddsA: " + oddsA);
        System.out.println("OddsDraw: " + oddsDraw);
        System.out.println("OddsB: " + oddsB);
        System.out.println("MaxSum: " + maxSum);

        if (name != null && event != null && oddsA != null && oddsDraw != null && oddsB != null && maxSum != null) {
            String betId = UUID.randomUUID().toString();
            Bet bet = new Bet(name, event, oddsA, oddsDraw, oddsB, Double.parseDouble(maxSum));
            offeredBets.put(betId, bet);
            String response = "HTTP/1.1 200 OK\r\n"
                    + "Content-Type: text/plain\r\n"
                    + "\r\n"
                    + "Bet received successfully!";
            out.println(response);
        } else {
            // If we did not find a valid bet, respond with an error
            String response = "HTTP/1.1 400 Bad Request\r\n"
                    + "Content-Type: text/plain\r\n"
                    + "\r\n"
                    + "Ceva nu a mers!";
            out.println(response);
        }
    }

    private static void handleBettorPost(String requestBody, PrintWriter out) {
        // Extract the bet data from the request
        String[] pairs = requestBody.split("&");
        Map<String, String> params = new HashMap<>();

        for (String pair : pairs) {
            String[] keyValue = pair.split("=");
            if (keyValue.length == 2) {
                params.put(URLDecoder.decode(keyValue[0], StandardCharsets.UTF_8),
                        URLDecoder.decode(keyValue[1], StandardCharsets.UTF_8));
            }
        }

        System.out.println("Parsed Params: " + params);

        String bettorName = params.get("bettorName");
        String event = params.get("event");
        String outcome = params.get("outcome");
        String amount = params.get("amount");

        System.out.println("Bettor Name: " + bettorName);
        System.out.println("Event: " + event);
        System.out.println("Outcome: " + outcome);
        System.out.println("Amount: " + amount);

        if (bettorName != null && event != null && outcome != null && amount != null) {
            String betKey = getMatchingBetKey(event, outcome);
            if (betKey != null) {
                Bet bet = offeredBets.get(betKey);
                if (Double.parseDouble(amount) <= bet.maxSum) {
                    String betDetails = String.format("Bettor: %s, Event: %s, Outcome: %s, Amount: %s", bettorName, event, outcome, amount);
                    placedBets.computeIfAbsent(bettorName, k -> new ArrayList<>()).add(betDetails);
                    String response = "HTTP/1.1 200 OK\r\n"
                            + "Content-Type: text/plain\r\n"
                            + "\r\n"
                            + "Bet placed successfully!";
                    out.println(response);
                } else {
                    String response = "HTTP/1.1 400 Bad Request\r\n"
                            + "Content-Type: text/plain\r\n"
                            + "\r\n"
                            + "Amount exceeds maximum allotted sum!";
                    out.println(response);
                }
            } else {
                String response = "HTTP/1.1 400 Bad Request\r\n"
                        + "Content-Type: text/plain\r\n"
                        + "\r\n"
                        + "No matching bet found!";
                out.println(response);
            }
        } else {
            // If we did not find a valid bet, respond with an error
            String response = "HTTP/1.1 400 Bad Request\r\n"
                    + "Content-Type: text/plain\r\n"
                    + "\r\n"
                    + "Ceva nu a mers!";
            out.println(response);
        }
    }

    private static void handleCheckOutcomePost(String requestBody, PrintWriter out) {
        // Extract the bet data from the request
        String[] pairs = requestBody.split("&");
        Map<String, String> params = new HashMap<>();

        for (String pair : pairs) {
            String[] keyValue = pair.split("=");
            if (keyValue.length == 2) {
                params.put(URLDecoder.decode(keyValue[0], StandardCharsets.UTF_8),
                        URLDecoder.decode(keyValue[1], StandardCharsets.UTF_8));
            }
        }

        System.out.println("Parsed Params: " + params);

        String event = params.get("event");
        String outcome = params.get("outcome");
        String amountStr = params.get("amount");

        System.out.println("Event: " + event);
        System.out.println("Outcome: " + outcome);
        System.out.println("Amount: " + amountStr);

        if (event != null && outcome != null && amountStr != null) {
            String betKey = getMatchingBetKey(event, outcome);
            if (betKey != null) {
                Bet bet = offeredBets.get(betKey);
                double amount = Double.parseDouble(amountStr);
                double odds = getOddsByOutcome(bet, Integer.parseInt(outcome));
                double totalOutcome = amount * odds;
                double taxedOutcome = totalOutcome - 2.5;

                String response = "HTTP/1.1 200 OK\r\n"
                        + "Content-Type: text/plain\r\n"
                        + "\r\n"
                        + "Total Outcome: " + totalOutcome + "\n"
                        + "Outcome after Tax: " + taxedOutcome;
                out.println(response);
            } else {
                String response = "HTTP/1.1 400 Bad Request\r\n"
                        + "Content-Type: text/plain\r\n"
                        + "\r\n"
                        + "No matching bet found!";
                out.println(response);
            }
        } else {
            // If we did not find a valid bet, respond with an error
            String response = "HTTP/1.1 400 Bad Request\r\n"
                    + "Content-Type: text/plain\r\n"
                    + "\r\n"
                    + "Ceva nu a mers!";
            out.println(response);
        }
    }

    private static String getMatchingBetKey(String event, String outcome) {
        for (Map.Entry<String, Bet> entry : offeredBets.entrySet()) {
            Bet bet = entry.getValue();
            if (bet.event.equals(event)) {
                return entry.getKey();
            }
        }
        return null;
    }

    private static double getOddsByOutcome(Bet bet, int outcome) {
        switch (outcome) {
            case 0:
                return Double.parseDouble(bet.oddsDraw);
            case 1:
                return Double.parseDouble(bet.oddsA);
            case 2:
                return Double.parseDouble(bet.oddsB);
            default:
                return 0;
        }
    }

    private static class Bet {
        String name;
        String event;
        String oddsA;
        String oddsDraw;
        String oddsB;
        double maxSum;

        Bet(String name, String event, String oddsA, String oddsDraw, String oddsB, double maxSum) {
            this.name = name;
            this.event = event;
            this.oddsA = oddsA;
            this.oddsDraw = oddsDraw;
            this.oddsB = oddsB;
            this.maxSum = maxSum;
        }
    }
}
