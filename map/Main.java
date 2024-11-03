/*
 * Crie um programa que conta a frequência de palavras 
 * únicas em uma frase.
Utilize um Map para armazenar a contagem de cada palavra 
(a chave é a palavra e o valor, o número de ocorrências).
Utilize expressões lambda para tornar o código mais conciso.
Dica: Use o seguinte comando para converter uma String que
 representa uma 
frase em um array de palavras e use esse 
array de palavras para preencher o mapa enquanto faz a contagem:
 */

import java.util.HashMap;
import java.util.Map;


public class Main{

    public static void main(String[] args){
        String frase = "Fala meu, vou alí para a praia, valeu aí!!!";
        Map<String , Integer> freqPalavra = contador(frase);
        //printando 
        System.out.println(freqPalavra);
    }
    public static Map<String , Integer> contador(String frase){
        Map<String, Integer> freqPalavra = new HashMap<>();

        String[] palavras = frase.replaceAll("[^a-zA-Z ]", "").toLowerCase().split("\\s+");

         //String[] palavras = frase.replaceAll("[^a-zA-Z ]", "").toLowerCase().split("\\s+");

        //agora vou iterar 
        for(String i : palavras){
            freqPalavra.merge(i , 1 , Integer::sum); 
        }
        return freqPalavra;
    }

}