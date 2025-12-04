package com.ms1.ms1.Communication;

import com.ms1.ms1.Service.AiClientService;
import com.ms1.ms1.Service.DbClientService;
import org.springframework.web.bind.annotation.*;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;


@RestController
@RequestMapping("/api/orchestrator")
public class OrchestratorController {

    private final AiClientService aiService;
    private final DbClientService dbService;

    @Autowired
    public OrchestratorController(AiClientService aiService, DbClientService dbService) {
        this.aiService = aiService;
        this.dbService = dbService;
    }

    @GetMapping("/processRedis")
    public void processRedisData() {
        dbService.processRedisData();
    }

    @PostMapping("/recordStock")
    public String recordStock(@RequestBody String stockData) {
        dbService.recordStock(stockData);
        return "\nStock "+ stockData + " recorded.\n";
    }

    @GetMapping("/analyze")
    public String analyse(){
        List<String> topStocks = dbService.processRedisData();
        System.out.println("Top stocks received for analysis: " + topStocks.toString());
        String finalPrompt = aiService.buildSmartPrompt(topStocks.get(0), topStocks);
        String aiResponse = aiService.getAnalysis(finalPrompt);
        return aiResponse;
    }   
}