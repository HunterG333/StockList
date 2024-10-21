package com.Greer.StockList.services;

import com.Greer.StockList.controller.APIController;
import com.Greer.StockList.model.StockEntity;
import com.Greer.StockList.repository.StockDailyRepository;
import com.Greer.StockList.repository.StockRepository;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.net.URISyntaxException;
import java.time.LocalDate;
import java.util.List;

@Service
public class StockService {

    private StockRepository stockRepository;
    private StockDailyRepository stockDailyRepository;
    private APIController apiController;
    private HolidaysService holidaysService;

    public StockService(StockRepository stockRepository, StockDailyRepository stockDailyRepository, APIController apiController, HolidaysService holidaysService){
        this.stockRepository = stockRepository;
        this.stockDailyRepository = stockDailyRepository;
        this.apiController = apiController;
        this.holidaysService = holidaysService;
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
        List<Double> closingPrices = apiController.getStockHistory(stock, trailingDays);
        return closingPrices;
    }

    public Boolean isMarketOpen() {
        return holidaysService.isMarketOpen(LocalDate.now());
    }

}
