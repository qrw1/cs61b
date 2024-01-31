public class HoFDome {
    public static int do_twice(IntUnaryFunction f,int x){
        return f.apply(f.apply(x));
    }


}
