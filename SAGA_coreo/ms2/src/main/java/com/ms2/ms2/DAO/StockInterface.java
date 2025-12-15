package com.ms2.ms2.DAO;


import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import com.ms2.ms2.Entity.Stocks;


public interface StockInterface extends ReactiveCrudRepository<Stocks , Integer> {


    Mono<Stocks> findByName(String name);

    Flux<Stocks> findByDate(String date);

    Mono<Stocks> findByNewsId(int newsId);

    Flux<Stocks> findAllByName(String name);

    Mono<Stocks> findByNameAndDate(String name , String date);

    // more latter
    

    
}