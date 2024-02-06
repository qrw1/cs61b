package deque;
import java.util.Iterator;


public class LinkedListDeque<T>  implements Deque<T>, Iterable<T> {

    private class Node {
        private Node prev;
        private T item;
        private Node next;

        private Node(T i, Node n) {
            item = i;
            next = n;
        }
    }
    private Node sentinel;
    private int size;

    public LinkedListDeque() {
        sentinel = new Node(null,null);
        sentinel.next = sentinel;
        sentinel.prev = sentinel;
        size = 0;
    }

    public void addFirst(T item){
        Node firstNode = sentinel.next;
        firstNode.prev = new Node(item, firstNode);
        sentinel.next = firstNode.prev;
        firstNode.prev.prev = sentinel;
        size = size + 1;
    }

    public void addLast(T item){
        Node lastNode = sentinel.prev;
        lastNode.next = new Node(item, sentinel);
        sentinel.prev = lastNode.next;
        lastNode.next.prev = lastNode;
        size += 1;
    }


    public int size(){
        return size;
    }


    public void printDeque() {
        for (int i = 0; i < size; i += 1) {
            System.out.print(get(i) + " ");
        }
    }



    public T removeFirst() {
        if (isEmpty()) {
            return null;
        }
        T x = sentinel.next.item;
        sentinel.next = sentinel.next.next;
        sentinel.next.prev = sentinel;
        size -= 1;
        return x;
    }

    public T removeLast() {
        if (isEmpty()) {
            return null;
        }

        T x = sentinel.prev.item;
        if (sentinel.prev.prev != null) {
            sentinel.prev.prev.next = sentinel;
        } else {
            sentinel.next = sentinel;
        }

        sentinel.prev = sentinel.prev.prev;

        size -= 1;
        return x;
    }

    public T get(int index){
        Node X = sentinel.next;
        if(index > size - 1 || index < 0){
            return null;
        }
        for (int i = 0; i <= index; i += 1) {
            X = X.next;
        }
        return X.item;
    }


    public Iterator<T> iterator(){
        return new LinkedListIterator();
    }

    private class LinkedListIterator implements Iterator<T> {
        private int wizPos;
        public  LinkedListIterator(){
            wizPos = 0;
        }

        public boolean hasNext(){
            return wizPos < size;
        }

        public T next(){
            T returnItem = get(wizPos);
            wizPos += 1;
            return returnItem;
        }
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null) {
            return false;
        }
        if (!(o instanceof Deque)) {
            return false;
        }
        Deque<T> ol = (Deque<T>) o;
        if (ol.size() != this.size()) {
            return false;
        }
        for (int i = 0; i < size; i++) {
            if (!(ol.get(i).equals(this.get(i)))) {
                return false;
            }
        }
        return true;
    }

    private T getRecursive(int index) {
        Node x = sentinel.next;
        if (index < 0 || index >= size) {
            return null;
        }
        if (index == 0) {
            return x.item;
        }
        x = x.next;
        return getRecursive(index - 1);
    }

    private static void main(String[] args) {
        int n = 99;

        LinkedListDeque<Integer> lld1 = new LinkedListDeque<>();
        for (int i = 0; i <= n; i++) {
            lld1.addLast(i);
        }

        LinkedListDeque<Integer> lld2 = new LinkedListDeque<>();
        for (int i = n; i >= 0; i--) {
            lld2.addFirst(i);
        }

        lld1.printDeque();

        System.out.println(lld1.equals(lld2));

        ArrayDeque<Integer> ad1 = new ArrayDeque<>();
        for (int i = 0; i <= n; i++) {
            ad1.addLast(i);
        }

        System.out.println(lld1.equals(ad1));
    }

}
