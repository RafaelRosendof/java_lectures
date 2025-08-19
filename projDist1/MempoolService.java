public class MempoolService {
    private final ConcurrentLinkedQueue<Transaction> mempool = new ConcurrentLinkedQueue<>();
    private final String gatewayAddress; // ex: "localhost:8080"
    private final int myPort;

    public MempoolService(String gatewayAddress, int myPort) {
        this.gatewayAddress = gatewayAddress;
        this.myPort = myPort;
    }

    public void start() {
        // 1. Registrar-se no Gateway
        registerWithGateway();

        // 2. Iniciar thread de Heartbeat para o Gateway
        startHeartbeat();

        // 3. Iniciar seu próprio servidor HTTP para receber requisições do Gateway
        // ... servidor escutando na myPort ...
    }

    private void handleRequest(HttpExchange exchange) {
        String path = exchange.getRequestURI().getPath();

        if (path.equals("/addTransaction")) {
            // Lógica para ler o corpo da requisição e adicionar à mempool
            // mempool.add(newTransaction);
        } else if (path.equals("/getTransactions")) {
            // Lógica para serializar a mempool para JSON/texto, enviá-la
            // e depois limpar a mempool local.
            // List<Transaction> transactionsToMine = new ArrayList<>(mempool);
            // mempool.clear();
            // sendResponse(transactionsToMine);
        }
    }
    
    private void registerWithGateway() {
        // Fazer uma requisição HTTP POST para http://{gatewayAddress}/register
        // com o corpo: { "serviceName": "mempool", "address": "meu_ip:" + myPort }
    }

    private void startHeartbeat() {
        // Usar um ScheduledExecutorService para a cada 5 segundos
        // fazer um POST para http://{gatewayAddress}/heartbeat
        // com o corpo: { "serviceName": "mempool", "address": "meu_ip:" + myPort }
    }
    
    public static void main(String[] args) {
        // Ler gatewayAddress e myPort dos args
        MempoolService service = new MempoolService("localhost:8080", 8081);
        service.start();
    }
}