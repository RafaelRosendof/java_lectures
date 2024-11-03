/*
 * Crie um método que recebe um número variável de parâmetros do tipo
inteiro e retorna um set contendo apenas os números pares. Caso não seja
passado nenhum argumento, o método deve lançar uma exceção
personalizada.
 */
import java.util.HashSet;
import java.util.Set;
public class Figas{

    public static Set<Integer> numPar(int... numeros) throws SemArgsException {

        if(numeros.length == 0){
            throw new SemArgsException("Nenhum argumento foi passado ");
        }

        Set<Integer> pares = new HashSet<>();
        for(int num : numeros){
            if(num % 2 == 0){pares.add(num);}
        }
        return pares;
    }

    public static void main(String[] args){
        try{
            Set<Integer> nums = numPar(2,4,6,5,4,3,2,11,23,45,67);
            System.out.println("numeros pares: " + nums);
        }catch(SemArgsException e){
            System.out.println(e.getMessage());
        }
    }
}