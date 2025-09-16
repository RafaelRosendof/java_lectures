

/*

Check-List:
Server-Side
- Server Request Handler  MiddwareServer.start()
- Message  RequestProcessor.run()
- Invoke Register  MiddwareServer.registerService()
- Invoke  RequestProcessor.invoke()
- Marshaller (marshall and unmarshal)  RequestProcessor.marshalResponse
- Invocation Data  
- Instance List  onde implementar isso?
- Remote Object   onde implementar isso?

Client-Side 
- Client  ClientRequestor
- Client Proxy  GatewayProxy e MinerProxy
- Requestor  ClientRequestor 
- Client Request Handler  HttpClient 
- clientError Remote Error  Erro padronizado nas Exceptions
- Server Request Handler   no cliente é o HttpClient, que lê a resposta.
- Invoker    equivalente ao RequestProcessor.invoke() no servidor.
- Marshaller (marshall and unmarshal)  ClientRequestor
- ServerError: Remote Error    mesmo que clientError: padronizar mensagem de erro do servidor para o cliente.





*/

import java.lang.reflect.Method;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.HashMap;
import java.util.Map;

public class MiddwareServer {

    private final Map<String, ObjectPool<?>> servicePools = new HashMap<>();
    private final Map<String, Method> routeMap = new HashMap<>();
    private final Map<String, Object> serviceInstances = new HashMap<>();

    // MÉTODO ANTIGO - mantém compatibilidade
    public void registerService(Object service) {
        this.serviceInstances.put("default", service);
        
        Class<?> serviceClass = service.getClass();
        String basePath = "";
        if (serviceClass.isAnnotationPresent(RequestMapping.class)) {
            RequestMapping classAnnotation = serviceClass.getAnnotation(RequestMapping.class);
            basePath = classAnnotation.path();
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

    // MÉTODO NOVO - com pool
    public void registerService(Class<?> serviceClass, int poolSize) throws Exception {
        ObjectPool<?> pool = new ObjectPool<>(serviceClass, poolSize);
        String serviceName = serviceClass.getSimpleName();
        servicePools.put(serviceName, pool);

        String basePath = "";
        if (serviceClass.isAnnotationPresent(RequestMapping.class)) {
            RequestMapping classAnnotation = serviceClass.getAnnotation(RequestMapping.class);
            basePath = classAnnotation.path();
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

    public void start(int port) {
        try {
            ServerSocket serverSocket = new ServerSocket(port);
            System.out.println("Servidor iniciado na porta " + port);
            
            while (true) {
                Socket clientSocket = serverSocket.accept();
                System.out.println("Nova conexão de " + clientSocket.getInetAddress().getHostAddress());
                
                // CORREÇÃO: Pega a primeira instância disponível
                Object service = serviceInstances.get("default");
                if (service == null && !servicePools.isEmpty()) {
                    // Se não tem instância, tenta criar uma do pool
                    ObjectPool<?> pool = servicePools.values().iterator().next();
                    service = pool.acquire();
                }
                
                RequestProcessor processor = new RequestProcessor(clientSocket, routeMap, service);
                new Thread(processor).start();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}

/*

import java.lang.reflect.Method;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.HashMap;
import java.util.Map;

public class MiddwareServer {

    private final Map<String, ObjectPool<?>> servicePools = new HashMap<>();
    private Object serviceImplementation;
    private final Map<String, Method> routeMap = new HashMap<>();
    private final Map<String, Object> serviceInstances = new HashMap<>();


    public void registerService(Class<?> serviceClass , int poolSize) throws Exception {

        ObjectPool<?> pool = new ObjectPool<>(serviceClass, poolSize);
        String serviceName = serviceClass.getSimpleName();
        servicePools.put(serviceName, pool);

        String basePath = "";
        if (serviceClass.isAnnotationPresent(RequestMapping.class)) {
            RequestMapping classAnnotation = serviceClass.getAnnotation(RequestMapping.class);
            basePath = classAnnotation.path();
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



   public void start(int port){
    try {
        ServerSocket serverSocket = new ServerSocket(port);
        System.out.println("Servidor iniciado na porta " + port);
        while(true){
            Socket clientSocket = serverSocket.accept();
            System.out.println("Nova conexão de " + clientSocket.getInetAddress().getHostAddress());
            RequestProcessor processor = new RequestProcessor(clientSocket, routeMap, serviceImplementation);
            new Thread(processor).start();
        }
    } catch (Exception e) {
        e.printStackTrace();
        }
    }

}

 */