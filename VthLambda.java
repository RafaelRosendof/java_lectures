

public class VthLambda{

    static Integer lambda(int x) throws InterruptedException {

        int x2 = x * x;
        System.out.println("Num " + x + " thread: " + Thread.currentThread());
        Thread.sleep(1);
        return x2;
    }

}