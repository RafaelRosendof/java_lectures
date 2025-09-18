import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.Socket;
import java.nio.Buffer;

public class HttpClient {
    
    public String sendRequest(String host , int port , String httpRequest , String protocol) throws Exception{

        if (protocol.equals("TCP")){ 
            // 
            return sendTCP(host, port, httpRequest);
        }

        if (protocol.equals("UDP")){
            return sendUDP(host, port, httpRequest);
        }

        throw new Exception("Protocol not supported");
    }

    // implementa com tcp 
    private String sendTCP(String host, int port, String httpRequest) throws Exception {
        try (Socket socket = new Socket(host, port)) {
            OutputStream out = socket.getOutputStream();
            BufferedReader reader = new BufferedReader(new InputStreamReader(socket.getInputStream()));

            out.write(httpRequest.getBytes());
            out.flush();

            StringBuilder resp = new StringBuilder();
            String line;
            while ((line = reader.readLine()) != null) {
                resp.append(line).append("\n");
            }
            return resp.toString();
        } catch (Exception e) {
            throw new Exception("Error in TCP request: " + e.getMessage());
        }
    }

    // http 3.0 com udp
    private String sendUDP(String host, int port, String httpRequest) throws Exception {
        try (DatagramSocket socket = new DatagramSocket()) {
            InetAddress address = InetAddress.getByName(host);

            // requisição
            byte[] sendData = httpRequest.getBytes();
            DatagramPacket sendPacket = new DatagramPacket(sendData, sendData.length, address, port);
            socket.send(sendPacket);

            // resposta
            byte[] receiveBuffer = new byte[4096];
            DatagramPacket receivePacket = new DatagramPacket(receiveBuffer, receiveBuffer.length);
            socket.receive(receivePacket);

            return new String(receivePacket.getData(), 0, receivePacket.getLength());
        } catch (Exception e) {
            throw new Exception("Error in UDP request: " + e.getMessage());
        }
    }
}
