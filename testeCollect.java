import java.io.IOException;
import java.math.*;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Random;
import java.util.function.*;
import java.util.function.Function;
import java.util.stream.Collectors.*;
import java.util.stream.Stream;
import javax.naming.ldap.Rdn;

public class testeCollect {

    private static void enche(List<Integer> lista) {
        Random rand = new Random(42);
        int size = 1_000_000;

        for (int i = 0; i < size; i++) {
            lista.add(rand.nextInt() / 100);
        }
    }

    private static void encheMap(Map<Integer, Double> mapa) {
        Random rand = new Random(40);
        int size = 1_000_000;

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
    }
}
