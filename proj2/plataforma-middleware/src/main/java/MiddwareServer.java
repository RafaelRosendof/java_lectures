

/*
 * this class gonna be the main server class 
 * will use java.socket to accept connections 
 * for each new connection will create a new thread
 * and will use reflection to call the right method
 */

/*
 Marshal and Unmarshal -> dont need to use Jackson because 

 wallet_A_${__threadNum()};wallet_B_${__threadNum()};${__Random(1,500)};${__Random(1,10)}

POST http://localhost:8082/ADD_TRANSACTION

POST data:
wallet_A_5;wallet_B_5;293;6


[no cookies]


Marshal: "HTTP/1.1 200 OK\r\nContent-Type: application/json\r\n\r\n{\"status\":\"sucesso\"}")

 */

import java.lang.reflect.Method;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.HashMap;
import java.util.Map;

public class MiddwareServer {


    private final Map<String, Method> routeMap = new HashMap<>();
    private Object serviceImplementation;


    public void registerService(Object service){
        this.serviceImplementation = service;

        for(Method method : service.getClass().getDeclaredMethods()){
            if(method.isAnnotationPresent(RequestMapping.class)){
                RequestMapping annotation = method.getAnnotation(RequestMapping.class);
                // chave = VERBO:/CAMINHO -> POST:/add_transaction
                String rKey = annotation.method().toUpperCase() + ":" + annotation.path();
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