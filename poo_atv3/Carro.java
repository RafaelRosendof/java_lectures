public class Carro{
  //criando classe mãe 
  protected int portas;
  protected String marca;
  protected int ano;
  protected int capacidade;
  
  public Carro(String marca , int portas , int ano , int capacidade){
    this.marca = marca;
    this.portas = portas;
    this.ano = ano;
    this.capacidade = capacidade;
  }

  public void setPortas(int portas){
    this.portas = portas;
  }
  public int GetPortas(){
    return this.portas;
  }

  public void setMarca(String marca){
    this.marca = marca;
  }
  public String GetMarca(){
    return this.marca;
  }

  public void setAno(int ano){
    this.ano = ano;
  }
  public int GetAno(){
    return this.ano;
  }

  public void setCapacidade(int capacidade){
    this.capacidade = capacidade;
  }
  public int GetCapacidade(){
    return this.capacidade;
  }

  public void objetivo(){
    System.out.println("\nTudo\n"); 
  }

}
