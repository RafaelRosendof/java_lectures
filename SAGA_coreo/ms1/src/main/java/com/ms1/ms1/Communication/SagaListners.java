package com.ms1.ms1.Communication;


import java.util.function.Consumer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import com.ms1.ms1.DTOS.RedisResponseDTO;
import com.ms1.ms1.DTOS.StockResponseDTO;


@Configuration
public class SagaListners {

    private final SagaOrchestrator orchestrator;

    public SagaListners(SagaOrchestrator orchestrator) {
        this.orchestrator = orchestrator;
    }

    @Bean
    public Consumer<StockResponseDTO> onStockResponse() {
        return response -> {
            System.out.println("MS1 LISTENER: Recebi resposta do MS2-Stock para Saga: " + response.getSagaId());
            
            if ("SUCCESS".equals(response.getStatus())) {
                System.out.println("MS1: Stock data recebido com sucesso");
                orchestrator.handleStockResponse(response.getSagaId(), response.getCollectedData());
            } else {
                System.err.println("MS1: FALHA no MS2-Stock: " + response.getErrorMessage());
                orchestrator.finishSaga(response.getSagaId(), "FALHA NO MS2: " + response.getErrorMessage());
            }
        };
    }

    @Bean
    public Consumer<RedisResponseDTO> onRedisResponse() {
        return response -> {
            System.out.println("MS1 LISTENER: Recebi resposta do Redis - Status: " + response.getStatus());
            
            // Processar diferentes tipos de resposta
            if ("SUCCESS".equals(response.getStatus())) {
                System.out.println("MS1: Redis registrou com sucesso: " + response.getCollectedData());
                orchestrator.handleRedisResponse(response);
                
            } else if ("BATCH_COMPLETED".equals(response.getStatus())) {
                System.out.println("MS1: Batch Redis completado! " + response.getCollectedData());
                orchestrator.handleBatchCompleted(response);
                
            } else {
                System.err.println("MS1: ERRO no Redis: " + response.getErrorMessage());
                orchestrator.handleRedisError(response);
            }
        };
    }

}