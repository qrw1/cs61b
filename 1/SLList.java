public class SLList {
    private static class IntNode {//少 time
        public int item;
        public IntNode next;

        public IntNode(int i,IntNode n ){
            item = i;
            next = n;
        }
    }
    private IntNode sentinel;
    private int size;

    public SLList(){
        sentinel = new IntNode(63,null);
        size = 0;
    }

    public SLList(int x) {
        sentinel = new IntNode(63,null);
        sentinel.next = new IntNode(x,null);
        size = 1;
    }
    public static void main(String[] args){
        SLList L = new SLList(10);
    }

    public void addFirst(int x){
        sentinel.next = new IntNode(x,sentinel.next);
         size += 1;
    }

    public void addlast(int x){

        IntNode p = sentinel;
        size += 1;
        while (p.next != null){
            p = p.next;
        }
        p.next = new IntNode(x,null);
    }

//    private  static  int Size(IntNode p){
//        if (p.next == null){
//            return 1;
//        }
//        return 1 + Size(p.next);
//    }
    public int size(){
        return size;
    }

    public int getFirst(){
        return sentinel.next.item;
    }
}
