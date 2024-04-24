import java.util.ArrayList;
import java.util.List;

public class Main{
  
  public static void main(String[] args){
    List<Pessoa> pessoas = new ArrayList<>();

    pessoas.add(new Pessoa("Rafael" , 19));

    pessoas.add(new Pessoa("Ana", 25));
    pessoas.add(new Pessoa("Pedro", 30));
    pessoas.add(new Pessoa("Maria", 40));
    pessoas.add(new Pessoa("João", 35));

    System.out.println("Lista após adição de elementos:");
    pessoas.forEach(p -> System.out.println(p.toString()));

    pessoas.remove(2);
    System.out.println("\nLista após remoção por índice:");
    pessoas.forEach(p -> System.out.println(p.toString()));

    Pessoa pessoaToRemove = new Pessoa("Maria", 40);
    pessoas.remove(pessoaToRemove);
    System.out.println("\nLista após remoção por objeto:");
    pessoas.forEach(p -> System.out.println(p.toString()));
    //continua dps.
  }
}


/* 
 *Crie uma classe que possua pelo menos um atributo e que sobrescreva os métodos
toString e equals, herdados de Object. feito

- Na Main, declare uma lista de objetos da classe definida anteriormente. A declaração
deve ser feita com a interface List e a implementação com o construtor da ArrayList.
- Adicione ao menos cinco elementos na lista utilizando o método add, de ArrayList.
- Remova um dos elementos utilizando a assinatura do método remove que recebe o
índice do elemento a ser removido como parâmetro.
- Remova um dos elementos utilizando a assinatura do método remove que recebe o
objeto a ser removido como parâmetro.
- Percorra a lista utilizando o método forEach. Utilize uma expressão lambda para definir a
ação realizada sobre cada elemento da lista.
- Entre cada operação definida anteriormente , imprima o conteúdo da lista passando ela
como parâmetro do método System.out.prinlnt().*/

