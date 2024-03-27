public class Professor extends Usuario implements Interface_prof{
  //2 metodos de usuário
  @Override
  public void Tenho_acesso(){
    System.out.println("Tenho acesso ao Sistema de professor \n");
  }
//   @Override 
//    public void Tem_matricula(){
//      System.out.println("Tenho matrícula como professor ! \n");
 // }



  //2 metodos de interface 
  @Override
  public void Aplicar_prova(){
    System.out.println("Estão lascados, prova vai torar ! \n");
  }
//':wq
  //  @Override
  public void Suspender_aluno(String nome){
    System.out.printf("Está suspenso %s",nome);
  }

}
