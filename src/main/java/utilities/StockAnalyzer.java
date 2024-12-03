package utilities;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.util.Map;

public class StockAnalyzer {
    private final String tickerSymbol;
    private final Map<LocalDate, BigDecimal> dailyOpeningPrices;
    private final Map<LocalDate, BigDecimal> dailyClosingPrices;

    // Constructor
    public StockAnalyzer(String tickerSymbol, Map<LocalDate, BigDecimal> dailyOpeningPrices, Map<LocalDate, BigDecimal> dailyClosingPrices) {
        this.tickerSymbol = tickerSymbol;
        this.dailyOpeningPrices = dailyOpeningPrices;
        this.dailyClosingPrices = dailyClosingPrices;
    }

    // Print the full analysis summary
    public void printAnalysisSummary(LocalDate startDate, LocalDate endDate) {
        System.out.println("Stock Analysis Summary:");
        System.out.println("Stock Symbol: " + tickerSymbol);
        System.out.println("Start Date: " + startDate);
        System.out.println("End Date: " + endDate);
        System.out.println("Daily Opening and Closing Prices in the Last Two Weeks:");

        dailyClosingPrices.forEach((date, closingPrice) -> {
            BigDecimal openingPrice = dailyOpeningPrices.getOrDefault(date, BigDecimal.ZERO);
            System.out.println("Date: " + date + " | Opening Price: " + openingPrice + " | Closing Price: " + closingPrice);
        });

        // Percentage change and recommendations
        BigDecimal percentageChange = calculatePercentageChange();
        System.out.println("Percentage Change Over Period: " + percentageChange.setScale(2, RoundingMode.HALF_UP) + "%");
        System.out.println("Recommendation: " + generateRecommendation());

        // Highs and Lows
        LocalDate highestDate = dailyClosingPrices.entrySet().stream()
                .max(Map.Entry.comparingByValue())
                .map(Map.Entry::getKey)
                .orElse(null);

        LocalDate lowestDate = dailyClosingPrices.entrySet().stream()
                .min(Map.Entry.comparingByValue())
                .map(Map.Entry::getKey)
                .orElse(null);

        System.out.println("Highest Closing Price: " + dailyClosingPrices.getOrDefault(highestDate, BigDecimal.ZERO)
                + " on " + highestDate);
        System.out.println("Lowest Closing Price: " + dailyClosingPrices.getOrDefault(lowestDate, BigDecimal.ZERO)
                + " on " + lowestDate);
    }

    // Helper to calculate percentage change
    public BigDecimal calculatePercentageChange() {
        LocalDate startDate = dailyClosingPrices.keySet().stream().findFirst().orElse(null);
        LocalDate endDate = dailyClosingPrices.keySet().stream().reduce((first, second) -> second).orElse(null);

        if (startDate == null || endDate == null) return BigDecimal.ZERO;

        BigDecimal startPrice = dailyClosingPrices.get(startDate);
        BigDecimal endPrice = dailyClosingPrices.get(endDate);

        if (startPrice == null || endPrice == null) return BigDecimal.ZERO;

        return endPrice.subtract(startPrice)
                .divide(startPrice, RoundingMode.HALF_UP)
                .multiply(BigDecimal.valueOf(100));
    }

    // Generate recommendation
    public String generateRecommendation() {
        BigDecimal percentageChange = calculatePercentageChange();

        if (percentageChange.compareTo(BigDecimal.valueOf(5)) > 0) {
            return "Buy (positive trend)";
        } else if (percentageChange.compareTo(BigDecimal.valueOf(-5)) < 0) {
            return "Sell (negative trend)";
        } else {
            return "Hold (neutral trend)";
        }
    }
}
