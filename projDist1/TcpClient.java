import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;

public class TcpClient implements ComponentClient {
    
    @Override
    public String send(String host, int port, String request) {
        try (
            Socket socket = new Socket(host, port);
            PrintWriter out = new PrintWriter(socket.getOutputStream(), true);
            BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
        ) {
            out.println(request); // Envia a requisição com uma nova linha
            return in.readLine(); // Lê a resposta
        } catch (IOException e) {
            e.printStackTrace();
            return "Erro: " + e.getMessage();
        }
    }
}
