package ExtensionPatterns;

import java.util.List;
import java.util.ArrayList;

public class InterceptorChain {
    private final List<InvocationInterceptor> interceptors = new java.util.ArrayList<>();
    
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