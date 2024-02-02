package deque;


import java.util.Iterator;

public class ArrayDeque<T> implements Deque<T>, Iterable<T> {
    private T [] items;
    private  int size;
    private int nextfirst;
    private int nextlast;


    public ArrayDeque(){
        items = (T[]) new Object[8];
        size = 0;
        nextfirst = 4;
        nextlast = 5;
    }

    private void resize(int capacity){
        T[] a = (T[]) new Object[capacity];
        int ind = 0;
        for (int i = 0; i < size; i += 1) {
            ind = arrayInd(i);
            a[capacity / 4 + i] = items[ind];
        }
        items = a;
        nextfirst = capacity / 4 - 1;
        nextlast = nextfirst + size + 1;
    }

    private int arrayInd(int ind) {
        if (nextfirst + 1 + ind >= items.length) {
            return nextfirst + 1 + ind - items.length;
        } else {
            return nextfirst + 1 + ind;
        }
    }


    public void addLast(T x){
        if(items.length == size){
            resize(size * 2);
        }
        items[nextlast] = x;
        size += 1;
        if(nextlast == items.length - 1){
            nextlast = 0;
        }
        else{nextlast += 1;}

    }

    public void addFirst(T x){
        if(items.length == size){
            resize(size * 2);
        }
        items[nextfirst] = x;
        size += 1;
        if(nextfirst == 0){
            nextfirst = items.length - 1;
        }else{
        nextfirst -= 1;}
    }

    public T getFirst() {
        int ind = arrayInd(0);
        return items[ind];
    }

    public T getLast() {
        int ind = arrayInd(size - 1);
        return items[ind];
    }

    public T get(int i) {
        int ind =  arrayInd(i);
        return items[ind];
    }


    public int size(){
        return size;
    }


    public T removeLast(){
        if(isEmpty()){
            return null;
        }

        if ((size < items.length / 4) && (size > 8)) {
            resize(items.length / 2);
        }

        T L = getLast();
        int ind = arrayInd(size - 1);
        items[ind] = null;
        size = size - 1;
        nextlast = ind;
        return L;

    }
    public T removeFirst(){
        if(isEmpty()){
            return null;
        }

        if ((size < items.length / 4) && (size > 8)) {
            resize(items.length / 2);
        }

        T L = getFirst();
        int ind = arrayInd(0);
        items[ind] = null;
        size = size - 1;
        nextfirst = ind;
        return L;

    }

    public void printDeque() {
        for (T i : this) {
            System.out.print(i + " ");
        }
    }

    public Iterator<T> iterator() {
        return new ArrayDequeIterator();
    }


    private class ArrayDequeIterator implements Iterator<T> {
        private int wizPos;

        private ArrayDequeIterator() {
            wizPos = 0;
        }

        public boolean hasNext() {
            return wizPos < size;
        }

        public T next() {
            T item = get(wizPos);
            wizPos += 1;
            return item;
        }
    }

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
        Deque<T> oa = (Deque<T>) o;
        if (oa.size() != this.size()) {
            return false;
        }
        for (int i = 0; i < size; i += 1) {
            if (!(oa.get(i).equals(this.get(i)))) {
                return false;
            }
        }
        return true;
    }

    private void printArray() {
        for (int i = 0; i < items.length; i += 1) {
            System.out.print(items[i] + " ");
        }
    }

    private static void main(String[] args) {
        int n = 99;

        ArrayDeque<Integer> ad1 = new ArrayDeque<>();
        for (int i = 0; i <= n; i++) {
            ad1.addLast(i);
        }

        ArrayDeque<Integer> ad2 = new ArrayDeque<>();
        for (int i = n; i >= 0; i--) {
            ad2.addFirst(i);
        }

        ad1.printDeque();

        System.out.println(ad1.equals(ad2));
    }
}
