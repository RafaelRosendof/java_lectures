public interface ComponentServer {
    void start(int port, RequestHandler handler) throws Exception;
    void stop();
}
