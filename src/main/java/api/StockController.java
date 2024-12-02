package api;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import services.StockService;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.List;

@RestController
@RequestMapping("/api/stocks")
public class StockController {
    private final StockService stockService;

    public StockController(StockService stockService) {
        this.stockService = stockService;
    }

    // Existing endpoint to fetch current stock price
    @GetMapping("/price")
    public ResponseEntity<?> getStockPrice(@RequestParam String ticker) {
        try {
            var stockPrice = stockService.getCurrentStockPrice(ticker.toUpperCase());
            if (stockPrice.compareTo(java.math.BigDecimal.ZERO) == 0) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Stock not found");
            }
            return ResponseEntity.ok(stockPrice);
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error fetching stock price");
        }
    }

    // Existing endpoint to search stock symbols (using service logic)
    @GetMapping("/search")
    public ResponseEntity<List<String>> searchStockSymbols(@RequestParam String query) {
        try {
            List<String> symbols = stockService.searchStockSymbols(query);
            System.out.println(symbols);
            return ResponseEntity.ok(symbols);
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    // Existing endpoint to fetch stock news
    @GetMapping("/news")
    public ResponseEntity<List<String>> getStockNews(@RequestParam String ticker) {
        try {
            List<String> newsArticles = stockService.getStockNews(ticker);
            return ResponseEntity.ok(newsArticles);
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    // New proxy endpoint to fetch stock symbols from Polygon API
    @GetMapping("/proxy/tickers")
    public ResponseEntity<?> proxyPolygonRequest(@RequestParam String search) {
        try {
            // Polygon API URL
            String apiUrl = String.format(
                    "https://api.polygon.io/v3/reference/tickers?search=%s&active=true&apiKey=GDhO3GppI6630LpQ1yZ1Fd_gIezs0rWj",
                    search
            );

            // Make HTTP request to Polygon API
            HttpURLConnection connection = (HttpURLConnection) new URL(apiUrl).openConnection();
            connection.setRequestMethod("GET");

            BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream()));
            StringBuilder response = new StringBuilder();
            String line;
            while ((line = reader.readLine()) != null) {
                response.append(line);
            }
            reader.close();

            // Return the raw JSON response to the frontend
            return ResponseEntity.ok(response.toString());
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error fetching data from Polygon API");
        }
    }
}
