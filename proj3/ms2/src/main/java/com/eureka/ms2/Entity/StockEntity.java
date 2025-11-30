package com.eureka.ms2.Entity;

import jakarta.annotation.Generated;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import jakarta.persistence.Id;
import jakarta.persistence.GenerationType;

@Entity
@Table(name = "stock")
public class StockEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private int id;

    @Column(name = "name")
    private String name;

    //@Column(name = "open_price")
    //private double openPrice;
//
    //@Column(name = "close_price")
    //private double closePrice;
//
    //@Column(name = "high_price")
    //private double highPrice;
//
    //@Column(name = "date")
    //private String date;
//
    //@Column(name = "news_id")
    ////@OneToOne(mappedBy = "news")
    //private int news_id; //adjust here, foreign key to the news id

    public StockEntity() {
    }

    public StockEntity(int id, String name/*double openPrice, double closePrice, double highPrice, String date, int news_id*/) {
        this.id = id;
        this.name = name;
        //this.openPrice = openPrice;
        //this.closePrice = closePrice;
        //this.highPrice = highPrice;
        //this.date = date;
        //this.news_id = news_id;
    }

    public int getId() {
        return id;
    }
    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }
    public void setName(String name) {
        this.name = name;
    }
    @Override
    public String toString() {
        return "StockEntity{" +
                "id=" + id +
                ", name='" + name + '\'' +
                '}';
    }
//
//    public double getOpenPrice() {
//        return openPrice;
//    }
//    public void setOpenPrice(double openPrice) {
//        this.openPrice = openPrice;
//    }
//    public double getClosePrice() {
//        return closePrice;
//    }
//    public void setClosePrice(double closePrice) {
//        this.closePrice = closePrice;
//    }
//    public double getHighPrice() {
//        return highPrice;
//    }
//    public void setHighPrice(double highPrice) {
//        this.highPrice = highPrice;
//    }
//    public String getDate() {
//        return date;
//    }
//    public void setDate(String date) {
//        this.date = date;
//    }
//    public int getNews_id() {
//        return news_id;
//    }
//    public void setNews_id(int news_id) {
//        this.news_id = news_id;
//    }
//
//    @Override
//    public String toString() {
//        return "StockEntity{" +
//                "id=" + id +
//                ", name='" + name + '\'' +
//                ", openPrice=" + openPrice +
//                ", closePrice=" + closePrice +
//                ", highPrice=" + highPrice +
//                ", date='" + date + '\'' +
//                ", news_id=" + news_id +
//                '}';
//    }

}