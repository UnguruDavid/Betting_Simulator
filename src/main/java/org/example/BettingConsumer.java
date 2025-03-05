package org.example;

import com.rabbitmq.client.ConnectionFactory;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.Channel;
import com.rabbitmq.client.DeliverCallback;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class BettingConsumer {
    private static final Logger logger = LoggerFactory.getLogger(BettingConsumer.class);
    private final static String QUEUE_NAME = "betting_queue";

    public static void main(String[] argv) throws Exception {
        ConnectionFactory factory = new ConnectionFactory();
        factory.setHost("localhost");
        Connection connection = null;
        Channel channel = null;

        try {
            connection = factory.newConnection();
            channel = connection.createChannel();
            channel.queueDeclare(QUEUE_NAME, true, false, false, null);
            logger.info(" [*] Waiting for messages. To exit press CTRL+C");

            DeliverCallback deliverCallback = (consumerTag, delivery) -> {
                String message = new String(delivery.getBody(), "UTF-8");
                logger.info(" [x] Received '{}'", message);
            };

            channel.basicConsume(QUEUE_NAME, true, deliverCallback, consumerTag -> { });

            // Keep the program running to listen for messages
            while (true) {
                Thread.sleep(1000);
            }

        } catch (Exception e) {
            logger.error("Failed to consume message", e);
        } finally {
            if (channel != null && channel.isOpen()) {
                try {
                    channel.close();
                } catch (Exception e) {
                    logger.error("Failed to close channel", e);
                }
            }
            if (connection != null && connection.isOpen()) {
                try {
                    connection.close();
                } catch (Exception e) {
                    logger.error("Failed to close connection", e);
                }
            }
        }
    }
}
