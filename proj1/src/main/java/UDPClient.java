import java.io.IOException;
import java.net.*;



public class UDPClient implements ComponentClient {

    private static final int DEFAULT_TIMEOUT = 10000; // 10 
    private static final int MAX_RETRIES = 3;
    private static final int RETRY_DELAY = 1000;

    
    @Override 
    public String send(String host, int port, String request) throws IOException {
        return sendWithRetry(host, port, request, DEFAULT_TIMEOUT, MAX_RETRIES);
    }


    public String sendWithRetry(String host , int port , String request , int timeoutMS , int maxRetries) throws IOException {

        IOException lastException = null;

        for(int att = 0 ; att <= maxRetries ; att++){

            try(DatagramSocket socket = new DatagramSocket()){

                InetAddress addres = InetAddress.getByName(host);
                byte[] requestBytes = request.getBytes();

                DatagramPacket requestPacket = new DatagramPacket(requestBytes, requestBytes.length, addres, port);
                socket.send(requestPacket);

                byte[] buffer = new byte[16384];
                DatagramPacket responsePacket = new DatagramPacket(buffer, buffer.length);
                
                int timeoutAdaptativo = request.startsWith("MINE_BLOCK") ? timeoutMS * 3 : timeoutMS;

                socket.setSoTimeout(timeoutAdaptativo);
                socket.receive(responsePacket);

                String response = new String(responsePacket.getData(), 0, responsePacket.getLength());

                if (att > 1) {
                    System.out.println("[UDPClient] Requisição atendida após " + att + " tentativas.");
                }

                return response;

            }catch(IOException e){
                lastException = e;

                System.err.printf("[UDPClient] Tentativa %d falhou para %s:%d - %s\n", 
                                 att, host, port, e.getMessage());

                if (att < maxRetries) {
                    try {
                        Thread.sleep(RETRY_DELAY);
                    } catch (InterruptedException ie) {
                        Thread.currentThread().interrupt();
                        throw new IOException("Interrompido durante retry", ie);
                    }
                
                }
            }
        }

        throw new IOException("Falha após " + maxRetries + " tentativas: " + lastException.getMessage(), lastException);
    }

}
