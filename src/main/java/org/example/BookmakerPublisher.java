package org.example;

import com.rabbitmq.client.ConnectionFactory;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.Channel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class BookmakerPublisher {
    private static final Logger logger = LoggerFactory.getLogger(BookmakerPublisher.class);
    private final static String EXCHANGE_NAME = "betting_exchange";

    public static void main(String[] argv) throws Exception {
        ConnectionFactory factory = new ConnectionFactory();
        factory.setHost("localhost");
        try (Connection connection = factory.newConnection();
             Channel channel = connection.createChannel()) {
            channel.exchangeDeclare(EXCHANGE_NAME, "direct", true);

            String[] messages = {"Bet 1: Team A vs Team B", "Bet 2: Team C vs Team D", "Bet 3: Team E vs Team F"};
            for (String message : messages) {
                channel.basicPublish(EXCHANGE_NAME, "betting_key", null, message.getBytes("UTF-8"));
                logger.info(" [x] Sent '{}'", message);
            }
        } catch (Exception e) {
            logger.error("Failed to send message", e);
        }
    }
}
