
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.PriorityBlockingQueue;




@RequestMapping(path="/blockchain")
public class BlockChainService {

    private final PriorityBlockingQueue<Transaction> mempool = new PriorityBlockingQueue<>();
    private final List<Block> blockchain = new CopyOnWriteArrayList<>();

    public BlockChainService() {
        // Construtor pode inicializar algo se necessário
        System.out.println("BlockChainService instanciado.");
    }

    // Este será o método exposto pela rota POST /blockchain/transactions
    @RequestMapping(method = "POST", path = "/transactions")
    public String addTransaction(String from, String to, double value, double fee) {
        try {
            Transaction tx = new Transaction(from, to, value, fee);
            mempool.add(tx);
            System.out.println("[Service] Transação recebida. Mempool size: " + mempool.size());

            // Lógica para disparar a mineração
            if (mempool.size() >= 400) { // Limite para mineração
                // Para não bloquear a resposta, podemos rodar a mineração em outra thread
                new Thread(this::performMining).start();
            }

            return "SUCESSO: Transacao adicionada.";
        } catch (Exception e) {
            return "ERRO: Falha ao processar transacao - " + e.getMessage();
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
}
