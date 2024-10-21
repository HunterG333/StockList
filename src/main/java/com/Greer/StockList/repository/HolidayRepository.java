package com.Greer.StockList.repository;

import com.Greer.StockList.model.HolidaysEntity;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Repository
public interface HolidayRepository extends CrudRepository<HolidaysEntity, Long> {


    Optional<HolidaysEntity> findByHolidayDate(LocalDate holidayDate);

    List<HolidaysEntity> findAllByHolidayDate(LocalDate today);
}
