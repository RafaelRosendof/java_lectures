public abstract class Servidor{
  protected String nome;
  protected String matricula;
  protected String functional;

  public Servidor(String nome , String matricula, String functional){
   this.nome = nome;
    this.matricula = matricula;
    this.functional = functional;
  }
  public void setMatricula(String matricula){
    this.matricula = matricula;
  } 
  public void setFunctional(String functional){
    this.functional = functional;
  }
  public String getMatricula(){
    return this.matricula;
  }
  public String getFunctional(){
    return this.functional;
  }
  public abstract void service();
//    System.out.println("Sou um Servidor !!!");
}
