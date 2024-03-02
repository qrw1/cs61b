import java.util.ArrayList;
import java.util.List;
public class Dup2 {
    public static boolean dup2(int[] A) {
        for (int i = 0; i < A.length - 1; i += 1) {
            if (A[i] == A[i + 1]) {
                return true;
            }
        }
        return false;
    }

    public static int[] makeArray(int N){
        int[] A = new int[N];
        for (int i = 0; i < N; i += 1){
            A[i] = i;
        }
        return A;
    }

    public static void main(String[] args){
        int N = Integer.parseInt(args[0]);
        int[] A = makeArray(N);
        dup2(A);
    }

}
