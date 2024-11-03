/*
 * Crie um programa que repetidamente lê um número inteiro digitado pelo
usuário e imprime o seu módulo (usar Math.abs).
O programa deve tratar a exceção InputMismatchException, lançada quando
solicitamos que o Scanner leia uma informação de um tipo e o usuário digita
algo de um tipo não compatível.
 */
import java.util.InputMismatchException;
import java.util.Scanner;
 public class Main{
    //Scanner leitor = new Scanner(System.in);
    public static void main(String [] args){
        Scanner leitor = new Scanner(System.in);
        int x;
        while(true){
            System.out.println("Digite o número: ");
          //  x = leitor.nextInt();

            try{
                x = leitor.nextInt();
                // Calcula e imprime o módulo do número
                System.out.println("O módulo de " + x + " é: " + Math.abs(x));

            }catch(InputMismatchException e){
                System.out.println("Digitou algo de errado: ");

                x = leitor.nextInt();
            }
        }
    }
 }