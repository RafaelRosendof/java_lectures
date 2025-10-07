package ExtensionPatterns;


public class PerformanceInterceptor implements InvocationInterceptor {
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