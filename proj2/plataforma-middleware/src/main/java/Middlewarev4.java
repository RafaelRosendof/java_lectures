import java.lang.reflect.Method;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.HashMap;
import java.util.Map;

import Indentification.LookUp;
import Indentification.ObjectRegistry;
import ExtensionPatterns.InterceptorChain;
import ExtensionPatterns.LoggingInterceptor;
import ExtensionPatterns.PerformanceInterceptor;
import basicPatterns.RequestProcessor;

// Enum para definir estratégias de lifecycle
enum LifecycleStrategy {
    STATIC_INSTANCE,    // Singleton com lazy loading
    PER_REQUEST,        // Nova instância por requisição
    POOLING            // Pool de objetos
}

public class Middlewarev4 {
    
    private final Map<String, ObjectPool<Object>> servicePools = new HashMap<>();
    private final Map<String, Method> routeMap = new HashMap<>();
    private final Map<String, Object> serviceInstances = new HashMap<>();
    private final Map<String, LifecycleStrategy> serviceStrategies = new HashMap<>();
    private final ObjectRegistry registry = ObjectRegistry.getInstance();
    private final LookUp lookupService;
    private final InterceptorChain interceptorChain;
    private String host = "localhost";
    private int port = 8082;

    public Middlewarev4() {
        // Inicializa a cadeia de interceptors
        this.interceptorChain = new InterceptorChain();
        
        // Adiciona interceptors padrão
        this.interceptorChain.addInterceptor(new LoggingInterceptor());
        this.interceptorChain.addInterceptor(new PerformanceInterceptor());
        
        // Registra o serviço de lookup COM A ESTRATÉGIA
        this.lookupService = new LookUp(host, port);
        registerService(lookupService, LifecycleStrategy.STATIC_INSTANCE);
    }

    // Método original - usa STATIC_INSTANCE por padrão
    public void registerService(Object service) {
        registerService(service, LifecycleStrategy.STATIC_INSTANCE);
    }

    // Método para registrar com estratégia específica
    public void registerService(Object service, LifecycleStrategy strategy) {
        String serviceName = service.getClass().getSimpleName();
        this.serviceInstances.put(serviceName, service);
        this.serviceStrategies.put(serviceName, strategy);
        registerServiceRoutes(service);
        
        System.out.println("Serviço registrado: " + serviceName + " com estratégia " + strategy);
    }

    // Registrar classe com estratégia de lifecycle
    public void registerServiceClass(Class<?> serviceClass, LifecycleStrategy strategy) throws Exception {
        String serviceName = serviceClass.getName();
        this.serviceStrategies.put(serviceName, strategy);
        
        if (strategy == LifecycleStrategy.STATIC_INSTANCE) {
            System.out.println("Classe registrada para STATIC_INSTANCE (lazy): " + serviceName);
        } 
        else if (strategy == LifecycleStrategy.PER_REQUEST) {
            System.out.println("Classe registrada para PER_REQUEST: " + serviceName);
        }
        else if (strategy == LifecycleStrategy.POOLING) {
            registerServiceWithPool(serviceClass, 5);
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
        this.serviceStrategies.put(objectID, LifecycleStrategy.STATIC_INSTANCE);
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
            System.out.println("Interceptors ativos: " + interceptorChain.size());

            while (true) {
                Socket clientSocket = serverSocket.accept();
                System.out.println("Conexão aceita de " + clientSocket.getRemoteSocketAddress());

                Object service = getServiceInstance();
                
                // PASSA O INTERCEPTOR CHAIN PARA O REQUEST PROCESSOR
                RequestProcessor processor = new RequestProcessor(
                    clientSocket, 
                    routeMap, 
                    service, 
                    interceptorChain
                );
                
                new Thread(processor).start();
            }
            
        } catch (Exception e) {
            e.printStackTrace();
            System.out.println("Erro ao iniciar o servidor: " + e.getMessage());
        }
    }

    // Método auxiliar para obter instância de serviço
    private Object getServiceInstance() {
        Object service = serviceInstances.get("default");
        
        if (service == null) {
            // Tenta pegar do primeiro serviço registrado
            if (!serviceInstances.isEmpty()) {
                service = serviceInstances.values().iterator().next();
            }
        }
        
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

    // Método para adicionar interceptor customizado
    public void addInterceptor(ExtensionPatterns.InvocationInterceptor interceptor) {
        interceptorChain.addInterceptor(interceptor);
    }

    // Método para mostrar estatísticas
    public void showStats() {
        System.out.println("\n=== MIDDLEWARE STATISTICS ===");
        System.out.println("Registered Objects: " + registry.listObjects().size());
        System.out.println("Routes: " + routeMap.size());
        System.out.println("Service Pools: " + servicePools.size());
        System.out.println("Interceptors: " + interceptorChain.size());
        System.out.println("Server Address: " + host + ":" + port);
    }
}