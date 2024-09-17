package com.Greer.StockList.services;

import com.Greer.StockList.controller.APIController;
import com.Greer.StockList.model.StockDailyEntity;
import com.Greer.StockList.model.StockEntity;
import com.Greer.StockList.repository.StockDailyRepository;
import com.Greer.StockList.repository.StockRepository;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.net.URISyntaxException;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class StockService {

    private StockRepository stockRepository;
    private StockDailyRepository stockDailyRepository;
    private APIController apiController;

    public StockService(StockRepository stockRepository, StockDailyRepository stockDailyRepository, APIController apiController){
        this.stockRepository = stockRepository;
        this.stockDailyRepository = stockDailyRepository;
        this.apiController = apiController;
    }

    public StockEntity save(StockEntity stockEntity){
        return stockRepository.save(stockEntity);
    }

    //TODO: OPTIMIZE SO QUERY DATABASE INSTEAD OF API
    public double getLastUpdate(String stock) throws URISyntaxException, IOException, InterruptedException {
        // Fetch the stock data from the API
        String stockData = apiController.getStockLive(stock);
        System.out.println(stockData);

        // Create an ObjectMapper to parse the JSON response
        ObjectMapper mapper = new ObjectMapper();

        // Parse the JSON response
        JsonNode rootNode = mapper.readTree(stockData);

        // Extract the 'c' property (current price)
        if (rootNode.has("c")) {
            return rootNode.get("c").asDouble();  // Return the value of 'c'
        }

        // If 'c' is not present, return a default value or throw an exception
        System.out.println("Error: 'c' property not found in the response.");
        return -10.0;
    }

    //TODO: OPTIMIZE TO QUERY DATABASE FOR LIST OF VALUES IN REQUESTED TIME FRAME
    public List<Double> getDailyHistory(String stock, int trailingDays) throws URISyntaxException, IOException, InterruptedException {
        // Fetch the history data from the API
        String historyData = apiController.getStockHistory(stock, trailingDays);

        // Create an ObjectMapper to parse the JSON response
        ObjectMapper mapper = new ObjectMapper();
        JsonNode rootNode = mapper.readTree(historyData);

        // Since the dates are now directly in the root node, no need to check for "Time Series (Daily)"
        if (rootNode.isEmpty()) {
            System.out.println("Error: No data found in the response.");
            return Collections.emptyList();  // Return an empty list if data is missing
        }

        // Use a TreeMap to store the data, which automatically sorts by key (date)
        Map<String, Double> sortedClosingPrices = new TreeMap<>();

        // Iterate over each date entry in the root node
        rootNode.fields().forEachRemaining(entry -> {
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

        return closingPrices;
    }


}
