package com.redis.ms2.Communication;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class BatchCompletedEvent {
    private String eventId;
    private String filePath;
    private LocalDateTime timestamp;
}
