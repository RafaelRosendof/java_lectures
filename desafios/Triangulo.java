import java.util.Scanner;
public class Triangulo extends FormaGeometrica{
  

  private Ponto ponto1;
  private Ponto ponto2;
  
  public Triangulo(Ponto ponto1 , Ponto ponto2){
    this.ponto1 = ponto1;
    this.ponto2 = ponto2;
  }


  @Override
  public float calcula_area(){
    float deltax = ponto1.getX() - ponto2.getX();
    float deltay = ponto1.getY() - ponto2.getY(); 

    //criei um Triangulo retangulo

   // float c2 = deltay**2 + deltax**2;
   // float hipo = Math.sqrt(c2);
    float area = (deltax * deltay)/2;

    return Math.abs(area);
  }

  public static void main(String[] args){
  Scanner leitor = new Scanner(System.in);
    System.out.println("Digite o x e o y do primeiro ponto: ");


    float x1 = leitor.nextFloat();
    float y1 = leitor.nextFloat();
    Ponto ponto1 = new Ponto(x1,y1);

    System.out.println("Digite o x e o y do segundo ponto: ");
  
    float x2 = leitor.nextFloat();
    float y2 = leitor.nextFloat();
    Ponto ponto2 = new Ponto(x2,y2);

    Triangulo triangoulo = new Triangulo(ponto1,ponto2);
    float res = triangoulo.calcula_area();

    System.out.printf("Imprimindo %.2f\n",res);
  }
}
