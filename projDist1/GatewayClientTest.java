import java.io.*;
import java.net.*;
import java.util.concurrent.*;

public class GatewayClientTest {

    private static final int THREAD_POOL_SIZE = 20;
    private static final int NUM_REQUESTS = 50;

    public static void main(String[] args) throws InterruptedException {
        ExecutorService executor = Executors.newFixedThreadPool(THREAD_POOL_SIZE);

        // várias requisições POST simulando transações
        for (int i = 0; i < NUM_REQUESTS; i++) {
            int id = i;
            executor.submit(() -> {
                try {
                    sendPostTransaction("Wallet" + id, "Wallet" + (id + 1),
                            Math.random() * 100, 1 + Math.random() * 10);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            });
        }

        executor.shutdown();
        executor.awaitTermination(10, TimeUnit.SECONDS);

        // depois de enviar transações, consultar o bloco
        try {
            System.out.println("\n--- GET /block ---");
            sendGet("/block");

            System.out.println("\n--- GET /mine ---");
            sendGet("/mine");

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    // envia POST para /transaction
    private static void sendPostTransaction(String from, String to, double value, double fee) throws IOException {
        URL url = new URL("http://localhost:8080/transaction");
        HttpURLConnection con = (HttpURLConnection) url.openConnection();
        con.setRequestMethod("POST");
        con.setDoOutput(true);

        String json = String.format("{\"from\":\"%s\",\"to\":\"%s\",\"value\":%.2f,\"fee\":%.2f}",
                from, to, value, fee);

        byte[] input = json.getBytes("utf-8");
        con.setFixedLengthStreamingMode(input.length);
        con.setRequestProperty("Content-Type", "application/json; charset=UTF-8");

        try (OutputStream os = con.getOutputStream()) {
            os.write(input);
        }

        try (BufferedReader br = new BufferedReader(new InputStreamReader(con.getInputStream(), "utf-8"))) {
            StringBuilder response = new StringBuilder();
            String line;
            while ((line = br.readLine()) != null) {
                response.append(line.trim());
            }
            System.out.println("Resposta POST (" + from + "→" + to + "): " + response);
        }
    }

    // envia GET para qualquer rota
    private static void sendGet(String path) throws IOException {
        URL url = new URL("http://localhost:8080" + path);
        HttpURLConnection con = (HttpURLConnection) url.openConnection();
        con.setRequestMethod("GET");

        try (BufferedReader br = new BufferedReader(new InputStreamReader(con.getInputStream(), "utf-8"))) {
            String line;
            while ((line = br.readLine()) != null) {
                System.out.println(line);
            }
        }
    }
}
