import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.lang.Runnable;

public class threads {

    public static void main(String[] args){

        Thread t = new Thread(new Runnable() {
            public void run() {
                System.out.println("Hello from a thread!");
            }
        });

        Thread t2 = new Thread(() -> System.out.println("Fala figas, aqui é uma thread)"));

        t.start();
        t2.start();

        System.out.println("Testing some more features!");

        ExecutorService executorService = Executors.newCachedThreadPool();

        Future<String> t3 = executorService.submit(() -> Thread.currentThread().getName()); 


    }

}