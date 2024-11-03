import java.util.Random;
import java.util.ArrayList;
import java.util.List;
import java.util.Collections;
import java.util.function.Function;
import java.util.stream.Collectors;

public class MergeBS{

    public static List<Integer> MergeSort(List<Integer> lista){
        if(lista.size() <= 1){
            return lista;
        }

        int mid = lista.size() / 2;

        List<Integer> left = lista.subList(0 , mid);
        List<Integer> right = lista.subList(mid , lista.size());

        left = MergeSort(left.parallelStream().collect(Collectors.toList()));
        right = MergeSort(right.parallelStream().collect(Collectors.toList()));

        return merge(left , right);
    }

    private static List<Integer> merge(List<Integer> left , List<Integer> right){
        List<Integer> result = new ArrayList<>();
        int leftIndex = 0 , rightIndex = 0;

        while(leftIndex < left.size() && rightIndex < right.size()){
            if(left.get(leftIndex) < right.get(rightIndex)){
                result.add(left.get(leftIndex));
                leftIndex++;
            }
            else{
                result.add(right.get(rightIndex));
                rightIndex++;
            }
        }

        while(leftIndex < left.size()){
            result.add(left.get(leftIndex));
            leftIndex++;
        }
        while(rightIndex < right.size()){
            result.add(right.get(rightIndex));
            rightIndex++;
        }

        return result;
    }


    private static int Bsearch(List<Integer> lista , int target , int start , int end){
        if(start > end){
            return -1;
        }

        int mid = (start + end) / 2;

        if(lista.get(mid) == target){
            return mid;
        }
        else if(lista.get(mid) < target){
            return Bsearch(lista , target , mid+1 , end);
        }
        else{
            return Bsearch(lista , target , start , mid-1);
        }
    }

    public static int Bsearch(List<Integer> lista , int target){
        return Bsearch(lista , target , 0 , lista.size() - 1);
    }
    public static void main(String[] args){

        List <Integer> lista = new ArrayList<>();
        Random rand = new Random();

        for(int i = 0; i < 10000; i++){
           lista.add(rand.nextInt(10000));
       }

        System.out.println("Lista desordenada: " + lista);

       //Merge sort in java using streamParallel and lambda expressions
        List<Integer> sorted = MergeSort(lista);
        System.out.println("\n\n\n\n"+"Lista ordenada: " + sorted);

        int target = sorted.get(new Random().nextInt(sorted.size()));

        System.out.println("\n\n\n Procurando por: " + target);

        int index = Bsearch(sorted , target);

        if(index == -1){
            System.out.println("Elemento não encontrado");
        }
        else{
            System.out.println("Elemento encontrado na posição: " + index);
        }



    }
}
