package services;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Map;

public class StockService {
    private final MarketData marketData;

    public StockService() {
        this.marketData = new MarketData();
    }

    public BigDecimal getCurrentStockPrice(String ticker) {
        return marketData.getCurrentStockPrice(ticker);
    }

    public Map<LocalDate, BigDecimal> getDailyClosingPrices(String ticker) {
        return marketData.getDailyClosingPrices(ticker);
    }

    public Map<LocalDate, BigDecimal> getDailyOpeningPrices(String ticker) {
        return marketData.getDailyOpeningPrices(ticker);
    }

    public List<String> searchStockSymbols(String query) {
        return marketData.searchStockSymbols(query);
    }

    public List<String> getStockNews(String ticker) {
        return marketData.getStockNews(ticker);
    }
}
