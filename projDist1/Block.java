import java.util.concurrent.*;
import java.util.*;

public class Block {

    private static final int THREAD_POOL_SIZE = 10;
    private static final int NUM_TX = 50;


    public Block() {
        // Construtor vazio
    }

    public void addTransactionToMempool(ConcurrentLinkedQueue<Transaction> mempool, Transaction tx) {
        mempool.add(tx);
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

    /* 
    public static void main(String[] args) throws InterruptedException {
        ExecutorService executor = Executors.newFixedThreadPool(THREAD_POOL_SIZE);

        ConcurrentLinkedQueue<Transaction> mempool = new ConcurrentLinkedQueue<>();
        PriorityBlockingQueue<Transaction> minerQueue = new PriorityBlockingQueue<>();

        // Threads simulando usuários enviando transações
        for (int i = 0; i < NUM_TX; i++) {
            int id = i;
            executor.submit(() -> {
                Transaction tx = new Transaction(
                    "Wallet" + id,
                    "Wallet" + (id + 1),
                    Math.random() * 10,
                    1 + Math.random() * 5  // fee aleatória
                );
                mempool.add(tx);
                System.out.println("Nova TX recebida: " + tx);
            });
        }

        executor.shutdown();
        executor.awaitTermination(5, TimeUnit.SECONDS);

        // Minerador consome da mempool e joga no heap
        while (!mempool.isEmpty()) {
            minerQueue.add(mempool.poll());
        }

        System.out.println("\n--- Ordem final de mineração (maior fee primeiro) ---");
        while (!minerQueue.isEmpty()) {
            System.out.println(minerQueue.poll());
        }
    }

    */
}