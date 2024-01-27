public class SLList<LochNess> {
    private class StuffNode {//少 time
        public LochNess item;
        public StuffNode next;

        public StuffNode(LochNess i,StuffNode n ){
            item = i;
            next = n;
        }
    }
    private StuffNode sentinel;
    private int size;

//    public SLList(){
//        sentinel = new StuffNode(63,null);
//        size = 0;
//    }

    public SLList(LochNess x) {
//        sentinel = new StuffNode(63,null);
        sentinel.next = new StuffNode(x,null);
        size = 1;
    }
    public static void main(String[] args){
        SLList L = new SLList(10);
    }

    public void addFirst(LochNess x){
        sentinel.next = new StuffNode(x,sentinel.next);
         size += 1;
    }

    public void addlast(LochNess x){

        StuffNode p = sentinel;
        size += 1;
        while (p.next != null){
            p = p.next;
        }
        p.next = new StuffNode(x,null);
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

    public LochNess getFirst(){
        return sentinel.next.item;
    }


}


