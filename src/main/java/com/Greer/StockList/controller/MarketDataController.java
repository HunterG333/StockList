package com.Greer.StockList.controller;

import com.Greer.StockList.services.HolidaysService;
import com.Greer.StockList.services.StockService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;
import java.net.URISyntaxException;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@RestController
@RequestMapping("/api")
public class MarketDataController {

    HolidaysService holidaysService;
    StockService stockService;

    public MarketDataController(HolidaysService holidaysService, StockService stockService){
        this.holidaysService = holidaysService;
        this.stockService = stockService;
    }

    /**
     * Function that retrieves the market data for a particular stock
     * @param stock The stock that you want data for
     * @param days The number of days to get data for
     * @return A list of Doubles representing the price over X amount of days
     * @throws URISyntaxException
     * @throws IOException
     * @throws InterruptedException
     */
    @GetMapping("/marketdata")
    public List<Double> getMarketData(@RequestParam String stock, @RequestParam int days) throws URISyntaxException, IOException, InterruptedException {
        boolean isMarketOpen = holidaysService.isMarketOpen(LocalDateTime.now());

        List<Double> historicalData;

        if(isMarketOpen){
            historicalData = new ArrayList<>(stockService.getDailyHistory(stock, days-1));
            double lastUpdate = stockService.getLastUpdate(stock);
            historicalData.add(lastUpdate);
        }else{
            historicalData = new ArrayList<>(stockService.getDailyHistory(stock, days));
        }
        return historicalData;
    }

    /**
     * Function to update the holidays in the database
     * As of now there is nothing stored
     */
    @GetMapping("/holidays")
    public String updateHolidays() throws IOException, URISyntaxException, InterruptedException {
        holidaysService.updateHolidays();
        return "Congrats you found a secret page!";
    }
}