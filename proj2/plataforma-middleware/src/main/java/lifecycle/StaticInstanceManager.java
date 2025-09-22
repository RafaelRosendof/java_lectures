package lifecycle;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

// classe para o server application que gerencia instâncias estáticas + lazy acquisition
// ou seja, instâncias que são criadas uma única vez e compartilhadas por todas as requisições

public class StaticInstanceManager {
    private static final Map<String, Object> instances = new ConcurrentHashMap<>();
    
    // Lazy Acquisition - só cria quando necessário
    @SuppressWarnings("unchecked")
    public static <T> T getInstance(Class<T> serviceClass) {
        String className = serviceClass.getName();
        
        // Verifica se já existe (thread-safe)
        Object instance = instances.get(className);
        
        if (instance == null) {
            synchronized (StaticInstanceManager.class) {
                // Double-check locking pattern
                instance = instances.get(className);
                if (instance == null) {
                    try {
                        System.out.println("LAZY ACQUISITION: Criando nova instância de " + className);
                        instance = serviceClass.getDeclaredConstructor().newInstance();
                        instances.put(className, instance);
                    } catch (Exception e) {
                        throw new RuntimeException("Erro ao criar instância: " + e.getMessage(), e);
                    }
                }
            }
        } else {
            System.out.println("STATIC INSTANCE: Reutilizando instância de " + className);
        }
        
        return (T) instance;
    }
    
    // Para limpeza (opcional)
    public static void clearInstance(Class<?> serviceClass) {
        instances.remove(serviceClass.getName());
    }
    
    // Estatísticas
    public static int getInstanceCount() {
        return instances.size();
    }
    
    public static void showInstances() {
        System.out.println("=== Instâncias Estáticas Ativas ===");
        instances.forEach((className, instance) -> 
            System.out.println("- " + className + " -> " + instance.hashCode()));
    }
}
