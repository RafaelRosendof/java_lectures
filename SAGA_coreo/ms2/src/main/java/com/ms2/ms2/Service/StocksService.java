package com.ms2.ms2.Service;

import org.springframework.stereotype.Service;

import com.ms2.ms2.Aux.NewsCollector;
import com.ms2.ms2.Aux.StockCollect;
import com.ms2.ms2.DAO.NewsInterface;
import com.ms2.ms2.DAO.StockInterface;
import com.ms2.ms2.Entity.Stocks;


import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Collections;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;


@Service
public class StocksService {

    public StockInterface stockInterface;
    public NewsInterface newsInterface;
    public NewsService newsService;
    public StockCollect stockCollect;
    public NewsCollector newsCollector;

    public String path = "/home/rafael/reativas_p3/top_requests.txt";
    public String outputPath = "/home/rafael/reativas_p3/analysis_report.txt";

    @Autowired
    public StocksService(StockInterface stockInterface , NewsInterface newsInterface, NewsService newsService, StockCollect stockCollect, NewsCollector newsCollector) {
        this.stockInterface = stockInterface;
        this.newsInterface = newsInterface;
        this.newsService = newsService;
        this.stockCollect = stockCollect;
        this.newsCollector = newsCollector;
    }


    public Mono<Stocks> findById(int id) {
        return stockInterface.findById(id);
    }
    

    public Mono<Stocks> addStock(Stocks stock) {
        return stockInterface.save(stock);
    }



    public Flux<Stocks> getAllStocks() {
        return stockInterface.findAll();
    }

    public void saveStocksToPostgres(Flux<Stocks> stocksList) {
        stockInterface.saveAll(stocksList).subscribe(); // the method call a stream in the argument, maybe need to change it latter 
    }



    public Mono<String> createAllStock(String stockName){
        
        
        Mono<List<Stocks>> stocks = stockCollect.collectStockHistory(stockName);

        return stocks.flatMap(stockList -> {
            System.out.println("Collected " + stockList.size() + " stock entries for " + stockName);
            for (Stocks stock : stockList) {
                System.out.println("Saving stock entry: " + stock.getDate() + " for " + stock.getName());
                stockInterface.save(stock);
            }
            System.out.println("Stocks saved to the database.");

            if (stockList.isEmpty()) {
                return Mono.just("No stock data found for symbol: " + stockName);
            } else {
                return Mono.just("Stock data for " + stockName + " has been collected and saved. Total entries: " + stockList.size());
            }
        });
    }

    public List<String> readFile(String filePath) {
        try {
            return Files.readAllLines(Paths.get(filePath));
        } catch (IOException e) {
            e.printStackTrace();
            return Collections.emptyList();
        }
    }

    public Mono<String> getAnalysis(){
        List<String> stockNames = readFile(path);
        Mono<List<Stocks>> stocks = stockCollect.collectStockHistory(stockNames.get(0));
        Mono<List<Stocks>> stocks2 = stockCollect.collectStockHistory(stockNames.get(1));


        Mono<String> news1 = newsCollector.getNewsSummary(stockNames.get(0));
        Mono<String> news2 = newsCollector.getNewsSummary(stockNames.get(1));

        return Mono.zip(stocks, stocks2, news1, news2)
            .flatMap(tuple -> {
                List<Stocks> stockList1 = tuple.getT1();
                List<Stocks> stockList2 = tuple.getT2();
                String newsSummary1 = tuple.getT3();
                String newsSummary2 = tuple.getT4();

                StringBuilder analysisReport = new StringBuilder();
                analysisReport.append("Análise para ").append(stockNames.get(0)).append(":\n");
                analysisReport.append("Total de entradas de ações coletadas: ").append(stockList1.size()).append("\n");
                analysisReport.append(newsSummary1).append("\n\n");

                analysisReport.append("Análise para ").append(stockNames.get(1)).append(":\n");
                analysisReport.append("Total de entradas de ações coletadas: ").append(stockList2.size()).append("\n");
                analysisReport.append(newsSummary2).append("\n");

                return Mono.just(analysisReport.toString());
            });
    }
    public Mono<String> writeData() {
        return getAnalysis().flatMap(analysis -> Mono.fromCallable(() -> {
            Files.write(Paths.get(outputPath), analysis.getBytes());
            return "Dados escritos com sucesso em " + outputPath;
        })).onErrorResume(e -> {
            System.err.println("Erro ao escrever dados em " + outputPath + ": " + e.getMessage());
            return Mono.just("Erro ao escrever dados: " + e.getMessage());
        });
    }

}
/*
public Mono<String> writeData(Mono<String> data, String filePath){
        Mono<String> data = getAnalysis();
        return Mono.fromCallable(() -> {
            Files.write(Paths.get(filePath), data.getBytes());
            return "Dados escritos com sucesso em " + filePath;
        }).onErrorResume(e -> {
            System.err.println("Erro ao escrever dados em " + filePath + ": " + e.getMessage());
            return Mono.just("Erro ao escrever dados: " + e.getMessage());
        });
    }
*/