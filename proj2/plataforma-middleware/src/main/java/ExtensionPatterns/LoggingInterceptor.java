package ExtensionPatterns;

public class LoggingInterceptor implements InvocationInterceptor {
    
    @Override
    public void beforeInvocation(InvocationContext context) {
        System.out.println("[INTERCEPTOR] ANTES: " + context.getMethodName() + 
                         " | Cliente: " + context.getClientAddress() +
                         " | Timestamp: " + context.getTimestamp());
    }
    
    @Override
    public void afterInvocation(InvocationContext context, Object result) {
        long duration = System.currentTimeMillis() - context.getTimestamp();
        System.out.println("[INTERCEPTOR] DEPOIS: " + context.getMethodName() + 
                         " | Duração: " + duration + "ms" +
                         " | Resultado: " + (result != null ? result.toString() : "null"));
    }
    
    @Override
    public void onError(InvocationContext context, Exception error) {
        System.err.println("[INTERCEPTOR] ERRO: " + context.getMethodName() + 
                         " | Erro: " + error.getMessage());
    }
}