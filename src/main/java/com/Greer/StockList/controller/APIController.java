package com.Greer.StockList.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.stereotype.Controller;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.*;
import java.util.stream.Collectors;

@Controller
public class APIController {

    private static final String FINNHUB_API_KEY = System.getenv("FINNHUB_API_KEY");
    private static final String APLHA_VANTAGE_API_KEY = System.getenv("ALPHA_VANTAGE_API_KEY");

    /**
     * Returns the current information about a stock
     * @param symbol The stock symbol being checked
     * @return a JSON Body including the current data about a stock
     * @throws URISyntaxException
     * @throws IOException
     * @throws InterruptedException
     */
    public String getStockLive(String symbol) throws URISyntaxException, IOException, InterruptedException {
        HttpClient client = HttpClient.newHttpClient();

        HttpRequest getRequest = HttpRequest.newBuilder()
                .uri(new URI("https://finnhub.io/api/v1/quote?symbol=" + symbol + "&token=" + FINNHUB_API_KEY))
                .GET()
                .build();
        HttpResponse<String> response = client.send(getRequest, HttpResponse.BodyHandlers.ofString());

        return response.body();
    }

    //TODO: SAVE DATA TO DATABASE TO REDUCE API CALLS
    public List<Double> getStockHistory(String symbol, int trailingDays) throws URISyntaxException, IOException, InterruptedException {
        HttpClient client = HttpClient.newHttpClient();

        HttpRequest getRequest = HttpRequest.newBuilder()
                .uri(new URI("https://www.alphavantage.co/query?function=TIME_SERIES_DAILY&symbol=" + symbol + "&apikey=" + APLHA_VANTAGE_API_KEY))
                .GET()
                .build();
        HttpResponse<String> response = client.send(getRequest, HttpResponse.BodyHandlers.ofString());

        return parseAlphaVantageResponse(response, trailingDays);
    }

    public boolean isMarketOpen() throws URISyntaxException, IOException, InterruptedException {
        HttpClient client = HttpClient.newHttpClient();

        HttpRequest getRequest = HttpRequest.newBuilder()
                .uri(new URI("https://finnhub.io/api/v1/stock/market-status?exchange=US&token=" + FINNHUB_API_KEY))
                .GET()
                .build();
        HttpResponse<String> response = client.send(getRequest, HttpResponse.BodyHandlers.ofString());

        return parseAndReturnMarketStatus(response);
    }

    private boolean parseAndReturnMarketStatus(HttpResponse<String> response) throws JsonProcessingException {
        // Parse the response
        ObjectMapper mapper = new ObjectMapper();
        JsonNode rootNode = mapper.readTree(response.body());

        return rootNode.path("isOpen").asBoolean();
    }


    public List<Double> parseAlphaVantageResponse(HttpResponse<String> response, int trailingDays) throws JsonProcessingException {
        // Parse the response
        ObjectMapper mapper = new ObjectMapper();
        JsonNode rootNode = mapper.readTree(response.body());

        // Extract the time series data
        JsonNode timeSeriesNode = rootNode.path("Time Series (Daily)");

        // Use a TreeMap to store the data, which automatically sorts by key (date)
        Map<String, Double> sortedClosingPrices = new TreeMap<>(Collections.reverseOrder());

        // Iterate over each date entry in the timeSeries node
        timeSeriesNode.fields().forEachRemaining(entry -> {
            String date = entry.getKey();
            JsonNode dailyData = entry.getValue();

            // Extract the "4. close" value and put it in the sorted map
            if (dailyData.has("4. close")) {
                double closingPrice = dailyData.get("4. close").asDouble();
                sortedClosingPrices.put(date, closingPrice);
            }
        });

        // Convert the sorted closing prices to a list

        List<Double> closingPrices = sortedClosingPrices.values().stream()
                .limit(trailingDays)  // Limit to the number of trailingDays
                .collect(Collectors.toList());

        Collections.reverse(closingPrices);

        return closingPrices;
    }
}
