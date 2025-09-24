import java.lang.reflect.Method;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.HashMap;
import java.util.Map;

import lifecycle.PerRequestInst;
import lifecycle.StaticInstanceManager;

import Indentification.LookUp;
import Indentification.ObjectRegistry;
import basicPatterns.RequestProcessor;

// Enum para definir estratégias de lifecycle
enum LifecycleStrategy {
    STATIC_INSTANCE,    // Singleton com lazy loading
    PER_REQUEST,        // Nova instância por requisição
    POOLING            // Pool de objetos (já implementado)
}

public class Middlewarev3 {
    
    private final Map<String, ObjectPool<Object>> servicePools = new HashMap<>();
    private final Map<String, Method> routeMap = new HashMap<>();
    private final Map<String, Object> serviceInstances = new HashMap<>();
    private final Map<String, LifecycleStrategy> serviceStrategies = new HashMap<>();
    private final ObjectRegistry registry = ObjectRegistry.getInstance();
    private final LookUp lookupService;
    private String host = "localhost";
    private int port = 8082;

    public Middlewarev3() {
        this.lookupService = new LookUp(host, port);
        registerService(lookupService);
    }

    // Método original - usa STATIC_INSTANCE por padrão
    public void registerService(Object service) {
        this.serviceInstances.put("default", service);
        this.serviceStrategies.put("default", LifecycleStrategy.STATIC_INSTANCE);
        registerServiceRoutes(service);
    }

    // NOVO: Método para registrar com estratégia específica
    public void registerService(Object service, LifecycleStrategy strategy) {
        String serviceName = service.getClass().getSimpleName();
        this.serviceInstances.put(serviceName, service);
        this.serviceStrategies.put(serviceName, strategy);
        registerServiceRoutes(service);
        
        System.out.println("Serviço registrado: " + serviceName + " com estratégia " + strategy);
    }

    // NOVO: Registrar classe com estratégia de lifecycle
    public void registerServiceClass(Class<?> serviceClass, LifecycleStrategy strategy) throws Exception {
        String serviceName = serviceClass.getName();
        this.serviceStrategies.put(serviceName, strategy);
        
        // Para STATIC_INSTANCE, não cria instância ainda (lazy)
        if (strategy == LifecycleStrategy.STATIC_INSTANCE) {
            System.out.println("Classe registrada para STATIC_INSTANCE (lazy): " + serviceName);
        } 
        // Para PER_REQUEST, também só registra a classe
        else if (strategy == LifecycleStrategy.PER_REQUEST) {
            System.out.println("Classe registrada para PER_REQUEST: " + serviceName);
        }
        // Para POOLING, cria o pool
        else if (strategy == LifecycleStrategy.POOLING) {
            registerServiceWithPool(serviceClass, 5); // pool padrão de 5
        }
        
        // Registra rotas usando uma instância temporária
        Object tempInstance = serviceClass.getDeclaredConstructor().newInstance();
        registerServiceRoutes(tempInstance);
    }

    // Método auxiliar para registrar rotas
    public void registerServiceRoutes(Object service) {
        Class<?> serviceClass = service.getClass();
        String basePath = "";

        if (serviceClass.isAnnotationPresent(RequestMapping.class)) {
            RequestMapping classMapping = serviceClass.getAnnotation(RequestMapping.class);
            basePath = classMapping.path();
        }

        for (Method method : serviceClass.getDeclaredMethods()) {
            if (method.isAnnotationPresent(RequestMapping.class)) {
                RequestMapping methodAnnotation = method.getAnnotation(RequestMapping.class);

                String fullPath = basePath + methodAnnotation.path();
                String rKey = methodAnnotation.method().toUpperCase() + ":" + fullPath;

                routeMap.put(rKey, method);
                System.out.println("Rota Registrada: " + rKey + " -> " + method.getName());
            }
        }
    }
    
    public String registerRemoteService(Object service, Class<?> remoteInterface) {
        String objectID = registry.registerObject(service, remoteInterface);
        this.serviceInstances.put(objectID, service);
        this.serviceStrategies.put(objectID, LifecycleStrategy.STATIC_INSTANCE); // padrão
        registerServiceRoutes(service);

        System.out.println("Serviço remoto registrado com ID: " + objectID);
        return objectID;
    } 

