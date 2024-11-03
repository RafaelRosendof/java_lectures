
package com.Figas;


import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.assertFalse;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;


public class StreamTest{
    List<Integer> num = Arrays.asList(1, 2, 3, 4, 5 , 6, 7, 8, 9, 10);
    
    @Test
    void testPares(){
        List<Integer> pares = num.stream().filter(i -> i %2 == 0).collect(Collectors.toList());
        assertEquals(Arrays.asList(2, 4, 6, 8, 10), pares);
    }

    @Test
    void testQuads(){
        List<Integer> quads = num.stream().filter(i -> i % 2 == 0).map(i -> i * i).collect(Collectors.toList());
        assertEquals(Arrays.asList(4, 16, 36, 64, 100), quads);
    }

    @Test
    void testSum(){
        int sum = num.stream().filter(i -> i % 2 == 0).reduce(0 , Integer::sum);
        assertEquals(200, sum);
    }

    @Test
    void testMapReduce(){
        int mapReduce = num.stream().map(i -> i * i + 1).reduce(0, Integer::sum);
        assertEquals(386, mapReduce);
    }

    @Test
    void testAnyMatch(){
        assertTrue(num.stream().anyMatch(i -> i % 2 == 0));
    }

    @Test
    void testAllMatch(){
        assertFalse(num.stream().allMatch(i -> i % 2 == 0));
    }

}