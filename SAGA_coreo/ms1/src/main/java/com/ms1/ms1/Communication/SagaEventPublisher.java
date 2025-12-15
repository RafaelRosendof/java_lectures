package com.ms1.ms1.Communication;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.stream.function.StreamBridge;

import java.time.LocalDateTime;
import java.util.UUID;
import java.util.function.Function;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.stereotype.Service;

import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import com.ms1.ms1.DTOS.ProcessBatchEvent;
import com.ms1.ms1.DTOS.StockDataCollectedEvent;
import com.ms1.ms1.DTOS.StockRequestedEvent;
import com.ms1.ms1.DTOS.DocsEvent;


@Service
public class SagaEventPublisher {
    
    private final StreamBridge streamBridge;
    
    public SagaEventPublisher(StreamBridge streamBridge) {
        this.streamBridge = streamBridge;
    }
    
    // Publicar evento: "Quero registrar PETR4"
    public void publishStockRequested(String ticker) {
        StockRequestedEvent event = new StockRequestedEvent(
            UUID.randomUUID().toString(),
            ticker,
            LocalDateTime.now()
        );
        
        System.out.println("MS1: Publicando StockRequested -> " + ticker);
        streamBridge.send("stockRequested-out-0", event);
    }
    
    public void stopRedis() {
        ProcessBatchEvent event = new ProcessBatchEvent(
            UUID.randomUUID().toString(),
            LocalDateTime.now()
        );
        
        System.out.println("MS1: Publicando ProcessBatchRequested");
        streamBridge.send("processBatch-out-0", event);
    }


    public Mono<String> requestData(){
        DocsEvent event = new DocsEvent(
            UUID.randomUUID().toString(),
            "/home/rafael/java_lectures/SAGA_coreo/",
            LocalDateTime.now()
        );

        System.out.println("MS1:  Requisitando a geração do relatório\n\n");
        streamBridge.send("getStockNews-out-0", event);

        return Mono.just("Requisitado ");
    }
}

