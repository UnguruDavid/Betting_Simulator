package org.example;

import java.io.*;
import java.net.*;

public class Server {
    private static final int PORT = 8081;

    public static void main(String[] args) {
        try (ServerSocket serverSocket = new ServerSocket(PORT)) {
            System.out.println("Server is listening on port " + PORT);

            while (true) {
                Socket clientSocket = serverSocket.accept();
                System.out.println("New connection: " + clientSocket.getInetAddress());

                // Handle client request in a new thread
                new Thread(new RequestHandler(clientSocket)).start();
            }
        } catch (IOException e) {
            System.err.println("Error starting the server: " + e.getMessage());
        }
    }
}
///    docker build -t myapp:latest .      ///
///    docker run -p 8081:8081 myapp:latest      ///