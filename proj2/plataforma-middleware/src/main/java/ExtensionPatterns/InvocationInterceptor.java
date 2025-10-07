package ExtensionPatterns;

import java.lang.reflect.Method;

public interface InvocationInterceptor {
    
    // Chamado ANTES da invocação do método
    void beforeInvocation(InvocationContext context) throws Exception;
    
    // Chamado DEPOIS da invocação (com o resultado)
    void afterInvocation(InvocationContext context, Object result) throws Exception;
    
    // Chamado em caso de ERRO
    void onError(InvocationContext context, Exception error);
}

// InvocationContext.java - Contexto da invocação
class InvocationContext {
    private final Object target;           // Objeto alvo
    private final Method method;           // Método sendo invocado
    private final Object[] arguments;      // Argumentos
    private final String clientAddress;    // IP do cliente
    private final long timestamp;          // Quando foi chamado
    
    public InvocationContext(Object target, Method method, Object[] arguments, String clientAddress) {
        this.target = target;
        this.method = method;
        this.arguments = arguments;
        this.clientAddress = clientAddress;
        this.timestamp = System.currentTimeMillis();
    }
    
    // Getters
    public Object getTarget() { return target; }
    public Method getMethod() { return method; }
    public Object[] getArguments() { return arguments; }
    public String getClientAddress() { return clientAddress; }
    public long getTimestamp() { return timestamp; }
    
    public String getMethodName() {
        return method != null ? method.getName() : "unknown";
    }
    
    @Override
    public String toString() {
        return String.format("InvocationContext{method=%s, client=%s, args=%d}", 
            getMethodName(), clientAddress, arguments != null ? arguments.length : 0);
    }
}

// LoggingInterceptor.java - Exemplo: Logging simples
class LoggingInterceptor implements InvocationInterceptor {
    
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
                         " | Duração: " + duration + 


/*
// InvocationInterceptor.java - Interface do interceptor
package extensionPatterns;

import java.lang.reflect.Method;

public interface InvocationInterceptor {
    
    // Chamado ANTES da invocação do método
    void beforeInvocation(InvocationContext context) throws Exception;
    
    // Chamado DEPOIS da invocação (com o resultado)
    void afterInvocation(InvocationContext context, Object result) throws Exception;
    
    // Chamado em caso de ERRO
    void onError(InvocationContext context, Exception error);
}

// InvocationContext.java - Contexto da invocação
class InvocationContext {
    private final Object target;           // Objeto alvo
    private final Method method;           // Método sendo invocado
    private final Object[] arguments;      // Argumentos
    private final String clientAddress;    // IP do cliente
    private final long timestamp;          // Quando foi chamado
    
    public InvocationContext(Object target, Method method, Object[] arguments, String clientAddress) {
        this.target = target;
        this.method = method;
        this.arguments = arguments;
        this.clientAddress = clientAddress;
        this.timestamp = System.currentTimeMillis();
    }
    
    // Getters
    public Object getTarget() { return target; }
    public Method getMethod() { return method; }
    public Object[] getArguments() { return arguments; }
    public String getClientAddress() { return clientAddress; }
    public long getTimestamp() { return timestamp; }
    
    public String getMethodName() {
        return method != null ? method.getName() : "unknown";
    }
    
    @Override
    public String toString() {
        return String.format("InvocationContext{method=%s, client=%s, args=%d}", 
            getMethodName(), clientAddress, arguments != null ? arguments.length : 0);
    }
}

// LoggingInterceptor.java - Exemplo: Logging simples
class LoggingInterceptor implements InvocationInterceptor {
    
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

// PerformanceInterceptor.java - Exemplo: Medição de performance
class PerformanceInterceptor implements InvocationInterceptor {
    private static final long SLOW_THRESHOLD_MS = 100;
    
    @Override
    public void beforeInvocation(InvocationContext context) {
        // Não faz nada antes
    }
    
    @Override
    public void afterInvocation(InvocationContext context, Object result) {
        long duration = System.currentTimeMillis() - context.getTimestamp();
        if (duration > SLOW_THRESHOLD_MS) {
            System.out.println("⚠️  [PERFORMANCE] Método LENTO: " + context.getMethodName() + 
                             " levou " + duration + "ms");
        }
    }
    
    @Override
    public void onError(InvocationContext context, Exception error) {
        // Performance não se importa com erros
    }
}

// SimpleSecurityInterceptor.java - Exemplo: Validação simples (NÃO É SEGURANÇA REAL)
class SimpleSecurityInterceptor implements InvocationInterceptor {
    
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

// InterceptorChain.java - Cadeia de interceptors
class InterceptorChain {
    private final java.util.List<InvocationInterceptor> interceptors = new java.util.ArrayList<>();
    
    public void addInterceptor(InvocationInterceptor interceptor) {
        interceptors.add(interceptor);
        System.out.println("Interceptor adicionado: " + interceptor.getClass().getSimpleName());
    }
    
    public void beforeInvocation(InvocationContext context) throws Exception {
        for (InvocationInterceptor interceptor : interceptors) {
            interceptor.beforeInvocation(context);
        }
    }
    
    public void afterInvocation(InvocationContext context, Object result) throws Exception {
        // Executa em ordem reversa (LIFO)
        for (int i = interceptors.size() - 1; i >= 0; i--) {
            interceptors.get(i).afterInvocation(context, result);
        }
    }
    
    public void onError(InvocationContext context, Exception error) {
        for (InvocationInterceptor interceptor : interceptors) {
            interceptor.onError(context, error);
        }
    }
    
    public int size() {
        return interceptors.size();
    }
}
 */