package com.Greer.StockList.repository;

import com.Greer.StockList.model.StockEntity;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface StockRepository extends CrudRepository<StockEntity, Long> {

    public Optional<StockEntity> findBySymbol(String symbol);

}
