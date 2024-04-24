import java.util.function.Function;
import java.util.ArrayList;
import java.util.List;


public class Pessoa{
  public String nome;
  public int idade;

  public Pessoa(String nome , int idade){
  this.nome = nome;
  this.idade = idade;

  }

  //sobrescrevendo os métodos
  @Override
  public String toString(){
    return this.nome;
  }

  @Override
  public boolean equals(Object obj){
    if(this == obj){
      return true;
    }
    if(obj == null || getClass() != obj.getClass()){
      return false;
    }
    Pessoa pessoa = (Pessoa) obj;
    return idade == pessoa.idade && nome.equals(pessoa.nome);
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

