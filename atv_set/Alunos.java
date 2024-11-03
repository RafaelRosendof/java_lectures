/*
 * Crie uma classe com pelo menos dois atributos.
Na main, crie uma lista de elementos dessa classe, implementada por ArrayList.

Ordene a lista utilizando o método sort de List (implemente a interface funcional Comparator<T> 
utilizando uma expressão lambda e passe como parâmetro do método sort).

Embaralhe a lista utilizando o método estático shuffle, de Collections.

Ordene a lista novamente, dessa vez utilizando o método estático sort de Collections 
(a sua classe precisa implementar a interface Comparable<T>). 
Utilize um critério de ordenação diferente
 */

public class Alunos implements Comparable<Alunos>{
  private String nome;
  private int nota;

  public Alunos(String nome , int nota){
    this.nome = nome;
    this.nota = nota;
  }
  public void setNota(int nota){
    this.nota = nota;
  }

  public int getNota(){
    return this.nota;
  }

  public void setNome(String nome){
    this.nome = nome;
  }
  public String getNome(){
    return this.nome;
  }

      @Override
    public String toString() {
        return "Aluno{" +
                "nome='" + nome + '\'' +
                ", nota=" + nota +
                '}';
    }

    @Override
    public int compareTo(Alunos outroAluno) {
        return Integer.compare(this.nota, outroAluno.getNota());
    }

}
