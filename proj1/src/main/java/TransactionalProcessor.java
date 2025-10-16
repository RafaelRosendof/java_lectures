//Componente A STATELESS


import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.atomic.AtomicBoolean;

public class TransactionalProcessor extends BaseComponent {

    
    private final ComponentClient clientToGateway;

    

    public TransactionalProcessor(int port, String gatewayHost, int gatewayPort, CommunicationType commType, ComponentType componentType) {
        super(port, gatewayHost, gatewayPort, commType, componentType);
        this.clientToGateway = CommunicationFactory.createClient(commType);
    }

    @Override
    protected RequestHandler getRequestHandler() {
        return (request) -> {

            String[] parts = request.split("\\|", 2);
            String command = parts[0];
            String payload = parts.length > 1 ? parts[1] : "";

            if("ADD_TRANSACTION".equals(command)){
                return handleAddTransaction(payload);
            }
            else if("GET_MEMPOOL".equals(command)) {
                //return getMempoolSnapshot();
                System.out.println("Temporariamente indisponível");
            }

            return "ERRO: Comando desconhecido";
        };
    }


    private String handleAddTransaction(String payload) {
        try {
            // Monta a requisição que será enviada para o Gateway.
            // O Gateway saberá que "ADD_TRANSACTION" deve ser roteado para um Miner.
            
            String requestToGateway = "ROUTE_TO_MINER|" + payload;
            
            // Envia para o Gateway e retorna a resposta do Miner diretamente para o cliente.
            System.out.println("[TransactionProcessor] Repassando transação para o Gateway " + payload + " ...");
            return clientToGateway.send(gatewayHost, gatewayPort, requestToGateway);

        } catch (Exception e) {
            System.err.println("[TransactionProcessor] Falha ao repassar transação para o Gateway: " + e.getMessage());
            return "ERRO: Falha na comunicação com o Gateway.";
        }
    }


    public static void main(String[] args) throws Exception {
        if (args.length < 4) {
            System.out.println("Uso: java TransactionalProcessor <porta> <gateway_host> <gateway_porta> <UDP|TCP>");
            return;
        }
        int myPort = Integer.parseInt(args[0]);
        String gatewayHost = args[1];
        int gatewayPort = Integer.parseInt(args[2]);
        // Lê o tipo de comunicação da linha de comando
        CommunicationType commType = CommunicationType.valueOf(args[3].toUpperCase());

        new TransactionalProcessor(myPort, gatewayHost, gatewayPort, commType, ComponentType.TRANSACTION_PROCESSOR).start();
    }
}

