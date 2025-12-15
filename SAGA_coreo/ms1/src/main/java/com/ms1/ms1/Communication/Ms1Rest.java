package com.ms1.ms1.Communication;

import org.springframework.stereotype.Controller;
import org.springframework.web.reactive.config.EnableWebFlux;

import reactor.core.publisher.Mono;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.http.MediaType;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;


@RestController
@RequestMapping("/api")
public class Ms1Rest {

    private final SagaEventPublisher sagaEventPublisher;

    public Ms1Rest(SagaEventPublisher sagaEventPublisher) {
        this.sagaEventPublisher = sagaEventPublisher;
    }

    @PostMapping(value = "/stock/{ticker}", produces = MediaType.APPLICATION_JSON_VALUE)
    public Mono<String> registerStock(@PathVariable String ticker) {
        System.out.println("MS1 API: Registrando stock no Redis: " + ticker);
        sagaEventPublisher.publishStockRequested(ticker);
        return Mono.just("Pedido aceito. Processando em background...");
    }

    @PostMapping("/admin/stop-redis")
    public Mono<String> stopRedisProcess() {
        System.out.println("MS1 API: Enviando comando STOP ao MS2-Redis");
        sagaEventPublisher.stopRedis();
        return Mono.just("Feito a requisição, processo em backgroung");
    }

    @PostMapping("/analysis/start")
    public Mono<String> startAnalysis() {
        return sagaEventPublisher.requestData()
            .timeout(java.time.Duration.ofSeconds(30))
            .then(Mono.fromCallable(() -> {
                String outputPath = "/home/rafael/reativas_p3/analysis_report.txt";
                Path path = java.nio.file.Paths.get(outputPath);
                String content = java.nio.file.Files.readString(path);
                if (content.trim().isEmpty()) {
                    return "Erro: O arquivo de análise está vazio.";
                } else {
                    return content;
                }
            }));
    }
}