package org.example;

import org.apache.http.HttpEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;
import java.io.IOException;

public class SportsNewsFetcher {

    private static final String API_KEY = "f4bd191a56d64031966572c3e054980c";  // Replace with your actual API key
    private static final String API_URL = "https://native-stats.org/competition/CL/";

    public static String fetchNews() throws IOException {
        CloseableHttpClient httpClient = HttpClients.createDefault();
        HttpGet request = new HttpGet(API_URL);
        request.addHeader("X-Auth-Token", API_KEY);

        try (CloseableHttpResponse response = httpClient.execute(request)) {
            if (response.getStatusLine().getStatusCode() == 200) {
                HttpEntity entity = response.getEntity();
                return EntityUtils.toString(entity);
            } else {
                throw new IOException("Failed to fetch news: HTTP error code " + response.getStatusLine().getStatusCode());
            }
        }
    }

    public static void main(String[] args) {
        try {
            String news = fetchNews();
            System.out.println("Fetched News: " + news);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
