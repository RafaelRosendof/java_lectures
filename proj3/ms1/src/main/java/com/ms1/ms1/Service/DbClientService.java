package com.ms1.ms1.Service;

import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import java.util.List;

@Service
public class DbClientService {

    private final RestTemplate restTemplate;
    private final String MS2_URL = "http://localhost:8082/db/";

    @Autowired
    public DbClientService(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }

    @CircuitBreaker(name = "dbService", fallbackMethod = "fallbackSave")
    public void recordStock(String action) {
        String MS2_URL = this.MS2_URL + "requestStock";
        try {
            restTemplate.postForObject(MS2_URL, action, String.class);
        } catch (Exception e) {
            System.err.println("Falha ao salvar log no MS2: " + e.getMessage());
        }
    }

    @CircuitBreaker(name = "dbService", fallbackMethod = "fallbackSave")
    public List<String> processRedisData(){
        //String MS2_URL = this.MS2_URL + "processData";
        String urlFinal = this.MS2_URL.endsWith("/") ? this.MS2_URL + "processData" : this.MS2_URL + "/processData";
        try{
            System.out.println("Solicitando processamento de dados Redis no MS2...");
            List<String> results = restTemplate.getForObject(urlFinal, List.class);
            System.out.println("Dados processados do Redis no MS2: " + results.toString());
            return results;
        }catch(Exception e){
            System.err.println("Falha ao processar dados Redis no MS2: " + e.getMessage());
            return List.of();
        }
    }
    
    public void fallbackSave(String action, Throwable t) {
        System.out.println("⚠️ Banco de dados indisponível. Ação não registrada: " + action);
    }
}