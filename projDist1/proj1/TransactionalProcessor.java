//Componente A -> faz apenas a transação Stateless

import java.util.concurrent.ConcurrentLinkedQueue;

public class TransactionalProcessor extends BaseComponent {

    private final ConcurrentLinkedQueue<Transaction> mempool = new ConcurrentLinkedQueue<>();

    public TransactionalProcessor(int componentPort, String gatewayHost, int gatewayPort, CommunicationType commType) {
        super(componentPort, gatewayHost, gatewayPort, commType, ComponentType.TRANSACTIONAL_PROCESSOR);
    }

    @Override
    protected RequestHandler gRequestHandler() {
        return (request) -> {

            String[] parts = request.split("\\|", 2);
            String command = parts[0];
            String payload = parts.length > 1 ? parts[1] : "";

            if("ADD_TRANSACTION".equals(command)){
                return handleAddTransaction(payload);
            }

            return "ERRO: Comando desconhecido";
        };
    }

    private String handleAddTransaction(String jsonPayload) {

        try{

            String[] partsBody = jsonPayload.replace("{", "").replace("}", "").replace("\"", "").split(",");
            String from = partsBody[0].split(":")[1].trim();
            String to = partsBody[1].split(":")[1].trim();
            double value = Double.parseDouble(partsBody[2].split(":")[1].trim());
            double fee = Double.parseDouble(partsBody[3].split(":")[1].trim());

            Transaction txDATA = new Transaction(from, to, value, fee);
            mempool.add(txDATA);

            System.out.println("[TransactionProcessor] Nova transação na mempool: " + txData);
            return "SUCESSO: Transação adicionada.";

        }catch (Exception e) {
            return "ERRO: Falha ao processar transação - " + e.getMessage();
        }
    }

    public static void main(String[] args) {

        int mPort = (args.length > 0) ? Integer.parseInt(args[0]) : 9001;
        String gatewayHost = (args.length > 1) ? args[1] : "localhost";
        int gatewayPort = (args.length > 2) ? Integer.parseInt(args[2]) : 8080;
        TransactionProcessor processor = new TransactionProcessor(mPort, gatewayHost, gatewayPort);
        processor.start();
    }   
}