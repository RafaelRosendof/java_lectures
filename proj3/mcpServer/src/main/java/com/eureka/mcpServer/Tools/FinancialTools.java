package com.eureka.mcpServer.Tools;

import org.springframework.ai.tool.annotation.Tool;
import org.springframework.stereotype.Component;
import com.eureka.mcpServer.Service.AlphaVantage;

@Component
public class FinancialTools {
    private final AlphaVantage alphaVantage;

    public FinancialTools(AlphaVantage alphaVantage) {
        this.alphaVantage = alphaVantage;
    }

    // Ferramenta 1: Histórico de Ações
    @Tool(description = "Obtém o histórico diário de preços (abertura e fechamento) de uma ação específica na Alpha Vantage. " +
            "Use esta ferramenta quando precisar analisar a tendência recente de preços. " +
            "Parâmetro obrigatório: symbol (ex: IBM, PETR4.SA, AAPL).")
    public String getStockHistoryTool(String symbol) {
        System.out.println("--- MCP SERVER (Interno): Buscando histórico para " + symbol + " ---");
        return alphaVantage.getStockHistory(symbol);
    }

    // Ferramenta 2: Notícias (Opcional, já que tem o yfinance)
    @Tool(description = "Busca notícias e sentimento de mercado para uma ação específica. " +
            "Use esta ferramenta se precisar de dados de sentimento da Alpha Vantage. " +
            "Parâmetro: symbol.")
    public String getNewsSentimentTool(String symbol) {
        System.out.println("--- MCP SERVER (Interno): Buscando notícias para " + symbol + " ---");
        return alphaVantage.getNews(symbol);
    }
}