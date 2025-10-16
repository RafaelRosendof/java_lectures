// File: HttpServer.java
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class HttpServer implements ComponentServer {

    private ServerSocket serverSocket;
    private volatile boolean running = false;
    private ExecutorService threadPool;
    private static final int THREAD_POOL_SIZE = 50;

    @Override
    public void start(int port, RequestHandler handler) throws Exception {
        serverSocket = new ServerSocket(port);
        threadPool = Executors.newFixedThreadPool(THREAD_POOL_SIZE);
        // espécie de semáforo para limitar o número de threads
        running = true;
        System.out.println("Servidor HTTP (custom) escutando na porta " + port);

        new Thread(() -> {
            while (running) {
                try {
                    Socket clientSocket = serverSocket.accept();
                    threadPool.submit(() -> handleClient(clientSocket, handler));
                } catch (IOException e) {
                    if (running) {
                        System.err.println("Erro no servidor HTTP: " + e.getMessage());
                    }
                }
            }
        }).start();
    }

    private void handleClient(Socket clientSocket, RequestHandler handler) {
        try (
            BufferedReader reader = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
            OutputStream out = clientSocket.getOutputStream()
        ) {
            //  linha de requisição (ex: "POST /ADD_TRANSACTION HTTP/1.1")
            String requestLine = reader.readLine();
            if (requestLine == null || requestLine.isEmpty()) {
                return;
            }
            String[] requestParts = requestLine.split(" ");
            String method = requestParts[0];
            String path = requestParts[1];

            // extrair o comando do path (removendo a "/")
            String command = path.startsWith("/") ? path.substring(1) : path;

            //  os cabeçalhos (headers) até encontrar uma linha em branco
            Map<String, String> headers = new HashMap<>();
            String headerLine;
            while ((headerLine = reader.readLine()) != null && !headerLine.isEmpty()) {
                String[] headerParts = headerLine.split(": ", 2);
                if (headerParts.length == 2) {
                    headers.put(headerParts[0], headerParts[1]);
                }
            }

            // corpo (body) da requisição
            String payload = "";
            if ("POST".equalsIgnoreCase(method)) {
                if (headers.containsKey("Content-Length")) {
                    int contentLength = Integer.parseInt(headers.get("Content-Length"));
                    char[] bodyChars = new char[contentLength];
                    reader.read(bodyChars, 0, contentLength);
                    payload = new String(bodyChars);
                }
            }

            // string no formato que o Gateway espera: "COMMAND|PAYLOAD"
            String gatewayRequest = command + "|" + payload;
            
            
            
            String responsePayload = handler.handle(gatewayRequest);
            byte[] payloadBytes = responsePayload.getBytes("UTF-8");

            //montar e enviar a resposta HTTP
            String httpResponseHeaders = "HTTP/1.1 200 OK\r\n" +
                                     "Content-Type: text/plain; charset=utf-8\r\n" +
                                     "Content-Length: " + payloadBytes.length + "\r\n" +
                                     "\r\n"; // Linha em branco crucial

        // os headers e DEPOIS o corpo (payload) separadamente.
        out.write(httpResponseHeaders.getBytes("UTF-8"));
        out.write(payloadBytes);
        out.flush();

        } catch (IOException e) {
            System.err.println("Erro ao processar requisição HTTP: " + e.getMessage());
        } finally {
            try {
                clientSocket.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    public void stop() {
        running = false;
        // mesmo do tcp
        if (threadPool != null) {
            threadPool.shutdown();
        }
        if (serverSocket != null && !serverSocket.isClosed()) {
            try {
                serverSocket.close();
            } catch (IOException e) {
                System.err.println("Erro ao fechar ServerSocket HTTP: " + e.getMessage());
            }
        }
        System.out.println("Servidor HTTP parado.");
    }
}