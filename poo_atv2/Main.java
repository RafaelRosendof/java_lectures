import java.util.Scanner;

public class Main {

    Scanner leitor = new Scanner(System.in);
    // colocar todos os métodos e testar para ver se está correto.

    public static void main(String[] args){
        Main main = new Main();
        Cliente figas = main.cadastro();

        figas.Mostrador();

        figas.setNome("Figao");

        figas.setIdade(20);

        figas.setPlaca("HDJFK34");        

        figas.setModelo("Tank M1 Abrans");
        
        System.out.println("\nMostrando novo cadastro: ");

        figas.Mostrador();

    }  //colocar a opção para mudar os nomes. e ler do terminal
    
    public Cliente cadastro() {
        System.out.println("Insira o nome:  ");
       String nome = leitor.nextLine();

        System.out.println("Insira a idade: ");
        int idade = leitor.nextInt();
        leitor.nextLine();

        System.out.println("Insira o salário: ");
        int salario = leitor.nextInt();
        leitor.nextLine();

        System.out.println("Insira o modelo: ");
        String modelo = leitor.nextLine();

        System.out.println("Insira o ano: ");
        int ano = leitor.nextInt();
        leitor.nextLine();

        System.out.println("Insira a placa: ");
        String placa = leitor.nextLine();

        return new Cliente(nome, idade, salario, modelo ,ano, placa);
    }
    
}