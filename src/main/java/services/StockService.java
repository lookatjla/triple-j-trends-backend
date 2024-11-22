
package services;


import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Map;

public class StockService {
    private final MarketData marketData;

    public StockService() {
        this.marketData = new MarketData();
    }

    public Map<LocalDate, BigDecimal> getDailyClosingPrices(String ticker) {
        return marketData.getDailyClosingPrices(ticker);
    }

    public Map<LocalDate, BigDecimal> getDailyOpeningPrices(String ticker) {
        return marketData.getDailyOpeningPrices(ticker);
    }

    public BigDecimal getCurrentStockPrice(String ticker) {
        return marketData.getCurrentStockPrice(ticker);
    }
}

