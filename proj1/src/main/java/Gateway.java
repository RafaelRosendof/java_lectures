import java.net.*;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

public class Gateway {

    private final int port;
    private final CommunicationType communicationType;
    private ComponentServer server;

    
    private final Map<String, ComponentInfo> serviceRegistry = new ConcurrentHashMap<>();
    private final ComponentClient internalClient;
    private final ScheduledExecutorService heartbeatScheduler = Executors.newSingleThreadScheduledExecutor();

    // rounding robin fracionar o Miner 

    private final AtomicInteger minerRobing = new AtomicInteger(0);
    private final AtomicInteger txProcessor = new AtomicInteger(0);

    public Gateway(int port, CommunicationType type) {
        this.port = port;
        this.communicationType = type;
        this.internalClient = CommunicationFactory.createClient(type);
    }

    public void start() throws Exception {
        this.server = CommunicationFactory.createServer(communicationType);
        
        RequestHandler handler = (requestWithSenderInfo) -> {
            // O request agora pode conter informações do remetente, dependendo da implementação do servidor
            String[] parts = requestWithSenderInfo.split("\\|");
            String command = parts[0];
            String payload = (parts.length > 1) ? parts[1] : "";

            if ("REGISTER".equals(command)) {
                //  IP_COMPONENTE|PORTA_COMPONENTE|TIPO_COMPONENTE
                return handleRegistration(parts);
            }
            if ("HEARTBEAT".equals(command)) {
                // IP_COMPONENTE|PORTA_COMPONENTE|TIPO_COMPONENTE
                return handleHeartbeat(parts);
            }

            return routeRequest(command, payload);
        };

        server.start(port, handler);
        startHeartbeatCheck(); 
        System.out.println("[Gateway] Roteador iniciado em modo " + communicationType + " na porta " + port);
    }

    private String routeRequest(String command, String payload) {
        ComponentInfo targetComponent = null;
        String jmeterErrorResponse = "ERRO_GATEWAY: SERVICO_INDISPONIVEL";
        try {
            targetComponent = switch (command) {
                // 1. Comando inicial do JMeter vai para o TransactionalProcessor.
                case "ADD_TRANSACTION" -> findAvailableComponentRoundRobin(ComponentType.TRANSACTION_PROCESSOR);
            
                // 2. Novo comando do TransactionalProcessor vai para o Miner.
                case "ROUTE_TO_MINER" -> findAvailableComponentRoundRobin(ComponentType.MINER);
                
                // Comandos de debug/consulta continuam indo para o Miner.
                case "GET_BLOCKCHAIN" -> findAvailableComponentRoundRobin(ComponentType.MINER);
                
                default -> null;
            };
        
            if (targetComponent == null) {
                System.err.println("[Gateway] Nenhum componente disponível para o comando " + command);
                return jmeterErrorResponse;
            }
        
            // A requisição para o Miner deve ser o comando original "ADD_TRANSACTION"
            String internalRequest;
            if ("ROUTE_TO_MINER".equals(command)) {
                // O Miner espera "ADD_TRANSACTION", não "ROUTE_TO_MINER". Nós traduzimos de volta.
                internalRequest = "ADD_TRANSACTION|" + payload;
            } else {
                internalRequest = command + "|" + payload;
            }
        
            System.out.printf("[Gateway] Roteando comando original '%s' como '%s' para %s\n", 
                            command, internalRequest.split("\\|")[0], targetComponent);
            
            return sendWithTimeout(targetComponent, internalRequest, 10000);
            
        } catch (Exception e) {
            System.err.println("[Gateway] Erro de comunicação ao rotear '" + command + "' para " + targetComponent + ": " + e.getMessage());
            return jmeterErrorResponse;
        }
    }

    private String sendWithTimeout(ComponentInfo target, String request, int timeout) throws Exception {
        int maxtentativas = 3;

        Exception lastException = null;

        for (int i = 0; i < maxtentativas; i++) {
            try {
                return internalClient.send(target.getAddress().getHostAddress(), target.getPort(), request);
            } catch (Exception e) {
                lastException = e;
                System.err.println("[Gateway] Tentativa " + (i + 1) + " falhou para " + target + ": " + e.getMessage());
                
                if (i < maxtentativas - 1) {
                    Thread.sleep(1000); // Espera aí
                }
            }
        }

        throw lastException;
    }

