// Gonna have the jar lib 
/*
 * Here gonna implement the @Post based on my jar lib 
 */

import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.PriorityBlockingQueue;
import java.util.List;
import java.util.ArrayList;

public class Miner {
    
    private PriorityBlockingQueue<Transaction> mempool = new PriorityBlockingQueue<>();

    private final List<Block> blockchain = new CopyOnWriteArrayList<>();

    public Miner() {
        
    }

    public void reciveTransaction(Transaction tx) {
        mempool.add(tx);
        System.out.println("[Miner] Transação recebida e adicionada à mempool. Tamanho atual da mempool: " + mempool.size());
    }

    public void performMining() {
        System.out.printf("[Miner] Condição de mineração atingida. Tamanho da Mempool: %d. Iniciando...\n", mempool.size());

        List<Transaction> transactionsToMine = new ArrayList<>();
        mempool.drainTo(transactionsToMine, 400);

        if(transactionsToMine.isEmpty()){
            return;
        }

        System.out.println("[Miner] Minerando bloco com " + transactionsToMine.size() + " transações de mais alta prioridade.");

        long nextBlockId = blockchain.isEmpty() ? 1 : blockchain.get(blockchain.size() - 1).getId() + 1;
        Block newBlock = new Block(nextBlockId, transactionsToMine);
        blockchain.add(newBlock);   

        System.out.println("[Miner] Bloco minerado com sucesso! ID do bloco: " + newBlock.getId());

    }

    public List<Block> getBlockchain() {
        return blockchain;
    }

    public String handleAddTransaction(String payload){

        try{

            String[] fields = payload.split(";");
            if (fields.length != 4) {
                return "ERRO: Payload da transação mal formatado.";
            }

            String sender = fields[0];
            String to = fields[1];
            double value = Double.parseDouble(fields[2]);
            double fee = Double.parseDouble(fields[3]);

            Transaction tx = new Transaction(sender, to, value, fee);
            reciveTransaction(tx);
            return "Transação adicionada com sucesso.";

        }catch(Exception e){
            return "ERRO: Falha ao processar transação no Miner - " + e.getMessage();
        }
    }


} 