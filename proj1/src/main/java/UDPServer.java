import java.io.IOException;
import java.net.*;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;


public class UDPServer implements ComponentServer {
    private DatagramSocket socket;
    private volatile boolean running = false;
    private ExecutorService threadPool;

    private static final int BUFFER_SIZE = 16384;
    private static final int THREAD_POOL_SIZE = 500;


    @Override
    public void start(int port , RequestHandler handler) throws SocketException {
        socket = new DatagramSocket(port);
        socket.setReceiveBufferSize(1024 * 1024);
        socket.setSendBufferSize(1024 * 1024); 

        threadPool = Executors.newFixedThreadPool(THREAD_POOL_SIZE);
        running = true;
        System.out.println("Servidor UDP escutando na porta " + port);

        new Thread( () -> {
            while(running){
                try{
                    byte[] buffer = new byte[BUFFER_SIZE];
                    DatagramPacket packet = new DatagramPacket(buffer, buffer.length);
                    socket.receive(packet); 

                    threadPool.submit( () -> handlePacket(packet, handler) );
                } catch(IOException e){
                    if(running){
                        System.err.println("Erro no servidor UDP: " + e.getMessage());
                    }
                }
            }
        }).start();


        //thread que monitora thread pool 
        new Thread(() -> {
            while(running) {
                try{
                    Thread.sleep(10000);
                    if(threadPool instanceof ThreadPoolExecutor){
                        ThreadPoolExecutor tpe = (ThreadPoolExecutor) threadPool;
                        System.out.printf("[UDPServer] Pool status - Ativas: %d, Completadas: %d, Queue: %d\n",
                                         tpe.getActiveCount(), tpe.getCompletedTaskCount(), tpe.getQueue().size());
                    }
                }catch(InterruptedException e){
                    break;
                }
            }
        }).start();
    }

    private void handlePacket(DatagramPacket packet , RequestHandler handler){
        try{
            String request = new String(packet.getData(), 0 , packet.getLength());

            if(packet.getLength() > BUFFER_SIZE * 0.9){
                System.out.println("[UDP] AVISO: Packet grande: " + packet.getLength() + " bytes");
            }

            String response = handler.handle(request);

            byte[] responseBytes = response.getBytes();
            if (responseBytes.length > BUFFER_SIZE) {
                System.err.println("[UDP] ERRO: Resposta muito grande: " + responseBytes.length + " bytes");
                response = "ERRO: Resposta muito grande para UDP";
                responseBytes = response.getBytes();
            }

            DatagramPacket responsePacket = new DatagramPacket(
                responseBytes, responseBytes.length, packet.getAddress(), packet.getPort());

            synchronized (socket) { // Sincroniza envio
                socket.send(responsePacket);
            }  
        
        }catch(IOException e) {
            System.err.println("[UDP] Erro ao processar pacote: " + e.getMessage());
        }
    }
    
    @Override
    public void stop() {
        running = false;
        
        if (threadPool != null) {
            threadPool.shutdown();
            try {
                if (!threadPool.awaitTermination(5, TimeUnit.SECONDS)) {
                    threadPool.shutdownNow();
                }
            } catch (InterruptedException e) {
                threadPool.shutdownNow();
            }
        }
        
        if (socket != null && !socket.isClosed()) {
            socket.close();
        }
        System.out.println("Servidor UDP parado.");
    }

}

/*

private void handlePacket(DatagramPacket packet, RequestHandler handler) {
    System.out.println("DEBUG: 1. handlePacket INICIADO para " + packet.getSocketAddress());
    String response = "ERRO_PADRAO_DEBUG";
    try {
        String request = new String(packet.getData(), 0, packet.getLength());
        System.out.println("DEBUG: 2. Requisição recebida: " + request);

        System.out.println("DEBUG: 3. Chamando handler.handle()...");
        String handlerResponse = handler.handle(request);
        System.out.println("DEBUG: 4. Handler retornou: " + handlerResponse);

        if (handlerResponse == null) {
            System.err.println("[UDPServer] ERRO CRÍTICO: Handler retornou resposta nula.");
            response = "ERRO: Resposta interna do servidor foi nula.";
        } else {
            response = handlerResponse;
        }

    } catch (Exception e) {
        System.err.println("[UDPServer] ERRO CRÍTICO NO CATCH: " + e.getClass().getName());
        e.printStackTrace();
        response = "ERRO_CATCH_DEBUG";
    } finally {
        System.out.println("DEBUG: 5. Entrou no bloco FINALLY. Tentando enviar resposta: " + response);
        try {
            byte[] responseBytes = response.getBytes();
            DatagramPacket responsePacket = new DatagramPacket(
                responseBytes, responseBytes.length, packet.getAddress(), packet.getPort());
            
            synchronized (socket) { 
                socket.send(responsePacket);
            }
            System.out.println("DEBUG: 6. Resposta ENVIADA com sucesso.");
        } catch (IOException e) {
            System.err.println("[UDPServer] ERRO DE I/O AO ENVIAR RESPOSTA: " + e.getMessage());
        }
    }
}



    private void handlePacket(DatagramPacket packet , RequestHandler handler){
        try{
            String request = new String(packet.getData(), 0 , packet.getLength());

            if(packet.getLength() > BUFFER_SIZE * 0.9){
                System.out.println("[UDP] AVISO: Packet grande: " + packet.getLength() + " bytes");
            }

            String response = handler.handle(request);

            byte[] responseBytes = response.getBytes();
            if (responseBytes.length > BUFFER_SIZE) {
                System.err.println("[UDP] ERRO: Resposta muito grande: " + responseBytes.length + " bytes");
                response = "ERRO: Resposta muito grande para UDP";
                responseBytes = response.getBytes();
            }

            DatagramPacket responsePacket = new DatagramPacket(
                responseBytes, responseBytes.length, packet.getAddress(), packet.getPort());

            synchronized (socket) { // Sincroniza envio
                socket.send(responsePacket);
            }  
        
        }catch(IOException e) {
            System.err.println("[UDP] Erro ao processar pacote: " + e.getMessage());
        }
    }

    @Override
    public void stop() {
        running = false;
        if (socket != null && !socket.isClosed()) {
            socket.close();
        }
        System.out.println("Servidor UDP parado.");
    }

    @Override
    public void start(int port, RequestHandler handler) throws SocketException {
        socket = new DatagramSocket(port);
        socket.setReceiveBufferSize(1024 * 1024);
        socket.setSendBufferSize(1024 * 1024); 

        threadPool = Executors.newFixedThreadPool(THREAD_POOL_SIZE);
        running = true;
        System.out.println("Servidor UDP escutando na porta " + port);

        
        new Thread(() -> {
            while (running) {
                try {
                    byte[] buffer = new byte[BUFFER_SIZE];
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
 */