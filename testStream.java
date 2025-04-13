import java.io.IOException;
import java.math.*;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Random;
import java.util.function.*;
import java.util.function.Function;
import java.util.stream.Collectors.*;
import java.util.stream.Stream;

public class testStream {

    private static long res = -1;

    public static void enche(List<Integer> lista, Random rand) {
        if (lista.size() >= 10000) {
            return;
        }

        lista.add(rand.nextInt());
        enche(lista, rand);
    }

    public static long paralelo(List<Integer> lista) {
        long res = lista
            .stream()
            .mapToLong(i -> (long) i * i)
            .parallel()
            .filter(i -> i % 2 == 0)
            .sum();
        return res;
    }

    public static long leitor(String arquivo) {
        try (
            Stream<String> linhas = Files.lines(
                Paths.get(arquivo),
                Charset.defaultCharset()
            )
        ) {
            return linhas
                .flatMap(line -> Arrays.stream(line.split("\\s+")))
                .distinct()
                .count();
        } catch (IOException e) {
            e.printStackTrace();

            return -1;
        }
    }

    public static void th1(String arquivo) {
        Thread t = new Thread(() -> {
            res = leitor(arquivo);
        });

        t.start();

        while (t.isAlive()) {
            System.out.println("Esperando !!!");
            try {
                Thread.sleep(100);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }

        System.out.println("O resultado da quantidade de palavras foi: " + res);
    }

    public static long pow2(List<Integer> lista) {
        //partindo para a operação paralela de strings
        Random rand = new Random();
        return lista
            .stream()
            .parallel()
            .mapToLong(i -> (long) (i * 2 * Math.sin(rand.nextInt())))
            .sum();
    }

    public static int slice(ArrayList<Integer> lista) {
        int tam = lista.size();

        int num_chunks = tam / 100_000;

        return Math.max(num_chunks, 1);
    }

    public static List<Integer> aleatorio(int size) {
        Random random = new Random();
        List<Integer> novaLista = new ArrayList<>(size);
        for (int i = 0; i < size; i++) {
            novaLista.add(random.nextInt());
        }
        return novaLista;
    }

    public static void fazThreads(ArrayList<Integer> lista) {
        int num_chunks = slice(lista);
        int tam = lista.size();
        int chunk_size = tam / num_chunks;

        List<Thread> threads = new ArrayList<>();
        List<Long> results = Collections.synchronizedList(new ArrayList<>());

        //cada thread vai fazer a função aleatorio e pow2

        for (int i = 0; i < num_chunks; i++) {
            //cada thread recebe um pseudo array e faz a execução das duas funções
            int start = i * chunk_size;
            int end = (i == num_chunks - 1) ? tam : start + chunk_size;

            List<Integer> subLista = new ArrayList<>(lista.subList(start, end));

            Thread t = new Thread(() -> {
                List<Integer> aleatoria = aleatorio(subLista.size());
                //aleatoria.forEach(System.out::println);
                long parcial = pow2(aleatoria);
                results.add(parcial);
                System.out.println("Resultado : " + parcial);
            });

            threads.add(t);
            t.start();
        }

        for (Thread t : threads) {
            try {
                t.join();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        results.forEach(System.out::println);
        long total = results.stream().mapToLong(Long::longValue).sum();
        System.out.println("Resultado final: " + total);
    }

    // Fazer pequeno teste com threads, uma função que lança uma thread para consumir um arquivo e contar as palavras desse arquivo
    //Para ser concorrente tem que ter corrida de threads? e consumo? depois poderia por isso para consumir api's de bitcoin

    public static void main(String args[]) {
        System.out.println("Iniciando procedicmento de streams paralelas");

        List<Integer> lista = new ArrayList<>();
        Random random = new Random();

        enche(lista, random);

        System.out.println("Printando lista ");
        lista.forEach(System.out::println);

        long res = paralelo(lista);

        System.out.println("\n\n Saida do long res " + res);

        th1("/home/rafael/modulo_ia_hubbi/dados/dados1/arquivo_5.txt");

        System.out.println(
            "Iniciando experimento de processamento concorrente e paralelo \n\n"
        );

        ArrayList<Integer> lista2 = new ArrayList<>();
        for (int i = 0; i < 1_000_000; i++) {
            lista2.add(i);
        }

        //erro asqui

        fazThreads(lista2);
    }
}
