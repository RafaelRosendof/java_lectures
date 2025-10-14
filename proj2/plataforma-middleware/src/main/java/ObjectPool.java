import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.Queue;

// classe genérica para pool de objetos
// nessa class é implementado o padrão Object Pool
// Usado para gerenciar instâncias de serviços com lifecycle POOLING
// métodos acquire() e release() para pegar e devolver objetos ao pool

public class ObjectPool<T> {

    private final Queue<T> pool = new ConcurrentLinkedQueue<>();
    private final Class<T> clazz;

    @SuppressWarnings("unchecked")
    public ObjectPool(Class<?> clazz, int size) throws Exception {
        this.clazz = (Class<T>) clazz;
        for (int i = 0; i < size; i++) {
            T instance = this.clazz.getDeclaredConstructor().newInstance();
            pool.add(instance);
        }
    }

    

    public T acquire() {
        T obj = pool.poll(); // Pega um objeto da fila
        if (obj == null) {
            // Cria nova instância se pool estiver vazio
            try {
                obj = clazz.getDeclaredConstructor().newInstance();
                System.out.println("Pool vazio, criando nova instância de " + clazz.getSimpleName());
            } catch (Exception e) {
                System.err.println("Erro ao criar nova instância: " + e.getMessage());
                return null;
            }
        }
        return obj;
    }

    public int size() {
        return pool.size();
    }

    public boolean isEmpty() {
        return pool.isEmpty();
    }

    public void release(T obj) {
        if (obj != null) {
            pool.offer(obj); // Devolve o objeto para a fila
        }
    }
}