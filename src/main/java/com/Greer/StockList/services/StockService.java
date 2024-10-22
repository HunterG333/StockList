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
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

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

    public double getLastUpdate(String stock) throws URISyntaxException, IOException, InterruptedException {

        Optional<StockEntity> stockEntityOptional = stockRepository.findBySymbol(stock);
        boolean stockEntityExists = stockEntityOptional.isPresent();
        if(stockEntityExists){
            StockEntity stockEntity = stockEntityOptional.get();

            LocalDateTime lastUpdated = stockEntity.getLastUpdated();
            LocalDateTime now = LocalDateTime.now();

            Duration duration = Duration.between(lastUpdated, now);

            if(duration.toMinutes() <= 10){
                return stockEntity.getValue();
            }
        }

        //Fetch the stock data from the API
        String stockData = apiController.getStockLive(stock);

        // Create an ObjectMapper to parse the JSON response
        ObjectMapper mapper = new ObjectMapper();

        // Parse the JSON response
        JsonNode rootNode = mapper.readTree(stockData);

        // Extract the 'c' property (current price)
        if (rootNode.has("c")) {
            double stockValue = rootNode.get("c").asDouble();

            if(stockEntityExists){
                StockEntity stockEntity = stockEntityOptional.get();
                stockEntity.setValue(stockValue);
                stockEntity.setLastUpdated(LocalDateTime.now());

                stockRepository.save(stockEntity);
            }else{
                StockEntity newStockEntity = new StockEntity();
                newStockEntity.setSymbol(stock);
                newStockEntity.setValue(stockValue);
                newStockEntity.setLastUpdated(LocalDateTime.now());

                // Save the new entity to the database
                stockRepository.save(newStockEntity);
            }


            return stockValue; // Return the value of 'c'
        }

        // If 'c' is not present, return a default value or throw an exception
        System.out.println("Error: 'c' property not found in the response.");
        return -10.0;
    }

    //TODO: OPTIMIZE TO QUERY DATABASE FOR LIST OF VALUES IN REQUESTED TIME FRAME
    public List<Double> getDailyHistory(String stock, int trailingDays) throws URISyntaxException, IOException, InterruptedException {

        holidaysService.isMarketOpen(LocalDateTime.now());

        // Fetch the history data from the API
        List<Double> closingPrices = apiController.getStockHistory(stock, trailingDays);
        return closingPrices;
    }

}
