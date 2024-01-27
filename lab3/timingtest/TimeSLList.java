package timingtest;
import edu.princeton.cs.algs4.Stopwatch;

/**
 * Created by hug.
 */
public class TimeSLList {
    private static void printTimingTable(AList<Integer> Ns, AList<Double> times, AList<Integer> opCounts) {
        System.out.printf("%12s %12s %12s %12s\n", "N", "time (s)", "# ops", "microsec/op");
        System.out.printf("------------------------------------------------------------\n");
        for (int i = 0; i < Ns.size(); i += 1) {
            int N = Ns.get(i);
            double time = times.get(i);
            int opCount = opCounts.get(i);
            double timePerOp = time / opCount * 1e6;
            System.out.printf("%12d %12.2f %12d %12.2f\n", N, time, opCount, timePerOp);
        }
    }

    public static void main(String[] args) {
        timeGetLast();
    }

    public static void timeGetLast() {
        // TODO: YOUR CODE HERE
        System.out.println("Timing table for getLast");
        SLList<Integer> N = new SLList<>();
        AList<Integer> Ns = new AList<>();
        AList<Double> time = new AList<>();
        AList<Integer> ops = new AList<>();

        int ops1 = 10000;
        int n = 0;
        Stopwatch sw = new Stopwatch();

        for(int i = 1;i <= 128000; i++){
            N.addLast(i);
            if(N.size() == 1000 * Math.pow(2,n)){
                for (int j = 0; j < ops1; j += 1){
                    N.getLast();
                }
                Ns.addLast(N.size());
                time.addLast(sw.elapsedTime());
                ops.addLast(ops1);
                n += 1;
            }
        }
        printTimingTable(Ns,time,ops);

    }

}
