
package services;

import api.StockApi;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Map;

public class MarketData {
    private final StockApi stockApi;

    public MarketData() {
        this.stockApi = StockApi.getApiImplementation();
    }

    public Map<LocalDate, BigDecimal> getDailyClosingPrices(String ticker) {
        return stockApi.getDailyClosingPrices(ticker);
    }

    public Map<LocalDate, BigDecimal> getDailyOpeningPrices(String ticker) {
        return stockApi.getDailyOpeningPrices(ticker);
    }

    public BigDecimal getCurrentStockPrice(String ticker) {
        return stockApi.getCurrentStockPrice(ticker);
    }

    public String getSector(String ticker) {
        // Fetch the stock's sector using the API
        return stockApi.getSector(ticker);
    }

    public Map<String, Map<LocalDate, BigDecimal>> getCompetingStocks(String sector) {
        // Fetch competing stocks in the same sector
        return stockApi.getCompetingStocks(sector);
    }
}

