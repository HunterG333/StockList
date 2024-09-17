package com.Greer.StockList.services;

import com.Greer.StockList.model.StockEntity;
import com.Greer.StockList.repository.StockRepository;
import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

//TODO: Finish Implementing database quering for stock history and current
@Service
public class StockService {

    private StockRepository stockRepository;

    public StockService(StockRepository stockRepository){
        this.stockRepository = stockRepository;
    }

    //TODO: QUERY DATABASE FOR MOST RECENT VALUE
    public double getLastUpdate(String stock){
        //query database for stock
        Optional<StockEntity> returnedStock = stockRepository.findBySymbol(stock);


        return returnedStock.map(StockEntity::getValue).orElse(0.0);
    }

    //TODO: QUERY DATABASE FOR LIST OF VALUES IN REQUESTED TIME FRAME
    public List<Double> getHistorical(String stock, int trailingDays){
        return Arrays.asList(10.0, 20.0, 30.0, 40.0);
    }

}
