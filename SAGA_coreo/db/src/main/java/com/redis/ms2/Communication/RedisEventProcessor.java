package com.redis.ms2.Communication;

import java.time.LocalDateTime;
import java.util.function.Consumer;

import org.springframework.cloud.stream.function.StreamBridge;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.redis.ms2.Service.RedisReactive;


@Configuration
public class RedisEventProcessor {
    
    private final RedisReactive redisReactive;
    private final StreamBridge streamBridge;

    public RedisEventProcessor(RedisReactive redisReactive, StreamBridge streamBridge){
        this.redisReactive = redisReactive;
        this.streamBridge = streamBridge;
    }

    @Bean
    public Consumer<StockRequestedEvent> stockRequested() {
        return event -> {
            System.out.println("MS2-Redis: Registrando " + event.getTicker());
            
            redisReactive.recordStockRequest(event.getTicker())
                .doOnSuccess(res -> System.out.println("MS2-Redis: Registrado com sucesso"))
                .doOnError(err -> System.err.println("MS2-Redis: Erro ao registrar: " + err))
                .subscribe();
        };
    }

    @Bean
    public Consumer<ProcessBatchEvent> processBatch() {
        return event -> {
            System.out.println("MS2-Redis: Processando batch...");
            
            redisReactive.processRedis()
                .doOnSuccess(result -> {
                    System.out.println("MS2-Redis: Batch processado! Publicando evento...");
                    
                    // Publicar: "Batch pronto!"
                    BatchCompletedEvent completedEvent = new BatchCompletedEvent(
                        event.getEventId(),
                        "/home/rafael/reativas_p3/top_requests.txt",
                        LocalDateTime.now()
                    );
                    
                    streamBridge.send("batchCompleted-out-0", completedEvent);
                })
                .subscribe();
        };
    }
}
