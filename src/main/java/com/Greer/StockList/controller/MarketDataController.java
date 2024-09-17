package com.Greer.StockList.controller;

import com.Greer.StockList.services.StockService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.List;

@RestController
@RequestMapping("/api")
public class MarketDataController {

    StockService stockService;

    public MarketDataController(StockService stockService){
        this.stockService = stockService;
    }

    //TODO: CURRENTLY HARDCODED FOR DAYS, MODIFY TO COMPENSATE FOR LONGER TIME PERIODS
    @GetMapping("/marketdata")
    public List<Double> getMarketData(@RequestParam String stock, @RequestParam int days) throws URISyntaxException, IOException, InterruptedException {
        List<Double> historicalData = new ArrayList<>(stockService.getDailyHistory(stock, days-1));
        double lastUpdate = stockService.getLastUpdate(stock);
        historicalData.add(lastUpdate);
        return historicalData;
    }
}