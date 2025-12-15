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

    private final SagaOrchestrator sagaOrchestrator;

    public Ms1Rest(SagaOrchestrator sagaOrchestrator) {
        this.sagaOrchestrator = sagaOrchestrator;
    }

    @PostMapping(value = "/stock/{ticker}", produces = MediaType.APPLICATION_JSON_VALUE)
    public Mono<String> registerStock(@PathVariable String ticker) {
        System.out.println("MS1 API: Registrando stock no Redis: " + ticker);
        sagaOrchestrator.registerStockInRedis(ticker);
        return Mono.just("Pedido aceito. Processando em background...");
    }

    @PostMapping("/admin/stop-redis")
    public Mono<String> stopRedisProcess() {
        System.out.println("MS1 API: Enviando comando STOP ao MS2-Redis");
        return sagaOrchestrator.sendStopCommand();
    }

    @PostMapping("/analysis/start")
    public Mono<String> startAnalysis() {
        return sagaOrchestrator.startAnalysisSaga()
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