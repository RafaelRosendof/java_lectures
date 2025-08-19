public class MinerService {
    private final String gatewayAddress;
    private final int myPort;

    // Construtor, métodos start(), registerWithGateway(), startHeartbeat()
    // são estruturalmente idênticos ao do MempoolService, mas se registram como "miner".

    private void handleRequest(HttpExchange exchange) {
        String path = exchange.getRequestURI().getPath();

        if (path.equals("/mine")) {
            // 1. Pedir transações ao MempoolService (VIA GATEWAY)
            // Faz um GET para http://{gatewayAddress}/mempool/getTransactions
            List<Transaction> transactions = getTransactionsFromMempool();

            // 2. Minerar (ordenar)
            PriorityBlockingQueue<Transaction> minerQueue = new PriorityBlockingQueue<>(transactions);
            
            StringBuilder result = new StringBuilder();
            while (!minerQueue.isEmpty()) {
                result.append(minerQueue.poll().toString()).append("\n");
            }
            
            // 3. Enviar o resultado de volta
            sendResponse(result.toString());
        }
    }
    
    private List<Transaction> getTransactionsFromMempool() {
        // Esta função faz um client HTTP GET para o Gateway,
        // que irá rotear para o MempoolService. O corpo da resposta
        // conterá a lista de transações.
        // Você precisará de uma biblioteca HTTP client aqui (ou usar a nativa do Java)
        // e deserializar a resposta.
        return new ArrayList<>(); // Placeholder
    }

    public static void main(String[] args) {
        // Exemplo: rodar duas instâncias em portas diferentes
        // java MinerService localhost:8080 9090
        // java MinerService localhost:8080 9091
        MinerService service = new MinerService(args[0], Integer.parseInt(args[1]));
        service.start();
    }
}