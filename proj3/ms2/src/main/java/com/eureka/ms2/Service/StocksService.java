package com.eureka.ms2.Service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

//import com.eureka.ms2.Aux.StockCollect;
import com.eureka.ms2.DAO.StockDAO;
import com.eureka.ms2.Entity.StockEntity;
import com.eureka.ms2.redis.RedisImperativeService;

import io.micrometer.core.annotation.Timed;

@Service
public class StocksService {

    private final StockDAO stockDAO;
    //private final StockEntity stockEntity;
    private final RedisImperativeService redisService;
    //private final StockCollect stockCollect;
    private final NewsService newsService;
    private final StockEntity stockEntity;

    @Autowired
    public StocksService(StockDAO stockDAO, RedisImperativeService redisService, NewsService newsService) {
        this.stockDAO = stockDAO;
        this.redisService = redisService;
        this.newsService = newsService;
        this.stockEntity = new StockEntity();
    }


    public StockEntity findById(int id) {
        return stockDAO.findById(id).orElse(null);
    }

    @Timed(value = "db.stocks.findAll", description = "Tempo gasto para buscar todas as stocks no banco de dados")
    public List<StockEntity> findAll() {
        return stockDAO.findAll();
    }

    @Timed(value = "db.stocks.save", description = "Tempo gasto para salvar uma stock no banco de dados")
    public StockEntity createStock(StockEntity stock) {
        return stockDAO.save(stock);
    }
    public StockEntity getStockById(int id) {
        return stockDAO.findById(id).orElse(null);
    }

    public void deleteStock(int id) {
        stockDAO.deleteById(id);
    }

    public List<StockEntity> getAllStocks() {
        return stockDAO.findAll();
    }

    public StockEntity getStockByName(String name) {
        return stockDAO.findByName(name);
    }

    public List<String> stopRedis(){
        try{
            System.out.println("\n\n\nIs been here in the stopRedis method of StocksService\n\n\n");

            List<String> topStocks = redisService.getTop2Request();
            if(topStocks == null || topStocks.size() < 2){
                return List.of("Not enough stock requests recorded in Redis.");
            }

            System.out.println("Top requested stocks: " + topStocks);

            for (String stockName : topStocks) {
                StockEntity stockEntity = new StockEntity();
                stockEntity.setName(stockName);
                createStock(stockEntity);
            }

            System.out.println("Data collection and storage completed for top requested stocks.");

            redisService.clearRedis();

            //return "Data collection and storage completed for top requested stocks.";
            return topStocks;
        }catch (Exception e){
            e.printStackTrace();
            return List.of("Erro interno ao processar dados do Redis: " + e.getMessage());
        }
    }

    public List<String> giveBackStocks(){
        System.out.println("Buscando os dados do redis ");
        return stopRedis();
    }

    // latter for the methods of the redis 


    //public String createAllStock(String stockName){
    //    /*
    //     * 1. collect the data from the API, the API is gonna return a lot of json one for each day
    //     * 2. the parser is already done in the StockCollect class
    //     * 3. get the data here and in a for loop create a StockEntity for each day and save it in the database
    //     * 4. return the list of StockEntity
    //     */
    //    
    //    String body = stockCollect.collectData(stockName);
    //    List<StockEntity> stocks = stockCollect.processingData(body, stockName);
//
    //    System.out.println("Collected " + stocks.size() + " stock entries for " + stockName);
    //    for (int i = 0 ; i < stocks.size() ; i++){
    //        System.out.println("Saving stock entry: " + stocks.get(i).getDate() + " for " + stocks.get(i).getName());
    //        stockDAO.save(stocks.get(i));
    //    }
    //    System.out.println("Stocks saved to the database.");
//
    //    if (stocks.isEmpty()) {
    //        return "No stock data found for symbol: " + stockName;
    //    } else {
    //        return "Stock data for " + stockName + " has been collected and saved. Total entries: " + stocks.size();
    //    }
//
    //}

//    public void createAllStock(String stockname){
//        // pegar do redis 
//    }
//
//    public String stopRedis(){
//
//        System.out.println("\n\n\nIs been here in the stopRedis method of StocksService\n\n\n");
//
//        List<String> topStocks = redisService.getTop2Request();
//        
//        if(topStocks == null || topStocks.size() < 2){
//            return "Not enough stock requests recorded in Redis.";
//        }
//        
//        System.out.println("Top requested stocks: " + topStocks);
//
//        for (String stockName : topStocks) {
//            createAllStock(stockName);
//        }
//
//        System.out.println("Data collection and storage completed for top requested stocks.");
//
//        redisService.clearRedis();
//
//        return "Data collection and storage completed for top requested stocks.";
//    }

//    public List<StockEntity> giveBackStock1(){
//        /*
//         * 1. get the top 2 stocks from the redis
//         * 2. get all the data from the database based on a stockName and the stockname is the top of the redis function getTop2Request
//         */
//        System.out.println("\n\n\n Is been here in the giveBackStock1 method of StocksService\n\n\n");
//        String stock1 = redisService.getTop2Request().get(0);
//        System.out.println("Top requested stock 1: " + stock1);
//
//        return stockDAO.findAllByName(stock1);
//    }
//
//    public List<StockEntity> giveBackStock2 (){
//        /*
//         * 1. get the top 2 stocks from the redis
//         * 2. get all the data from the database based on a stockName and the stockname is the top of the redis function getTop2Request
//         */
//        System.out.println("\n\n\n Is been here in the giveBackStock2 method of StocksService\n\n\n");
//        String stock2 = redisService.getTop2Request().get(1);
//        System.out.println("Top requested stock 2: " + stock2);
//
//        return stockDAO.findAllByName(stock2);
//    }

//   // public String giveBackPrompt1(){
//        List<StockEntity> stockList1 = giveBackStock1();
//        
//        StringBuilder promptBuilder = new StringBuilder();
//        promptBuilder.append("Stock Data for ").append(stockList1).append(":\n\n\n");
//        
//        String noticias1 = newsService.getNewsTop1();
//
//        promptBuilder.append("News Data:\n").append(noticias1).append("\n\n");
//
//        return promptBuilder.toString();
//
//    }
//
//   // public String giveBackPrompt2(){
//        List<StockEntity> stockList2 = giveBackStock2();
//        
//        StringBuilder promptBuilder = new StringBuilder();
//        promptBuilder.append("Stock Data for ").append(stockList2).append(":\n\n\n");
//
//        String noticias2 = newsService.getNewsTop2();
//
//        promptBuilder.append("News Data:\n").append(noticias2).append("\n\n");
//
//        return promptBuilder.toString();
//        
//    }
}