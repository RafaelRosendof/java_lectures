

public class Main {
    public static void main(String[] args) {
        System.out.println("Iniciando aplicacao-blockchain...");

        // 1. Instancia o serviço da sua aplicação (o objeto remoto) 
        BlockChainService blockchainService = new BlockChainService();

        // instancia
        MiddwareServer server = new MiddwareServer();

        // 3. Registra seu serviço no middleware para que ele possa encontrar os métodos anotados 
        server.registerService(blockchainService);

        // 4. Inicia o servidor do middleware na porta desejada 
        server.start(8082);
    }
}