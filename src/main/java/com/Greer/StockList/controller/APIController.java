package com.Greer.StockList.controller;

import com.Greer.StockList.model.HolidaysEntity;
import com.Greer.StockList.model.StockDailyEntity;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.stereotype.Controller;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.LocalDate;
import java.util.*;
import java.util.stream.Collectors;

@Controller
public class APIController {

    private static final String FINNHUB_API_KEY = System.getenv("FINNHUB_API_KEY");
    private static final String APLHA_VANTAGE_API_KEY = System.getenv("ALPHA_VANTAGE_API_KEY");

    /**
     * Returns the current information about a stock
     * @param symbol The stock symbol being checked
     * @return a JSON Body including the current data about a stock
     * @throws URISyntaxException
     * @throws IOException
     * @throws InterruptedException
     */
    public String getStockLive(String symbol) throws URISyntaxException, IOException, InterruptedException {
        System.out.println("API CALL: Called getStockLive()");

        HttpClient client = HttpClient.newHttpClient();

        HttpRequest getRequest = HttpRequest.newBuilder()
                .uri(new URI("https://finnhub.io/api/v1/quote?symbol=" + symbol + "&token=" + FINNHUB_API_KEY))
                .GET()
                .build();
        HttpResponse<String> response = client.send(getRequest, HttpResponse.BodyHandlers.ofString());

        return response.body();
    }

    public List<StockDailyEntity> getStockHistory(String symbol, int trailingDays) throws URISyntaxException, IOException, InterruptedException {
        System.out.println("API CALL: Called getStockHistory()");

        HttpClient client = HttpClient.newHttpClient();

        HttpRequest getRequest = HttpRequest.newBuilder()
                .uri(new URI("https://www.alphavantage.co/query?function=TIME_SERIES_DAILY&symbol=" + symbol + "&apikey=" + APLHA_VANTAGE_API_KEY))
                .GET()
                .build();
        HttpResponse<String> response = client.send(getRequest, HttpResponse.BodyHandlers.ofString());


        return parseAlphaVantageResponse(response, trailingDays, symbol);
    }


    public List<StockDailyEntity> parseAlphaVantageResponse(HttpResponse<String> response, int trailingDays, String symbol) throws JsonProcessingException {
        // Parse the response
        ObjectMapper mapper = new ObjectMapper();
        JsonNode rootNode = mapper.readTree(response.body());

        // Extract the time series data
        JsonNode timeSeriesNode = rootNode.path("Time Series (Daily)");

        // Use a TreeMap to store the data, which automatically sorts by key (date)
        Map<String, JsonNode> sortedDailyData = new TreeMap<>(Collections.reverseOrder());

        // Iterate over each date entry in the timeSeries node
        timeSeriesNode.fields().forEachRemaining(entry -> {
            String date = entry.getKey();
            JsonNode dailyData = entry.getValue();

            // Add to the map only if "4. close" is present
            if (dailyData.has("4. close")) {
                sortedDailyData.put(date, dailyData);
            }
        });

        // Create a list of StockDailyEntity
        List<StockDailyEntity> stockDailyEntities = sortedDailyData.entrySet().stream()
                .limit(trailingDays) // Limit to the number of trailingDays
                .map(entry -> {
                    String date = entry.getKey();
                    JsonNode dailyData = entry.getValue();
                    double closingPrice = dailyData.get("4. close").asDouble();

                    // Create a new StockDailyEntity for each entry
                    StockDailyEntity entity = new StockDailyEntity();
                    entity.setDate(LocalDate.parse(date));
                    entity.setValue(closingPrice);
                    entity.setStock(symbol);

                    return entity;
                })
                .collect(Collectors.toList());

        // Reverse the list so that the oldest entries come first
        Collections.reverse(stockDailyEntities);

        return stockDailyEntities;
    }


    public List<HolidaysEntity> getHolidays() throws IOException, InterruptedException, URISyntaxException {
        System.out.println("API CALL: Called getStockHistory()");

        HttpClient client = HttpClient.newHttpClient();

        HttpRequest getRequest = HttpRequest.newBuilder()
                .uri(new URI("https://finnhub.io/api/v1/stock/market-holiday?exchange=US" + "&token=" + FINNHUB_API_KEY))
                .GET()
                .build();
        HttpResponse<String> response = client.send(getRequest, HttpResponse.BodyHandlers.ofString());

        return parseHolidays(response.body());
    }

    /**
     * Parses the api response from hinnhub into a list of holidays entity object
     * @param apiResponse The api response from finnhub regarding holidays in the US exchange
     * @return a list of holiday entities
     */
    public List<HolidaysEntity> parseHolidays(String apiResponse){
        List<HolidaysEntity> holidaysList = new ArrayList<>();

        try {
            ObjectMapper objectMapper = new ObjectMapper();
            JsonNode rootNode = objectMapper.readTree(apiResponse);
            JsonNode dataNode = rootNode.get("data");

            if (dataNode != null && dataNode.isArray()) {
                for (JsonNode eventNode : dataNode) {
                    String holiday = eventNode.get("eventName").asText();
                    String atDate = eventNode.get("atDate").asText();
                    String tradingHour = eventNode.get("tradingHour").asText();

                    // Convert atDate string to LocalDate
                    LocalDate holidayDate = LocalDate.parse(atDate);

                    // Create HolidaysEntity object using the builder pattern
                    HolidaysEntity holidayEntity = HolidaysEntity.builder()
                            .holiday(holiday)
                            .holidayDate(holidayDate)
                            .tradingHour(tradingHour)
                            .build();

                    // Add to the list
                    holidaysList.add(holidayEntity);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return holidaysList;
    }
}
