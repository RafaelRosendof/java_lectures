public class Main{

  Compara comp = new Compara();
  public static <T> boolean Emaior( T n1 , T n2){

    return comp.compara(n1 , n2);
  }

  public static void main (String[] args){
    System.out.println("Imprimindo para ver se é maior \n");

    comp( 10 , 11);

    System.out.println("\nFIM!!!!")
  }
}
