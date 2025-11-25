package com.ms3.ms3.Config;

import org.springframework.ai.mcp.client.McpClient;
import org.springframework.ai.mcp.client.transport.ServerSentEventMcpTransport;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestClient;
import org.springframework.web.client.RestTemplate;

import java.time.Duration;

@Configuration
public class AppConfig {

    // --- PARTE 1: O RestTemplate para seu Banco de Dados (GraphQL) ---
    // Essencial para o OpenAiService não quebrar
    @Bean(name = "internalRestTemplate")
    public RestTemplate internalRestTemplate(RestTemplateBuilder builder) {
        return builder
                .setConnectTimeout(Duration.ofSeconds(5))
                .setReadTimeout(Duration.ofSeconds(5))
                .build();
    }

    // --- PARTE 2: Clientes MCP (Seus Braços) ---
    
    // Cliente para o MS4 (AlphaVantage - Preços)
    @Bean
    public McpClient alphaVantageClient(RestClient.Builder builder) {
        var transport = new ServerSentEventMcpTransport(builder.baseUrl("http://localhost:8084/stocksInfo").build());
        var client = McpClient.sync(transport).build();
        client.initialize();
        return client;
    }

    // Cliente para o MS5 (Seu Scraping - Notícias)
    @Bean
    public McpClient scrapingClient(RestClient.Builder builder) {
        var transport = new ServerSentEventMcpTransport(builder.baseUrl("http://localhost:8085/newsApi").build());
        var client = McpClient.sync(transport).build();
        client.initialize();
        return client;
    }
}