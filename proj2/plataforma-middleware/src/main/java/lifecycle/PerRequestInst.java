package lifecycle;

// classe para criar uma nova instância para cada requisição

public class PerRequestInst {
    private static long requestCounter = 0;
    
    // cria uma nova instância para cada requisição
    public static <T> T createInstance(Class<T> serviceClass) {
        synchronized (PerRequestInst.class) {
            requestCounter++;
        }
        
        try {
            System.out.println("PER-REQUEST INSTANCE: Criando instância #" + requestCounter + 
                             " de " + serviceClass.getSimpleName());
            return serviceClass.getDeclaredConstructor().newInstance();
        } catch (Exception e) {
            throw new RuntimeException("Erro ao criar instância per-request: " + e.getMessage(), e);
        }
    }
    
    // acho que o gc cuida disso
    public static void destroyInstance(Object instance) {
        if (instance != null) {
            System.out.println("PER-REQUEST: Destruindo instância " + 
                             instance.getClass().getSimpleName() + 
                             "@" + instance.hashCode());
        }
    }

    public static long getRequestCount() {
        return requestCounter;
    }
    
    public static void resetCounter() {
        requestCounter = 0;
    }
}
