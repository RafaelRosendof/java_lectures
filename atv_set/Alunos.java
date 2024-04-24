public class Alunos{
  public String nome;
  public int nota;

  public Aluno(String nome , int nota){
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

}
