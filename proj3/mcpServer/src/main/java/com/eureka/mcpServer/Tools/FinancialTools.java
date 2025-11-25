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

    @Tool(description = "Calcula o índice RSI (Relative Strength Index) de uma ação para análise técnica. " +
            "Use esta ferramenta quando precisar saber se uma ação está 'sobrecomprada' ou 'sobrevendida'. " +
            "Parâmetros: " +
            "ticker (ex: IBM, PETR4.SA), " +
            "interval (ex: weekly, daily, 60min), " +
            "timePeriod (ex: 10, 14, 20), " +
            "seriesType (ex: open, high, low, close).")
            public String calculateRSI(String ticker, String interval, int timePeriod, String seriesType) {
                System.out.println("Calculating RSI for " + ticker);
                return alphaVantage.getRSI(ticker, interval, timePeriod, seriesType);

            }
}