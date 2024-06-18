package org.example;

import com.rabbitmq.client.ConnectionFactory;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.Channel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.everit.json.schema.Schema;
import org.everit.json.schema.loader.SchemaLoader;
import org.json.JSONObject;
import org.json.JSONTokener;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.time.Instant;

public class NewsPublisher {
    private static final Logger logger = LoggerFactory.getLogger(NewsPublisher.class);
    private final static String EXCHANGE_NAME = "news_exchange";

    public static void main(String[] argv) throws Exception {
        String news = fetchNews();

        if (news != null) {
            logger.info("Fetched News: {}", news);
            String transformedNews = transformNews(news);
            if (validateNews(transformedNews)) {
                publishNews(transformedNews);
            } else {
                logger.error("News validation failed");
            }
        }
    }

    private static String fetchNews() {
        String newsUrl = "https://www.thesportsdb.com/api/v1/json/3/searchteams.php?t=Arsenal"; // Replace with your actual endpoint and API key

        try {
            URL url = new URL(newsUrl);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("GET");

            int responseCode = conn.getResponseCode();
            if (responseCode == 200) {
                BufferedReader in = new BufferedReader(new InputStreamReader(conn.getInputStream()));
                String inputLine;
                StringBuilder content = new StringBuilder();

                while ((inputLine = in.readLine()) != null) {
                    content.append(inputLine);
                }

                in.close();
                conn.disconnect();
                return content.toString();
            } else {
                logger.error("Failed to fetch news: HTTP error code {}", responseCode);
                return null;
            }
        } catch (Exception e) {
            logger.error("Error occurred while fetching news", e);
            return null;
        }
    }

    private static String transformNews(String news) {
        JSONObject jsonNews = new JSONObject(news);
        JSONObject team = jsonNews.getJSONArray("teams").getJSONObject(0);

        JSONObject transformedNews = new JSONObject();
        transformedNews.put("eventType", "team_info"); // Define the event type as appropriate
        transformedNews.put("description", team.getString("strDescriptionEN"));
        transformedNews.put("timestamp", Instant.now().toString()); // Convert timestamp to string

        return transformedNews.toString();
    }

    private static boolean validateNews(String news) {
        try {
            InputStream schemaStream = NewsPublisher.class.getClassLoader().getResourceAsStream("news-schema.json");
            if (schemaStream == null) {
                logger.error("Schema file not found");
                return false;
            }
            JSONObject jsonSchema = new JSONObject(new JSONTokener(schemaStream));
            Schema schema = SchemaLoader.load(jsonSchema);

            JSONObject jsonNews = new JSONObject(news);
            schema.validate(jsonNews);

            return true;
        } catch (org.everit.json.schema.ValidationException e) {
            logger.error("News validation failed: " + e.getMessage(), e);
            e.getCausingExceptions().stream()
                    .map(org.everit.json.schema.ValidationException::getMessage)
                    .forEach(logger::error);
            return false;
        } catch (Exception e) {
            logger.error("Error during news validation", e);
            return false;
        }
    }

    private static void publishNews(String news) {
        ConnectionFactory factory = new ConnectionFactory();
        factory.setHost("localhost");

        try (Connection connection = factory.newConnection();
             Channel channel = connection.createChannel()) {
            channel.exchangeDeclare(EXCHANGE_NAME, "fanout", true);

            channel.basicPublish(EXCHANGE_NAME, "", null, news.getBytes(StandardCharsets.UTF_8));
            logger.info(" [x] Sent '{}'", news);
        } catch (Exception e) {
            logger.error("Failed to publish news", e);
        }
    }
}
