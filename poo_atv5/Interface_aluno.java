public interface Interface_aluno{

  void Fazendo_prova(String nome);

  default public void Materias(){
    System.out.println("Matemáica \n Portugês \n Geografia \n História \n");
  }

}
