import java.net.InetAddress;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

/*
 * Classe responsável por gerenciar uma abstração entre o gateway e os componentes. N componentes vão herdar dessa classe aqui
 */

public abstract class BaseComponent {

    protected final int componentPort;
    protected final String gatwayHost;
    protected final int gatewayPort;
    protected final CommunicationType commType;
    protected final ComponentType componentType;

    private ComponentServer server;
    private final ComponentClient client;
    private final ScheduledExecutorService heartbeatScheduler = Executors.newSingleThreadScheduledExecutor();

    public BaseComponent(int componentPort, String gatewayHost, int gatewayPort, CommunicationType commType, ComponentType componentType) {
        this.componentPort = componentPort;
        this.gatewayHost = gatewayHost;
        this.gatewayPort = gatewayPort;
        this.commType = commType;
        this.componentType = componentType;
        this.clientToGateway = CommunicationFactory.createClient(commType);
    }

    public void start() throws Exception {

        server = CommunicationFactory.createServer(commType);
        server.start(componentPort , getRequestHandler());

        System.out.printf("[%s] Servidor iniciado na porta %d\n", componentType, componentPort);

        registerWithGateway();
        startHeartbeat();
    }

    private void registerWithGateway() throws Exception {
        // protocolo -> registra o meu ip + porta + tipo UDP, grpc, http
        String myIp = InetAddress.getLocalHost().getHostAddress(); // talvez coloque o docker dps aqui
        String request = String.format("REGISTER|%s|%d|%s", myIp, componentPort, componentType);
        String response = clientToGateway.send(gatewayHost, gatewayPort, request);
        System.out.printf("[%s] Resposta do registro no Gateway: %s\n", componentType, response);

    }

    private void startHeartbeat() {
        heartbeatScheduler.scheduleAtFixedRate(() -> {
            try {
                String myIp = InetAddress.getLocalHost().getHostAddress();
                // Protocolo: HEARTBEAT|MEU_IP|MINHA_PORTA|MEU_TIPO
                String request = String.format("HEARTBEAT|%s|%d|%s", myIp, componentPort, componentType);
                clientToGateway.send(gatewayHost, gatewayPort, request);
            } catch (Exception e) {
                System.err.printf("[%s] Falha ao enviar heartbeat: %s\n", componentType, e.getMessage());
            }
        }, 5, 5, TimeUnit.SECONDS); // Envia a cada 5 segundos
    }

    // cada componente vai implementar o handling de requs
    protected abstract RequestHandler getRequestHandler();

    public void stop() {
        heartbeatScheduler.shutdownNow();
        if (server != null) server.stop();
    }
}