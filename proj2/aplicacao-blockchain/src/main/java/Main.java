// Importações necessárias do seu middleware
//import br.ufrn.imd.middleware.MiddwareServer;

public class Main {
    public static void main(String[] args) {
        System.out.println("Iniciando aplicacao-blockchain...");

        // 1. Instancia o serviço da sua aplicação (o objeto remoto) 
        BlockChainService blockchainService = new BlockChainService();

        // 2. Instancia o servidor do middleware
        //MiddwareServer server = new MiddwareServer();
        Middlewarev2 server = new Middlewarev2();
        // 3. Registra o serviço no servidor
        server.registerService(blockchainService);

        // 4. Inicia o servidor do middleware na porta desejada 
        server.start(8082);
        
        System.out.println("Servidor blockchain iniciado na porta 8082");
        System.out.println("Rotas disponíveis:");
        System.out.println("  POST /blockchain/transactions - Adicionar transação");
        System.out.println("  GET  /blockchain/blocks - Listar blocos");
    }
}