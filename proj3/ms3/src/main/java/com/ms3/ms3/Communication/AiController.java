package com.ms3.ms3.Communication;

import com.ms3.ms3.Service.AiOrchestratorService;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/ai")
public class AiController {

    private final AiOrchestratorService aiService;

    public AiController(AiOrchestratorService aiService) {
        this.aiService = aiService;
    }

    // Endpoint que o MS1 vai chamar
    @PostMapping("/analyze")
    public String getAnalysis(@RequestBody String prompt) {
        return aiService.analyzeEconomy(prompt);
    }
}
