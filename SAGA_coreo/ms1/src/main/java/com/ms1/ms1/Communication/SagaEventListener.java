package com.ms1.ms1.Communication;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.function.Consumer;
import java.util.function.Function;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;


import com.ms1.ms1.DTOS.StockDataCollectedEvent;

@Configuration
public class SagaEventListener {
    
    @Bean
    public Consumer<StockDataCollectedEvent> stockDataReady() {
        return event -> {
            System.out.println("MS1: Dados prontos! Arquivo: " + event.getFilePath());
            
            try {
                String content = Files.readString(Paths.get(event.getFilePath()));
                System.out.println("MS1: Análise completa:\n" + content);
                
            } catch (IOException e) {
                System.err.println("MS1: Erro ao ler arquivo: " + e.getMessage());
            }
        };
    }
}