import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadLocalRandom;
import java.util.concurrent.TimeUnit;
import java.util.Random;

public class DSA {

    private static Random random;

    /*
     * Vamos criar duas estruturas de dados:
     * 1 -> ConcurrentLinkedQueue<Integer> queue
     * 2 -> HeapSort<Integer> heapSort
     */

    private static final int NUM_REQUESTS = 1000;

    private static final int THREAD_POOL_SIZE = 100;



    public static void addToQueue(ConcurrentLinkedQueue<Integer> queue, Integer value) {
        queue.add(value);
    }

    public static void removeFromQueue(ConcurrentLinkedQueue<Integer> queue) {
        queue.poll();
    }

    // Gonna simulate a multiple adds in a concurrent env 

    public static void main(String[] args){
        random = new Random();
        ConcurrentLinkedQueue<Integer> queue = new ConcurrentLinkedQueue<>();

        ExecutorService executor = Executors.newFixedThreadPool(THREAD_POOL_SIZE);


        queue.add(1);
        queue.add(2);
        queue.add(3);
        queue.add(4); 

        System.out.println("Queue contents: " + queue);

        // Gonna simulate multiples adds in a concurrent env

        for (int i = 0; i < NUM_REQUESTS; i++) {
            final int valueToAdd = i;
            
            
            Runnable addTask = () -> {
                addToQueue(queue, valueToAdd * ThreadLocalRandom.current().nextInt(100));
                System.out.println("Thread '" + Thread.currentThread().getName() + "' adicionou o valor: " + valueToAdd);
            };
            
            executor.submit(addTask);
        }

        executor.shutdown();

        try{

            if( !executor.awaitTermination(1, TimeUnit.MINUTES) ){
               System.err.println("Timeout: Executor did not terminate in the specified time.");
               executor.shutdownNow();
            }
        } catch (InterruptedException e) {
            System.err.println("Thread was interrupted: " + e.getMessage());
            executor.shutdownNow();
        }


        System.out.println("Final queue contents: " + queue);
       // System.out.println("Final sorted contents: " + heapSort.sort(queue));

    }


}
