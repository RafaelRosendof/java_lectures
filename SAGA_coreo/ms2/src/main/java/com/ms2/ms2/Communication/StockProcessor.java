package com.ms2.ms2.Communication;

import java.util.function.Function;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.ms2.ms2.Service.StocksService;

import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import com.ms2.ms2.Communication.StockResponseDTO;
@Configuration
public class StockProcessor {

    private final StocksService stocksService;

    public StockProcessor(StocksService stocksService) {
        this.stocksService = stocksService;
    }

    @Bean
    public Function<Flux<StockDataCollectedEvent>, Mono<Void>> processStockData() {
        return flux -> flux.flatMap(event -> {
            System.out.println("MS2-Postgres: Recebeu arquivo para análise: " + event.getFilePath());
            return stocksService.writeData(event.getFilePath())
                    .doOnSuccess(msg -> System.out.println("MS2-Postgres: " + msg))
                    .doOnError(err -> System.err.println("MS2-Postgres Erro: " + err))
                    .then();
        })
        .then();
    }
}
