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
            //  HTTP Request
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

            //  Invoker
            Object result = invoke(message);

            //  Marshal Response
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
            throw new Exception("404 Not Found: " + routeKey);
        }

        String contentType = message.getHeaders().getOrDefault("Content-Type", "text/plain");
        
        // Determina os argumentos baseado no Content-Type e nos parâmetros do método
        Object[] args = determineMethodArguments(methodToInvoke, message, contentType);
        
        System.out.println("Invocando método: " + methodToInvoke.getName() + " com " + args.length + " argumentos");
        
        return methodToInvoke.invoke(serviceImplementation, args);
    }


    private Object[] determineMethodArguments(Method method, InvocationMessage message, String contentType) throws Exception {
        Class<?>[] paramTypes = method.getParameterTypes();
        
        // Se o método não tem parâmetros (como GET /blocks)
        if (paramTypes.length == 0) {
            return new Object[0];
        }
        
        // Se o método tem um parâmetro String (como nosso addTransaction modificado)
        if (paramTypes.length == 1 && paramTypes[0] == String.class) {
            return new Object[]{message.getBody()};
        }
        
        // Para múltiplos parâmetros, tenta diferentes estratégias
        if (contentType.contains("application/json")) {
            return parseJsonArguments(method, message.getBody());
        } else {
            return parseTextArguments(method, message.getBody());
        }
    }

    // Parse argumentos do JSON
    private Object[] parseJsonArguments(Method method, String body) throws Exception {
        try {
            Map<String, Object> params = JsonUtil.parseJson(body);
            Class<?>[] paramTypes = method.getParameterTypes();
            Object[] args = new Object[paramTypes.length];

            // Tenta usar nomes dos parâmetros se disponível (precisa -parameters no javac)
            java.lang.reflect.Parameter[] parameters = method.getParameters();
            
            for (int i = 0; i < paramTypes.length; i++) {
                String paramName = parameters[i].getName();
                Object value = params.get(paramName);
                
                // Se o nome do parâmetro é genérico (arg0, arg1), tenta estratégias alternativas
                if (value == null && paramName.startsWith("arg")) {
                    System.out.println("[RequestProcessor] Parâmetro genérico detectado: " + paramName);
                    // Poderia tentar mapear por posição ou outros critérios aqui
                    throw new Exception("Não foi possível mapear parâmetros JSON. Parâmetro '" + paramName + "' não encontrado no JSON");
                }

                if (value == null) {
                    throw new Exception("Parâmetro obrigatório não encontrado: " + paramName);
                }

                args[i] = castValue(paramTypes[i], value);
            }

            return args;
        } catch (Exception e) {
            System.err.println("[RequestProcessor] Erro no parse JSON: " + e.getMessage());
            throw e;
        }
    }

    // Parse argumentos do texto (formato: valor1;valor2;valor3;valor4)
    private Object[] parseTextArguments(Method method, String body) throws Exception {
        String[] parts = body.split(";");
        Class<?>[] paramTypes = method.getParameterTypes();
        
        if (parts.length != paramTypes.length) {
            throw new Exception("Número de parâmetros não confere. Esperado: " + paramTypes.length + ", Recebido: " + parts.length);
        }

        Object[] args = new Object[paramTypes.length];
        for (int i = 0; i < paramTypes.length; i++) {
            args[i] = castValue(paramTypes[i], parts[i].trim());
        }

        return args;
    }

    private Object castValue(Class<?> type, Object value) {
        try {
            if (type == double.class || type == Double.class) {
                return Double.parseDouble(value.toString());
            } else if (type == int.class || type == Integer.class) {
                return Integer.parseInt(value.toString());
            } else if (type == long.class || type == Long.class) {
                return Long.parseLong(value.toString());
            } else {
                return value.toString();
            }
        } catch (NumberFormatException e) {
            throw new RuntimeException("Erro ao converter valor '" + value + "' para tipo " + type.getSimpleName());
        }
    }

    // Marshal Response de sucesso
    private String marshalResponse(Object result) {
        try {
            String responseBody;
            String contentType;
            
            if (result == null) {
                responseBody = "null";
                contentType = "text/plain";
            } else if (result instanceof String) {
                responseBody = result.toString();
                contentType = "text/plain";
            } else {
                // Para objetos complexos, converte para JSON
                responseBody = JsonUtil.toJson(Map.of("response", result.toString()));
                contentType = "application/json";
            }
            
            StringBuilder response = new StringBuilder();
            response.append("HTTP/1.1 200 OK\r\n");
            response.append("Content-Type: ").append(contentType).append("\r\n");
            response.append("Content-Length: ").append(responseBody.getBytes().length).append("\r\n");
            response.append("Connection: close\r\n");
            response.append("\r\n");
            response.append(responseBody);
            
            return response.toString();
        } catch (Exception e) {
            return marshalErrorResponse("Erro ao serializar resposta: " + e.getMessage());
        }
    }

    // Marshal Response de erro
    private String marshalErrorResponse(String errorMessage) {
        try {
            String errorBody = JsonUtil.toJson(Map.of("error", errorMessage));
            StringBuilder response = new StringBuilder();
            response.append("HTTP/1.1 500 Internal Server Error\r\n");
            response.append("Content-Type: application/json\r\n");
            response.append("Content-Length: ").append(errorBody.getBytes().length).append("\r\n");
            response.append("Connection: close\r\n");
            response.append("\r\n");
            response.append(errorBody);
            return response.toString();
        } catch (Exception e) {
            String simpleError = "{\"error\":\"Erro interno do servidor\"}";
            return "HTTP/1.1 500 Internal Server Error\r\n" +
                   "Content-Type: application/json\r\n" +
                   "Content-Length: " + simpleError.getBytes().length + "\r\n" +
                   "Connection: close\r\n\r\n" +
                   simpleError;
        }
    }







}


    /* 
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

    */

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