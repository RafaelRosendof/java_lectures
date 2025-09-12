import java.util.Map;


public class ClientRequestor {
    private final HttpClient client;
    private final String protocol; // TCP ou UDP

    public ClientRequestor() {
        this.client = new HttpClient();
        this.protocol = "TCP";
    }

    public String send(String host, int port, String method, String path, Map<String, Object> bodyParams) throws Exception {
        String body = "";
        if (bodyParams != null && !bodyParams.isEmpty()) {
            body = JsonUtil.toJson(bodyParams);
        }

        StringBuilder httpRequest = new StringBuilder();
        httpRequest.append(method.toUpperCase()).append(" ").append(path).append(" HTTP/1.1\r\n");
        httpRequest.append("Host: ").append(host).append(":").append(port).append("\r\n");

        if (!body.isEmpty()) {
            httpRequest.append("Content-Type: application/json\r\n");
            httpRequest.append("Content-Length: ").append(body.length()).append("\r\n");
            httpRequest.append("\r\n");
            httpRequest.append(body);
        } else {
            httpRequest.append("\r\n");
        }

        String httpResp = client.sendRequest(host, port, httpRequest.toString(), protocol);

        int bodyIndex = httpResp.indexOf("\r\n\r\n");
        if (bodyIndex != -1) {
            return httpResp.substring(bodyIndex + 4).trim();
        }

        return httpResp;
    }
}
