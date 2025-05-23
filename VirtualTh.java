public class VirtualTh {
    public static void main(String[] args) throws InterruptedException {
        // Create a virtual thread per task
        try (var executor = java.util.concurrent.Executors.newVirtualThreadPerTaskExecutor()) {
            for (int i = 0; i < 1000; i++) {
                int taskId = i;
                executor.submit(() -> {
                    doWork(taskId);
                });
            }
        } // Automatically shuts down and waits for all tasks
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
}
