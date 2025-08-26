import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketTimeoutException;
import java.net.UnknownHostException;
import java.net.ConnectException;

public class TCPClient implements ComponentClient {

    private static final int DEFAULT_TIMEOUT = 5000;
    private static final int MAX_RETRIES = 3;
    private static final int RETRY_DELAY = 1000;


    @Override 
    public String send(String host , int port , String request) throws IOException {
        return sendWithRetry(host, port, request, DEFAULT_TIMEOUT, MAX_RETRIES);
    }


    public String sendWithRetry(String host , int port , String request , int timeoutMS , int maxRetries) throws IOException{

        IOException lastException = null;

        for (int i = 0 ; i <= maxRetries ; i++){
            Socket socket = null;
            BufferedReader reader = null;
            PrintWriter writer = null;

            try{

                socket = new Socket();
                socket.setSoTimeout(timeoutMS);
                socket.connect(new java.net.InetSocketAddress(host, port), timeoutMS);

                // Configura streams
                writer = new PrintWriter(socket.getOutputStream(), true);
                reader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
                
                // Envia requisição
                writer.println(request);
                writer.flush();

                StringBuilder responseBuilder = new StringBuilder();
                String line;

                while((line = reader.readLine()) != null){
                    responseBuilder.append(line);
                    break;
                }

                String response = responseBuilder.toString();

                if(i > 1){
                    System.out.println("[TCPClient] Requisição atendida após " + i + " tentativas.");
                }

                return response;
                
                

            }catch (UnknownHostException e) {
                lastException = new IOException("Host desconhecido: " + host, e);
                System.err.printf("[TCPClient] Host desconhecido %s:%d (tentativa %d)\n", host, port, i);
                break; // Não adianta tentar novamente
                
            } catch (ConnectException e) {
                lastException = new IOException("Conexão recusada para " + host + ":" + port, e);
                System.err.printf("[TCPClient] Conexão recusada %s:%d (tentativa %d)\n", host, port, i);
                
            } catch (SocketTimeoutException e) {
                lastException = new IOException("Timeout na conexão/leitura para " + host + ":" + port, e);
                System.err.printf("[TCPClient] Timeout %s:%d (tentativa %d)\n", host, port, i);
                
            } catch (IOException e) {
                lastException = new IOException("Erro de E/S na tentativa " + i + ": " + e.getMessage(), e);
                System.err.printf("[TCPClient] Erro E/S %s:%d (tentativa %d): %s\n", host, port, i, e.getMessage());
                
            } finally {
                // Fecha recursos na ordem correta
                if (reader != null) {
                    try { reader.close(); } catch (IOException e) { /* ignore */ }
                }
                if (writer != null) {
                    writer.close();
                }
                if (socket != null) {
                    try { socket.close(); } catch (IOException e) { /* ignore */ }
                }
            }

            if(i < maxRetries){
                try{
                    Thread.sleep(RETRY_DELAY);
                }catch(InterruptedException e){
                    Thread.currentThread().interrupt();
                    throw new IOException("Tentativa interrompida", e);
                }
            }
        }

        throw new IOException("Falha após " + maxRetries + " tentativas: " + 
                             (lastException != null ? lastException.getMessage() : "Erro desconhecido"), 
                             lastException);
    }
}