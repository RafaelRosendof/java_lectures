package Indentification;

// classe para cadastrar as informações dos objetos remotos

public class RemoteObjectInfo {

    private final String objectId;
    private final Object instance;
    private final Class<?> remoteInterface;
    private final String serverAddress;
    private final int serverPort;

    public RemoteObjectInfo(String objectId, Object instance, Class<?> remoteInterface) {
        this.objectId = objectId;
        this.instance = instance;
        this.remoteInterface = remoteInterface;
        this.serverAddress = "localhost"; // variável
        this.serverPort = 8082; // variável
    }

    public String getObjectId() { return objectId; }
    public Object getInstance() { return instance; }
    public Class<?> getRemoteInterface() { return remoteInterface; }
    public String getServerAddress() { return serverAddress; }
    public int getServerPort() { return serverPort; }

    @Override
    public String toString() {
        return "RemoteObjectInfo{" +
                "objectId='" + objectId + '\'' +
                ", interface=" + remoteInterface.getSimpleName() +
                ", server=" + serverAddress + ":" + serverPort +
                '}';
    }
}