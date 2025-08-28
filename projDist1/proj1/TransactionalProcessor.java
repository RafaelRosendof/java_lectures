//Componente A -> faz apenas a transação Stateless

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.atomic.AtomicBoolean;

public class TransactionalProcessor extends BaseComponent {

    private final ConcurrentLinkedQueue<Transaction> mempool = new ConcurrentLinkedQueue<>();
    private final ComponentClient clientToGateway;

    // não quero mais de uma thread aqui 
    private final AtomicBoolean isMiningInProgress = new AtomicBoolean(false);

    public TransactionalProcessor(int port, String gatewayHost, int gatewayPort, CommunicationType commType, ComponentType componentType) {
        super(port, gatewayHost, gatewayPort, commType, componentType);
        this.clientToGateway = CommunicationFactory.createClient(commType);
    }

    // Esse aqui tem que chamar o Miner mine, o cliente não pode fazer requisição para minerar o nodo 
    // Caso o bloco tenha tamanho de 300 aí chama o mine do Mine 
    // 

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
                return getMempoolSnapshot();
            }

            return "ERRO: Comando desconhecido";
        };
    }

    private String getMempoolSnapshot() {
        System.out.println("[TransactionProcessor] Solicitada cópia da mempool. Tamanho atual: " + mempool.size());
    
        List<Transaction> transactionsToMine = new ArrayList<>();
        
        for (int i = 0; i < 400 && !mempool.isEmpty(); i++) {
            transactionsToMine.add(mempool.poll());
        }

        if (!transactionsToMine.isEmpty()) {
            isMiningInProgress.set(false);
            System.out.println("[TransactionProcessor] Sinalizador de mineração resetado para 'false'.");
        }
        StringBuilder sb = new StringBuilder();
        for (Transaction tx : transactionsToMine) {
            sb.append(tx.getFrom()).append(";")
              .append(tx.getTo()).append(";")
              .append(tx.getValue()).append(";")
              .append(tx.getFee()).append("\n");
        }
        System.out.println("[TransactionProcessor] Enviando " + transactionsToMine.size() + " transações para o Miner.");
        return sb.toString();
    }

    private String handleAddTransaction(String payload) {
        try {
            String[] fields = payload.split(";");
            if (fields.length != 4) {
                return "ERRO: Payload da transação mal formatado.";
            }

            String from = fields[0];
            String to = fields[1];
            double value = Double.parseDouble(fields[2]);
            double fee = Double.parseDouble(fields[3]);
            Transaction txData = new Transaction(from, to, value, fee);
            mempool.add(txData);

            int currentSize = mempool.size();

            System.out.printf("[TransactionProcessor] Nova transação na mempool: %s (Tamanho atual: %d)\n", 
                  txData.toString(), currentSize);
            
            if (currentSize >= 400) {
                // compareAndSet para garantir que apenas uma thread dispare a mineração
                if (isMiningInProgress.compareAndSet(false, true)) {
                    System.out.println("[TransactionProcessor] CONDIÇÃO ATINGIDA! Disparando mineração...");
                    // Dispara a mineração em uma nova thread para não bloquear a resposta.
                    new Thread(() -> {
                        try {
                            String response = clientToGateway.send(gatewayHost, gatewayPort, "MINE_BLOCK|");
                            System.out.println("[TransactionProcessor] Resposta da mineração: " + response);
                        } catch (Exception e) {
                            System.err.println("[TransactionProcessor] Falha ao disparar a mineração: " + e.getMessage());
                            // Se a notificação falhar, reseta o sinalizador para permitir uma nova tentativa.
                            isMiningInProgress.set(false);
                        }
                    }).start();
                } else {
                    System.out.println("[TransactionProcessor] Mineração já em andamento, aguardando...");
                }
            }

            return "SUCESSO: Transação adicionada.";

        } catch (Exception e) {
            
            return "ERRO: Falha ao processar transação - " + e.getMessage();
            
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
