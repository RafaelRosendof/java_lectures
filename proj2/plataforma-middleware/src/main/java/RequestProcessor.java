import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.lang.reflect.Method;
import java.net.Socket;
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
            // **2a. Parsear a Requisição HTTP**
            String requestLine = reader.readLine();
            if (requestLine == null) return; // Conexão fechada

            String[] requestParts = requestLine.split(" ");
            String httpMethod = requestParts[0];
            String path = requestParts[1];

            // Ler os Headers para encontrar o Content-Length
            int contentLength = 0;
            String line;
            while (!(line = reader.readLine()).isEmpty()) {
                if (line.startsWith("Content-Length:")) {
                    contentLength = Integer.parseInt(line.substring(16).trim());
                }
            }

            // Ler o corpo (body) da requisição
            char[] bodyChars = new char[contentLength];
            reader.read(bodyChars, 0, contentLength);
            String body = new String(bodyChars);
            
            System.out.println("Requisição Recebida: " + httpMethod + " " + path);
            System.out.println("Corpo: " + body);

            // **2b. Chamar o Invoker**
            Object result = invoke(httpMethod, path, body);

            // **2c. Marshal e Enviar a Resposta**
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

    // **Invoker**: Lógica de invocação usando Reflection
    private Object invoke(String httpMethod, String path, String body) throws Exception {
        String routeKey = httpMethod.toUpperCase() + ":" + path;
        Method methodToInvoke = routeMap.get(routeKey);

        if (methodToInvoke == null) {
            return "ERRO: Rota não encontrada - " + routeKey;
        }

        // **Unmarshal**: Transforma o corpo da requisição nos parâmetros do método
        // Neste caso, o corpo é a string "valor;valor;...", e o método espera
        // vários parâmetros do tipo String, int, double etc.
        String[] params = body.split(";");

        // *** IMPORTANTE: Esta parte precisa ser mais robusta ***
        // A conversão de String para os tipos corretos (int, double) é necessária.
        // Por simplicidade, vamos assumir que o método aceita um array de Strings.
        try {
            // Exemplo simples: o método aceita um único parâmetro que é o array de strings
            // return methodToInvoke.invoke(serviceImplementation, (Object) params);
            
            // Exemplo mais realista: convertendo os parâmetros
            // Este código assume que o método sempre terá a mesma assinatura
            String from = params[0];
            String to = params[1];
            double value = Double.parseDouble(params[2]);
            double fee = Double.parseDouble(params[3]);
            return methodToInvoke.invoke(serviceImplementation, from, to, value, fee);

        } catch (Exception e) {
            return "ERRO ao invocar método: " + e.getMessage();
        }
    }


    // Marshal Cria a string de resposta HTTP
    private String marshalResponse(Object result) {
        String jsonBody = "{\"response\": \"" + result.toString() + "\"}";
        StringBuilder response = new StringBuilder();
        response.append("HTTP/1.1 200 OK\r\n");
        response.append("Content-Type: application/json\r\n");
        response.append("Content-Length: ").append(jsonBody.length()).append("\r\n");
        response.append("\r\n"); // Linha em branco crucial
        response.append(jsonBody);
        return response.toString();
    }
}