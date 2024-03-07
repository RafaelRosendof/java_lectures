import java.util.Scanner;
public class Carro{
    Scanner leitor = new Scanner(System.in);

        private String placa;
        public int ano;
        private String marca;

        

        public void cadastro(){

            System.out.println("\n Informe a placa: ");
            this.placa = leitor.nextLine();

            System.out.println("\n Informe o ano do carro: ");
            this.ano = leitor.nextInt();


            System.out.println("\n Informe a marca do carro:  ");
            this.marca = leitor.nextLine();            
        } 



    }