import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.Queue;

public class ObjectPool<T> {
    private final Queue<T> pool = new ConcurrentLinkedQueue<>();

    public ObjectPool(Class<T> clazz, int size) throws Exception {
        for (int i = 0; i < size; i++) {
            pool.add(clazz.getDeclaredConstructor().newInstance());
        }
    }

    public T acquire() {
        T obj = pool.poll(); // Pega um objeto da fila
        if (obj == null) {
            // Lógica para lidar com pool vazio (ex: esperar ou criar novo)
            System.err.println("Pool de objetos vazio!");
        }
        return obj;
    }

    public void release(T obj) {
        if (obj != null) {
            pool.offer(obj); // Devolve o objeto para a fila
        }
    }
}