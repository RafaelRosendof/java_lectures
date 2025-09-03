// File: GrpcServer.java
import io.grpc.Server;
import io.grpc.ServerBuilder;
import io.grpc.stub.StreamObserver;

public class GrpcServer implements ComponentServer {
    private Server server;
    
    @Override
    public void start(int port, RequestHandler handler) throws Exception {
        server = ServerBuilder.forPort(port)
                .addService(new ComponentServiceImpl(handler))
                .build()
                .start();
        
        System.out.println("Servidor gRPC escutando na porta " + port);
    }

    @Override
    public void stop() {
        if (server != null) {
            server.shutdown();
            System.out.println("Servidor gRPC parado.");
        }
    }
    
    // Implementação do serviço
    static class ComponentServiceImpl extends ComponentServiceGrpc.ComponentServiceImplBase {
        private final RequestHandler handler;

        public ComponentServiceImpl(RequestHandler handler) {
            this.handler = handler;
        }

        @Override
        public void processRequest(        ServiceOuterClass.RequestMessage request, 
                                 StreamObserver<ServiceOuterClass.ResponseMessage> responseObserver) {
            try {
                // Formato esperado pelo handler: "COMMAND|PAYLOAD"
                String gatewayRequest = request.getCommand() + "|" + request.getPayload();
                String result = handler.handle(gatewayRequest);
                
                ServiceOuterClass.ResponseMessage response = 
                    ServiceOuterClass.ResponseMessage.newBuilder()
                        .setResult(result)
                        .build();
                
                responseObserver.onNext(response);
                responseObserver.onCompleted();
                
            } catch (Exception e) {
                System.err.println("Erro gRPC: " + e.getMessage());
                responseObserver.onError(e);
            }
        }
    }
}