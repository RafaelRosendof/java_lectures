import java.util.Map;

// classsse para encapsular o proxy do minerador com add e get 

public class MinerProxy {
    private final String host;
    private final int port;
    private final ClientRequestor requestor;

    public MinerProxy(String host, int port) {
        this.host = host;
        this.port = port;
        this.requestor = new ClientRequestor();
    }

    // cria a transação e envia para o endereço do minerador
    public String addTransaction(String from, String to, double value, double fee) {
        try {
            Map<String, Object> body = Map.of(
                "from", from,
                "to", to,
                "value", value,
                "fee", fee
            );
            return requestor.send(host, port, "POST", "/add_transaction", body);
        } catch (Exception e) {
            return "Erro ao enviar transação: " + e.getMessage();
        }
    }

    public String getBlock() {
        try {
            Map<String, Object> body = Map.of();
            return requestor.send(host, port, "GET", "/getBlock", body);
        } catch (Exception e) {
            return "Erro ao recuperar bloco: " + e.getMessage();
        }
    }
}
