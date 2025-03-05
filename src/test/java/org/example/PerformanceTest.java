package org.example;

import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;

import org.junit.jupiter.api.Test;

public class PerformanceTest {

    @Test
    public void testServerResponseTime() throws Exception {
        // Start the server in a new thread
        new Thread(() -> {
            try {
                Server.main(new String[0]);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }).start();

        // Allow the server some time to start
        Thread.sleep(1000);

        // Measure the time taken to handle a GET request
        long startTime = System.nanoTime();
        HttpURLConnection connection = (HttpURLConnection) new URL("http://localhost:8081/").openConnection();
        connection.setRequestMethod("GET");

        int responseCode = connection.getResponseCode();
        long endTime = System.nanoTime();

        // Ensure the response code is 200 OK
        assertTrue(responseCode == 200);

        // Calculate the duration in milliseconds
        long duration = (endTime - startTime) / 1_000_000;

        // Output the duration for debugging purposes
        System.out.println("Server response time: " + duration + " ms");

        // Assert that the response time is within acceptable limits (e.g., less than 500ms)
        assertTrue(duration < 500);
    }
}
