package com.ms2.ms2.Aux;


import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.Iterator;
import java.util.ArrayList;
import java.util.Arrays;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;

import com.google.gson.Gson;
import com.google.gson.JsonObject;

import reactor.core.publisher.Mono;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import java.net.URI;
import org.springframework.web.reactive.function.client.WebClient;


@Service
public class NewsCollector {

    private final WebClient webClient;
    private final ObjectMapper objectMapper;

    //@Value("${alphavantage.api.key:NOT_DEFINED}")
    private String apiKey = "P4V6CEV6271X11P3";

    public NewsCollector(WebClient.Builder webClientBuilder, ObjectMapper objectMapper) {
        this.webClient = webClientBuilder.baseUrl("https://www.alphavantage.co").build();
        this.objectMapper = objectMapper;
    }

    public Mono<String> getNewsSummary(String symbol) {
        return webClient.get()
                .uri(uriBuilder -> uriBuilder
                        .path("/query")
                        .queryParam("function", "NEWS_SENTIMENT")
                        .queryParam("tickers", symbol)
                        .queryParam("apikey", apiKey)
                        .build())
                .retrieve()
                .bodyToMono(String.class)
                .map(this::parseAndFormatNews) 
                .onErrorResume(e -> {
                    System.err.println("Erro ao buscar notícias: " + e.getMessage());
                    return Mono.just("Não foi possível coletar notícias para " + symbol);
                });
    }

    private String parseAndFormatNews(String jsonBody) {
        try {
            JsonNode root = objectMapper.readTree(jsonBody);
            JsonNode feed = root.path("feed");

            if (feed.isMissingNode() || feed.isEmpty()) {
                return "Nenhuma notícia relevante encontrada.";
            }

            StringBuilder sb = new StringBuilder();
            sb.append("--- Notícias Recentes para ").append(" ---");

            int limit = Math.min(5, feed.size());
            for (int i = 0; i < limit; i++) {
                JsonNode article = feed.get(i);
                sb.append("\n\nNotícia ").append(i + 1).append(":");
                sb.append("\n  Título: ").append(article.path("title").asText());
                sb.append("\n  Resumo: ").append(article.path("summary").asText());
                sb.append("\n  Sentimento: ").append(article.path("overall_sentiment_label").asText());
            }
            return sb.toString();

        } catch (Exception e) {
            return "Erro ao processar JSON de notícias: " + e.getMessage();
        }
    }
}