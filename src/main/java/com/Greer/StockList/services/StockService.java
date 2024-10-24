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
import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class StockService {

    private final StockRepository stockRepository;
    private final StockDailyRepository stockDailyRepository;
    private final APIController apiController;
    private final HolidaysService holidaysService;

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

    //TODO: Probably easier to check when the last time history was updated for a current stock instead of cross checking dates
    public List<Double> getDailyHistory(String stock, int trailingDays) throws URISyntaxException, IOException, InterruptedException {

        //TODO: CHANGE FROM TOP5 TO TOPX WHEN WE ADD SUPPORT FOR LONGER CHARTS
        List<StockDailyEntity> returnedHistory = stockDailyRepository.findTop5BySymbol(stock);

        if(returnedHistory.isEmpty()){ //save history to database from API call and return data

            // Fetch the history data from the API
            List<StockDailyEntity> stockHistoryEntities = apiController.getStockHistory(stock, trailingDays);

            stockHistoryEntities.forEach(stockDailyRepository::save);

            List<Double> closingPrices = stockHistoryEntities.stream()
                    .map(StockDailyEntity::getValue)
                    .collect(Collectors.toList());

            return closingPrices;
        }
        //returnedHistory is not empty. Check if it is updated

        List<LocalDate> tradingDays = getLastValidTradingDays(trailingDays);


        List<LocalDate> existingTradingDays = new ArrayList<>(); //days that exist in the history
        List<LocalDate> nonExistingTradingDays = new ArrayList<>(); //days that need to be added to the history

        // Check if each trading day exists in returnedHistory
        for (LocalDate tradingDay : tradingDays) {
            boolean exists = false; // Flag to check existence

            for (StockDailyEntity entity : returnedHistory) {
                if (entity.getDate().isEqual(tradingDay)) {
                    existingTradingDays.add(tradingDay);
                    exists = true; // Mark as found
                    break; // Stop checking further once found
                }
            }

            // If it doesn't exist in the history, add to the non-existing list
            if (!exists) {
                nonExistingTradingDays.add(tradingDay);
            }
        }

        if(nonExistingTradingDays.isEmpty()){
            //data is valid, return it

            return returnedHistory.stream()
                    .map(StockDailyEntity::getValue)
                    .collect(Collectors.toList())
                    .reversed();
        }

        //call the api and save the amount of non existing trading days to the history
        List<StockDailyEntity> stockHistoryEntities = apiController.getStockHistory(stock, nonExistingTradingDays.size());
        stockHistoryEntities.forEach(stockDailyRepository::save);

        stockHistoryEntities.addAll(returnedHistory);

        return stockHistoryEntities.stream()
                .map(StockDailyEntity::getValue)
                .collect(Collectors.toList())
                .reversed();
    }

    public List<LocalDate> getLastValidTradingDays(int numDays) {
        List<LocalDate> validTradingDays = new ArrayList<>();
        LocalDate dateToCheck = LocalDate.now();
        int daysChecked = 0;

        // Loop until we find 5 valid trading days
        while (validTradingDays.size() < numDays) {
            // Check if it's a valid trading day
            if (holidaysService.isMarketOpenDate(dateToCheck)) {
                // Special condition for today: the market must be closed for the day
                if (!dateToCheck.equals(LocalDate.now()) || !holidaysService.isMarketOpenTime(LocalDateTime.now())) {
                    validTradingDays.add(dateToCheck);
                }
            }

            // Move to the previous day and increment the counter
            dateToCheck = dateToCheck.minusDays(1);
            daysChecked++;

            // Optional: Add a safety limit for how far back to check
            if (daysChecked > 30) {
                throw new RuntimeException("Unable to find 5 valid trading days within the last 30 days.");
            }
        }

        return validTradingDays;
    }

}
