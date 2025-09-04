// File: GrpcClient.java
import io.grpc.ManagedChannel;
import io.grpc.ManagedChannelBuilder;
import java.io.IOException;
import java.util.concurrent.TimeUnit;

public class GrpcClient implements ComponentClient {
    private static final int MAX_RETRIES = 3;
    private static final int RETRY_DELAY = 1000;

    @Override
    public String send(String host, int port, String request) throws IOException {
        IOException lastException = null;

        for (int i = 0; i < MAX_RETRIES; i++) {
            ManagedChannel channel = null;
            try {
                // Criar canal
                channel = ManagedChannelBuilder.forAddress(host, port)
                        .usePlaintext()
                        .build();

                // Criar stub
                ComponentServiceGrpc.ComponentServiceBlockingStub stub = 
                    ComponentServiceGrpc.newBlockingStub(channel)
                        .withDeadlineAfter(5, TimeUnit.SECONDS);

                // Separar comando e payload
                String[] parts = request.split("\\|", 2);
                String command = parts[0];
                String payload = parts.length > 1 ? parts[1] : "";

                // Fazer requisição
                ServiceOuterClass.RequestMessage grpcRequest = 
                    ServiceOuterClass.RequestMessage.newBuilder()
                        .setCommand(command)
                        .setPayload(payload)
                        .build();

                ServiceOuterClass.ResponseMessage response = stub.processRequest(grpcRequest);
                return response.getResult();

            } catch (Exception e) {
                lastException = new IOException("Erro gRPC: " + e.getMessage(), e);
                System.err.printf("[GrpcClient] Tentativa %d falhou para %s:%d - %s\n", 
                    (i + 1), host, port, e.getMessage());
                
                if (i < MAX_RETRIES - 1) {
                    try {
                        Thread.sleep(RETRY_DELAY);
                    } catch (InterruptedException ie) {
                        Thread.currentThread().interrupt();
                    }
                }
            } finally {
                if (channel != null) {
                    try {
                        channel.shutdown().awaitTermination(1, TimeUnit.SECONDS);
                    } catch (InterruptedException e) {
                        channel.shutdownNow();
                    }
                }
            }
        }
        
        throw new IOException("Falha após " + MAX_RETRIES + " tentativas.", lastException);
    }
}