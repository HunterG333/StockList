package com.Greer.StockList.services;

import com.Greer.StockList.controller.APIController;
import com.Greer.StockList.model.HolidaysEntity;
import com.Greer.StockList.repository.HolidayRepository;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.net.URISyntaxException;
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;
import java.util.Optional;

@Service
public class HolidaysService {

    private final APIController apiController;
    private final HolidayRepository holidayRepository;

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

    /**
     * Checks if the market is open on a specific date
     * @param dateToCheck The date of the day to check
     * @return A boolean if the market is open on that day
     */
    public boolean isMarketOpenDate(LocalDate dateToCheck){
        DayOfWeek dayOfWeek = dateToCheck.getDayOfWeek();
        if(isWeekend(dateToCheck)){
            return false;
        }

        Optional<HolidaysEntity> holiday = holidayRepository.findAllByHolidayDate(LocalDate.from(dateToCheck));
        if(holiday.isPresent() && holiday.get().getTradingHour().isEmpty()){
            return false;
        }
        return true;
    }

    /**
     * Checks if the market is open at the specific date & time given.
     * @param dateToCheck The time and date of the day to check
     * @return A boolean if the market is open at the time specified
     */
    public boolean isMarketOpenTime(LocalDateTime dateToCheck){

        // Check if today is a weekend (Saturday or Sunday)
        if(isWeekend(dateToCheck)){
            return false;
        }

        //Check if today is a holiday by querying the database
        Optional<HolidaysEntity> holiday = holidayRepository.findAllByHolidayDate(LocalDate.from(dateToCheck));
        if (holiday.isPresent()) {
            //check if time now is in trading hours
            String tradingHours = holiday.get().getTradingHour();
            if(tradingHours.isEmpty()){
                return false; //market is closed because it is a holiday and there are no trading hours
            }

            //need to parse trading hours to a beginning time and end time
            String[] hours = tradingHours.split("-");
            if (hours.length != 2) {
                throw new IllegalArgumentException("Invalid trading hours format: " + tradingHours);
            }

            // Parse the start and end times
            LocalTime startTime = LocalTime.parse(hours[0]);
            LocalTime endTime = LocalTime.parse(hours[1]);

            // Extract the time from dateToCheck
            LocalTime currentTime = dateToCheck.toLocalTime();

            // Check if the current time is within the trading hours
            return !currentTime.isBefore(startTime) && !currentTime.isAfter(endTime); // Market is open (inclusive of start and end times)
        }

        // Market is open if it's not a weekend or a holiday
        return true;
    }

    public boolean isWeekend(LocalDate dateToCheck){
        DayOfWeek dayOfWeek = dateToCheck.getDayOfWeek();
        return dayOfWeek == DayOfWeek.SATURDAY || dayOfWeek == DayOfWeek.SUNDAY;  // Market is closed on weekends
    }

    public boolean isWeekend(LocalDateTime dateToCheck){
        DayOfWeek dayOfWeek = dateToCheck.getDayOfWeek();
        return dayOfWeek == DayOfWeek.SATURDAY || dayOfWeek == DayOfWeek.SUNDAY;  // Market is closed on weekends
    }
}
