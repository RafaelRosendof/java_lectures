public class Alunos implements Interface_aluno{
  
  //implementando primeiro a classe usuario
//  @Override
  //public void Tenho_acesso(){
  //  System.out.println("Tenho acesso como Aluno! \n");
 // }

//  @Override
//  public void Tem_matricula(){
//    System.out.println("Tenho matricula como estudante \n");
  //}
  
  @Override
  public void Fazendo_prova(String prova){
    System.out.printf("Fazendo prova %s \n",prova);
  }

  public void listarMaterias(){
    Materias();
  }
}
