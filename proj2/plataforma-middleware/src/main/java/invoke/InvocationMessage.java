package invoke;
import java.util.HashMap;
import java.util.Map;

// classe para encapsular a mensagem de invocação HTTP com get e toString

public class InvocationMessage {
    private String httpMethod;
    private String path;
    private Map<String, String> headers = new HashMap<>();
    private String body;

    public InvocationMessage(String httpMethod, String path, Map<String, String> headers, String body) {
        this.httpMethod = httpMethod;
        this.path = path;
        this.headers = headers;
        this.body = body;
    }

    public String getHttpMethod() {
        return httpMethod;
    }

    public String getPath() {
        return path;
    }

    public Map<String, String> getHeaders() {
        return headers;
    }

    public String getHeader(String key) {
        return headers.get(key);
    }

    public String getBody() {
        return body;
    }

    @Override
    public String toString() {
        return "InvocationMessage{" +
                "httpMethod='" + httpMethod + '\'' +
                ", path='" + path + '\'' +
                ", headers=" + headers +
                ", body='" + body + '\'' +
                '}';
    }
}
