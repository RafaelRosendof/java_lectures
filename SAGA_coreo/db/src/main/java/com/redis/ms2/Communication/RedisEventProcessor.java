package com.redis.ms2.Communication;

import com.redis.ms2.Service.RedisReactive;
import org.springframework.cloud.stream.function.StreamBridge;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.time.LocalDateTime;
import java.util.function.Function;

@Configuration
public class RedisEventProcessor {

    private final RedisReactive redisReactive;
    private final StreamBridge streamBridge;

    public RedisEventProcessor(RedisReactive redisReactive, StreamBridge streamBridge) {
        this.redisReactive = redisReactive;
        this.streamBridge = streamBridge;
    }

    @Bean
    public Function<Flux<StockRequestedEvent>, Mono<Void>> stockRequested() {
        return flux -> flux.flatMap(event -> {
            System.out.println("MS2-Redis: RECEBEU EVENTO -> " + event.getTicker());

            return redisReactive.recordStockRequest(event.getTicker())
                    .doOnSuccess(res -> System.out.println("MS2-Redis: Registrado com sucesso"))
                    .then();
        }).then();
    }

    @Bean
    public Function<Flux<ProcessBatchEvent>, Mono<Void>> processBatch() {
        return flux -> flux.flatMap(event -> {
            System.out.println("MS2-Redis: Processando batch...");

            return redisReactive.processRedis()
                    .flatMap(result -> {
                        if ("Error".equals(result)) {
                            return Mono.error(new RuntimeException("Falha no processamento do arquivo"));
                        }

                        System.out.println("MS2-Redis: Batch processado! Publicando evento...");

                        BatchCompletedEvent completedEvent = new BatchCompletedEvent(
                                event.getEventId(),
                                "/home/rafael/java_lectures/SAGA_coreo/top_requests.txt",
                                LocalDateTime.now()
                        );
                        streamBridge.send("batchCompleted-out-0", completedEvent);

                        return Mono.empty();
                    });
        }).then();
    }
}
