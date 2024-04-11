import java.util.Functional.*;

public class Main{
  
  public static void main(String[] args){
    List<Pessoa> pessoas = new ArrayList<>();

    pessoas.add(new Pessoa("Rafael" , 19));

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

