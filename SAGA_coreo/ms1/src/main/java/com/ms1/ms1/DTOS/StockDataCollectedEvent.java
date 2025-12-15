package com.ms1.ms1.DTOS;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.LocalDateTime;


@Data
@AllArgsConstructor
@NoArgsConstructor
public class StockDataCollectedEvent {
    private String eventId;
    private String filePath;
    private LocalDateTime timestamp;
}