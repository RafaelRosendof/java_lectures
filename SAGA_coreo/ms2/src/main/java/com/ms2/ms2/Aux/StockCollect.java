package com.ms2.ms2.Aux;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;
import com.ms2.ms2.Entity.Stocks;

@Service
public class StockCollect {

    private final WebClient webClient;
    private final ObjectMapper objectMapper;

    //@Value("${alphavantage.api.key:NOT_DEFINED}")
    //@Value("ALPHAVANTAGE_API_KEY_HERE")
    private String apiKey = "P4V6CEV6271X11P3";

    public StockCollect(WebClient.Builder webClientBuilder, ObjectMapper objectMapper) {
        this.webClient = webClientBuilder.baseUrl("https://www.alphavantage.co").build();
        this.objectMapper = objectMapper;
    }

    public Mono<List<Stocks>> collectStockHistory(String symbol) {
        return webClient.get()
                .uri(uriBuilder -> uriBuilder
                        .path("/query")
                        .queryParam("function", "TIME_SERIES_DAILY")
                        .queryParam("symbol", symbol)
                        .queryParam("outputsize", "compact")
                        .queryParam("apikey", apiKey)
                        .build())
                .retrieve()
                .bodyToMono(String.class)
                .map(json -> parseStockData(json, symbol))
                .onErrorResume(e -> {
                    System.err.println("Erro ao buscar ações: " + e.getMessage());
                    return Mono.just(Collections.emptyList());
                });
    }

    private List<Stocks> parseStockData(String jsonBody, String symbol) {
        List<Stocks> stockList = new ArrayList<>();
        try {
            JsonNode root = objectMapper.readTree(jsonBody);
            JsonNode timeSeries = root.path("Time Series (Daily)");

            if (timeSeries.isMissingNode()) {
                return Collections.emptyList();
            }

            Iterator<String> fieldNames = timeSeries.fieldNames();
            while (fieldNames.hasNext()) {
                String date = fieldNames.next();
                JsonNode data = timeSeries.get(date);

                Stocks stock = new Stocks();
                stock.setName(symbol);
                stock.setDate(date);
                stock.setOpenPrice(data.path("1. open").asDouble());
                stock.setHighPrice(data.path("2. high").asDouble());
                stock.setClosePrice(data.path("4. close").asDouble());
                
                stockList.add(stock);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return stockList;
    }
    
    public String convertStocksToString(List<Stocks> stocks) {
        if(stocks.isEmpty()) return "Sem dados históricos de preço.";
        
        StringBuilder sb = new StringBuilder("\n--- Histórico de Preços (Últimos dias) ---\n");
        stocks.stream().limit(5).forEach(s -> 
            sb.append(String.format("Data: %s | Fechamento: %.2f\n", s.getDate(), s.getClosePrice()))
        );
        return sb.toString();
    }
}