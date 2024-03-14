public class Caminhonete extends Carro{
  protected String nome; 
  protected int litros;

  public Carro id_carro;

  //aqui deve entrar o construtor de carro 
  public Caminhonete(String marca , int portas , int ano , int capacidade , String nome , int litros){
    
  super(marca , portas , ano , capacidade);
  this.nome = nome;
  this.litros = litros;
  }

 @Override 
  public void objetivo(){
    System.out.println("\nANDAR NA LAMA\n");
  } 

  public void consumo(){
    System.out.println("\n CONSUMO ALTO\n");
  }

  @Override
  public void capacidade(int capacidade , int litros){
    return this.capacidade = capacidade + litros;   //diferentes assinaturas
  }

}

/*
 *  public Carro(String marca , int portas , int ano , int capacidade)
 *  */
