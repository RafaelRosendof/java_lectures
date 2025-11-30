package com.eureka.ms2.Communication;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.eureka.ms2.Service.NewsService;
import com.eureka.ms2.Service.StocksService;
import com.eureka.ms2.redis.RedisImperativeService;

import io.github.resilience4j.ratelimiter.annotation.RateLimiter;
import io.micrometer.core.annotation.Timed;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;



@RestController
@RequestMapping("/db")
public class RestCommunication{

    private final StocksService stocksService;
    private final NewsService newsService;
    private final RedisImperativeService redisService;

    @Autowired
    public RestCommunication(StocksService stocksService, NewsService newsService, RedisImperativeService redisService) {
        this.stocksService = stocksService;
        this.newsService = newsService;
        this.redisService = redisService;
    }

    @Timed(value = "db.stocks.recordData", description = "Tempo gasto para salvar stocks no Postgres")
    @PostMapping("/requestStock")
    public String requestStock(@RequestBody String stockName) {
        redisService.recordStockRequest(stockName);
        return "Stock request for " + stockName + " recorded.";
    }
    
    @RateLimiter(name = "dbRequestLimiter", fallbackMethod = "fallbackRequestStock")
    @Timed(value = "db.stocks.processData", description = "Tempo gasto para salvar stocks no Postgres")
    @GetMapping("/processData")
    public List<String> processRedis(@RequestParam String param) {
        List<String> topRequests = redisService.getTop2Request();
        System.out.println("Top 2 stock requests: " + topRequests.toString());
        stocksService.stopRedis();
        return topRequests;
    }
    
}