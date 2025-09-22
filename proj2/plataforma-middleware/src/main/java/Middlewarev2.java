import java.lang.reflect.Method;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.HashMap;
import java.util.Map;

import Indentification.LookUp;
import Indentification.ObjectRegistry;
import basicPatterns.RequestProcessor;

public class Middlewarev2 {
    
    private final Map<String, ObjectPool<Object>> servicePools = new HashMap<>();
    private final Map<String, Method> routeMap = new HashMap<>();
    private final Map<String, Object> serviceInstances = new HashMap<>();
    private final ObjectRegistry registry = ObjectRegistry.getInstance();
    private final LookUp lookupService;
    private String host = "localhost";
    private int port = 8082;

    public Middlewarev2() {
        // Registra o serviço de lookup
        this.lookupService = new LookUp(host, port);
        registerService(lookupService);
    }

    public void registerService(Object service) {
        this.serviceInstances.put("default", service);
        registerServiceRoutes(service);
    }

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
        registerServiceRoutes(service);

        System.out.println("Serviço remote registrado com ID: " + objectID);
        return objectID;
    } 

    // Método com pool atualizado - CORRIGIDO
    @SuppressWarnings("unchecked")
    public String registerService(Class<?> serviceClass, int poolSize) throws Exception {
        ObjectPool<Object> pool = new ObjectPool<>(serviceClass, poolSize);
        String serviceName = serviceClass.getSimpleName();
        servicePools.put(serviceName, pool);

        // Pega uma instância do pool para registrar as rotas
        Object serviceInstance = pool.acquire();
        if (serviceInstance != null) {
            registerServiceRoutes(serviceInstance);
            pool.release(serviceInstance);
        }

        // Registra no ObjectRegistry se implementa nossa interface RemoteObject customizada
        if (implementsCustomRemoteObject(serviceClass)) {
            Object instance = serviceClass.getDeclaredConstructor().newInstance();
            // Cast para nossa interface customizada
            RemoteObject remoteObj = (RemoteObject) instance;
            return registry.registerObject(instance, remoteObj.getRemoteInterface());
        }

        return serviceName;
    }

    // Método auxiliar para verificar se implementa nossa interface customizada
    private boolean implementsCustomRemoteObject(Class<?> serviceClass) {
        // Verifica se implementa nossa interface RemoteObject (não a do java.rmi)
        for (Class<?> interfaceClass : serviceClass.getInterfaces()) {
            if (interfaceClass.getSimpleName().equals("RemoteObject") && 
                !interfaceClass.getName().startsWith("java.rmi")) {
                return true;
            }
        }
        
        // Verifica se tem os métodos necessários
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

    // Método auxiliar para obter instância de serviço
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

    // Método para obter estatísticas do servidor
    public String getServerStats() {
        try {
            Map<String, Object> stats = Map.of(
                "registeredObjects", registry.listObjects().size(),
                "routes", routeMap.size(),
                "servicePools", servicePools.size(),
                "serverAddress", host + ":" + port
            );
            return basicPatterns.JsonUtil.toJson(stats);
        } catch (Exception e) {
            return "Erro ao obter estatísticas: " + e.getMessage();
        }
    }
}