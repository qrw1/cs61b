package randomizedtest;

import edu.princeton.cs.algs4.In;
import edu.princeton.cs.algs4.StdRandom;
import org.junit.Test;
import timingtest.AList;

import static org.junit.Assert.*;

/**
 * Created by hug.
 */
public class TestBuggyAList {
  // YOUR TESTS HERE
    @Test
    public void testThreeAddThreeRemove(){
        BuggyAList<Integer> BA = new BuggyAList<>();
        AListNoResizing<Integer> ANR = new AListNoResizing<>();

        for (int i = 4; i < 7; i += 1) {
            BA.addLast(i);
            ANR.addLast(i);
        }

        for(int i = 0; i <3 ; i++){
            assertEquals(BA.removeLast(),ANR.removeLast());
        }
    }
    @Test
    public void randomizedTest(){
        AListNoResizing<Integer> L = new AListNoResizing<>();
        BuggyAList<Integer> B = new BuggyAList<>();

        int N = 500;
        for (int i = 0; i < N; i += 1) {
            int operationNumber = StdRandom.uniform(0, 3);
            if (operationNumber == 0) {
                int randVal = StdRandom.uniform(0, 100);
                L.addLast(randVal);
                B.addLast(randVal);
                System.out.println("addLast(" + randVal + ")");
            } else if (operationNumber == 1) {
                int size = L.size();
                int size2 = B.size();
                System.out.println("size: " + size + " size2: " + size2);
                assertEquals(size, size2);
            } else if (operationNumber == 2) {
                if (L.size() > 0 && B.size() > 0){
                    int ret = B.getLast();
                    int ret2 = L.getLast();
                    System.out.println("getLast(" + ret + ")" + " getLast(" + ret2 + ")");
                    assertEquals(ret, ret2);

                }
            }else {
                if (L.size() > 0 && B.size() > 0){
                    int ret = B.removeLast();
                    int ret2 = L.removeLast();
                    System.out.println("removeLast(" + ret + ")" + " removeLast(" + ret2 + ")");
                    assertEquals(ret, ret2);
                }
            }
        }
    }
}
