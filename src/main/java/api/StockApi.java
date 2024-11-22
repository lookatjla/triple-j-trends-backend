
package api;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Map;
public interface StockApi {
    Map<LocalDate, BigDecimal> getDailyClosingPrices(String ticker);
    Map<LocalDate, BigDecimal> getDailyOpeningPrices(String ticker);
    BigDecimal getCurrentStockPrice(String ticker);

    static StockApi getApiImplementation() {
        String selectedApi = System.getProperty("api.provider", "Polygon").toUpperCase();
        if ("ALPHA".equals(selectedApi)) {
            return new AlphaVantageApi();
        } else {
            return new PolygonApi(); // Default to Yahoo Finance
        }
    }

    String getSector(String ticker);

    Map<String, Map<LocalDate, BigDecimal>> getCompetingStocks(String sector);
}


