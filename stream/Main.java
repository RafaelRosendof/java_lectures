import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import java.util.Arrays;
/*
 * Crie pipelines (sequência de operações) utilizando Streams que incluam
todas as operações intermediárias estudadas (as operações podem estar
divididas em pipelines diferentes).
- Tentem criar cenários em que faça sentido utilizar cada uma dessas
operações.
- As Streams criadas devem iniciar como Streams de objetos de classes
personalizadas.
 */

 public class Main{

    public static void main(String[] args){


        Stream<Pessoa> streamPessoa = Stream.of( 
            new Pessoa("Rafael", 19, 1.77, Arrays.asList("video game", "futebol")),
            new Pessoa("Nareba", 21, 1.79, Arrays.asList("videos", "tenis de mesa")),
            new Pessoa("Tetola", 20, 1.73, Arrays.asList("falar merda", "surf")),
            new Pessoa("Cleitin", 21, 1.62, Arrays.asList("fazer nadaj", "basquete")),
            new Pessoa("Figas", 19, 1.81, Arrays.asList("video game", "MMA"))
        );

        //filter   TIVE QUE COMENTAR OS OUTROS POIS TAVA DANDO ERRO ENTÃO FIZ ALGUNS ISOLADAMENTE
        //streamPessoa.filter(pessoa -> pessoa.getAltura() > 1.80).forEach(System.out::println); 

        //map
        //Stream<Pessoa> idadeDobrada = streamPessoa.map(pessoa -> new Pessoa(pessoa.nome, pessoa.idade * 2, pessoa.altura));
        //idadeDobrada.forEach(System.out::println);

        //flatMap
        Stream<String> streamHob = streamPessoa.flatMap(pessoa -> pessoa.getHobbies().stream());
        //streamHob.forEach(System.out::println);

        //distinct
        streamHob = streamHob.distinct();

        //sorted
        streamHob = streamHob.sorted();

        //skip
        streamHob = streamHob.skip(2);

        //limit
        streamHob = streamHob.limit(3);

        //peelkkk
        streamHob.peek(System.out::println).count();

    }
 }