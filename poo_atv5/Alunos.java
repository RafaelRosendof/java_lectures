public class Alunos extends Usuario implements Interface{
  
  //implementando primeiro a classe usuario
  @Override
  public void Tenho_acesso(){
    System.out.println("Tenho acesso como Aluno! \n");
  }

  @Override
  public void Tem_matricula(){
    System.out.println("Tenho matricula como estudante \n");
  }
  
  @Override
  public void Fazendo_prova(String prova){
    System.out.println("Fazendo prova %s \n",prova);
  }
  @Override
  Aluno.super.Materias();  
}
