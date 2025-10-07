package ExtensionPatterns;

public class InvocationContext {
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