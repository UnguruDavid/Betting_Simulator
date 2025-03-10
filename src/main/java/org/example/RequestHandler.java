package org.example;

import java.io.*;
import java.net.Socket;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;

public class RequestHandler implements Runnable {
    private Socket clientSocket;

    // Constructor to initialize the client socket
    public RequestHandler(Socket clientSocket) {
        this.clientSocket = clientSocket;
    }

    @Override
    public void run() {
        try (
                BufferedReader in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
                PrintWriter out = new PrintWriter(clientSocket.getOutputStream(), true)
        ) {
            // Read the request line from the client
            String requestLine = in.readLine();
            System.out.println("Request: " + requestLine);

            // Handle the request based on its type (GET or POST)
            if (requestLine != null) {
                if (requestLine.startsWith("GET")) {
                    handleGetRequest(requestLine, out);
                } else if (requestLine.startsWith("POST")) {
                    handlePostRequest(requestLine, in, out);
                } else {
                    out.println("HTTP/1.1 400 Bad Request\r\n\r\n");
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
    }

    // Method to handle GET requests
    private void handleGetRequest(String requestLine, PrintWriter out) {
        String response;
        if (requestLine.contains("/bettor")) {
            response = PageGenerator.generateBettorPage();
        } else if (requestLine.contains("/simulation-user")) {
            response = PageGenerator.generateSimulationUserPage();
        } else if (requestLine.contains("/bookmaker")) {
            response = PageGenerator.generateBookmakerPage();
        } else if (requestLine.contains("/view-offered-bets")) {
            response = PageGenerator.viewOfferedBets();
        } else {
            response = PageGenerator.generateHomePage();
        }
        out.println(response);
    }

    // Method to handle POST requests
    private void handlePostRequest(String requestLine, BufferedReader in, PrintWriter out) throws IOException {
        StringBuilder requestBody = new StringBuilder();
        String line;
        int contentLength = 0;

        // Read the headers and extract the content length
        while (!(line = in.readLine()).isEmpty()) {
            if (line.startsWith("Content-Length:")) {
                contentLength = Integer.parseInt(line.split(":")[1].trim());
            }
        }

        // Read the request body
        char[] body = new char[contentLength];
        in.read(body, 0, contentLength);
        requestBody.append(body);

        // Delegate the request to the appropriate handler in BetManager
        if (requestLine.contains("/bookmaker")) {
            BetManager.handleBookmakerPost(requestBody.toString(), out);
        } else if (requestLine.contains("/bettor")) {
            BetManager.handleBettorPost(requestBody.toString(), out);
        } else if (requestLine.contains("/check-outcome")) {
            BetManager.handleCheckOutcomePost(requestBody.toString(), out);
        } else {
            out.println("HTTP/1.1 400 Bad Request\r\nContent-Type: text/plain\r\n\r\nUnknown action!");
        }
    }
}
