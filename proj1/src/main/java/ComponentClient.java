public interface ComponentClient {
    String send(String host, int port, String request) throws Exception;
}