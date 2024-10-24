package com.Greer.StockList.repository;

import com.Greer.StockList.model.HolidaysEntity;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.Optional;

@Repository
public interface HolidayRepository extends CrudRepository<HolidaysEntity, Long> {


    Optional<HolidaysEntity> findByHolidayDate(LocalDate holidayDate);

    Optional<HolidaysEntity> findAllByHolidayDate(LocalDate today);
}
