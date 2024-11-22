
package api;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.math.BigDecimal;
import java.net.HttpURLConnection;
import java.net.URL;
import java.time.LocalDate;
import java.util.HashMap;
import java.util.Map;

public class AlphaVantageApi implements StockApi {
    private static final String API_KEY = "0A3JS9MOCB0VYNK0";
    private static final String BASE_URL = "https://www.alphavantage.co/query?";

    @Override
    public BigDecimal getCurrentStockPrice(String ticker) {
        try {
            String apiUrl = String.format(
                    "%sfunction=GLOBAL_QUOTE&symbol=%s&apikey=%s",
                    BASE_URL, ticker, API_KEY
            );
            HttpURLConnection connection = (HttpURLConnection) new URL(apiUrl).openConnection();
            connection.setRequestMethod("GET");

            BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream()));
            StringBuilder response = new StringBuilder();
            String line;
            while ((line = reader.readLine()) != null) {
                response.append(line);
            }
            reader.close();

            ObjectMapper objectMapper = new ObjectMapper();
            JsonNode rootNode = objectMapper.readTree(response.toString());
            JsonNode globalQuoteNode = rootNode.path("Global Quote");

            // Debugging to ensure API response contains the necessary data
            System.out.println("Global Quote API Response: " + response.toString());

            return new BigDecimal(globalQuoteNode.path("05. price").asText());
        } catch (Exception e) {
            System.err.println("Error fetching current stock price: " + e.getMessage());
            e.printStackTrace();
            return BigDecimal.ZERO;
        }
    }

    @Override
    public Map<LocalDate, BigDecimal> getDailyClosingPrices(String ticker) {
        return fetchTimeSeriesData(ticker, "4. close");
    }

    @Override
    public Map<LocalDate, BigDecimal> getDailyOpeningPrices(String ticker) {
        return fetchTimeSeriesData(ticker, "1. open");
    }

    @Override
    public String getSector(String ticker) {
        try {
            String apiUrl = String.format(
                    "%sfunction=OVERVIEW&symbol=%s&apikey=%s",
                    BASE_URL, ticker, API_KEY
            );
            HttpURLConnection connection = (HttpURLConnection) new URL(apiUrl).openConnection();
            connection.setRequestMethod("GET");

            BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream()));
            StringBuilder response = new StringBuilder();
            String line;
            while ((line = reader.readLine()) != null) {
                response.append(line);
            }
            reader.close();

            // Debugging to ensure API response contains the necessary data
            System.out.println("Overview API Response: " + response.toString());

            ObjectMapper objectMapper = new ObjectMapper();
            JsonNode rootNode = objectMapper.readTree(response.toString());
            String sector = rootNode.path("Sector").asText();

            return sector.isEmpty() ? "Unknown" : sector;
        } catch (Exception e) {
            System.err.println("Error fetching sector information: " + e.getMessage());
            e.printStackTrace();
            return "Unknown";
        }
    }

    @Override
    public Map<String, Map<LocalDate, BigDecimal>> getCompetingStocks(String sector) {
        // Alpha Vantage does not provide a direct API to fetch competitors by sector.
        // Implement a fallback mechanism for predefined competitors.
        System.err.println("Alpha Vantage does not support fetching competing stocks dynamically.");
        return new HashMap<>();
    }

    private Map<LocalDate, BigDecimal> fetchTimeSeriesData(String ticker, String priceKey) {
        Map<LocalDate, BigDecimal> priceData = new HashMap<>();
        try {
            String apiUrl = String.format(
                    "%sfunction=TIME_SERIES_DAILY&symbol=%s&apikey=%s",
                    BASE_URL, ticker, API_KEY
            );
            HttpURLConnection connection = (HttpURLConnection) new URL(apiUrl).openConnection();
            connection.setRequestMethod("GET");

            BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream()));
            StringBuilder response = new StringBuilder();
            String line;
            while ((line = reader.readLine()) != null) {
                response.append(line);
            }
            reader.close();

            ObjectMapper objectMapper = new ObjectMapper();
            JsonNode rootNode = objectMapper.readTree(response.toString());
            JsonNode timeSeriesNode = rootNode.path("Time Series (Daily)");

            // Debugging to ensure API response contains the necessary data
            System.out.println("Time Series API Response: " + response.toString());

            timeSeriesNode.fields().forEachRemaining(entry -> {
                LocalDate date = LocalDate.parse(entry.getKey());
                BigDecimal price = new BigDecimal(entry.getValue().path(priceKey).asText());
                priceData.put(date, price);
            });
        } catch (Exception e) {
            System.err.println("Error fetching time series data: " + e.getMessage());
            e.printStackTrace();
        }
        return priceData;
    }
}

