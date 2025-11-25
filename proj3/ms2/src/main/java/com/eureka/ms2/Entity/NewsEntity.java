package com.eureka.ms2.Entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import jakarta.persistence.GenerationType;


@Entity
@Table(name = "news")
public class NewsEntity {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private int id;

    @Column(name = "font")
    private String font;

    @Column(name = "title")
    private String title;

    @Column(name = "description")
    private String description;

    @Column(name = "url")
    private String url;

    @Column(name = "stock_id")
    //@OneToOne(mappedBy = "stock")
    private int stock_id; //adjust here, foreign key to the stock id

    public NewsEntity() {
    }

    public NewsEntity(int id, String font, String title, String description, String url, int stock_id) {
        this.id = id;
        this.font = font;
        this.title = title;
        this.description = description;
        this.url = url;
        this.stock_id = stock_id;
    }

    public int getId() {
        return id;
    }
    public void setId(int id) {
        this.id = id;
    }
    public String getFont() {
        return font;
    }
    public void setFont(String font) {
        this.font = font;
    
    }
    public String getTitle() {
        return title;
    }
    public void setTitle(String title) {
        this.title = title;
    }
    public String getDescription() {
        return description;
    }
    public void setDescription(String description) {
        this.description = description;
    }
    public String getUrl() {
        return url;
    }
    public void setUrl(String url) {
        this.url = url;
    }
    public int getStock_id() {
        return stock_id;
    }
    public void setStock_id(int stock_id) {
        this.stock_id = stock_id;
    }

    @Override
    public String toString() {
        return "NewsEntity [id=" + id + ", font=" + font + ", title=" + title + ", description=" + description + ", url=" + url
                + ", stock_id=" + stock_id + "]";
    }
}