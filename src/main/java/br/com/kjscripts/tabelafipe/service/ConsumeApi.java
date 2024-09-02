package br.com.kjscripts.tabelafipe.service;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

public class ConsumeApi {

    /**
     * Fetches data from the provided URL using an HTTP GET request.
     *
     * @param url The URL of the API endpoint to consume.
     * @return The response body as a String, containing the data from the API.
     * @throws RuntimeException  If an IOException or InterruptedException occurs during communication.
     */
    public String getData(String url) {

        // Create a new HttpClient instance for making HTTP requests
        HttpClient client = HttpClient.newHttpClient();

        // Build an HttpRequest object with the provided URL
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(url))  // Set the request URI (URL)
                .build();

        // Variable to hold the HttpResponse object
        HttpResponse<String> response = null;

        try {
            // Send the HTTP request and wait for the response
            response = client.send(request, HttpResponse.BodyHandlers.ofString());
        } catch (IOException e) {
            // Handle IOException if an error occurs during communication
            throw new RuntimeException(e);
        } catch (InterruptedException e) {
            // Handle InterruptedException if the request is interrupted
            throw new RuntimeException(e);
        }

        // Extract the response body containing the data as a String
        String json = response.body();

        // Return the retrieved data from the API
        return json;
    }
}