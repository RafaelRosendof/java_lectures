public class CommunicationFactory {
    public static ComponentServer createServer(CommunicationType type) {
        switch (type) {
            case UDP:
                return new UDPServer();
            
            case HTTP:
                return new HttpServer(); 

            case TCP:
                return new TCPServer();
            case GRPC:
                return new GrpcServer();

            default:
                throw new IllegalArgumentException("Tipo de servidor desconhecido: " + type);
        }
    }

    public static ComponentClient createClient(CommunicationType type) {
        switch (type) {
            case UDP:
                return new UDPClient();
            case HTTP:
                return new HttpClient();
            case GRPC:
                return new GrpcClient();
            case TCP:
                return new TCPClient();
//
            default:
                throw new IllegalArgumentException("Tipo de cliente desconhecido: " + type);
        }
    }
}