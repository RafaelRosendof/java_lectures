package com.ms1.ms1.Communication;

import java.time.Duration;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;

import org.springframework.cloud.stream.function.StreamBridge;
import org.springframework.stereotype.Service;

import com.ms1.ms1.DTOS.AnalysisCommandDTO;
import com.ms1.ms1.DTOS.StockCommandDTO;
import com.ms1.ms1.DTOS.StockResponseDTO;
import com.ms1.ms1.DTOS.RedisCommandDTO;
import com.ms1.ms1.DTOS.RedisResponseDTO;

import reactor.core.publisher.Mono;
import reactor.core.publisher.Sinks;


@Service
public class SagaOrchestrator {

    private final StreamBridge streamBridge;
    
    // Caminho dos arquivos compartilhados
    private static final String TOP_REQUESTS_FILE = "/home/rafael/reativas_p3/top_requests.txt";
    private static final String STOCK_DATA_FILE = "/home/rafael/reativas_p3/analysis_report.txt";

    public SagaOrchestrator(StreamBridge streamBridge) {
        this.streamBridge = streamBridge;
    }


    public Mono<String> registerStockInRedis(String ticker) {
        String sagaId = UUID.randomUUID().toString();
        RedisCommandDTO command = new RedisCommandDTO(sagaId, ticker);
        
        System.out.println("MS1: Registrando no Redis (fire-and-forget): " + ticker);
        streamBridge.send("redis-command-out", command);
        
        // Retorna imediatamente sem esperar resposta
        return Mono.just("Ticker " + ticker + " enviado para registro no Redis");
    }


    public Mono<String> sendStopCommand() {
        String sagaId = UUID.randomUUID().toString();
        RedisCommandDTO stopCommand = new RedisCommandDTO(sagaId, "STOP_PROCESS");
        
        System.out.println("MS1: Enviando STOP command ao MS2-Redis");
        streamBridge.send("redis-command-out", stopCommand);
        
        // Aguarda 2 segundos para o MS2 processar e salvar o arquivo
        return Mono.delay(Duration.ofSeconds(2))
            .then(Mono.just("Comando STOP enviado. Aguarde processamento do batch..."));
    }

    //modificar aqui 
    public Mono<String> startAnalysisSaga() {
        System.out.println("MS1: Iniciando Saga de Análise");
        
        return Mono.fromCallable(() -> {
            List<String> topStocks = Files.readAllLines(Paths.get(TOP_REQUESTS_FILE));
            if (topStocks.isEmpty()) {
                throw new RuntimeException("Arquivo top_requests.txt está vazio!");
            }
            System.out.println("MS1: Top stocks encontrados: " + topStocks);
            return topStocks;
        })
        .flatMap(topStocks -> {
            String sagaId = UUID.randomUUID().toString();
            String stocksToAnalyze = String.join(",", topStocks);
            
            StockCommandDTO command = new StockCommandDTO(sagaId, "GET_ANALYSIS", stocksToAnalyze);
            System.out.println("MS1: Enviando para MS2-Stock coletar dados: " + stocksToAnalyze);
            
            streamBridge.send("stock-command-out", command);
            
            return Mono.delay(Duration.ofSeconds(5))
                .then(readStockDataFile());
        })
        .onErrorResume(e -> {
            System.err.println("MS1: Erro na Saga de Análise: " + e.getMessage());
            return Mono.just("Erro: " + e.getMessage());
        });
    }

    private Mono<String> readStockDataFile() {
        return Mono.fromCallable(() -> {
            System.out.println("MS1: Lendo arquivo stock_data.txt");
            
            List<String> lines = Files.readAllLines(Paths.get(STOCK_DATA_FILE));
            String result = String.join("\n", lines);
            
            System.out.println("MS1: Dados coletados com sucesso!");
            return result;
        })
        .onErrorResume(e -> {
            System.err.println("MS1: Erro ao ler stock_data.txt: " + e.getMessage());
            return Mono.just("Erro ao ler dados processados: " + e.getMessage());
        });
    }

    
    public Mono<Boolean> checkIfBatchReady() {
        return Mono.fromCallable(() -> Files.exists(Paths.get(TOP_REQUESTS_FILE)));
    }

    public void handleRedisError(RedisResponseDTO response) {
        
        throw new UnsupportedOperationException("Unimplemented method 'handleRedisError'");
    }

    public Mono<String> handleRedisResponse(RedisResponseDTO response) {
        return Mono.just("Ação concluída com sucesso no Redis: " + response.getCollectedData());
    }

    public Mono<String> handleBatchCompleted(RedisResponseDTO response) {
        return Mono.just("Batch processado com sucesso no Redis: " + response.getCollectedData());  
    }

    public void finishSaga(String sagaId, String finalAnalysis) {
        System.out.println("MS1: Finalizando Saga " + sagaId + " com análise: " + finalAnalysis);
    }

    public void handleStockResponse(String sagaId, String collectedData) {
        System.out.println("MS1: Processando dados coletados do MS2-Stock para Saga " + sagaId);    
    
    }


}