    private ComponentInfo findAvailableComponentRoundRobin(ComponentType type) {
        List<ComponentInfo> candidatos = serviceRegistry.values().stream()
                .filter(c -> c.getType() == type)
                .filter(c -> (System.currentTimeMillis() - c.getLastHeartbeat()) <= 30000) //quero os componentes ativos
                .collect(ArrayList::new , (list , item) -> list.add(item), List::addAll);

        if(candidatos.isEmpty()) {
            return null;
        }

        //Round robin
        AtomicInteger counter = (type == ComponentType.MINER) ? minerRobing : txProcessor;

        int index = counter.getAndIncrement() % candidatos.size();

        ComponentInfo escolhido = candidatos.get(index);
        System.out.println("[Gateway] Componente escolhido (round robin): " + escolhido);
        return escolhido;
    }

 

    private String handleRegistration(String[] parts) {
        try {
            // Formato esperado: REGISTER|IP|PORTA|TIPO
            InetAddress address = InetAddress.getByName(parts[1]);
            int componentPort = Integer.parseInt(parts[2]);
            ComponentType type = ComponentType.valueOf(parts[3]);
            String componentId = type + "@" + address.getHostAddress() + ":" + componentPort;
            
            serviceRegistry.put(componentId, new ComponentInfo(type, address, componentPort));
            System.out.println("[Gateway] Componente registrado: " + componentId);


            long minerCount = serviceRegistry.values().stream().filter(c -> c.getType() == ComponentType.MINER).count();
            long txCount = serviceRegistry.values().stream().filter(c -> c.getType() == ComponentType.TRANSACTION_PROCESSOR).count();
            System.out.printf("[Gateway] Status atual - Miners: %d, TransactionProcessors: %d\n", minerCount, txCount);

            return "SUCESSO: Registrado.";
        } catch (Exception e) {
            return "ERRO: Falha ao registrar componente. Formato inválido.";
        }
    }

    private String handleHeartbeat(String[] parts) {
        try {
            // Formato esperado: HEARTBEAT|IP|PORTA|TIPO
            String componentId = parts[3] + "@" + parts[1] + ":" + parts[2];
            ComponentInfo info = serviceRegistry.get(componentId);
            if (info != null) {
                info.updateHeartbeat();
                return "OK";
            }
            return "ERRO: Componente não registrado, não é possível atualizar heartbeat.";
        } catch(Exception e) {
            return "ERRO: Formato de heartbeat inválido.";
        }
    }


    private void startHeartbeatCheck() {
        // A cada 10 segundos, verifica se algum componente não envia heartbeat há mais de 15 segundos.

        heartbeatScheduler.scheduleAtFixedRate(() -> {
            long now = System.currentTimeMillis();
            serviceRegistry.entrySet().removeIf(entry -> {
                boolean isStale = (now - entry.getValue().getLastHeartbeat()) > 30000; // isStale = true se o componente está inativo
                if (isStale) {
                    System.out.println("[Gateway] Componente inativo removido: " + entry.getKey());
                }
                return isStale;
            });
        }, 20, 20, TimeUnit.SECONDS);
    }


    public static void main(String[] args) throws Exception {
        if (args.length < 2) {
            System.out.println("Uso: java Gateway <porta> <UDP|TCP|GRPC|HTTP>");
            return;
        }
        int port = Integer.parseInt(args[0]);
        CommunicationType commType = CommunicationType.valueOf(args[1].toUpperCase());
        new Gateway(port, commType).start();
    }


}


