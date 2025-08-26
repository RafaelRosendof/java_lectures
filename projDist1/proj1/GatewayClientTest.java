import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

// java TransactionalProcessor 9001
// java Gateway
// java Miner 9002 

public class GatewayClientTest {

    private static final int THREAD_POOL_SIZE = 20;
    private static final int NUM_REQUESTS = 50;
    private static final String HOST = "localhost";
    private static final int PORT = 8080;
    private static final CommunicationType CLIENT_TYPE = CommunicationType.UDP;
    
 

    public static void main(String[] args) throws InterruptedException {
        // Usa a factory para criar o cliente
        ComponentClient client = CommunicationFactory.createClient(CLIENT_TYPE);
        
        ExecutorService executor = Executors.newFixedThreadPool(THREAD_POOL_SIZE);

        // Várias requisições simulando transações
        for (int i = 0; i < NUM_REQUESTS; i++) {
            int id = i;
            executor.submit(() -> {
                try {
                    sendTransaction(client, "Wallet" + id, "Wallet" + (id + 1),
                            Math.random() * 100, 1 + Math.random() * 10);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            });
        }

        executor.shutdown();
        executor.awaitTermination(10, TimeUnit.SECONDS);

        // Depois de enviar transações, consultar o bloco e minerar
        try {
            System.out.println("\n--- REQUISIÇÃO GET_BLOCK ---");
            String blockResponse = client.send(HOST, PORT, "GET_BLOCK");
            System.out.println(blockResponse);

            System.out.println("\n--- REQUISIÇÃO MINE_BLOCK ---");
            String mineResponse = client.send(HOST, PORT, "MINE_BLOCK");
            System.out.println(mineResponse);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private static void sendTransaction(ComponentClient client, String from, String to, double value, double fee) throws Exception {
        String jsonPayload = String.format("{\"from\":\"%s\",\"to\":\"%s\",\"value\":%.2f,\"fee\":%.2f}", from, to, value, fee);
        
        // Protocolo para o Gateway: COMANDO|IP_CLIENTE|PORTA_CLIENTE|PAYLOAD
        // Nota: Em UDP, o IP/Porta do remetente já está no DatagramPacket, então não precisaríamos enviar.
        // Mas para manter um protocolo consistente entre diferentes transportes, vamos mantê-lo.
        // Para este teste, o IP/Porta do cliente não são cruciais.
        String request = "ADD_TRANSACTION|127.0.0.1|12345|" + jsonPayload;

        String response = client.send(HOST, PORT, request);
        System.out.println("Resposta do Gateway (" + from + "→" + to + "): " + response);
    }
    
    // Método para os outros comandos
    private static void sendCommand(ComponentClient client, String command) throws Exception {
        String request = command + "|127.0.0.1|12345|"; // Sem payload
        String response = client.send(HOST, PORT, request);
        System.out.println("Resposta do Gateway para " + command + ":\n" + response);
    }
}