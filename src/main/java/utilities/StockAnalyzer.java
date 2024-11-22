package utilities;

import api.PolygonApi;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.util.List;
import java.util.Map;

public class StockAnalyzer {
    private final Map<LocalDate, BigDecimal> dailyClosingPrices;
    private final Map<LocalDate, BigDecimal> dailyOpeningPrices;
    private final String tickerSymbol;
    private final String sector; // Sector information
    private final Map<String, Map<LocalDate, BigDecimal>> competingStocks; // Competing stocks
    private final PolygonApi polygonApi;

    // Constructor
    public StockAnalyzer(
            String tickerSymbol,
            Map<LocalDate, BigDecimal> dailyOpeningPrices,
            Map<LocalDate, BigDecimal> dailyClosingPrices,
            String sector,
            Map<String, Map<LocalDate, BigDecimal>> competingStocks
    ) {
        this.tickerSymbol = tickerSymbol;
        this.dailyOpeningPrices = dailyOpeningPrices;
        this.dailyClosingPrices = dailyClosingPrices;
        this.sector = sector;
        this.competingStocks = competingStocks;
        this.polygonApi = new PolygonApi(); // Instantiate Polygon API
    }

    // Print the full analysis summary
    public void printAnalysisSummary(LocalDate startDate, LocalDate endDate) {
        System.out.println("Stock Analysis Summary:");
        System.out.println("Stock Symbol: " + tickerSymbol);
        System.out.println("Sector: " + sector);
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

        // Competing stocks comparison
        System.out.println("\nComparison with Competing Stocks:");
        compareWithCompetingStocks();

        // Fetch and print news articles
        printNewsArticles();
    }

    // Print news articles for the ticker
    public void printNewsArticles() {
        System.out.println("\nLatest News Articles for " + tickerSymbol + ":");
        List<String> newsArticles = polygonApi.getStockNews(tickerSymbol);
        if (newsArticles.isEmpty()) {
            System.out.println("No news articles found for " + tickerSymbol + ".");
        } else {
            for (String article : newsArticles) {
                System.out.println("- " + article);
            }
        }
    }

    // Compare competing stocks
    private void compareWithCompetingStocks() {
        System.out.println("Ticker Symbol | Avg Closing Price | % Change");

        // Chosen stock
        BigDecimal avgClosing = calculateAveragePrice(dailyClosingPrices);
        BigDecimal percentageChange = calculatePercentageChange();
        System.out.printf("%-14s | %-17.2f | %-10.2f%%%n", tickerSymbol, avgClosing, percentageChange);

        // Competitors
        for (Map.Entry<String, Map<LocalDate, BigDecimal>> entry : competingStocks.entrySet()) {
            String competitor = entry.getKey();
            Map<LocalDate, BigDecimal> competitorClosingPrices = entry.getValue();

            BigDecimal competitorAvgClosing = calculateAveragePrice(competitorClosingPrices);
            BigDecimal competitorPercentageChange = calculatePercentageChange(competitorClosingPrices);

            System.out.printf("%-14s | %-17.2f | %-10.2f%%%n",
                    competitor, competitorAvgClosing, competitorPercentageChange);
        }
    }

    // Helper to calculate percentage change
    private BigDecimal calculatePercentageChange() {
        return calculatePercentageChange(dailyClosingPrices);
    }

    private BigDecimal calculatePercentageChange(Map<LocalDate, BigDecimal> closingPrices) {
        LocalDate startDate = closingPrices.keySet().stream().findFirst().orElse(null);
        LocalDate endDate = closingPrices.keySet().stream().reduce((first, second) -> second).orElse(null);

        if (startDate == null || endDate == null) return BigDecimal.ZERO;

        BigDecimal startPrice = closingPrices.get(startDate);
        BigDecimal endPrice = closingPrices.get(endDate);

        if (startPrice == null || endPrice == null) return BigDecimal.ZERO;

        return endPrice.subtract(startPrice)
                .divide(startPrice, RoundingMode.HALF_UP)
                .multiply(BigDecimal.valueOf(100));
    }

    // Helper to calculate average price
    private BigDecimal calculateAveragePrice(Map<LocalDate, BigDecimal> prices) {
        if (prices.isEmpty()) return BigDecimal.ZERO;

        BigDecimal total = prices.values().stream()
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        return total.divide(BigDecimal.valueOf(prices.size()), RoundingMode.HALF_UP);
    }

    // Generate recommendation
    private String generateRecommendation() {
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
