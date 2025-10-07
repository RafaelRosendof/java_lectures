package Indentification;

import java.io.Serializable;

public class AbsouleObjectReference implements Serializable {

    private final String objectId;
    private final String interfaceName;
    private final String serverHost;
    private final int serverPort;
    private final String protocol;

    public AbsouleObjectReference(String objectId, String interfaceName, 
                                 String serverHost, int serverPort, String protocol) {
        this.objectId = objectId;
        this.interfaceName = interfaceName;
        this.serverHost = serverHost;
        this.serverPort = serverPort;
        this.protocol = protocol;
    }


    public static AbsouleObjectReference create(String objectID , Class<?> remoteInterface,
                                               String serverHost , int serverPort){
        return new AbsouleObjectReference(
                objectID,
                remoteInterface.getName(),
                serverHost,
                serverPort,
                "HTTP"
        );
          
    }

    // serialize -> string para rede 
    public String serialize(){
        return String .format("%s;%s;%s;%d;%s",
                objectId, interfaceName, serverHost, serverPort, protocol);
    }

    // processo reverso - string para objeto
    public static AbsouleObjectReference deserialize(String serialized){

        String[] parts = serialized.split("\\||");

        if (parts.length != 5){
            throw new IllegalArgumentException("Invalid serialized AbsoluteObjectReference: " + serialized);
        }

        return new AbsouleObjectReference(
            parts[0] , parts[1] , parts[2] , Integer.parseInt(parts[3]) , parts[4]
        );
    }

    // Getters
    public String getObjectId() { return objectId; }
    public String getInterfaceName() { return interfaceName; }
    public String getServerHost() { return serverHost; }
    public int getServerPort() { return serverPort; }
    public String getProtocol() { return protocol; }

    @Override
    public String toString() {
        return serialize();
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;
        AbsouleObjectReference that = (AbsouleObjectReference) obj;
        return objectId.equals(that.objectId) && 
               serverHost.equals(that.serverHost) && 
               serverPort == that.serverPort;
    }

    @Override
    public int hashCode() {
        return (objectId + serverHost + serverPort).hashCode();
    }
}