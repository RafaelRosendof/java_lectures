import java.io.IOException;
import java.net.*;

public class UDPServer implements ComponentServer {
    private DatagramSocket socket;
    private volatile boolean running = false;

    @Override
    public void start(int port, RequestHandler handler) throws SocketException {
        socket = new DatagramSocket(port);
        running = true;
        System.out.println("Servidor UDP escutando na porta " + port);

        // O servidor roda em uma nova thread para não bloquear a aplicação principal.
        new Thread(() -> {
            byte[] buffer = new byte[65535]; // Buffer grande para transações em JSON

            while (running) {
                try {
                    DatagramPacket packet = new DatagramPacket(buffer, buffer.length);
                    socket.receive(packet); // Bloqueia até receber um pacote

                    String request = new String(packet.getData(), 0, packet.getLength());
                    String response = handler.handle(request);
                    
                    // Envia a resposta de volta para o remetente
                    byte[] responseBytes = response.getBytes();
                    DatagramPacket responsePacket = new DatagramPacket(
                        responseBytes, responseBytes.length, packet.getAddress(), packet.getPort());
                    socket.send(responsePacket);
                } catch (IOException e) {
                    if (running) {
                        System.err.println("Erro no servidor UDP: " + e.getMessage());
                    }
                }
            }
        }).start();
    }

    @Override
    public void stop() {
        running = false;
        if (socket != null && !socket.isClosed()) {
            socket.close();
        }
        System.out.println("Servidor UDP parado.");
    }
}