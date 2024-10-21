package com.Greer.StockList.repository;

import com.Greer.StockList.model.StockDailyEntity;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface StockDailyRepository extends CrudRepository<StockDailyEntity, Long> {

    //TODO: FIND WAY TO QUERY THE LAST DAYS STORED

    //TODO: NEED TO VERIFY THAT THE LAST DAY RETRIEVED IS THE LAST DAY THE MARKET WAS OPEN.
    // EX TODAY IS MONDAY THE 21st THE LAST OPEN DAY WAS FRIDAY THE 18th. HOW DO I VERIFY THAT??
    // MAYBE WORK OUT STORING HOLIDAYS IN A TABLE THAT I CAN CHECK TO VERIFY THAT THIS DATA IS CURRENT

    //TODO: MAKE QUERY
    //public List<Double> findHistoryBySymbol(int max);

}
