public class ApiGatway {
    

    /*
     * 
     * Essa classe aqui deve implementar somente o Gatway {
     *  - Receber requisições e encaminhar para o serviço correto (o serviço vai escutar em uma porta diferente)
     *  - Manter uma lista de serviços registrados (mempool, miner) e seus endereços
     *  - Implementar endpoints para registro (/register) e heartbeat (/heartbeat)
     *  - Implementar endpoints para roteamento (/mempool/addTransaction, /mempool/getTransactions, /miner/mine)
     *  - Usar ConcurrentHashMap para armazenar os serviços registrados
     *  - Usar HttpServer para criar o servidor HTTP
     *  - Usar Gson ou Jackson para serializar/deserializar JSON
     *  - Usar ScheduledExecutorService para checar heartbeats e remover serviços inativos
     *  - Implementar lógica para rotear requisições para o serviço correto baseado no path
     *  - Implementar lógica para responder com erro se o serviço não estiver disponível
     * }
     */

}
