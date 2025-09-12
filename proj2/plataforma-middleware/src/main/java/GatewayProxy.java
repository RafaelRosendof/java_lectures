import java.util.Map;

public class GatewayProxy {
    private final String gatewayHost;
    private final int gatewayPort;
    private final ClientRequestor requestor;

    public GatewayProxy(String host, int port) {
        this.gatewayHost = host;
        this.gatewayPort = port;
        this.requestor = new ClientRequestor();
    }

    // Método para enviar transação (espelha o serviço no GatewayService)
    public String addTransaction(String from, String to, double value, double fee) {
        try {
            Map<String, Object> body = Map.of(
                "from", from,
                "to", to,
                "value", value,
                "fee", fee
            );
            return requestor.send(gatewayHost, gatewayPort, "POST", "/add_transaction", body);
        } catch (Exception e) {
            return "Erro ao enviar transação para gateway: " + e.getMessage();
        }
    }

    // Método para recuperar um bloco (espelha o serviço no GatewayService)
    public String getBlock() {
        try {
            return requestor.send(gatewayHost, gatewayPort, "GET", "/getBlock", null);
        } catch (Exception e) {
            return "Erro ao recuperar bloco do gateway: " + e.getMessage();
        }
    }
}
