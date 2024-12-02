package services;

import api.StockApi;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Map;

public class MarketData {
    private final StockApi stockApi;

    public MarketData() {
        this.stockApi = StockApi.getApiImplementation();
    }

    public BigDecimal getCurrentStockPrice(String ticker) {
        return stockApi.getCurrentStockPrice(ticker);
    }

    public Map<LocalDate, BigDecimal> getDailyClosingPrices(String ticker) {
        return stockApi.getDailyClosingPrices(ticker);
    }

    public Map<LocalDate, BigDecimal> getDailyOpeningPrices(String ticker) {
        return stockApi.getDailyOpeningPrices(ticker);
    }

    public List<String> searchStockSymbols(String query) {
        return stockApi.searchStockSymbols(query);
    }

    public List<String> getStockNews(String ticker) {
        return stockApi.getStockNews(ticker);
    }
}
