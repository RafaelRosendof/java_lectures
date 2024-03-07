import java.util.Scanner;
public class exercicios_1_2_3{

    Scanner leitor = new Scanner(System.in);

    public void primeira(){
        System.out.println("Digite o numero do tamanho do array");
        int n = leitor.nextInt();

        int []arr = new int[n];

        for(int i = 0; i < arr.length; i++){
            System.out.println("Digite o " + i + " : ");
            arr[i] = leitor.nextInt();
        }

        System.out.println("Printando apenas números pares: ");

        for(int i = 0; i < arr.length; i++){
            if(arr[i] % 2 == 0){
                System.out.println(" " + arr[i] + " ");
            }
        }
    }

    public void segunda(){

        System.out.println("\n\nDigite P para começar ou q para sair");
        String s = leitor.nextLine();

        while(!s.equalsIgnoreCase("q")){
            System.out.println("Digite a palavra ou aperte q para sair: ");

            String palavra = leitor.nextLine();

            String invert = new StringBuilder(palavra).reverse().toString();


            if(palavra.equalsIgnoreCase(invert)){
                System.out.print("É um palindromo \n");
            }else{ System.out.println("Não é um palindromo \n");}


            System.out.println("Digite P para começar ou q para sair");
            s = leitor.nextLine();

            if(s.equalsIgnoreCase("q")){break;}

        }
    }

    public void terceira(){

        System.out.println("\n\nDigite a palavra: ");

        String [] arr = {"carro" , "moto" , "figao" , "america"};

        while(true){
            System.out.println("Digite a palavra ou digite sair: ");
            String palavra = leitor.nextLine();

            if(palavra.equalsIgnoreCase("sair")){
                break;
            }

            boolean achou = false;
            for(String p : arr){ 
                if(palavra.equalsIgnoreCase(p)){ achou = true; break; }
        }
        if(achou){
                System.out.println("Palavra encontrada.");
            } else {
                System.out.println("Palavra não encontrada.");
            }
        }

    }


    public void quarta(){

        System.out.println("\n\nDigite o número: ");
        int numero = leitor.nextInt();

        int hora = numero % 3600;
        int minuto = (numero - 3600) / 60;
        numero = numero % 60;

        System.out.println(String.format("%02d:%02d:%02d", hora , minuto, numero));
    }

    public void quinta(){
        System.out.println("Qual o seu tier? ");

        int tier = leitor.nextInt();

        switch (tier) {
            case 1:
            System.out.println("Permissão para jogar online");
            break;

            case 2:
            System.out.println("Permissão para jogar online" + " " + "Acesso ao catálogo de jogos");
            break;


            case  3:
            System.out.println("Permissão para jogar online" +" " + "Acesso ao catálogo de jogos" + " " + "Acesso ao catálogo de jogos clássicos e game trials");
            break;

            default:
                break;
        }
    }



        public static void main(String[] args) {
        exercicios_1_2_3 exercicios = new exercicios_1_2_3();

        // Exemplo de uso
        exercicios.primeira();
        exercicios.segunda();
        exercicios.terceira();
        exercicios.quarta();
        exercicios.quinta();
    }

    
}