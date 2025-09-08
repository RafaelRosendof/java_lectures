import java.util.concurrent.*;
import java.util.*;

public class Block {

    private static final int THREAD_POOL_SIZE = 10;
    private static final int NUM_TX = 50;

    private final long id;
    private final List<Transaction> transactions;
    private final long timestamp;

    public Block(long id, List<Transaction> transactions) {
        this.id = id;
        this.transactions = transactions; // A lista já vem ordenada do Miner
        this.timestamp = System.currentTimeMillis();
    }

    public long getId() {
        return id;
    }

    public List<Transaction> getTransactions() {
        return transactions;
    }

    public void addTransactionToMempool(ConcurrentLinkedQueue<Transaction> mempool, Transaction tx) {
        mempool.add(tx);
    }

    public List<String> printaBlockList(List<PriorityBlockingQueue<Transaction>> blockchains) {
        //vai ser iterativo mesmo
        List<String> result = new ArrayList<>();
        for (int i = 0 ; i < blockchains.size(); i++) {
            StringBuilder sb = new StringBuilder();
            sb.append("Blockchain ").append(i + 1).append(":\n");
            PriorityBlockingQueue<Transaction> blockchain = blockchains.get(i);
            for (Transaction tx : blockchain) {
                sb.append(tx).append("\n");
            }
            result.add(sb.toString());
        }
        return result;
    }

    public String printBlockChain(ConcurrentLinkedQueue<Transaction> mempool) {
        StringBuilder sb = new StringBuilder();
        for (Transaction tx : mempool) {
            sb.append(tx).append("\n");
        }
        return sb.toString();
    }

    public String mineBlock(ConcurrentLinkedQueue<Transaction> mempool) {
        PriorityBlockingQueue<Transaction> minerQueue = new PriorityBlockingQueue<>();

        while (!mempool.isEmpty()) {
            minerQueue.add(mempool.poll());
        }

        StringBuilder sb = new StringBuilder();
        while (!minerQueue.isEmpty()) {
            sb.append(minerQueue.poll()).append("\n");
        }
        return sb.toString();
    }

    
}