package com.redis.ms2.Communication;

import java.util.function.Function;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.redis.ms2.Service.RedisReactive;

import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Configuration
public class StockProcessor {

    private final RedisReactive redisService;

    public StockProcessor(RedisReactive redisService) {

        this.redisService = redisService;
    }

    @Bean
    public Consumer<StockRequestedEvent> stockRequested() {
        return event -> {
            System.out.println("MS2-Redis: Registrando " + event.getTicker());
            
            redisService.recordStockRequest(event.getTicker())
                .doOnSuccess(res -> System.out.println("MS2-Redis: Registrado com sucesso"))
                .doOnError(err -> System.err.println("MS2-Redis: Erro ao registrar: " + err))
                .subscribe();
        };
    }
    

    @Bean
    public Consumer<ProcessBatchEvent> processBatch() {
        return event -> {
            System.out.println("MS2-Redis: Processando batch...");
            
            redisService.processRedis()
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
