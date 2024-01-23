public class IntList {
    public int first;
    public IntList rest;

    public IntList(int f, IntList r) {
        first = f;
        rest = r;
    }

//    public int size() {
//        if (rest == null) {
//            return 1;
//        }
//        return 1 + this.rest.size();
//    }


//    public int get(int x) {
//        IntList p = this;
//        int b = 0;
//        for (int a = 0; a <= x; a++) {
//            b = p.first;
//            p = p.rest;
//        }
//        return b;
//    }
//    public int get(int i){
//        if (i == 0){
//            return first;
//        }
//        return rest.get(i-1);
//    }
//    //简洁的思路，吐了，我就不行，我的过于臃肿
//
//
//
//    public int iterativeSize(){
//        IntList x = this;
//        int totalSize = 0;
//        while (x != null){
//            totalSize += 1;
//            x = x.rest;
//        }
//        return totalSize;
//    }
//
//
//    public static IntList incrList(IntList L, int x) {
//
//        return L;
//    }


    public static IntList dincrList(IntList L, int x) {
        L.first += x;
        if (L.rest != null) {
            dincrList(L.rest, x);
        }
        return L;
    }

    public static void main(String[] args) {
        IntList L = new IntList(5, null);
        L.rest = new IntList(7, null);
        L.rest.rest = new IntList(9, null);

//        System.out.println(L.size());
//        System.out.println(L.iterativeSize());

        // Test your answers by uncommenting. Or copy and paste the
        // code for incrList and dincrList into IntList.java and
        // run it in the visualizer.
        // System.out.println(L.get(1));
//       System.out.println(incrList(L, 3));
         System.out.println(dincrList(L, 3));
    }


//    public static void main(String[] args) {
//        IntList L = new IntList(15, null);
//        L = new IntList(10, L);
//        L = new IntList(5, L);
//
//    System.out.println(L.get(4));
//    }

}
