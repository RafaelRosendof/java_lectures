package com.ms2.ms2.DAO;

import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import com.ms2.ms2.Entity.News;

public interface NewsInterface extends ReactiveCrudRepository<News, Integer> {

    /*
     * basic crud
     */
    
    Mono<News> findByStockId(int id);

    Mono<News> findByTitle(String title);

    Flux<News> findAllByFont(String font);

    // more latter


    
}