package com.Greer.StockList.Services;

import org.springframework.stereotype.Service;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

@Service
public class APIService {

    private static final String API_KEY = System.getenv("FINNHUB_API_KEY");

    //TODO: Implement websocket
    private static final String WS_URL = "wss://ws.finnhub.io?token=" + API_KEY;

    /**
     * Returns the current information about a stock
     * @param symbol The stock symbol being checked
     * @return a JSON Body including the current data about a stock
     * @throws URISyntaxException
     * @throws IOException
     * @throws InterruptedException
     */
    public String getStock(String symbol) throws URISyntaxException, IOException, InterruptedException {
        HttpClient client = HttpClient.newHttpClient();

        HttpRequest getRequest = HttpRequest.newBuilder()
                .uri(new URI("https://finnhub.io/api/v1/quote?symbol=" + symbol + "&token=" + API_KEY))
                .GET()
                .build();
        HttpResponse<String> response = client.send(getRequest, HttpResponse.BodyHandlers.ofString());

        return response.body();
    }
}
