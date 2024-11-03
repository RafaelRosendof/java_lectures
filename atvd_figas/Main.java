public class Main{
    public static void main(String[] args){
        Square feito = n -> (double) n*n;
        printa(feito, 25.25);

        Square matematica = n -> Math.pow(n,n);
        printa(matematica, 32.12);

        Square forro = n -> {
            int res = 0;
            for(int i =0; i<n ; i++){
                res +=n;
            }
            return res;
        };
        printa(forro , 45.87);

    }
    public static void printa(Square square , double x){
        double res = square.calculate(x);

        System.out.println("Res = " + res);
    }
}