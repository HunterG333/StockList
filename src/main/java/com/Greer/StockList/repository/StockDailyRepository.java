package com.Greer.StockList.repository;

import com.Greer.StockList.model.StockDailyEntity;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface StockDailyRepository extends CrudRepository<StockDailyEntity, Long> {

    @Query("SELECT s FROM StockDailyEntity s WHERE s.stock = :symbol ORDER BY s.date DESC")
    public List<StockDailyEntity> findTop5BySymbol(@Param("symbol") String symbol);


}
