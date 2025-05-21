public class VirtualTh {
    public static void main(String[] args) throws InterruptedException {
        // Create a virtual thread per task
        try (var executor = java.util.concurrent.Executors.newVirtualThreadPerTaskExecutor()) {
            
            /* 
            for (int i = 0; i < 1000; i++) {
                int taskId = i;
                executor.submit(() -> {
                    doWork(taskId);
                });
            }

            */
            for(int i = 0 ; i < 10_000_000 ; i++){
                int taskId = i;
                executor.submit(() -> {
                    thTest(taskId);
                });
            }

        } 
    }

    static void doWork(int id) {
        // Simulate a light task, e.g., logging or a mock HTTP call
        System.out.println("Task " + id + " is running on " + Thread.currentThread());
        try {
            Thread.sleep(10); // simulate IO delay
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }


    public static void thTest(int x){
       // System.out.println("Num " + x + " thread: " + Thread.currentThread());
        try {
            
            VthLambda.lambda(x);
            
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }
}


class VthLambda {
    public static Integer lambda(int x) throws InterruptedException {
        int x2 = x * x;
        //System.out.println("Lambda Num " + x + " thread: " + Thread.currentThread());
        System.out.println("Num " + x + "res " + x2);
        return x2;
    }
}