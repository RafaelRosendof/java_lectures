

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

/*
 * Request handelr  É o componente do lado do servidor que escuta na rede, aceita conexões e passa os dados brutos para o próximo nível. Ele lida com o I/O de baixo nível.
 * O Invoker é responsável por receber uma requisição já decodificada (unmarshalled), descobrir qual método do objeto de negócio deve ser chamado e invocá-lo, passando os parâmetros corretos.
 * 
 * 
 * O que é? É o responsável por transformar a representação de dados da linguagem (objetos, tipos primitivos) em um formato para transmissão na rede (bytes, JSON, XML) e vice-versa. 
 * O processo de conversão para o formato de rede é o Marshalling, e o processo inverso é o Unmarshalling
 * 
 * 
 * Interface Description 
 * Define a "interface" do objeto remoto: quais métodos estão disponíveis, seus nomes, parâmetros e tipos de retorno. 
 * Isso permite que o cliente e o servidor concordem sobre como a comunicação deve ocorrer.
 * 
 * 
 * Client Request Handler

    O que é? É o espelho do Server Request Handler. Ele é responsável por estabelecer a conexão com o servidor, enviar os bytes da requisição pela rede e receber a resposta.

    Onde criar no seu código? Você precisa criar um cliente HTTP "na mão", assim como fez com o servidor. Você pode criar uma classe HttpClient.java que usa java.net.Socket.


Requestor

    O que é? O Requestor constrói a requisição. Ele pega o nome do método e os parâmetros, usa o Marshaller para 
    formatá-los no protocolo de rede (neste caso, uma string HTTP) e entrega para o Client Request Handler enviar.

Client Proxy

    O que é? É um objeto no lado do cliente que se parece exatamente com o objeto remoto no servidor. Quando seu código de aplicação chama um método no Proxy, ele, 
    por baixo dos panos, executa toda a lógica de comunicação remota (Requestor -> Marshalling -> Client Request Handler).


Remoting Error

    O que é? É um padrão para comunicar erros que ocorrem durante a invocação remota de volta para o cliente de uma forma padronizada.
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

        Class<?> serviceClass = service.getClass();
        if (serviceClass.isAnnotationPresent(RequestMapping.class)){
            RequestMapping classAnnotation = serviceClass.getAnnotation(RequestMapping.class);
            String basePath = classAnnotation.path();
            System.out.println("Service registrado com path base: " + basePath);
        }

        for(Method method : serviceClass.getDeclaredMethods()){
            if(method.isAnnotationPresent(RequestMapping.class)){
                RequestMapping methodAnnotation = method.getAnnotation(RequestMapping.class);

                String fullPath = methodAnnotation.path();
                if(serviceClass.isAnnotationPresent(RequestMapping.class)){
                    RequestMapping classAnnotation = serviceClass.getAnnotation(RequestMapping.class);
                    String basePath = classAnnotation.path();
                    if(!basePath.isEmpty()){
                        fullPath = basePath + (methodAnnotation.path().isEmpty() ? "" : methodAnnotation.path());
                    }
                }

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
