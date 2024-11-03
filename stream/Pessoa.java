import java.util.List;
public class Pessoa {

    protected String nome;
    protected int idade;
    protected double altura;
    protected List<String> hobbies;

    public Pessoa(String nome , int idade , double altura, List<String>hobbies){
        this.nome = nome;
        this.idade = idade;
        this.altura = altura;
        this.hobbies = hobbies;
    }

    public String getNome(){
        return nome;
    }

    public int getIdade(){
        return idade;
    } 

    public double getAltura(){
        return altura;
    }

    public List<String> getHobbies(){
        return hobbies;
    }

    @Override
    public String toString() {
        return "Pessoa{" +
                "nome='" + nome + '\'' +
                ", idade=" + idade +
                ", altura=" + altura +
                '}';
    }

}