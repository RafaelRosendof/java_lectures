public class Professor extends Usuario implements Interface{
  //2 metodos de usuário
  @Override
  public void Tenho_acesso(){
    System.out.println("Tenho acesso ao Sistema de professor \n");
  }
   @Override 
    public void Tem_matricula(){
      System.out.println("Tenho matrícula como professor ! \n");
  }



  //2 metodos de interface 
  @Override
  public void Aplicar_prova(){
    System.out.println("Estão lascados, prova vai torar ! \n");
  }
  @Override
  public void Suspender_aluno(String nome){
    System.out.println("Está suspenso %s",nome)
  }

}

/*
 * public interface Professor{
  private void Aplicar_prova();

  default public void Suspender_aluno(){
    System.out.println("Você está suspenso!!! \n");
  }

}

*/
