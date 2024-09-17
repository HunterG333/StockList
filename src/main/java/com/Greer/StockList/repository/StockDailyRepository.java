package com.Greer.StockList.repository;

import com.Greer.StockList.model.StockDailyEntity;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface StockDailyRepository extends CrudRepository<StockDailyEntity, Long> {

    //TODO: MAKE QUERY
    //public List<StockDailyEntity> findHistoryBySymbol(int max);

}
