import java.io.*;
import java.net.*;

public class TcpServer implements ComponentServer {
    private ServerSocket serverSocket;
    private boolean running = true;

    @Override
    public void start(int port , RequestHandler handler) throws IOException {
        try{

            serverSocket = new ServerSocket(port);
            System.out.println("TCP Server started on port " + port);

            while (running) {
                Socket clientSocket = serverSocket.accept(); //vai ficar bloqueado aqui esperando conexões
                // para cada cliente uma nova thread
                new Thread(() -> handleClient(clientSocket, handler)).start();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void handleClient(Socket socket, RequestHandler handler) {
        try (
            BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            PrintWriter out = new PrintWriter(socket.getOutputStream(), true);
        ) {
            String request;
            while ((request = in.readLine()) != null) { // Leitura linha a linha
                String response = handler.handle(request);
                out.println(response); // Envia resposta de volta
            }
        } catch (IOException e) {
            System.err.println("Erro de comunicação com cliente: " + e.getMessage());
        }
    }


    @Override 
    public void stop() throws IOException {
        running = false;
        try{
            if (serverSocket != null && !serverSocket.isClosed()) {
            serverSocket.close();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}