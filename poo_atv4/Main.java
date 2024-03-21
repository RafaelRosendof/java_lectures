import java.util.Scanner;

public class Main {
    Scanner leitor = new Scanner(System.in);

    public static void main(String[] args) {
        String nome = "Rafael";
        String matricula = "34frt45";
        String functional = "Gerenciar";
        String obrigacao = "Responsável pelo projeto ";
        Main main = new Main();
        Servidor servidor = new Gerente(nome, matricula, functional, "", obrigacao);

        System.out.println(servidor instanceof Gerente);
        System.out.println(servidor instanceof Servidor);
    }
}
