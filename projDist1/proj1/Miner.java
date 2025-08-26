// Componente B -> statefull, faz o bloco valida a transação e da um snapshot

// testar nova implemetnação do Miner com assincronicidade

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.PriorityBlockingQueue;
import java.util.concurrent.atomic.AtomicBoolean;

public class Miner extends BaseComponent {

    private final List<Block> blockchain = new CopyOnWriteArrayList<>();

    private final ComponentClient clientToGateway;
    private final AtomicBoolean isCurrentlyMining = new AtomicBoolean(false);

    public Miner(int port, String gatewayHost, int gatewayPort, CommunicationType commType, ComponentType componentType) {
        super(port, gatewayHost, gatewayPort, commType, componentType);
        
        this.clientToGateway = CommunicationFactory.createClient(commType);
    }


    private String handleMineBlockAsync() {
        //  existe uma mineração em andamento
        if (!isCurrentlyMining.get()) {
            
            if (isCurrentlyMining.compareAndSet(false, true)) {
                System.out.println("[Miner] Request do mine_block recebida, iniciando processo de mineração assíncrona.");
                new Thread(this::performMining).start();
                return "SUCESSO: Mineração iniciada em segundo plano.";
            }
        }
        
        System.out.println("[Miner] Requisição MINE_BLOCK ignorada, pois uma mineração já está em andamento.");
        return "INFO: Mineração já em andamento.";
    }

    private void performMining() {
        /*
         * 1 -> busca as transações (chamada com a rede bloqueante , mas em uma safe thread)
         * 2 -> ordernar criar o bloco e adicionar 
         * 3 -> independente da falha liberar o semáforo 
         * 4 -> deixar assincrono
         */
        
        try{

            System.out.println("[Miner] Iniciando processo de mineração...");
            List<Transaction> transactionsToMine = getMem();

            if(transactionsToMine.isEmpty()) {
                System.out.println("[Miner] Mempool vazia, nenhuma transação para minerar.");
                return;
            }

            System.out.println("[Miner] Ordenando " + transactionsToMine.size() + " transações...");
            // orfena por taxa 
            transactionsToMine.sort(Transaction::compareTo);

            long nextBlockId = blockchain.size() + 1;
            Block newBlock = new Block(nextBlockId, transactionsToMine);
            blockchain.add(newBlock);

            snapshotBlockchain();
            System.out.println("[Miner] Bloco #" + nextBlockId + " minerado com " + transactionsToMine.size() + " transações.");

        }catch(Exception e) {
            e.printStackTrace();
            System.err.println("[Miner] Falha ao minerar o bloco: " + e.getMessage());
        } finally {
            isCurrentlyMining.set(false);
            System.out.println("[Miner] Processo de mineração finalizado. Sinalizador liberado.");
        }
    }

    private List<Transaction> getMem() throws Exception {
        
        String request = "GET_MEMPOOL|";
        System.out.println("[Miner] Solicitando mempool do TransactionProcessor...");
        
        String response = clientToGateway.send(gatewayHost, gatewayPort, request);

        List<Transaction> reconstructedMempool = new ArrayList<>();
        if (response == null || response.startsWith("ERRO") || response.trim().isEmpty()) {
            System.err.println("[Miner] Não foi possível obter a mempool: " + response);
            return reconstructedMempool;
        }

        String[] trLines = response.split("\n");

        for( String line : trLines) {

            if(line.trim().isEmpty()) continue; // linhas vazias
            String[] fields = line.split(";");

            if (fields.length == 4) {
                String from = fields[0];
                String to = fields[1];
                double value = Double.parseDouble(fields[2]);
                double fee = Double.parseDouble(fields[3]);

                reconstructedMempool.add(new Transaction(from, to, value, fee));
            }
        }
        return reconstructedMempool;
    }

    private void snapshotBlockchain() {
        System.out.println("[Miner] Snapshot da blockchain realizado. Total de blocos: " + blockchain.size());
    }

