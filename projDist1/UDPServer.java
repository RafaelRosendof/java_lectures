import java.io.IOException;
import java.net.*;

public class UDPServer implements ComponentServer {
    private DatagramSocket socket;
    private boolean running = true;

    @Override
    public void start(int port, RequestHandler handler) {
        try {
            socket = new DatagramSocket(port);
            byte[] buffer = new byte[4096]; // Buffer para receber dados

            while (running) {
                DatagramPacket packet = new DatagramPacket(buffer, buffer.length);
                socket.receive(packet); // Bloqueia até receber um pacote

                String request = new String(packet.getData(), 0, packet.getLength());
                String response = handler.handle(request);
                
                // Envia a resposta de volta para quem enviou
                byte[] responseBytes = response.getBytes();
                DatagramPacket responsePacket = new DatagramPacket(
                    responseBytes, responseBytes.length, packet.getAddress(), packet.getPort());
                socket.send(responsePacket);
            }
        } catch (IOException e) {
            if (running) e.printStackTrace();
        }
    }

    @Override
    public void stop() {
        running = false;
        if (socket != null) socket.close();
    }
}