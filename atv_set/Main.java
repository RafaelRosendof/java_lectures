import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

public class Main {

  public static void main(String[] args) {

    List<Alunos> listaAlunos = new ArrayList<>();

    listaAlunos.add(new Alunos("Rafael", 10));
    listaAlunos.add(new Alunos("Grilo", 8));
    listaAlunos.add(new Alunos("Nareba", 9));
    listaAlunos.add(new Alunos("Figão", 7));

    listaAlunos.sort((a1,a2) -> a1.getNome().compareTo(a2.getNome()));
    System.out.println("Ordenado por nome");
    System.out.println(listaAlunos);

    Collections.shuffle(listaAlunos);
    System.out.println("baralhando");
    System.out.println(listaAlunos);

    Collections.sort(listaAlunos);
    System.out.println("Ordenando por nota ");
    System.out.println(listaAlunos);

  }
}
