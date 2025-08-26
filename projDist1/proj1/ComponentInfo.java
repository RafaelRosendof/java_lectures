import java.net.InetAddress;

public class ComponentInfo {
    private final ComponentType type;
    private final InetAddress address;
    private final int port;
    private long lastHeartbeat;

    public ComponentInfo(ComponentType type, InetAddress address, int port) {
        this.type = type;
        this.address = address;
        this.port = port;
        this.lastHeartbeat = System.currentTimeMillis();
    }

    public ComponentType getType() {
        return type;
    }

    public InetAddress getAddress() {
        return address;
    }

    public int getPort() {
        return port;
    }

    public void updateHeartbeat() {
        this.lastHeartbeat = System.currentTimeMillis();
    }

    public long getLastHeartbeat() {
        return lastHeartbeat;
    }

    @Override
    public String toString() {
        return String.format("%s @ %s:%d", type, address.getHostAddress(), port);
    }
}