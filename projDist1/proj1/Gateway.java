import java.util.concurrent.ConcurrentLinkedQueue;

public class Gateway {
    private final int port;
    private final CommunicationType communicationType;
    private final ConcurrentLinkedQueue<Transaction> mempool = new ConcurrentLinkedQueue<>();
    private final Block blockchain = new Block();
    private ComponentServer server;

    public Gateway(int port, CommunicationType type) {
        this.port = port;
        this.communicationType = type;
    }

    public void start() throws Exception {
        // Usa a factory para criar o servidor apropriado
        this.server = CommunicationFactory.createServer(communicationType);
        
        // O RequestHandler contém toda a lógica de negócio que antes estava no handleClient
        RequestHandler handler = (request) -> {
            System.out.println("Gateway recebeu: " + request);
            String[] parts = request.split("\\|", 2);
            String command = parts[0];
            String payload = (parts.length > 1) ? parts[1] : "";

            switch (command) {
                case "ADD_TRANSACTION":
                    return handleAddTransaction(payload);
                case "GET_BLOCK":
                    return handleGetBlock();
                case "MINE_BLOCK":
                    return handleMineBlock();
                default:
                    return "ERRO: Comando desconhecido.";
            }
        };

        // Inicia o servidor (UDP ou outro) com a lógica de handling
        server.start(port, handler);
    }

    private String handleAddTransaction(String jsonPayload) {
        try {
            // Lógica para desserializar o JSON da transação
            // Exemplo simples, pode ser substituído por uma biblioteca como GSON
            String[] partsBody = jsonPayload.replace("{", "").replace("}", "").replace("\"", "").split(",");
            String from = partsBody[0].split(":")[1].trim();
            String to = partsBody[1].split(":")[1].trim();
            double value = Double.parseDouble(partsBody[2].split(":")[1].trim());
            double fee = Double.parseDouble(partsBody[3].split(":")[1].trim());

            Transaction txData = new Transaction(from, to, value, fee);
            mempool.add(txData);
            
            System.out.println("Nova transação recebida: " + txData);
            return "SUCESSO: Transação adicionada.";
        } catch (Exception e) {
            e.printStackTrace();
            return "ERRO: Formato da transação inválido.";
        }
    }

    private String handleGetBlock() {
        String result = blockchain.printBlockChain(mempool);
        System.out.println("Bloco atual solicitado.");
        return "Bloco atual:\n" + result;
    }

    private String handleMineBlock() {
        String mined = blockchain.mineBlock(mempool);
        System.out.println("Bloco minerado solicitado.");
        return "Bloco minerado:\n" + mined;
    }
    
    public void stop() {
        if (server != null) {
            server.stop();
        }
    }

    public static void main(String[] args) throws Exception {
        // Para iniciar o Gateway com UDP, basta passar o tipo na construção
        Gateway gateway = new Gateway(8080, CommunicationType.UDP);
        gateway.start();
        
        System.out.println("Gateway iniciado em modo UDP. Pressione Ctrl+C para parar.");
    }
}