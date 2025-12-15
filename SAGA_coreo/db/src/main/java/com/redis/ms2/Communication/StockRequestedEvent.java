package com.redis.ms2.Communication;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class StockRequestedEvent {
    private String eventId;
    private String ticker;
    private LocalDateTime timestamp;
}