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
        public Function<Flux<StockCommandDTO>, Flux<StockResponseDTO>> processStockData() {
            return flux -> flux
                .doOnNext(command -> System.out.println("MS2 RECEBEU: " + command))
                .flatMap(command -> {
                    
                    if ("GET_ANALYSIS".equals(command.getTicker())) {
                        System.out.println("MS2: PARANDO E PROCESSANDO BATCH...");
                        return stocksService.writeData()
                                .map(msg -> new StockResponseDTO(
                                        command.getSagaId(),
                                        "BATCH_COMPLETED",
                                        msg,
                                        null
                                ));
                            
                    } else {
                        System.out.println("MS2: Comando ignorado ou processamento padrão: " + command.getTicker());
                        return Mono.empty(); 
                    }
                })
                .doOnError(err -> System.out.println("ERRO FATAL REDIS PROCESSOR: " + err));
        }

}
