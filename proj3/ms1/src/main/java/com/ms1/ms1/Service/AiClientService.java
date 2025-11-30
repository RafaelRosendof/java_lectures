package com.ms1.ms1.Service;

import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;

@Service
public class AiClientService {

    private final RestTemplate restTemplate;
    // URL do MS3 (Se usar Docker/Eureka, pode ser o nome do serviço)
    private final String MS3_URL = "http://localhost:8083/ai/analyze"; 

    @Autowired
    public AiClientService(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }

    @CircuitBreaker(name = "aiService", fallbackMethod = "fallbackAnalysis")
    public String getAnalysis(String prompt) {
        return restTemplate.postForObject(MS3_URL, prompt, String.class);
    }

    public String fallbackAnalysis(String prompt, Throwable t) {
        return "serviço de Inteligência Artificial está indisponível no momento. " +
               "Erro: " + t.getMessage();
    }

    public String buildSmartPrompt(String userTicker, List<String> trendingStocks) {
        StringBuilder sb = new StringBuilder();
        
        sb.append("Atue como um analista financeiro sênior. ");
        sb.append("O usuário está interessado especificamente na ação: ").append(userTicker).append(". ");
        
        if (!trendingStocks.isEmpty()) {
            sb.append("Além disso, considere no contexto que as ações mais buscadas na plataforma hoje são: ");
            sb.append(String.join(", ", trendingStocks)).append(". ");
        }

        sb.append("Por favor, use suas ferramentas (Stock History e News) para: ");
        sb.append("1. Analisar o preço atual e histórico recente de ").append(userTicker).append(". ");
        sb.append("2. Verificar notícias de sentimento de mercado. ");
        sb.append("3. Se alguma das ações em alta (").append(String.join(",", trendingStocks)).append(") tiver correlação, cite-a. ");
        sb.append("Dê uma recomendação final de Compra, Venda ou Manter.");

        return sb.toString();
    }
}