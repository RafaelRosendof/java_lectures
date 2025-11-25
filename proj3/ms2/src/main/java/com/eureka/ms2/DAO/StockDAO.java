package com.eureka.ms2.DAO;

import org.springframework.stereotype.Repository;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;
import com.eureka.ms2.Entity.StockEntity;

@Repository
public interface StockDAO extends JpaRepository<StockEntity, Integer> {
    
    //StockEntity findByNews_id(int news_id);
    //List<StockEntity> findAllByNews_id(int news_id);
    StockEntity findByName(String name);
    
    List<StockEntity> findAllByDate(String date);

    
    List<StockEntity> findAllByName(String symbol);
}