package Indentification;

import java.util.Map;
import java.util.List;
import basicPatterns.JsonUtil;

public class LookUp {


    /*
     * Olhar para interfaces 
     * Olhar para objetos
     * Registar objetos 
     * Listar objetos 
     * e listar todos os objetos disponíveis ....
     */

    private final ObjectRegistry registry;
    private final String host;
    private final int port;

    public LookUp(String host , int port){
        this.registry = ObjectRegistry.getInstance();
        this.host = host;
        this.port = port;
    }

    // procura ID 
    //@RequestMapping(path = "/lookup/object" , method = "GET")
    public String lookUpByObj(String objectID) throws Exception {
        try{

            RemoteObjectInfo info = registry.lookupObject(objectID);
            if (info == null){
                return JsonUtil.toJson(Map.of("error", "Objeto nao encontrado"));
            }

            AbsouleObjectReference reference = AbsouleObjectReference.create(
                    info.getObjectId(),
                    info.getRemoteInterface(),
                    host,
                    port
            );

            return JsonUtil.toJson(Map.of(
                "objectID", objectID,
                "interface", info.getRemoteInterface().getName(),
                "refs", reference.serialize(),
                "status", "found"
            ));

        }catch (Exception e){
            e.printStackTrace();
            return JsonUtil.toJson(Map.of("error", "Erro no lookup: " + e.getMessage()));
        }
    }

    //@RequestMapping(path = "/lookup/interface" , method = "GET")
    /* 
    public String lookUpByInterface(String interfaceName){
        try{

            List<Map<String , Object>> results = registry.getAllObjects().values().stream()
                .filter(info -> info.getRemoteInterface().getName().equals(interfaceName)) // filtrar por interface
                .map(info -> {
                    AbsouleObjectReference ref = AbsouleObjectReference.create(
                        info.getObjectId(),
                        info.getRemoteInterface(),
                        host,
                        port
                    );
                    return Map.of(
                        "objectID", info.getObjectId(),
                        "reference", ref.serialize()
                    );
                })
                .collect(Collectors.toList());

            return JsonUtil.toJson(Map.of(
                "interface", interfaceName,
                "objects", results,
                "count", results.size()
            ));

        }catch (Exception e){
            e.printStackTrace();
            return JsonUtil.toJson(Map.of("error", "Erro no lookup: " + e.getMessage()));
        }
    
    }
        */

    // registrar um novo obj 

    //@RequestMapping(path = "/register" , method = "POST")
    public String registerObj(String className , String interfaceName) throws Exception {
        try{

            Class<?> objClass = Class.forName(className);
            Class<?> interfaceClass = Class.forName(interfaceName);

            Object instance = objClass.getDeclaredConstructor().newInstance(); // copiar o construtor da clase e criar uma nova instancia
            String objectID = registry.registerObject(instance , interfaceClass);
            
            AbsouleObjectReference refs = AbsouleObjectReference.create(
                objectID,
                interfaceClass,
                host,
                port
            );

            return JsonUtil.toJson(Map.of(
                "objectID", objectID,
                "refs", refs.serialize(),
                "status", "registered"
            ));

        }catch (Exception e){
            e.printStackTrace();
            return JsonUtil.toJson(Map.of("error", "Erro no registro: " + e.getMessage()));
        }
    }


    //TODO -> lookup for all
    
}
