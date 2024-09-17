package com.Greer.StockList.controller;

import com.Greer.StockList.services.StockService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.List;

@RestController
@RequestMapping("/api")
public class MarketDataController {

    StockService stockService;

    public MarketDataController(StockService stockService){
        this.stockService = stockService;
    }

    @GetMapping("/marketdata")
    public List<Double> getMarketData(@RequestParam String stock, @RequestParam int size) {
        System.out.println("Returning " + stock);
        List<Double> historicalData = new ArrayList<>(stockService.getHistorical(stock, size));
        double lastUpdate = stockService.getLastUpdate(stock);
        historicalData.add(lastUpdate);
        return historicalData;
    }
}