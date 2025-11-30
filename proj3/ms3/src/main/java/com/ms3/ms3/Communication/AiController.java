package com.ms3.ms3.Communication;

import com.ms3.ms3.Service.AiOrchestratorService;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/ai")
public class AiController {

    private final AiOrchestratorService aiService;

    public AiController(AiOrchestratorService aiService) {
        this.aiService = aiService;
    }

    // Endpoint que o MS1 vai chamar
    @GetMapping("/analyze")
    public String getAnalysis() {
        return aiService.analyzeEconomy();
    }
}
