package com.eureka.mcpServer.Service;

import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;
import org.springframework.beans.factory.annotation.Value;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import org.springframework.stereotype.Service;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.net.URI;
import java.util.Collections;


import java.net.URI;
//import lombok.Value;
import java.util.Map;

@Service
public class AlphaVantage{
    
    private final String apiKey = "SUA_KEY_AQUI"; 

    public String buildApiUrl(String typeOfTime, String symbol, String outputsize) {
        return "https://www.alphavantage.co/query?function=" + typeOfTime + 
               "&symbol=" + symbol + 
               "&outputsize=" + outputsize + 
               "&apikey=" + apiKey + 
               "&datatype=json";
    }

    public String getStockHistory(String symbol) {
        String url = buildApiUrl("TIME_SERIES_DAILY", symbol, "compact");
        try {
            HttpClient client = HttpClient.newHttpClient();
            HttpRequest request = HttpRequest.newBuilder().uri(URI.create(url)).GET().build();
            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
            
            if (response.statusCode() != 200) {
                return "Erro na API Alpha Vantage: " + response.body();
            }
            
            return processStockDataForAI(response.body(), symbol);
            
        } catch (Exception e) {
            e.printStackTrace();
            return "Erro interno ao buscar ações: " + e.getMessage();
        }
    }

    private String processStockDataForAI(String body, String symbol) {
        if (body == null || body.isEmpty()) return "Nenhum dado encontrado.";

        Gson gson = new Gson();
        JsonObject jsonObject = gson.fromJson(body, JsonObject.class);

        if (!jsonObject.has("Time Series (Daily)")) {
            return "Erro: A API não retornou a série diária. Verifique o limite da chave ou o ticker.";
        }

        JsonObject timeSeries = jsonObject.getAsJsonObject("Time Series (Daily)");
        StringBuilder sb = new StringBuilder();
        sb.append("Histórico recente para ").append(symbol).append(":\n");

        // Pegar apenas os 5 últimos dias para não estourar o contexto do GPT
        int count = 0;
        for (String date : timeSeries.keySet()) {
            if (count >= 5) break; 
            
            JsonObject data = timeSeries.getAsJsonObject(date);
            double close = data.get("4. close").getAsDouble();
            double open = data.get("1. open").getAsDouble();
            
            sb.append(String.format("- Data: %s | Fechamento: %.2f | Abertura: %.2f\n", date, close, open));
            count++;
        }
        return sb.toString();
    }

    public String getNews(String symbol) {
        String url = "https://www.alphavantage.co/query?function=NEWS_SENTIMENT&tickers=" + symbol + "&apikey=" + apiKey;
        
        try {
            HttpClient client = HttpClient.newHttpClient();
            HttpRequest request = HttpRequest.newBuilder().uri(URI.create(url)).GET().build();
            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

            if (response.statusCode() == 200) {
                 // Aqui você pode reusar seu método 'parseAndPrintNews' se quiser formatar
                 return "Notícias brutas (resumido): " + response.body().substring(0, Math.min(response.body().length(), 500)) + "...";
            }
        } catch (Exception e) {
            return "Erro ao buscar notícias.";
        }
        return "Sem notícias.";
    }
}