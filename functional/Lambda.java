import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.function.*;
import java.util.function.Function;
import java.util.stream.Collectors.*;

public class Lambda {

    public static void MakeList(List<Integer> lista, Random rand) {
        if (lista.size() >= 100) {
            return;
        }

        //lista.forEach(x -> x = rand.nextInt(100));

        lista.add(rand.nextInt(100));

        MakeList(lista, rand);
    }

    public static List<Integer> Map(
        List<Integer> lista,
        Function<Integer, Integer> f
    ) {
        if (lista.isEmpty()) {
            return new ArrayList<>();
        }
        List<Integer> res = new ArrayList<>();

        lista.forEach(x -> res.add(f.apply(x)));

        return res;
    }

    public static Integer Reduce(
        List<Integer> lista,
        BiFunction<Integer, Integer, Integer> f,
        int identity
    ) {
        int res = identity;

        for (int value : lista) {
            res = f.apply(res, value);
        }
        return res;
    }

    public static void main(String[] args) {
        Function<Integer, Integer> f = x -> x * x;

        System.out.println(f.apply(5));

        List<Integer> lista = new ArrayList<>();

        Random rand = new Random();
        MakeList(lista, rand);

        lista.forEach(num ->
            System.out.println("numero: " + num + "resultado: " + f.apply(num))
        );
        Function<Integer, Integer> map = x -> x * 10;
        // Function<Integer , Integer> red = x + y -> z;
        BiFunction<Integer, Integer, Integer> red = (x, y) -> x + y;

        List<Integer> lista2 = Map(lista, map);

        int reduce = Reduce(lista, red, 0);

        lista2.forEach(System.out::println);
        //lista.forEach(System.out::println);
        System.out.println("Reduce: " + reduce);

        lista2
            .stream()
            .map(x -> x + 10)
            .filter(x -> x % 2 == 0)
            .forEach(System.out::println);

        System.out.println("Stream finalizada");
    }
}
