public class CommunicationFactory {
    public static ComponentServer createServer(CommunicationType type) {
        switch (type) {
            case UDP:
                return new UDPServer();
            // Caso queira adicionar HTTP no futuro:
            // case HTTP:
            //     return new HttpServer(); 

            case TCP:
                return new HTTPServer();

            case GRPC:
                return new GRPCServer();

            default:
                throw new IllegalArgumentException("Tipo de servidor desconhecido: " + type);
        }
    }

    public static ComponentClient createClient(CommunicationType type) {
        switch (type) {
            case UDP:
                return new UDPClient();
            // Caso queira adicionar HTTP no futuro:
            // case HTTP:
            //     return new HttpClient();
            case GRPC:
                return new GRPCClient();

            case TCP:
                return new HTTPClient();
                
            default:
                throw new IllegalArgumentException("Tipo de cliente desconhecido: " + type);
        }
    }
}