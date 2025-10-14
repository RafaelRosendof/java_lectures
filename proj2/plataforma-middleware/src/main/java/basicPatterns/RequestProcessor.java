package basicPatterns;

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

import ExtensionPatterns.InterceptorChain;
import ExtensionPatterns.InvocationContext;
import invoke.InvocationMessage;

public class RequestProcessor implements Runnable {

    private final Socket clientSocket;
    private final Map<String, Method> routeMap;
    private final Object serviceImplementation;
    private final InterceptorChain interceptorChain;

    public RequestProcessor(Socket socket, Map<String, Method> routeMap, Object service, InterceptorChain interceptorChain) {
        this.clientSocket = socket;
        this.routeMap = routeMap;
        this.serviceImplementation = service;
        this.interceptorChain = interceptorChain;
    }

    @Override
    public void run() {
        InvocationContext context = null;
        Object result = null;

        try (
            InputStream input = clientSocket.getInputStream();
            OutputStream output = clientSocket.getOutputStream();
            BufferedReader reader = new BufferedReader(new InputStreamReader(input));
            PrintWriter writer = new PrintWriter(output, true)
        ) {
            // Lê HTTP request
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

            // Encontrar método
            String routeKey = httpMethod.toUpperCase() + ":" + path;
            Method methodToInvoke = routeMap.get(routeKey);

            if (methodToInvoke == null) {
                String httpResponse = marshalErrorResponse("404 Not Found: " + routeKey);
                writer.println(httpResponse);
                return;
            }

            // Determinar argumentos
            String contentType = headers.getOrDefault("Content-Type", "text/plain");
            InvocationMessage message = new InvocationMessage(httpMethod, path, headers, body);
            Object[] args = determineMethodArguments(methodToInvoke, message, contentType);

            // Criar contexto de invocação COM OS PARÂMETROS CORRETOS
            String clientAddress = clientSocket.getRemoteSocketAddress().toString();
            context = new InvocationContext(
                serviceImplementation,  // target object
                methodToInvoke,        // method
                args,                  // arguments
                clientAddress          // client address
            );

            System.out.println("Requisição Recebida: " + message);

            try {
                // BEFORE interceptors
                interceptorChain.beforeInvocation(context);
                
                // Invocação real do método
                result = methodToInvoke.invoke(serviceImplementation, args);
                
                // AFTER interceptors
                interceptorChain.afterInvocation(context, result);
                
                // Marshal response de sucesso
                String httpResponse = marshalResponse(result);
                writer.println(httpResponse);
                
            } catch (Exception invocationError) {
                // ERROR interceptors
                interceptorChain.onError(context, invocationError);
                
                // Marshal response de erro
                String httpResponse = marshalErrorResponse("Erro na invocação: " + invocationError.getMessage());
                writer.println(httpResponse);
            }

        } catch (Exception e) {
            System.err.println("Erro ao processar requisição: " + e.getMessage());
            e.printStackTrace();
            
            // Se temos um contexto, notifica os interceptors
            if (context != null && interceptorChain != null) {
                interceptorChain.onError(context, e);
            }
        } finally {
            try {
                clientSocket.close();
            } catch (IOException e) {
                // ignora
            }
        }
    }

    private Object[] determineMethodArguments(Method method, InvocationMessage message, String contentType) throws Exception {
        Class<?>[] paramTypes = method.getParameterTypes();
        
        // se o método não tem parâmetros (como GET /blocks)
        if (paramTypes.length == 0) {
            return new Object[0];
        }
        
        // se o método tem um parâmetro String (como nosso addTransaction modificado)
        if (paramTypes.length == 1 && paramTypes[0] == String.class) {
            return new Object[]{message.getBody()};
        }
        
        // múltiplos parâmetros, tenta diferentes estratégias seja texto ou json
        if (contentType.contains("application/json")) {
            return parseJsonArguments(method, message.getBody());
        } else {
            return parseTextArguments(method, message.getBody());
        }
    }

    // parser para argumentos do JSON
    private Object[] parseJsonArguments(Method method, String body) throws Exception {
        try {
            Map<String, Object> params = JsonUtil.parseJson(body);
            Class<?>[] paramTypes = method.getParameterTypes();
            Object[] args = new Object[paramTypes.length];

            // caso tenha os nomes dos parâmetros disponíveis (javac -parameters)
            java.lang.reflect.Parameter[] parameters = method.getParameters();
            
            for (int i = 0; i < paramTypes.length; i++) {
                String paramName = parameters[i].getName();
                Object value = params.get(paramName);
                
                // caso seja do tipo genérico arg0, arg1, etc (sem -parameters)
                if (value == null && paramName.startsWith("arg")) {
                    System.out.println("[RequestProcessor] Parâmetro genérico detectado: " + paramName);
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

    // parseando dado o método e body separado por ; caso de text/plain
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

    // método auxiliar para converter o valor para o tipo correto
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

    // Marshal sucesso
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

    // Marshal erro
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
