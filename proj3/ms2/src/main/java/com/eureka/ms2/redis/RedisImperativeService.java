package com.eureka.ms2.redis;

import java.time.Duration;
import java.util.ArrayList;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;
import java.util.List;
import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import com.eureka.ms2.Entity.StockEntity;

@Service
public class RedisImperativeService {
    
    private final RedisTemplate<String , String> redisTemplate;
    private static final String LEADERBOARD_KEY = "stock:requests:leaderboard";

    // a requisição do MS1 vem do tipo Eu quero PETR4 ou AAPL e só 
    // o redis serve como um broker de mensagens no final vou coletar somente as ações mais buscadas 

    @Autowired
    public RedisImperativeService(RedisTemplate<String, String> redisTemplate) {
        this.redisTemplate = redisTemplate;
    }

    public Boolean set(String key , String value , Duration ttl) {
        redisTemplate.opsForValue().set(key , value , ttl.toMillis() , TimeUnit.MILLISECONDS);

        return true;
    }

    public String recordStockRequest(String stockName){
        try{
            redisTemplate.opsForZSet().incrementScore(LEADERBOARD_KEY, stockName, 1);
            return "Success";
        } catch (Exception e){
            e.printStackTrace();
            return "Error";
        }
    }

 
    public List<String> getTop2Request(){
        Set<String> topStocks = redisTemplate.opsForZSet().reverseRange(LEADERBOARD_KEY, 0, 1);
        if (topStocks != null) {
            return topStocks.stream().collect(Collectors.toList());
        } else {
            return new ArrayList<>();
        }
    }

    public void clearRedis(){
        redisTemplate.delete(LEADERBOARD_KEY);
    }
    

}