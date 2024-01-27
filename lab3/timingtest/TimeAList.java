package timingtest;
import edu.princeton.cs.algs4.Stopwatch;

/**
 * Created by hug.
 */
public class TimeAList {
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
        timeAListConstruction();
    }

    public static void timeAListConstruction() {
        // TODO: YOUR CODE HERE
        System.out.println("Timing table for addLast");
        AList<Integer> N = new AList<Integer>();
        AList<Integer> Ns = new AList<Integer>();
        AList<Double> time = new AList<Double>();
        AList<Integer> ops = new AList<Integer>();

        int ops1 = 0;
        int n = 0;
        Stopwatch sw = new Stopwatch();

        for(int i = 1;i <= 128000; i++){
            N.addLast(1);
            ops1 += 1;

            if(i == 1000 * Math.pow(2,n)){
                Ns.addLast(i);
                time.addLast(sw.elapsedTime());
                ops.addLast(ops1);
                n += 1;

            }
        }
        printTimingTable(Ns,time,ops);
    }
}
