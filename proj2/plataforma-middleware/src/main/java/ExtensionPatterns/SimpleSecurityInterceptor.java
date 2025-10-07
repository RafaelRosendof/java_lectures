package ExtensionPatterns;

public class SimpleSecurityInterceptor implements InvocationInterceptor {
    
    @Override
    public void beforeInvocation(InvocationContext context) throws Exception {
        // Exemplo: Bloqueia IPs específicos
        String client = context.getClientAddress();
        if (client.startsWith("192.168.1.")) {
            System.out.println("✓ [SECURITY] Cliente permitido: " + client);
        } else if (client.equals("blocked.client")) {
            throw new SecurityException("Cliente bloqueado: " + client);
        }
        
        // Exemplo: Valida argumentos básicos
        Object[] args = context.getArguments();
        if (args != null) {
            for (Object arg : args) {
                if (arg instanceof String) {
                    String str = (String) arg;
                    if (str.contains("DROP TABLE") || str.contains("DELETE FROM")) {
                        throw new SecurityException("SQL Injection detectado!");
                    }
                }
            }
        }
    }
    
    @Override
    public void afterInvocation(InvocationContext context, Object result) {
        // Nada após
    }
    
    @Override
    public void onError(InvocationContext context, Exception error) {
        System.err.println("✗ [SECURITY] Erro de segurança: " + error.getMessage());
    }
}