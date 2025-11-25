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

    // 1. Legado: Banco de Dados (GraphQL)
    @Bean(name = "internalRestTemplate")
    public RestTemplate internalRestTemplate(RestTemplateBuilder builder) {
        return builder
                .setConnectTimeout(Duration.ofSeconds(5))
                .setReadTimeout(Duration.ofSeconds(5))
                .build();
    }

    // 2. TERCEIROS: yfinance (Preço e Notícias)
    // Esse é o container que você baixou pronto.
    @Bean
    public McpClient yfinanceClient(RestClient.Builder builder) {
        // Supondo que você rodou o docker do yfinance na porta 8084 com suporte a SSE
        var transport = new ServerSentEventMcpTransport(
            builder.baseUrl("http://localhost:8084/sse").build()
        );
        var client = McpClient.sync(transport).build();
        client.initialize();
        return client;
    }

    // 3. PRÓPRIO/SERVERLESS: Alpha Vantage (Indicadores Técnicos)
    // Esse é o seu código Serverless rodando na porta 8085
    @Bean
    public McpClient alphaVantageServerlessClient(RestClient.Builder builder) {
        var transport = new ServerSentEventMcpTransport(
            builder.baseUrl("http://localhost:8085/sse").build()
        );
        var client = McpClient.sync(transport).build();
        client.initialize();
        return client;
    }
}
// link do docker https://mcpservers.org/servers/Otman404/finance-mcp-server