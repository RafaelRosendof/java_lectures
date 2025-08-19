import java.io.IOException;
import java.net.*;

public class UDPClient implements ComponentClient {
    @Override
    public String send(String host, int port, String request) {
        try (DatagramSocket socket = new DatagramSocket()) {
            InetAddress address = InetAddress.getByName(host);
            byte[] requestBytes = request.getBytes();

            DatagramPacket requestPacket = new DatagramPacket(requestBytes, requestBytes.length, address, port);
            socket.send(requestPacket);

            // UDP é connectionless, então receber resposta é mais complexo.
            // Para simplicidade, podemos esperar uma resposta imediata.
            byte[] buffer = new byte[4096];
            DatagramPacket responsePacket = new DatagramPacket(buffer, buffer.length);
            socket.setSoTimeout(5000); // Timeout de 5 segundos
            socket.receive(responsePacket);
            
            return new String(responsePacket.getData(), 0, responsePacket.getLength());
        } catch (IOException e) {
            e.printStackTrace();
            return "Erro: " + e.getMessage();
        }
    }
}