/*

    private String routeRequest(String command, String payload) {
        ComponentInfo targetComponent = null;
        String jmeterErrorResponse = "ERRO_GATEWAY: SERVICO_INDISPONIVEL";
        try {
            targetComponent = switch (command) {
                //case "ADD_TRANSACTION", "GET_MEMPOOL" -> findAvailableComponentRoundRobin(ComponentType.MINER);
                case "ADD_TRANSACTION", "GET_MEMPOOL" -> findAvailableComponentRoundRobin(ComponentType.TRANSACTION_PROCESSOR);
                case "MINE_BLOCK", "GET_BLOCK", "GET_BLOCKCHAIN", "ROUTE_TO_MINER" -> findAvailableComponentRoundRobin(ComponentType.MINER);
                //case "ROUTE_TO_MINER" -> findAvailableComponentRoundRobin(ComponentType.MINER);
                default -> null;
            };

            if (targetComponent == null) {
                System.err.println("[Gateway] Nenhum componente disponível para o comando " + command);
                return jmeterErrorResponse;
            }

            System.out.printf("[Gateway] Roteando '%s' para %s\n", command, targetComponent);

            int timeout = 10000;
            
            
            String internalRequest = command + "|" + payload;
            
            return sendWithTimeout(targetComponent, internalRequest, timeout);
            

        } catch (Exception e) {
            System.err.println("[Gateway] Erro de comunicação ao rotear '" + command + "' para " + targetComponent + ": " + e.getMessage());
            return jmeterErrorResponse;
        }
    }


            
            switch (command) {
                case "ADD_TRANSACTION":
                case "GET_MEMPOOL":
                    //targetComponent = findAvailableComponent(ComponentType.TRANSACTION_PROCESSOR);    
                    targetComponent = findAvailableComponentRoundRobin(ComponentType.TRANSACTION_PROCESSOR);
                    break;
                case "MINE_BLOCK":
                case "GET_BLOCK": 
                case "GET_BLOCKCHAIN":
                    //targetComponent = findAvailableComponent(ComponentType.MINER);
                    targetComponent = findAvailableComponentRoundRobin(ComponentType.MINER);
                    break;
                default:
                    return "ERRO: Comando desconhecido no Gateway.";
            }
            

   private ComponentInfo findAvailableComponent(ComponentType type) {
        // Lógica simples: pega o primeiro que encontrar.
        // 
        return serviceRegistry.values().stream()
                .filter(c -> c.getType() == type)
                .findFirst()
                .orElse(null);
    }


import java.util.concurrent.ConcurrentLinkedQueue;

public class Gateway {
    private final int port;
    private final CommunicationType communicationType;
    //private final ConcurrentLinkedQueue<Transaction> mempool = new ConcurrentLinkedQueue<>();
    //private final Block blockchain = new Block();
    private ComponentServer server;

    public Gateway(int port, CommunicationType type) {
        this.port = port;
        this.communicationType = type;
    }

    public void start() throws Exception {
        
        this.server = CommunicationFactory.createServer(communicationType);
        
        // O RequestHandler contém toda a lógica de negócio que antes estava no handleClient
        RequestHandler handler = (request) -> {
            System.out.println("Gateway recebeu: " + request);
            String[] parts = request.split("\\|", 2);
            String command = parts[0];
            String payload = (parts.length > 1) ? parts[1] : "";

            switch (command) {
                case "ADD_TRANSACTION":
                    //return handleAddTransaction(payload);
                    return sendAddToTransaction(request);
                case "GET_BLOCK":
                    //return handleGetBlock();
                    return sendToGetBlock(request);
                case "MINE_BLOCK":
                    //return handleMineBlock();
                    return sendGetToMiner(request);
                case "GET_MEMPOOL":
                    //return printBlockChain(mempool);
                    return sendToGetMempool(request);
                default:
                    return "ERRO: Comando desconhecido.";
            }
        };

        // Inicia o servidor (UDP ou outro) com a lógica de handling
        server.start(port, handler);
    }

    // Métodos para encaminhar as requisições aos componentes apropriados Miner e TransactionProcessor

    public void sendToGetBlock(String request) {
        
    }


    public void sendAddToTransaction(String request) {
        
    }

    public void sendGetToMiner(String request) {
        
    }

    public void sendToGetMempool(String request) {
        
    }




    // esses métodos aqui não devem ficar aqui, deixarei aqui só de backup 

    private String handleAddTransaction(String jsonPayload) {
        try {

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


    private String printBlockChain(ConcurrentLinkedQueue<Transaction> mempool) {
        StringBuilder sb = new StringBuilder();
        sb.append("Mempool atual:\n");
        for (Transaction tx : mempool) {
            sb.append(tx).append("\n");
        }
        return sb.toString();
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
 * 
 */