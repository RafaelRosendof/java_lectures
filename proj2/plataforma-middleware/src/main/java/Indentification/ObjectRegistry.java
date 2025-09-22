package Indentification;

// classe singleton para registar e procurar objetos remotos

import java.util.Map;
import java.rmi.server.RemoteObject;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

public class ObjectRegistry{

    private static final ObjectRegistry instance = new ObjectRegistry();
    private final Map<String , RemoteObjectInfo> objects = new ConcurrentHashMap<>();
    private final Map<String , Class<?>> interfaces = new ConcurrentHashMap<>();


    private ObjectRegistry() {
    }

    public static ObjectRegistry getInstance() {
        return instance;
    }

    // para o registro de objetos remotos
    public String registerObject(Object obj , Class<?> remoteInterface) {
        String objectID = UUID.randomUUID().toString();
        RemoteObjectInfo remoteObjectInfo = new RemoteObjectInfo(objectID , obj , remoteInterface);
        objects.put(objectID , remoteObjectInfo);
        interfaces.put(remoteInterface.getName() , remoteInterface);

        System.out.println("Registered object with ID: " + objectID);
        return objectID;
    }


    public RemoteObjectInfo lookupObject(String objectID) {
        return objects.get(objectID);
    }

    public Class<?> lookupInterface(String interfaceName) {
        return interfaces.get(interfaceName);
    }

    public Map<String, RemoteObjectInfo> getAllObjects() {
        return new ConcurrentHashMap<>(objects);
    }

    //lista tudo 
    public Map<String , RemoteObjectInfo> listObjects() {
        return new ConcurrentHashMap<>(objects);
    }

    public boolean unregisterObject(String objectID){
        return objects.remove(objectID) != null;
    }

} 