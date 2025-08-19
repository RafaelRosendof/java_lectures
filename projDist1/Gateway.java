import java.io.*;
import java.net.*;
import java.util.concurrent.*;

public class Gateway {

    /*
     * 
     * Tenho que implementar aqui um padrão heartbeat para o gateway
     * Assim, o getway vai checar periodicamente se o block está vivo
     * Se não estiver, o gateway deve iniciar um novo block
     * 
     * Assim que o minerador estiver pronto, ele avisa o gateway
     * E o gateway passa a usar esse minerador isso vai ser guardado com um Map<String , ComponentInfo>
     * Onde ComponentInfo tem o IP, a porta e o timestamp do último heartbeat
     * 
     * Implementar uma função que recebe o heartbeat do minerador 
     * 
     * 
    Hoje, o Gateway executa tudo dentro dele (mempool + miner).

    Mas a arquitetura pede que ele seja somente o roteador.

    Ou seja:

    O Gateway não deveria minerar nem guardar a mempool.

    Ele só deveria receber requisições e repassar para os componentes corretos (que estão rodando como instâncias separadas).

    
     */


    private int port;
    public ConcurrentLinkedQueue<Transaction> mempool = new ConcurrentLinkedQueue<>();

    private Block blockchain = new Block();


    public Gateway(int port) {
        this.port = port;
    }

    public void start() throws IOException {
        try (ServerSocket serverSocket = new ServerSocket(port)) {
            System.out.println("Gateway escutando na porta " + port);

            while (true) {
                Socket client = serverSocket.accept();
                new Thread(() -> handleClient(client)).start();
            }
        }
    }

    private void handleClient(Socket client) {
        try (BufferedReader in = new BufferedReader(new InputStreamReader(client.getInputStream()));
             OutputStream out = client.getOutputStream()) {

            String requestLine = in.readLine();
            if (requestLine == null || requestLine.isEmpty()) {
                out.write("HTTP/1.1 400 Bad Request\r\n\r\n".getBytes());
                return;
            }

            String[] parts = requestLine.split(" ");
            if (parts.length < 2) {
                out.write("HTTP/1.1 400 Bad Request\r\n\r\n".getBytes());
                return;
            }

            String method = parts[0];
            String path = parts[1];

            int contentLen = 0;
            String line;
            while (!(line = in.readLine()).isEmpty()) {
                if (line.toLowerCase().startsWith("content-length:")) {
                    contentLen = Integer.parseInt(line.split(":")[1].trim());
                }
            }

            if (method.equals("POST") && path.equals("/transaction")) {

                char[] body = new char[contentLen];
                int readSoFar = 0;
                while (readSoFar < contentLen) {
                    int r = in.read(body, readSoFar, contentLen - readSoFar);
                    if (r == -1) break; // cliente fechou conexão
                    readSoFar += r;
                }
                String bodyString = new String(body, 0, readSoFar);

                

                String[] partsBody = bodyString.replace("{", "").replace("}", "").replace("\"", "").split(",");
                String from = partsBody[0].split(":")[1].trim();
                String to = partsBody[1].split(":")[1].trim();
                double value = Double.parseDouble(partsBody[2].split(":")[1].trim());
                double fee = Double.parseDouble(partsBody[3].split(":")[1].trim());

                Transaction txData = new Transaction(from, to, value, fee);
                mempool.add(txData);
                
                System.out.println("Nova transação recebida: " + txData);
                sendResponse(out, "Transação adicionada com sucesso.");

            } else if (method.equals("GET") && path.equals("/block")) {
                
                String result = blockchain.printBlockChain(mempool);
                sendResponse(out, "Bloco atual: \n" + result);
                System.out.println("Bloco atual solicitado.");

            }else if (method.equals("GET") && path.equals("/mine")) {
                String mined = blockchain.mineBlock(mempool);
                sendResponse(out, "Bloco minerado:\n" + mined);
                System.out.println("Bloco minerado solicitado.");
            }else {
                sendNotFound(out);
            }

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void sendResponse(OutputStream out, String body) throws IOException {
        String httpResponse = "HTTP/1.1 200 OK\r\n" +
                "Content-Type: text/plain\r\n" +
                "Content-Length: " + body.length() + "\r\n" +
                "\r\n" +
                body;
        out.write(httpResponse.getBytes());
    }

    private void sendNotFound(OutputStream out) throws IOException {
        String body = "Not Found";
        String httpResponse = "HTTP/1.1 404 Not Found\r\n" +
                "Content-Type: text/plain\r\n" +
                "Content-Length: " + body.length() + "\r\n" +
                "\r\n" +
                body;
        out.write(httpResponse.getBytes());
    }

    public static void main(String[] args) throws IOException {
        Gateway gateway = new Gateway(8080);
        gateway.start();
    }
}
