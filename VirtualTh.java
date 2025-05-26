
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicLong;
import java.util.Timer;

public class VirtualTh {
    public static void main(String[] args) throws InterruptedException, IOException {
        // Create a virtual thread per task


        String csvFile = "testeVirtual.csv";
        
        // Create the CSV file if it doesn't exist
        if (!new File(csvFile).exists()) {
            WriteCSV(csvFile);
        }

        final int totalLines = 100_000_000;
        final int chunkSize = 1_000_000;
        final int subChunkSize = 1_000;
        final int chunks = totalLines / chunkSize;

        //We gonna create 100 threads and each thread, each thread gonna read 1_000_000 lines, in each thread gonna create a virtual thread to read 100_000 lines so //
        // we gonna crate 100 theads and each thread gonna create 100 virtual threads to read 1_000_000 lines, so we gonna create 10_000 virtual threads to read 100_000 lines//
        // and in the end gonna return the sum of all the lines read//

        // Using a thread-safe counter for the sum
        AtomicLong totalSum = new AtomicLong(0);
        CountDownLatch mainLatch = new CountDownLatch(chunks);

        long startTime = System.nanoTime();

        try( var executor = Executors.newVirtualThreadPerTaskExecutor()){

            for(int chunk = 0 ; chunk < chunks ; chunk++){
                final int startLine = chunk * chunkSize;
                final int endLine = startLine + chunkSize;

                
                executor.submit( () -> {

                    try{
                        CountDownLatch subLatch = new CountDownLatch(chunkSize / subChunkSize);

                        try(var subExecutor = Executors.newVirtualThreadPerTaskExecutor()){
                            for(int subChunk = 0 ; subChunk < chunkSize / subChunkSize ; subChunk++ ){
                                final int subStartLine = startLine + (subChunk * subChunkSize);
                                final int subEndLine = startLine + subChunkSize;

                                subExecutor.submit(() -> {
                                    try{
                                        long localSum = processLines(csvFile, subStartLine, subEndLine);
                                        totalSum.addAndGet(localSum);
                                    }catch( IOException | InterruptedException e){
                                        e.printStackTrace();
                                    }finally{
                                        subLatch.countDown();
                                    }
                                });
                            }
                        }

                        subLatch.await();

                    }catch( InterruptedException e){
                        Thread.currentThread().interrupt();
                        e.printStackTrace();
                    }finally{
                        mainLatch.countDown();
                    }
                });
            }

            mainLatch.await();
        }

        long endTime = System.nanoTime();

        long total = endTime - startTime;

        System.out.println("Total time of computing is "+ total);
        System.out.println("Total sum of squares: " + totalSum.get());

    }




    public static void thTest(int x){
       // System.out.println("Num " + x + " thread: " + Thread.currentThread());
        try {
            
            VthLambda.lambda(x);
            
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }


    public static void WriteCSV(String csv_out) throws IOException {
        File file = new File(csv_out);
        file.createNewFile();

        try (FileWriter fileWriter = new FileWriter(file)) {
            fileWriter.write("Num\n");
            for (int i = 0; i < 100_000_000; i++) {
                fileWriter.write(i / 10000 + "\n");
            }
        }
        System.out.println("CSV file created: " + csv_out);
    }


    private static long processLines(String csvFile, int startLine, int endLine) throws IOException , InterruptedException{
        long sum = 0;
        try (BufferedReader br = new BufferedReader(new FileReader(csvFile))) {
            String line;
            int currentLine = 0;
            
            // Read through the file line by line
            while ((line = br.readLine()) != null && currentLine < endLine) {
                if (currentLine >= startLine) {
                    try {
                        // Skip header line
                        if (!line.equals("Num")) {
                            int num = Integer.parseInt(line.trim());
                            sum += lambda(num);
                        }
                    } catch (NumberFormatException e) {
                        System.err.println("Error parsing line " + currentLine + ": " + line);
                    }
                }
                currentLine++;
            }
        }
        return sum;
    }

    public static int lambda(int x) throws InterruptedException {
        int x2 = x * x;
        //System.out.println("Lambda Num " + x + " thread: " + Thread.currentThread());
        //System.out.println("Num " + x + "res " + x2);
        return x2;
    }
}

/*
 *     static void doWork(int id) {
        // Simulate a light task, e.g., logging or a mock HTTP call
        System.out.println("Task " + id + " is running on " + Thread.currentThread());
        try {
            Thread.sleep(10); // simulate IO delay
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }



        try (var executor = java.util.concurrent.Executors.newVirtualThreadPerTaskExecutor()) {
            
            for(int i = 0 ; i < 100_000 ; i++){
                int taskId = i;
                executor.submit(() -> {
                    thTest(taskId);
                });
            }

        } 
 */
