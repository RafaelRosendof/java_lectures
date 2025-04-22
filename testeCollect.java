import java.io.IOException;
import java.math.*;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.sql.Ref;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Random;
import java.util.function.*;
import java.util.function.*;
import java.util.function.Function;
import java.util.stream.Collectors.*;
import java.util.stream.Stream;
import javax.naming.ldap.Rdn;

public class testeCollect {

    private static void enche(List<Integer> lista) {
        Random rand = new Random(42);
        int size = 10;

        for (int i = 0; i < size; i++) {
            lista.add(rand.nextInt() % 99);
        }
    }

    private static <T> List<T> map(Function<T, T> f, List<T> list, int size) {
        for (int i = 0; i < size; i++) {
            list.add(f.apply(list.get(i)));
        }

        return list;
    }

    private static void encheMap(Map<Integer, Double> mapa) {
        Random rand = new Random(40);
        int size = 10;

        for (int i = 0; i < size; i++) {
            double log = Math.log(i);
            mapa.put(rand.nextInt(), log);
        }
    }

    public static void main(String[] args) {
        List<Integer> lista = new ArrayList<>();

        enche(lista);

        //lista.forEach(System.out::println);

        System.out.println("Agora fazendo o mesmo esquema para o mapa\n\n");

        Map<Integer, Double> mapa = new HashMap<>();

        encheMap(mapa);

        /*mapa.forEach((key, value) ->
            System.out.println(
                "Número aleatório: " + key + "Número log: " + value
            )
        );
        */

        System.out.println(
            "Tamanho da lista antes do removeif: " +
            lista.size() +
            "  tamanho do mapa: " +
            mapa.size()
        );

        lista.removeIf(x -> x % 2 != 0);
        //map don't have the method removeIf

        mapa
            .entrySet()
            .stream()
            .sorted(Entry.comparingByKey())
            .forEachOrdered(entry ->
                System.out.println(
                    "Chave: " + entry.getKey() + " valor: " + entry.getValue()
                )
            );

        System.out.println("Tamanho da lista: " + lista.size());

        System.out.println(
            "Criando expressões lambdas para utilizar funções em listas ou em DSA"
        );

        //Tenho uma função que dado uma lista, retorna a lista cheia com números aleatórios
        // Como que faço essa expressão lambda mapear a função na lista
        // I'm have a function that recive a list and return the list full of random numbers
        // How can i'm create a lambda function that put the list and the function and return the function
        Function<List<Integer>, List<Integer>> f = list -> {
            enche(list);
            return list;
        };

        List<Integer> lista2 = new ArrayList<>();

        f.apply(lista2);

        lista2.forEach(System.out::println);
        System.out.println("\n\n Antes do lambda x² \n\n");
        Function<Integer, Integer> f2 = x -> x * x;

        Function<List<Integer>, List<Integer>> f3 = list ->
            map(f2, lista2, lista2.size());

        f3.apply(lista2);

        lista2.forEach(System.out::println);
    }
}
