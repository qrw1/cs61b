package deque;
import java.util.Iterator;


public class LinkedListDeque<T> implements Deque<T>, Iterable<T> {

    public int size;

    private Node sentinel;

    private class Node {//少 time
        public T item;
        public Node next;
        public Node prev;

        public Node(T i, Node n) {
            item = i;
            next = n;
        }
    }


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
        Node X = sentinel.next;
        StringBuilder stringsb = new StringBuilder();
        for (int i = 0; i < size; i += 1) {
            if (X.item != null) {
                stringsb.append(X.item);
                stringsb.append(" ");
            }
            X = X.next;
        }
        System.out.println(stringsb);
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
        LinkedListDeque<Iterator> L = new LinkedListDeque<>();
    }

}
