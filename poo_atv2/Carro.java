public class Carro{
    private String modelo;
    private int ano;
    private String placa;

    public Carro(String modelo , int ano , String placa){
        this.modelo = modelo;
        this.ano = ano;
        this.placa = placa;
    }
   


    public String getModelo(){
        return this.modelo;
    }

    public String getPlaca(){
        return this.placa;
    }

//aqui estava com um get.placa
    public int getAno(){
        return this.ano;
    }
    public void setIdade(int ano){
        this.ano = ano;
    }



}