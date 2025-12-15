package com.ms2.ms2.Entity;

import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.Table;

@Table("news")
public class News {

    @Id
    private int id;

    @Column("font")
    private String font;
    
    @Column("title")
    private String title;
    
    @Column("description")
    private String description;
    
    @Column("url")
    private String url;

    
    @Column("stock_id")
    private int stockId; 

    public News() {
    }

    public News(int id , String font, String title, String description, String url , int stockId) {
        this.id = id;
        this.font = font;
        this.title = title;
        this.description = description;
        this.url = url;
        this.stockId = stockId;
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

    public int getStockId() {
        return stockId;
    }

    public void setStockId(int stockId) {
        this.stockId = stockId;
    }

    @Override
    public String toString() {
        return "News{" +
                "font='" + font + '\'' +
                ", title='" + title + '\'' +
                ", description='" + description + '\'' +
                ", url='" + url + '\'' +
                '}';

    }
}