    // Método com pool (estratégia POOLING)
    @SuppressWarnings("unchecked")
    public String registerServiceWithPool(Class<?> serviceClass, int poolSize) throws Exception {
        ObjectPool<Object> pool = new ObjectPool<>(serviceClass, poolSize);
        String serviceName = serviceClass.getSimpleName();
        servicePools.put(serviceName, pool);
        serviceStrategies.put(serviceName, LifecycleStrategy.POOLING);

        Object serviceInstance = pool.acquire();
        if (serviceInstance != null) {
            registerServiceRoutes(serviceInstance);
            pool.release(serviceInstance);
        }

        if (implementsCustomRemoteObject(serviceClass)) {
            Object instance = serviceClass.getDeclaredConstructor().newInstance();
            RemoteObject remoteObj = (RemoteObject) instance;
            return registry.registerObject(instance, remoteObj.getRemoteInterface());
        }

        return serviceName;
    }

    // NOVO: Método principal para obter instância baseado na estratégia
    private Object getServiceInstanceByStrategy(String serviceName, Class<?> serviceClass) throws Exception {
        LifecycleStrategy strategy = serviceStrategies.get(serviceName);
        if (strategy == null) {
            strategy = LifecycleStrategy.STATIC_INSTANCE; // padrão
        }

        switch (strategy) {
            case STATIC_INSTANCE:
                System.out.println("Usando STATIC_INSTANCE para " + serviceName);
                return StaticInstanceManager.getInstance(serviceClass);
                
            case PER_REQUEST:
                System.out.println("Usando PER_REQUEST para " + serviceName);
                return PerRequestInst.createInstance(serviceClass);
                
            case POOLING:
                System.out.println("Usando POOLING para " + serviceName);
                ObjectPool<Object> pool = servicePools.get(serviceName);
                if (pool != null) {
                    return pool.acquire();
                }
                // Fallback para per-request se pool não existir
                return PerRequestInst.createInstance(serviceClass);
                
            default:
                return StaticInstanceManager.getInstance(serviceClass);
        }
    }

    // Método auxiliar para verificar implementação de RemoteObject
    private boolean implementsCustomRemoteObject(Class<?> serviceClass) {
        for (Class<?> interfaceClass : serviceClass.getInterfaces()) {
            if (interfaceClass.getSimpleName().equals("RemoteObject") && 
                !interfaceClass.getName().startsWith("java.rmi")) {
                return true;
            }
        }
        
        try {
            serviceClass.getMethod("getObjectId");
            serviceClass.getMethod("getRemoteInterface");
            return true;
        } catch (NoSuchMethodException e) {
            return false;
        }
    }

    public void start(int port) {
        this.port = port;

        try {
            ServerSocket serverSocket = new ServerSocket(port);
            System.out.println("Middleware iniciado na porta " + port);
            System.out.println("Lifecycle Strategies ativas: " + serviceStrategies);

            while (true) {
                Socket clientSocket = serverSocket.accept();
                System.out.println("Conexão aceita de " + clientSocket.getRemoteSocketAddress());

                Object service = getServiceInstance();
                RequestProcessor processor = new RequestProcessor(clientSocket, routeMap, service);
                new Thread(processor).start();
            }
            
        } catch (Exception e) {
            e.printStackTrace();
            System.out.println("Erro ao iniciar o servidor: " + e.getMessage());
        }
    }

    // Método atualizado para obter instância de serviço
    private Object getServiceInstance() {
        Object service = serviceInstances.get("default");
        
        if (service == null && !servicePools.isEmpty()) {
            ObjectPool<Object> pool = servicePools.values().iterator().next();
            service = pool.acquire();
        }
        
        return service;
    }

    public void setServerAddress(String host, int port) {
        this.host = host;
        this.port = port;
    }

    // Método para mostrar estatísticas dos lifecycles
    public void showLifecycleStats() {
        System.out.println("\n=== LIFECYCLE STATISTICS ===");
        System.out.println("Static Instances: " + StaticInstanceManager.getInstanceCount());
        System.out.println("Per-Request Counter: " + PerRequestInst.getRequestCount());
        System.out.println("Object Pools: " + servicePools.size());
        
        StaticInstanceManager.showInstances();
        
        servicePools.forEach((name, pool) -> 
            System.out.println("Pool " + name + ": " + pool.size() + " objetos disponíveis"));
    }
}