package com.eureka.ms2.DAO;


import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.eureka.ms2.Entity.NewsEntity;

@Repository
public interface NewsDAO extends JpaRepository<NewsEntity, Integer> {

    //List<NewsEntity> findAllByStock_id(int stock_id);
    NewsEntity findByTitle(String title);
    //NewsEntity findByStock_id(int stock_id);

    NewsEntity findByUrl(String url);


}