package api;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.math.BigDecimal;
import java.net.HttpURLConnection;
import java.net.URL;
import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class PolygonApi implements StockApi {
    private static final String API_KEY = "GDhO3GppI6630LpQ1yZ1Fd_gIezs0rWj";
    private static final String BASE_URL = "https://api.polygon.io/";

    @Override
    public BigDecimal getCurrentStockPrice(String ticker) {
        try {
            String apiUrl = String.format("%sv2/last/trade/%s?apiKey=%s", BASE_URL, ticker, API_KEY);
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
            return new BigDecimal(rootNode.path("results").path("c").asText());
        } catch (Exception e) {
            e.printStackTrace();
            return BigDecimal.ZERO;
        }
    }

    @Override
    public Map<LocalDate, BigDecimal> getDailyClosingPrices(String ticker) {
        return fetchHistoricalData(ticker, "c");
    }

    @Override
    public Map<LocalDate, BigDecimal> getDailyOpeningPrices(String ticker) {
        return fetchHistoricalData(ticker, "o");
    }

    @Override
    public List<String> searchStockSymbols(String query) {
        List<String> symbols = new ArrayList<>();
        try {
            String apiUrl = String.format("%sv3/reference/tickers?search=%s&active=true&apiKey=%s", BASE_URL, query, API_KEY);
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
            JsonNode resultsNode = rootNode.path("results");

            for (JsonNode result : resultsNode) {
                String symbol = result.path("ticker").asText();
                symbols.add(symbol);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return symbols;
    }

    @Override
    public List<String> getStockNews(String ticker) {
        List<String> newsArticles = new ArrayList<>();
        try {
            String apiUrl = String.format("%sv2/reference/news?ticker=%s&limit=5&apiKey=%s", BASE_URL, ticker, API_KEY);
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
            for (JsonNode articleNode : rootNode.path("results")) {
                String title = articleNode.path("title").asText();
                String url = articleNode.path("article_url").asText();
                newsArticles.add(title + " - " + url);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return newsArticles;
    }

    private Map<LocalDate, BigDecimal> fetchHistoricalData(String ticker, String priceField) {
        Map<LocalDate, BigDecimal> priceData = new HashMap<>();
        try {
            LocalDate endDate = LocalDate.now();
            LocalDate startDate = endDate.minusWeeks(2);

            String apiUrl = String.format("%sv2/aggs/ticker/%s/range/1/day/%s/%s?adjusted=true&sort=asc&apiKey=%s", BASE_URL, ticker, startDate, endDate, API_KEY);
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
            for (JsonNode result : rootNode.path("results")) {
                long timestamp = result.path("t").asLong();
                LocalDate date = Instant.ofEpochMilli(timestamp).atZone(ZoneId.systemDefault()).toLocalDate();
                BigDecimal price = new BigDecimal(result.path(priceField).asText());
                priceData.put(date, price);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return priceData;
    }
}