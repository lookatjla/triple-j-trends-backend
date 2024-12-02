package model;

import lombok.*;
import java.time.LocalDateTime;
import java.util.List;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class StockWrapper {
    private List<String> symbols; // List of stock symbols being analyzed
    private String startDate;     // Analysis start date as a string
    private String endDate;       // Analysis end date as a string
    private LocalDateTime lastAccessed; // Timestamp of the last access
}