    @Override
    protected RequestHandler getRequestHandler() {
        return (request) -> {
            String command = request.split("\\|", 2)[0];
            
            switch (command) {
                case "MINE_BLOCK":
                    //return handleMineBlock();
                    return handleMineBlockAsync();
                case "GET_BLOCKCHAIN":
                    return handleGetBlockchain();
                default:
                    return "ERRO: Comando desconhecido para Miner.";
            }
        };
    }

    private String handleMineBlock() {
        try {
            System.out.println("[Miner] Requisição MINE_BLOCK recebida. Buscando mempool...");
            List<Transaction> transactionsToMine = getMem();

            if (transactionsToMine.isEmpty()) {
                return "Mempool vazia, nenhum bloco foi minerado.";
            }

            transactionsToMine.sort(Transaction::compareTo);

            long nextBlockId = blockchain.size() + 1;
            Block newBlock = new Block(nextBlockId, transactionsToMine);
            blockchain.add(newBlock);
            
            snapshotBlockchain();
            System.out.println("[Miner] Bloco #" + nextBlockId + " minerado com " + transactionsToMine.size() + " transações.");
            return "SUCESSO: Bloco #" + nextBlockId + " minerado.";
        
        } catch (Exception e) {
            e.printStackTrace();
            return "ERRO: Falha ao minerar o bloco.";
        }
    }

    private String handleGetBlockchain() {
        System.out.println("[Miner] Requisição GET_BLOCKCHAIN recebida.");
        if (blockchain.isEmpty()) {
            return "A blockchain ainda não possui blocos.";
        }
        
        StringBuilder sb = new StringBuilder("=== ESTADO ATUAL DA BLOCKCHAIN ===\n");
        sb.append("Total de blocos: ").append(blockchain.size()).append("\n\n");
        
        for (Block block : blockchain) {
            sb.append("Bloco #").append(block.getId()).append(" - ")
              .append(block.getTransactions().size()).append(" transações\n");
            
            // Mostra apenas as primeiras 3 transações para não sobrecarregar a resposta
            List<Transaction> transactions = block.getTransactions();
            for (int i = 0; i < Math.min(3, transactions.size()); i++) {
                sb.append("  ").append(transactions.get(i)).append("\n");
            }
            if (transactions.size() > 3) {
                sb.append("  ... e mais ").append(transactions.size() - 3).append(" transações\n");
            }
            sb.append("\n");
        }
        
        return sb.toString();
    }

    private String handleGetStatus() {
        return String.format("MINER STATUS:\n" +
                "- Mineração em andamento: %s\n" +
                "- Total de blocos na blockchain: %d\n" +
                "- Última atividade: %s",
                isCurrentlyMining.get() ? "SIM" : "NÃO",
                blockchain.size(),
                System.currentTimeMillis());
    }

    public static void main(String[] args) throws Exception {
        if (args.length < 4) {
            System.out.println("Uso: java Miner <porta> <gateway_host> <gateway_porta> <UDP|TCP>");
            return;
        }
        int myPort = Integer.parseInt(args[0]);
        String gatewayHost = args[1];
        int gatewayPort = Integer.parseInt(args[2]);
        // Lê o tipo de comunicação da linha de comando
        CommunicationType commType = CommunicationType.valueOf(args[3].toUpperCase());

        new Miner(myPort, gatewayHost, gatewayPort, commType, ComponentType.MINER).start();
    }
}


