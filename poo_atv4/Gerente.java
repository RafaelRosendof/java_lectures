public class Gerente extends Funcionario{
  protected String obrigacao;

  public Gerente(String nome , String matricula , String functional , String obrigation , String obrigacao){
    super(nome , matricula , functional);
    this.obrigacao = obrigacao;
  }

  @Override
  public void objetivos(){
    System.out.println("Gerenciar a empresa! ");
  }
  @Override
  public void service(){
    System.out.println("SOU O GERENTE DA EMPRESA !!!");
  }

  @Override
  public void setObrigation(String obrigacao){
    this.obrigacao = obrigacao;
  }
  
  public void salario(){
    System.out.println("EU GANHO MUITO DINHEIRO!!!");
  }

}
