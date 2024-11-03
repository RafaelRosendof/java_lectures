/*
 * 
 * Depois de criar a classe App, crie a classe AppTest
 */
package com.Figas;


import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.assertFalse;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;


public class AppTest{
    List<Integer> num = Arrays.asList(1, 2, 3, 4, 5 , 6, 7, 8, 9, 10);
    
    @Test
    public void testPares(){
        List<Integer> pares = num.stream().filter(i -> i %2 == 0).collect(Collectors.toList());
        assertEquals(Arrays.asList(2, 4, 6, 8, 10), pares);
    }

    @Test
    public void testQuads(){
        List<Integer> quads = num.stream().filter(i -> i % 2 == 0).map(i -> i * i).collect(Collectors.toList());
        assertEquals(Arrays.asList(4, 16, 36, 64, 100), quads);
    }

    @Test
    public void testSum(){
        int sum = num.stream().filter(i -> i % 2 == 0).reduce(0 , Integer::sum);
        assertEquals(30, sum);
    }

    @Test
    public void testMapReduce(){
        int mapReduce = num.stream().map(i -> i * i + 1).reduce(0, Integer::sum);
        assertEquals(395, mapReduce);
    }

    @Test
    public void testAnyMatch(){
        assertTrue(num.stream().anyMatch(i -> i % 2 == 0));
    }

    @Test
    public void testAllMatch(){
        assertFalse(num.stream().allMatch(i -> i % 2 == 0));
    }

}

/*
 * Para rodar outros testes colocar mvn test -Dtest=AppTest#nome_do_metodo
 */