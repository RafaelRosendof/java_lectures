import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.lang.reflect.Method;
import java.net.Socket;
import java.util.HashMap;
import java.util.Map;

public class RequestProcessor implements Runnable {

    private final Socket clientSocket;
    private final Map<String, Method> routeMap;
    private final Object serviceImplementation;

    public RequestProcessor(Socket socket, Map<String, Method> routeMap, Object service) {
        this.clientSocket = socket;
        this.routeMap = routeMap;
        this.serviceImplementation = service;
    }

    @Override
    public void run() {
        try (
            InputStream input = clientSocket.getInputStream();
            OutputStream output = clientSocket.getOutputStream();
            BufferedReader reader = new BufferedReader(new InputStreamReader(input));
            PrintWriter writer = new PrintWriter(output, true)
        ) {
            // 🔹 Parse HTTP Request
            String requestLine = reader.readLine();
            if (requestLine == null) return;

            String[] requestParts = requestLine.split(" ");
            String httpMethod = requestParts[0];
            String path = requestParts[1];

            Map<String, String> headers = new HashMap<>();
            int contentLength = 0;
            String line;
            while (!(line = reader.readLine()).isEmpty()) {
                String[] headerParts = line.split(":", 2);
                if (headerParts.length == 2) {
                    headers.put(headerParts[0].trim(), headerParts[1].trim());
                }
                if (line.startsWith("Content-Length:")) {
                    contentLength = Integer.parseInt(line.substring(16).trim());
                }
            }

            String body = "";
            if (contentLength > 0) {
                char[] bodyChars = new char[contentLength];
                reader.read(bodyChars, 0, contentLength);
                body = new String(bodyChars);
            }

            InvocationMessage message = new InvocationMessage(httpMethod, path, headers, body);
            System.out.println("Requisição Recebida: " + message);

            // 🔹 Invoker
            Object result = invoke(message);

            // 🔹 Marshal Response
            String httpResponse = marshalResponse(result);
            writer.println(httpResponse);

        } catch (Exception e) {
            System.err.println("Erro ao processar requisição: " + e.getMessage());
            e.printStackTrace();
        } finally {
            try {
                clientSocket.close();
            } catch (IOException e) {
                // ignora
            }
        }
    }

    // Invoker
    private Object invoke(InvocationMessage message) throws Exception {
        String routeKey = message.getHttpMethod().toUpperCase() + ":" + message.getPath();
        Method methodToInvoke = routeMap.get(routeKey);

        if (methodToInvoke == null) {
            throw new Exception("404 Not Found " + routeKey);
        }

        //  Exemplo de body: {"from":"walletA","to":"walletB","value":100,"fee":5}
        Map<String, Object> params = JsonUtil.parseJson(message.getBody()); //Jackson
        Class<?>[] paramTypes = methodToInvoke.getParameterTypes();
        Object[] args = new Object[paramTypes.length];

        for (int i = 0; i < paramTypes.length; i++) {
            String paramName = methodToInvoke.getParameters()[i].getName(); // precisa -parameters no javac
            Object value = params.get(paramName);

            if (typeMatch(paramTypes[i], value)) {
                args[i] = castValue(paramTypes[i], value);
            } else {
                throw new Exception("Parameter type mismatch for " + paramName);
            }
        }

        return methodToInvoke.invoke(serviceImplementation, args);
    }

    private boolean typeMatch(Class<?> type, Object value) {
        if (value == null) return false;
        if (type == double.class || type == Double.class) return true;
        if (type == int.class || type == Integer.class) return true;
        return type == String.class;
    }

    private Object castValue(Class<?> type, Object value) {
        if (type == double.class || type == Double.class) {
            return Double.parseDouble(value.toString());
        } else if (type == int.class || type == Integer.class) {
            return Integer.parseInt(value.toString());
        } else {
            return value.toString();
        }
    }

    // Marshal Response
    private String marshalResponse(Object result) {
        try {
            String jsonBody = JsonUtil.toJson(Map.of("response", result.toString()));
            StringBuilder response = new StringBuilder();
            response.append("HTTP/1.1 200 OK\r\n");
            response.append("Content-Type: application/json\r\n");
            response.append("Content-Length: ").append(jsonBody.length()).append("\r\n");
            response.append("\r\n");
            response.append(jsonBody);
            return response.toString();
        } catch (Exception e) {
            return "HTTP/1.1 500 ERROR\r\n\r\n{\"error\":\"Erro ao serializar resposta\"}";
        }
    }

}


/*
 * 
Colocar dentro do run 

// dentro do run(), substituímos o parsing atual:
String requestLine = reader.readLine();
if (requestLine == null) return;

String[] requestParts = requestLine.split(" ");
String httpMethod = requestParts[0];
String path = requestParts[1];

Map<String, String> headers = new HashMap<>();
int contentLength = 0;
String line;
while (!(line = reader.readLine()).isEmpty()) {
    String[] headerParts = line.split(":", 2);
    if (headerParts.length == 2) {
        headers.put(headerParts[0].trim(), headerParts[1].trim());
    }
    if (line.startsWith("Content-Length:")) {
        contentLength = Integer.parseInt(line.substring(16).trim());
    }
}

// Ler corpo
String body = "";
if (contentLength > 0) {
    char[] bodyChars = new char[contentLength];
    reader.read(bodyChars, 0, contentLength);
    body = new String(bodyChars);
}

// Agora temos a InvocationMessage
InvocationMessage message = new InvocationMessage(httpMethod, path, headers, body);

System.out.println("Requisição Recebida: " + message);

// Invoker
Object result = invoke(message);

// Marshal
String httpResponse = marshalResponse(result);
writer.println(httpResponse);


private Object invoke(InvocationMessage message) throws Exception {
    String routeKey = message.getHttpMethod().toUpperCase() + ":" + message.getPath();
    Method methodToInvoke = routeMap.get(routeKey);

    if (methodToInvoke == null) {
        throw new Exception("404 Not Found " + routeKey);
    }

    String[] params = message.getBody().split(";");
    try {
        // fixo (por enquanto): from, to, value, fee
        String from = params[0];
        String to = params[1];
        double value = Double.parseDouble(params[2]);
        double fee = Double.parseDouble(params[3]);
        return methodToInvoke.invoke(serviceImplementation, from, to, value, fee);

    } catch (Exception e) {
        return "ERRO ao invocar método: " + e.getMessage();
    }
}
 */