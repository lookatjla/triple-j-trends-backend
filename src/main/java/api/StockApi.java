package api;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Map;

public interface StockApi {
    BigDecimal getCurrentStockPrice(String ticker);

    Map<LocalDate, BigDecimal> getDailyClosingPrices(String ticker);

    Map<LocalDate, BigDecimal> getDailyOpeningPrices(String ticker);

    List<String> searchStockSymbols(String query);

    List<String> getStockNews(String ticker);

    static StockApi getApiImplementation() {
        return new PolygonApi();
    }
}
