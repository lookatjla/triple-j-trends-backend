package services;

import org.springframework.boot.SpringApplication;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import utilities.StockAnalyzer;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Map;

@RestController
@RequestMapping("/api/stocks")
public class Main {

    private final MarketData marketData = new MarketData();

    public static void main(String[] args) {
        SpringApplication.run(Main.class, args);
    }

    @GetMapping("/analyze")
    public ResponseEntity<?> analyzeStock(
            @RequestParam String ticker,
            @RequestParam String startDate,
            @RequestParam String endDate
    ) {
        try {
            LocalDate start = LocalDate.parse(startDate);
            LocalDate end = LocalDate.parse(endDate);

            Map<LocalDate, BigDecimal> closingPrices = marketData.getDailyClosingPrices(ticker);
            Map<LocalDate, BigDecimal> openingPrices = marketData.getDailyOpeningPrices(ticker);

            if (closingPrices.isEmpty() || openingPrices.isEmpty()) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body("No stock data found for the given ticker.");
            }

            StockAnalyzer analyzer = new StockAnalyzer(ticker, openingPrices, closingPrices);

            // Generate the analysis summary as a string
            StringBuilder summary = new StringBuilder();
            summary.append("Stock Symbol: ").append(ticker).append("\n")
                    .append("Start Date: ").append(startDate).append("\n")
                    .append("End Date: ").append(endDate).append("\n");

            closingPrices.forEach((date, closingPrice) -> {
                if (!date.isBefore(start) && !date.isAfter(end)) {
                    BigDecimal openingPrice = openingPrices.getOrDefault(date, BigDecimal.ZERO);
                    summary.append(String.format("Date: %s | Opening: %s | Closing: %s%n", date, openingPrice, closingPrice));
                }
            });

            BigDecimal percentageChange = analyzer.calculatePercentageChange();
            summary.append(String.format("Percentage Change: %.2f%%%n", percentageChange))
                    .append("Recommendation: ").append(analyzer.generateRecommendation()).append("\n");

            return ResponseEntity.ok(summary.toString());
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("An error occurred while analyzing stock data.");
        }
    }
}

//public class Main {
//    public static void main(String[] args) {
//        MarketData marketData = new MarketData();
//
//        String ticker = "MSFT"; // Example stock ticker
//        LocalDate startDate = LocalDate.now().minusWeeks(2);
//        LocalDate endDate = LocalDate.now();
//
//        // Fetch stock data
//        Map<LocalDate, BigDecimal> closingPrices = marketData.getDailyClosingPrices(ticker);
//        Map<LocalDate, BigDecimal> openingPrices = marketData.getDailyOpeningPrices(ticker);
//
//        if (closingPrices.isEmpty() || openingPrices.isEmpty()) {
//            System.err.println("Unable to retrieve stock data. Please check your API configuration or internet connection.");
//            return;
//        }
//
//        // Analyze and display stock data
//        StockAnalyzer analyzer = new StockAnalyzer(ticker, openingPrices, closingPrices);
//        analyzer.printAnalysisSummary(startDate, endDate);
//    }
// }