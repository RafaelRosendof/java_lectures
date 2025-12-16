package com.redis.ms2.Service;

import org.springframework.stereotype.Service;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Range;
import org.springframework.data.redis.core.ReactiveRedisTemplate;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Schedulers; // Importante para I/O de arquivo

import java.nio.file.Files;
import java.nio.file.Paths;
import java.time.Duration;
import java.util.List;

@Service
public class RedisReactive {

    private final ReactiveRedisTemplate<String, String> redisTemplate;
    private static final String LEADERBOARD_KEY = "stock:requests:leaderboard";
    // Caminho do arquivo extraído para constante para facilitar
    private static final String FILE_PATH = "/home/rafael/java_lectures/SAGA_coreo/top_requests.txt";

    @Autowired
    public RedisReactive(ReactiveRedisTemplate<String, String> redisTemplate) {
        this.redisTemplate = redisTemplate;
    }

    public Mono<String> recordStockRequest(String stockName) {
        System.out.println("Recorded stock request for: " + stockName);
        return redisTemplate.opsForZSet().incrementScore(LEADERBOARD_KEY, stockName, 1)
                .map(newScore -> "Success")
                .onErrorResume(e -> {
                    System.err.println("Erro ao gravar no Redis: " + e.getMessage());
                    return Mono.just("Error");
                });
    }

    public Mono<String> processRedis() {
        return getTop2Request()
            .collectList()
            .flatMap(list -> {
                if (list.isEmpty()) {
                    return Mono.just("Lista vazia, nada a processar.");
                }

                // 1. Isole o I/O de arquivo (bloqueante) em uma thread separada
                return Mono.fromCallable(() -> {
                    System.out.println("Escrevendo no arquivo: " + list);
                    Files.write(Paths.get(FILE_PATH), list);
                    return "Success";
                })
                .subscribeOn(Schedulers.boundedElastic()) // <--- VITAL: Não trava o Netty
                
                // 2. Só limpa o Redis DEPOIS que o arquivo foi escrito com sucesso
                .flatMap(result -> clearRedis().thenReturn(result))
                
                .onErrorResume(e -> {
                    System.err.println("Erro ao processar arquivo: " + e.getMessage());
                    return Mono.just("Error");
                });
            });
    }

    public Flux<String> getTop2Request() {
        // Range 0 a 1 pega os 2 primeiros (top scores se for reverse)
        return redisTemplate.opsForZSet().reverseRange(LEADERBOARD_KEY, Range.closed(0L, 1L));
    }

    public Mono<Void> clearRedis() {
        System.out.println("Limpando Redis...");
        return redisTemplate.delete(LEADERBOARD_KEY).then();
    }
}

/*
package com.redis.ms2.Service;


import org.springframework.stereotype.Service;

import java.util.List;

import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.time.Duration;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Range;
import org.springframework.data.redis.core.ReactiveRedisTemplate;

@Service
public class RedisReactive{
    
    private final ReactiveRedisTemplate<String , String> redisTemplate;
    private static final String LEADERBOARD_KEY = "stock:requests:leaderboard";

    @Autowired
    public RedisReactive(ReactiveRedisTemplate<String, String> redisTemplate) {
        this.redisTemplate = redisTemplate;
    }

    public Mono<Boolean> set(String key , String value , Duration ttl){
        return redisTemplate.opsForValue().set(key, value, ttl);
    }

    public Mono<String> recordStockRequest(String stockName){ 
        System.out.println("Recorded stock request for: " + stockName);
        return redisTemplate.opsForZSet().incrementScore(LEADERBOARD_KEY, stockName, 1)
            .map(newScore -> "Success") 
            .onErrorResume(e -> {
                e.printStackTrace(); 
                return Mono.just("Error");
            });

        //System.out.println("Recorded stock request for: " + stockName);
    }

    public Mono<String> processRedis(){
        return getTop2Request()
            .collectList()
            .flatMap(list -> {
                try {
                    System.out.println("Top 2 requests: " + list);
                    java.nio.file.Files.write(java.nio.file.Paths.get("/home/rafael/reativas_p3/top_requests.txt"), list);
                    clearRedis().subscribe();
                    return Mono.just("Success");
                } catch (Exception e) {
                    return Mono.just("Error");
                }
            });
    }

    public Flux<String> getTop2Request(){
        return redisTemplate.opsForZSet().reverseRange(LEADERBOARD_KEY, Range.closed(0L, 1L));
    }

    public Mono<Void> clearRedis(){
        return redisTemplate.delete(LEADERBOARD_KEY).then();
    }

}
*/