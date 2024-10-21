package com.Greer.StockList.services;

import com.Greer.StockList.controller.APIController;
import com.Greer.StockList.model.HolidaysEntity;
import com.Greer.StockList.repository.HolidayRepository;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.net.URISyntaxException;
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.util.List;

@Service
public class HolidaysService {

    private APIController apiController;
    private HolidayRepository holidayRepository;

    public HolidaysService(APIController apiController, HolidayRepository holidayRepository){
        this.apiController = apiController;
        this.holidayRepository = holidayRepository;
    }

    public HolidaysEntity save(HolidaysEntity holidaysEntity){
        return holidayRepository.save(holidaysEntity);
    }

    /**
     * Method that updated the holidays stored in the database
     * @throws IOException
     * @throws URISyntaxException
     * @throws InterruptedException
     */
    public void updateHolidays() throws IOException, URISyntaxException, InterruptedException {

        List<HolidaysEntity> apiResponse = apiController.getHolidays();

        // Iterate over holidays from the API and check if they exist in the database
        for (HolidaysEntity holiday : apiResponse) {
            // Check if the holiday already exists in the database
            if (holidayRepository.findByHolidayDate(holiday.getHolidayDate()).isEmpty()) {
                // If not found, save it to the database
                System.out.println("Saved: " + holiday);
                holidayRepository.save(holiday);
            }
        }
    }

    //TODO: Logic for determining if the market is open or not
    public boolean isMarketOpen(LocalDate dateToCheck){

        // Check if today is a weekend (Saturday or Sunday)
        DayOfWeek dayOfWeek = dateToCheck.getDayOfWeek();
        if (dayOfWeek == DayOfWeek.SATURDAY || dayOfWeek == DayOfWeek.SUNDAY) {
            return false;  // Market is closed on weekends
        }

        //TODO: EDGE CASE if the market is partially open. Enable partially open markets to be shown
        //Check if today is a holiday by querying the database
        List<HolidaysEntity> holidays = holidayRepository.findAllByHolidayDate(dateToCheck);
        if (!holidays.isEmpty()) {
            return false;  // Market is closed on holidays
        }

        // Market is open if it's not a weekend or a holiday
        return true;
    }
}
