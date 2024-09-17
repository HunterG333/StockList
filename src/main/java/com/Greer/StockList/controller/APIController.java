package com.Greer.StockList.controller;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Controller;

import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Controller
public class APIController {

    private static final String FINNHUB_API_KEY = System.getenv("FINNHUB_API_KEY");
    private static final String APLHA_VANTAGE_API_KEY = System.getenv("ALPHA_VANTAGE_API_KEY");

    //TODO: Implement websocket
    private static final String WS_URL = "wss://ws.finnhub.io?token=" + FINNHUB_API_KEY;

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

    public String getStockHistory(String symbol, int trailingDays) throws URISyntaxException, IOException, InterruptedException {
        HttpClient client = HttpClient.newHttpClient();

        HttpRequest getRequest = HttpRequest.newBuilder()
                .uri(new URI("https://www.alphavantage.co/query?function=TIME_SERIES_DAILY&symbol=" + symbol + "&apikey=" + APLHA_VANTAGE_API_KEY))
                .GET()
                .build();
        HttpResponse<String> response = client.send(getRequest, HttpResponse.BodyHandlers.ofString());

        // Load the mock file from src/main/resources
        //ClassPathResource mockFile = new ClassPathResource("mockData.txt");

        // Read the content of the file as a String
        //InputStream inputStream = mockFile.getInputStream();
        //String fileContent = new String(inputStream.readAllBytes(), StandardCharsets.UTF_8);

        // Parse the response
        ObjectMapper mapper = new ObjectMapper();
        JsonNode rootNode = mapper.readTree(response.body());
        //JsonNode rootNode = mapper.readTree(fileContent);

        // Extract the time series data
        JsonNode timeSeriesNode = rootNode.path("Time Series (Daily)");

        // Convert JsonNode to Map
        Map<String, JsonNode> timeSeriesMap = mapper.convertValue(timeSeriesNode, Map.class);

        // Limit the number of entries
        List<Map.Entry<String, JsonNode>> limitedEntries = timeSeriesMap.entrySet().stream()
                .limit(trailingDays)
                .collect(Collectors.toList());

        // Convert the limited entries back to JSON
        ObjectMapper resultMapper = new ObjectMapper();
        JsonNode resultNode = resultMapper.valueToTree(new LinkedHashMap<>(limitedEntries.stream()
                .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue))));

        return resultMapper.writeValueAsString(resultNode);
    }
}
