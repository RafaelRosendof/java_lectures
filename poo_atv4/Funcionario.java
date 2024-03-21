public abstract class Funcionario extends Servidor{
//metodos...
  protected String obrigation;

  public Servidor servidor;

  public Funcionario(String nome , String matricula , String functional ){
    super(nome , matricula , functional);
    this.obrigation = obrigation;
  }
  public abstract void objetivos();

  public void setObrigation(String obrigation){
    this.obrigation = obrigation;
  } 
  public String getObrigation(){
    return this.obrigation;
  }
}
