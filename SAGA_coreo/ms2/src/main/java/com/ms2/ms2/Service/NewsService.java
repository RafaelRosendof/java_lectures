package com.ms2.ms2.Service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;
import reactor.core.publisher.Flux;

import com.ms2.ms2.Aux.NewsCollector;
import com.ms2.ms2.DAO.NewsInterface;
import com.ms2.ms2.DAO.StockInterface;
import com.ms2.ms2.Entity.News;

@Service
public class NewsService {

    public NewsInterface newsInterface;
    public StockInterface stockInterface;
    public NewsCollector newsCollector;

    @Autowired
    public NewsService(NewsInterface newsInterface , StockInterface stockInterface, NewsCollector newsCollector) {
        this.newsInterface = newsInterface;
        this.stockInterface = stockInterface;
        this.newsCollector = newsCollector;
    }
    
    public Mono<News> findById(int id) {
        return newsInterface.findById(id);
    }

    public Mono<News> addNews(News news) {
        return newsInterface.save(news);
    }


    public Mono<String> createNews(String news){
        return newsCollector.getNewsSummary(news);
    }

    // correct this method and done 
    public Mono<String> getNewsTop1(){

        String top1 = "AMZN";

        Mono<String> news1 = createNews(top1);

        System.out.println("News 1: " + news1);

        return news1;

    }

    public Mono<String> getNewsTop2(){

        String top2 = "APPL";

        Mono<String> news2 = createNews(top2);

        System.out.println("News 2: " + news2);

        return news2;

    }


}