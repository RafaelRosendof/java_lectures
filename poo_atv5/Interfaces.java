public interface Professor{
  private void Aplicar_prova();

  default public void Suspender_aluno(){
    System.out.println("Você está suspenso!!! \n");
  }

}

public interface Aluno{

  public void Fazendo_prova();

  default public void Materias(){
    System.out.println("Matemáica \n Portugês \n Geografia \n História \n");
  }

}
