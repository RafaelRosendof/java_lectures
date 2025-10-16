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
            System.out.println("Gateway recebeu: " + requestWithSenderInfo);
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
                
                case "ADD_TRANSACTION" -> findAvailableComponentRoundRobin(ComponentType.TRANSACTION_PROCESSOR);
            
                
                case "ROUTE_TO_MINER" -> findAvailableComponentRoundRobin(ComponentType.MINER);
                
                
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
