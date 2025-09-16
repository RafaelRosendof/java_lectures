
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.PriorityBlockingQueue;




@RequestMapping(path="/blockchain")
public class BlockChainService {

    private final PriorityBlockingQueue<Transaction> mempool = new PriorityBlockingQueue<>();
    private final List<Block> blockchain = new CopyOnWriteArrayList<>();

    public BlockChainService() {
        
        System.out.println("BlockChainService instanciado.");
    }

    // Este será o método exposto pela rota POST /blockchain/transactions
    @RequestMapping(method = "POST", path = "/transactions")
    public String addTransaction(String requestBody) {
        try {

            Map<String, Object> data = JsonUtil.parseJson(requestBody);
            
           
            String from = (String) data.get("from");
            String to = (String) data.get("to");
            Double value = Double.valueOf(data.get("value").toString());
            Double fee = Double.valueOf(data.get("fee").toString());

            if (from == null || from.trim().isEmpty()) {
                return createErrorResponse("Campo 'from' é obrigatório");
            }
            if (to == null || to.trim().isEmpty()) {
                return createErrorResponse("Campo 'to' é obrigatório");
            }
            if (value == null || value <= 0) {
                return createErrorResponse("Campo 'value' deve ser maior que zero");
            }
            if (fee == null || fee < 0) {
                return createErrorResponse("Campo 'fee' deve ser maior ou igual a zero");
            }

            Transaction tx = new Transaction(from.trim(), to.trim(), value, fee);
            mempool.add(tx);
            
            System.out.println("[Service] Transação recebida: " + tx.toString() + 
                             " - Mempool size: " + mempool.size());

            //  para 3 para facilitar testes)
            if (mempool.size() >= 3) {
                new Thread(this::performMining).start();
            }

            return createSuccessResponse("Transação adicionada com sucesso", 
                Map.of(
                    "transactionId", System.nanoTime(), // ID temporário da transação
                    "mempoolSize", mempool.size(),
                    "from", from,
                    "to", to,
                    "value", value,
                    "fee", fee
                ));

        } catch (Exception e) {
            System.err.println("[Service] Erro ao processar transação: " + e.getMessage());
            e.printStackTrace();
            return createErrorResponse("Falha ao processar transação: " + e.getMessage());
        }
    }
    
    // Este será o método exposto pela rota GET /blockchain/blocks
    @RequestMapping(method = "GET", path = "/blocks")
    public String getBlockchainAsString() {
        if (blockchain.isEmpty()) {
            return "A blockchain esta vazia.";
        }
        // Retorna uma representação simples da blockchain
        StringBuilder sb = new StringBuilder();
        for(Block block : blockchain) {
            sb.append("Block ID: ").append(block.getId())
              .append(", TXs: ").append(block.getTransactions().size()).append("\n");
        }
        return sb.toString();
    }

    // Lógica interna, não exposta como uma rota
    private void performMining() {
        System.out.printf("[Service] Condição de mineração atingida. Mempool: %d. Iniciando...\n", mempool.size());

        List<Transaction> transactionsToMine = new ArrayList<>();
        mempool.drainTo(transactionsToMine, 400);

        if(transactionsToMine.isEmpty()){
            return;
        }

        long nextBlockId = blockchain.isEmpty() ? 1 : blockchain.get(blockchain.size() - 1).getId() + 1;
        Block newBlock = new Block(nextBlockId, transactionsToMine);
        blockchain.add(newBlock);   

        System.out.println("[Service] Bloco minerado com sucesso! ID: " + newBlock.getId());
    }



    private String createSuccessResponse(String message, Map<String, Object> data) {
        try {
            Map<String, Object> response = Map.of(
                "success", true,
                "message", message,
                "data", data,
                "timestamp", System.currentTimeMillis()
            );
            return JsonUtil.toJson(response);
        } catch (Exception e) {
            return "{\"success\":false,\"message\":\"Erro ao criar resposta JSON\"}";
        }
    }

    private String createErrorResponse(String message) {
        try {
            Map<String, Object> response = Map.of(
                "success", false,
                "message", message,
                "timestamp", System.currentTimeMillis()
            );
            return JsonUtil.toJson(response);
        } catch (Exception e) {
            return "{\"success\":false,\"message\":\"Erro interno do servidor\"}";
        }
    }
}
