// Componente B -> statefull, faz o bloco valida a transação e da um snapshot

// testar nova implemetnação do Miner com assincronicidade

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.PriorityBlockingQueue;
import java.util.concurrent.atomic.AtomicBoolean;

public class Miner extends BaseComponent {

    private final PriorityBlockingQueue<Transaction> mempool = new PriorityBlockingQueue<>();
    private final List<Block> blockchain = new CopyOnWriteArrayList<>();

    private final ComponentClient clientToGateway;
    //private final AtomicBoolean isCurrentlyMining = new AtomicBoolean(false);

    public Miner(int port, String gatewayHost, int gatewayPort, CommunicationType commType, ComponentType componentType) {
        super(port, gatewayHost, gatewayPort, commType, componentType);
        
        this.clientToGateway = CommunicationFactory.createClient(commType);
        startMiningLoop();
    }


    private void startMiningLoop() {
        Thread miningThread = new Thread(() -> {
            System.out.println("[Miner] Loop de mineração iniciado. Aguardando transações...");
            while (!Thread.currentThread().isInterrupted()) {
                try {
                    // Condição para minerar: ter pelo menos 400 transações na mempool.
                    if (mempool.size() >= 400) {
                        performMining();
                    }
                    // Pausa para não consumir 100% de CPU verificando o tamanho da fila.
                    Thread.sleep(100);
                } catch (InterruptedException e) {
                    System.out.println("[Miner] Loop de mineração interrompido.");
                    Thread.currentThread().interrupt();
                } catch (Exception e) {
                    System.err.println("[Miner] Erro inesperado no loop de mineração: " + e.getMessage());
                    e.printStackTrace();
                }
            }
        });
        miningThread.setDaemon(true); // Garante que a thread não impeça o programa de fechar.
        miningThread.start();
    }

    private void performMining() {
        System.out.printf("[Miner] Condição de mineração atingida. Tamanho da Mempool: %d. Iniciando...\n", mempool.size());

        List<Transaction> transactionsToMine = new ArrayList<>();
        mempool.drainTo(transactionsToMine, 400);

        if(transactionsToMine.isEmpty()){
            return;
        }

        System.out.println("[Miner] Minerando bloco com " + transactionsToMine.size() + " transações de mais alta prioridade.");
        
        // A ETAPA DE SORT FOI ELIMINADA!
        // transactionsToMine.sort(Transaction::compareTo); // <-- NÃO PRECISA MAIS!

        long nextBlockId = blockchain.size() + 1;
        Block newBlock = new Block(nextBlockId, transactionsToMine);
        blockchain.add(newBlock);

        System.out.println("[Miner] Bloco #" + nextBlockId + " minerado.");
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
            
            // Adiciona na fila de prioridade local.
            mempool.add(txData);

            // Log é útil para debug, mas pode ser removido em produção para performance.
             System.out.printf("[Miner] Nova transação na mempool: %s (Tamanho atual: %d)\n",
                   txData.toString(), mempool.size());

            //return "SUCESSO: Transação enfileirada no Miner.";
            return "SUCESSO";
        } catch (Exception e) {
            return "ERRO: Falha ao processar transação no Miner - " + e.getMessage();
        }
    }

    @Override
    protected RequestHandler getRequestHandler() {
        return (request) -> {
            String[] parts = request.split("\\|", 2);
            String command = parts[0];
            String payload = parts.length > 1 ? parts[1] : "";

            switch (command) {
                //case "MINE_BLOCK":
                //    //return handleMineBlock();
                //    return handleMineBlockAsync();
                //case "GET_BLOCKCHAIN":
                //    return handleGetBlockchain();
                case "ADD_TRANSACTION":
                    return handleAddTransaction(payload);
                default:
                    return "ERRO: Comando desconhecido para Miner.";
            }
        };
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
            
            //snapshotBlockchain();
            System.out.println("[Miner] Bloco #" + nextBlockId + " minerado com " + transactionsToMine.size() + " transações.");
            return "SUCESSO: Bloco #" + nextBlockId + " minerado.";
        
        } catch (Exception e) {
            e.printStackTrace();
            return "ERRO: Falha ao minerar o bloco.";
        }
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
        try{

            System.out.println("[Miner] Iniciando processo de mineração...");
            List<Transaction> transactionsToMine = getMem();

            if(transactionsToMine.isEmpty()) {
                System.out.println("[Miner] Mempool vazia, nenhuma transação para minerar.");
                return;
            }

            System.out.println("[Miner] Ordenando " + transactionsToMine.size() + " transações...");
            // orfena por taxa 
            transactionsToMine.sort(Transaction::compareTo); //aqui pode ser custoso 

            long nextBlockId = blockchain.size() + 1;
            Block newBlock = new Block(nextBlockId, transactionsToMine);
            blockchain.add(newBlock);

            //snapshotBlockchain();
            System.out.println("[Miner] Bloco #" + nextBlockId + " minerado com " + transactionsToMine.size() + " transações.");

        }catch(Exception e) {
            e.printStackTrace();
            System.err.println("[Miner] Falha ao minerar o bloco: " + e.getMessage());
        } finally {
            isCurrentlyMining.set(false);
            System.out.println("[Miner] Processo de mineração finalizado. Sinalizador liberado.");
        }
    }
    */


/*

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
 */