/*
 * 
 * 


    private final Block blockchain = new Block();
    ComponentClient clientToGateway = CommunicationFactory.createClient(clientType);

    private final List<PriorityBlockingQueue<Transaction>> blockchains = new ArrayList<>();
    private final PriorityBlockingQueue<Transaction> mempool = new PriorityBlockingQueue<>();
    

    public Miner(int port, String gatewayHost, int gatewayPort) {
        super(port, gatewayHost, gatewayPort, CommunicationType.UDP, ComponentType.MINER);
    }

    // Função para pegar a mempool do Transaction via API Gateway
    private List<Transaction> getMem() throws Exception {
        // "GET_MEMPOOL" é o comando 
        String request = "GET_MEMPOOL|miner_ip|miner_port";
        String response = clientToGateway.send(gatewayHost, gatewayPort, request);

        List<Transaction> transactions = new ArrayList<>();

        if (response.startsWith("ERRO") || response == null || response.trim().isEmpty()) {
            System.err.println("[Miner] Erro ao obter mempool: " + response);
            return transactions;
        }

        String [] trLines = response.split("\n");

        for(String line : trLines) {
            if (line.trim().isEmpty()) continue; // linhas vazias 
            String[] fields = line.split(";");

            if(fields.length == 4){
                String from = fields[0];
                String to = fields[1];
                double value = Double.parseDouble(fields[2]);
                double fee = Double.parseDouble(fields[3]); 

                reconstructedMemPool.add(new Transaction(from, to, value, fee));
            }
        }

        return reconstructedMemPool;
        
    }


    private void snapshotBlockchain() {

        System.out.println("Snapshot da blockchain realizado.");
    }
        //TODO -> Terminar esses métodos aqui e testar mais no jmeter antes do tcp e grpc
    @Override
    protected RequestHandler getRequestHandler() {
        return (request) -> {
            String[] parts = request.split("\\|", 2);
            String command = parts[0];
            
            switch (command) {
                case "GET_BLOCK":
                    System.out.println("[Miner] Requisição GET_BLOCK recebida.");
                    return blockchain.printBlockChain(localMempoolCopy);
                case "MINE_BLOCK":
                    
                    System.out.println("[Miner] Requisição MINE_BLOCK recebida.");
                    //return blockchain.mineBlock(localMempoolCopy);
                    //String mineBlock = blockchain.mineBlock(localMempoolCopy);
                    //blockchains.add(mineBlock);
                    //snapshotBlockchain(); // Salva o estado atual da blockchain
                    //return mineBlock;


                    try{ 

                        List<Transaction> transactionsToMine = getMem();
                        if (transactionsToMine.isEmpty()) {
                            return "ERRO: Mempool vazia, não há transações para minerar.";
                        }
                        String minedBlock = blockchain.mineBlock(transactionsToMine);
                        blockchains.add(new PriorityBlockingQueue<>(transactionsToMine));
                        snapshotBlockchain(); // Salva o estado atual da blockchain
                        return "SUCESSO: Bloco minerado com " + transactionsToMine.size() + " transações:\n" + minedBlock;
                    
                    
                    }catch (Exception e) {
                        e.printStackTrace();
                        return "ERRO: Falha ao minerar o bloco.";
                    }



                case "SNAPSHOT_BLOCKCHAIN":
                    System.out.println("[Miner] Requisição SNAPSHOT_BLOCKCHAIN recebida.");
                    snapshotBlockchain();
                    return "Snapshot da blockchain realizado.";
                case "GET_BLOCKCHAIN":
                    System.out.println("[Miner] Requisição GET_BLOCKCHAIN recebida.");
                    

                    String allBlocks = blockchain.printaBlockList(blockchains);
                    return allBlocks;
                default:
                    return "ERRO: Comando desconhecido para Miner.";
            }
        };
    }

    public static void main(String[] args) throws Exception {
        // Uso: java Miner <sua_porta> <gateway_host> <gateway_porta>
        int myPort = (args.length > 0) ? Integer.parseInt(args[0]) : 9002;
        String gatewayHost = (args.length > 1) ? args[1] : "localhost";
        int gatewayPort = (args.length > 2) ? Integer.parseInt(args[2]) : 8080;
        String clientType = (args.length > 3) ? CommunicationType.valueOf(args[3].toUpperCase()) : CommunicationType.UDP;

        Miner miner = new Miner(myPort, gatewayHost, gatewayPort);
        miner.start();
    }
 */

