/*
Lista de métodos para implementar sobre streams
List<Dish> vegMenu = menu.stream()
                          .filter(Dish::isVegetarian)
                          .collect(toList());


List<Integer> numbers = Arrays.asList(1, 2, 1, 3, 3, 2, 4);
numbers.stream()
       .filter(i -> i % 2 == 0)
       .distinct()
       .forEach(System.out::println);

List<Dish> dishes = menu.stream()
                       .filter(dish -> dish.getCalories() > 320)
                       .limit(2)
                       .collect(toList());


takeWhile and dropWhile and .skip(int)

for apply a function to each element of a stream, use map
List<Integer> numbers = Arrays.asList(1, 2, 3, 4, 5);
numbers.stream()
       .map(n -> n * n)
       .forEach(System.out::println);
       map(String::length)


List<String> words = Arrays.asList("Hello", "World");
words.stream()
.map(word -> word.split(""))
.distinct()
.collect(toList());


TODO métodos

flatMap
distinct

*/
package com.Figas;

import java.util.List;
import java.nio.charset.Charset;
import java.util.Arrays;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import java.util.stream.IntStream;
import java.util.Optional;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Map;
import java.io.IOException;



public class todo{

       public static void main(String[] args){


              List<Integer> num = Arrays.asList(1, 2, 3, 4, 5 , 6, 7, 8, 9, 10);

              List<Integer> pares = num.stream().filter(i -> i %2 == 0).collect(Collectors.toList());

              List<Integer> quads = pares.stream().map( i -> i * i).collect(Collectors.toList());

              int sum = quads.stream().reduce(0, Integer::sum);

              System.out.println("\n\n" + sum + "\n\n");


              int mapReduce = num.stream().map(i -> i * i + 1).reduce(0, Integer::sum);

              System.out.println("\n\n" + mapReduce + "\n\n");

              if(num.stream().anyMatch(i -> i % 2 == 0)){
                     System.out.println("Hay pares");
              }

              if(num.stream().allMatch(i -> i % 2 == 0)){
                     System.out.println("Todos son pares");
              }

              //findAny() y findFirst()
              num.stream().filter(i -> i % 2 == 0).findAny().ifPresent(System.out::println);

              System.out.println("\n\n");

              Optional<Integer> res = num.stream().map(i -> i * i).filter(i -> i % 3 == 0).findFirst();
              Optional<Integer> res2 = num.stream().map(i -> i * 10 + (i * i)).reduce((x,y) -> x < y ? x : y);

              System.out.println(res.orElse(0));
              System.out.println("\n\n" + res2.orElse(0));

              long palavras = 0;

              try(Stream<String> linhas = Files.lines(Paths.get("data.txt"), Charset.defaultCharset())){
                     palavras = linhas.flatMap(line -> Arrays.stream(line.split(" ")))
                     .distinct()
                     .count();
              }
              catch(IOException e){
                     e.printStackTrace();
              }

              try(Stream<String> Cont = Files.lines(Paths.get("data.txt"), Charset.defaultCharset())){
                    Map<String , Long> contagem = Cont.flatMap(line -> Arrays.stream(line.split("\\W+"))) 
                    .filter(word -> !word.isEmpty())
                    .collect(Collectors.groupingBy(String::toLowerCase, Collectors.counting()));

                    contagem.forEach((palavra, numeros) ->
                       System.out.println(palavra + ":" + contagem));
              }
              catch(IOException e){
                     e.printStackTrace();
              }
              
                     


              System.out.println("Contando palavras distintas");
              System.out.println("\n\n" + palavras + "\n\n");

              List<Double> listaNum = Stream.generate(Math::random).limit((long)1e4).collect(Collectors.toList());

              listaNum.forEach(System.out::println);
       }
}
