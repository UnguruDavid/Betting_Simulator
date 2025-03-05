package org.example;

import com.rabbitmq.client.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.json.JSONObject;

public class BettingAlgorithm {
    private static final Logger logger = LoggerFactory.getLogger(BettingAlgorithm.class);
    private static final double ODDS_THRESHOLD = 2.0;

    public static void main(String[] argv) throws Exception {
        String QUEUE_NAME = "betting_queue";
        ConnectionFactory factory = new ConnectionFactory();
        factory.setHost("localhost");

        try (Connection connection = factory.newConnection();
             Channel channel = connection.createChannel()) {
            channel.queueDeclare(QUEUE_NAME, true, false, false, null);
            logger.info(" [*] Waiting for messages. To exit press CTRL+C");

            DeliverCallback deliverCallback = (consumerTag, delivery) -> {
                String message = new String(delivery.getBody(), "UTF-8");
                logger.info("Received message: '{}'", message);
                processMessage(message);
            };

            channel.basicConsume(QUEUE_NAME, true, deliverCallback, consumerTag -> {
            });
        }
    }

    public static void processMessage(String message) {
        try {
            // Parse the message and extract the odds
            // Assuming message is in JSON format and contains an "odds" field
            JSONObject jsonMessage = new JSONObject(message);
            double odds = jsonMessage.getDouble("odds");

            if (odds > ODDS_THRESHOLD) {
                logger.info("Closing bet as odds {} are greater than threshold {}", odds, ODDS_THRESHOLD);
                // Implement bet closing logic here
            } else {
                logger.info("Odds {} are within the acceptable range", odds);
            }
        } catch (Exception e) {
            logger.error("Failed to process message", e);
        }
    }
}
