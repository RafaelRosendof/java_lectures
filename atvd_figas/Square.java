/*
 Crie uma interface funcional chamada Square, cujo método abstrato calculate recebe um número inteiro n
  e retorna um outro número inteiro, que corresponde a n².

Na classe Main, crie um método estático que recebe como parâmetros uma implementação de Square e
 um número inteiro, e imprime o resultado da operação calculate utilizando a implementação recebida.

Na função main, crie três implementações diferentes para a interface funcional Square utilizando 
expressões lambda: Uma que utilize a classe Math, uma que utilize apenas o operador de multiplicação 
(*), e uma que utilize um laço e o operador soma (+). Em seguida, chame o método estático definido 
anteriormente para cada uma das três implementações.
 */
@FunctionalInterface
public interface Square{

     double calculate (double x);
}