import java.util.Scanner;
    
public class Cadastro{
    Scanner leitor = new Scanner(System.in);
    private String nome;
    private int idade;
    private long cpf;
    private float altura;

    public Carro qual_Carro = new Carro();


    public Cadastro(){
        qual_Carro.cadastro();
    }




    public void nova_pessoa(){
        System.out.println("Qual o nome: ");
        this.nome = leitor.nextLine();

        System.out.println("\n Qual a idade: ");
        this.idade = leitor.nextInt();

        System.out.println("\n Qual o cpf: ");
        this.cpf = leitor.nextLong();

        System.out.println("\n Qual a altura: ");
        this.altura = leitor.nextFloat();
            }

    public void altera_altura(){
        System.out.println("\n Qual a nova altura: ");
        this.altura = leitor.nextFloat();
            }

    public int getIdade(){
        return this.idade;
        }

    public Carro get_carro (){
        return this.qual_Carro;
    }

}