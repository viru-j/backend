package com.example.assistant;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

/**
 * Simple client for the Spirare API model.
 */
public class SpirareClient {
    private final HttpClient httpClient;
    private final String apiEndpoint;

    public SpirareClient(String apiEndpoint) {
        this.httpClient = HttpClient.newHttpClient();
        this.apiEndpoint = apiEndpoint;
    }

    /**
     * Sends a prompt to the Spirare model and returns the response string.
     */
    public String sendPrompt(String prompt) throws IOException, InterruptedException {
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(apiEndpoint))
                .header("Content-Type", "application/json")
                .POST(HttpRequest.BodyPublishers.ofString("{\"prompt\":\"" + escape(prompt) + "\"}"))
                .build();
        HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
        return response.body();
    }

    private static String escape(String input) {
        return input.replace("\"", "\\\"").replace("\n", "\\n");
    }
}
