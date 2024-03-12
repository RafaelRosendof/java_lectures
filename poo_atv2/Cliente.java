public class Cliente{
    private String nome;
    private int idade;
    public double salario;

    private Carro id_carro;

   // private String modelo = id_carro.getModelo();
   // private String placa = id_carro.getPlaca();
   // private int ano = id_carro.getAno();

   private String modelo;
   private String placa;
   private int ano;

     


    public Cliente(String nome , int idade , double salario ,String modelo , int  ano , String placa){
       // id_carro = new Carro(modelo, ano, placa);
        this.nome = nome;
        this.idade = idade;
        this.salario = salario;
        this.id_carro = new Carro(modelo, ano, placa);
        this.modelo =  id_carro.getModelo();
        this.placa = id_carro.getPlaca();
        this.ano = id_carro.getAno();
    }


    public void setNome(String nome){
        this.nome = nome;
    }
    
    public void setIdade(int idade){
        this.idade = idade;
    }

    public int getIdade(){
        return this.idade;
    }
    public String getNome(){
        return this.nome;
    }

    public void aumento(float aumento){
        this.salario = this.salario * (aumento + 1); 
    }

    public void setSalario(double salario){
        this.salario = salario;
    }
    public double getSalario(){
        return this.salario;
    }

    public void setModelo(String modelo){
        this.modelo = modelo;
    }
    public String getModelo(){
        return this.modelo;
    }

    public void setPlaca(String placa){
        this.placa = placa;
    }
    public String getPlaca(){
        return this.placa;
    }

    public void setAno(){
        this.ano = ano;
    }
    public int getAno(){
        return this.ano;
    }

    public void Mostrador() {
        System.out.printf("Nome: %s, Idade: %d, Salário: %.2f, Modelo: %s, Ano: %d, Placa: %s%n", 
                      this.nome, this.idade, this.salario, this.modelo, this.ano, this.placa);
    }


}