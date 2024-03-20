package hashmap;

import java.util.*;

/**
 *  A hash table-backed Map implementation. Provides amortized constant time
 *  access to elements via get(), remove(), and put() in the best case.
 *
 *  Assumes null keys will never be inserted, and does not resize down upon remove().
 *  @author YOUR NAME HERE
 */
public class MyHashMap<K, V> implements Map61B<K, V> {

    /**
     * Protected helper class to store key/value pairs
     * The protected qualifier allows subclass access
     */

    protected class Node {
        K key;
        V value;

        Node(K k, V v) {
            key = k;
            value = v;
        }
    }

    private static final int DEFAULT_INITIAL_SIZE = 16;
    private static final double DEFAULT_MAX_LOAD_FACTOR = 0.75;
    private final double maxLoadFactor;
    private Collection<Node>[] buckets;
    private int size = 0;


    public MyHashMap() {
        this(DEFAULT_INITIAL_SIZE, DEFAULT_MAX_LOAD_FACTOR);
    }

    public MyHashMap(int initialSize) {
        this(initialSize, DEFAULT_MAX_LOAD_FACTOR);
    }

    /**
     * MyHashMap constructor that creates a backing array of initialSize.
     * The load factor (# items / # buckets) should always be <= loadFactor
     *
     * @param initialSize initial size of backing array
     * @param maxLoad maximum load factor
     */
    public MyHashMap(int initialSize, double maxLoad) {
        buckets = createTable(initialSize);
        maxLoadFactor = maxLoad;
    }

    private Node createNode(K key, V value) {
        return new Node(key,value);
    }

    /**
     * Returns a data structure to be a hash table bucket
     *
     * The only requirements of a hash table bucket are that we can:
     *  1. Insert items (`add` method)
     *  2. Remove items (`remove` method)
     *  3. Iterate through items (`iterator` method)
     *
     * Each of these methods is supported by java.util.Collection,
     * Most data structures in Java inherit from Collection, so we
     * can use almost any data structure as our buckets.
     *
     * Override this method to use different data structures as
     * the underlying bucket type
     *
     * BE SURE TO CALL THIS FACTORY METHOD INSTEAD OF CREATING YOUR
     * OWN BUCKET DATA STRUCTURES WITH THE NEW OPERATOR!
     */
    protected Collection<Node> createBucket() {
        return new LinkedList<>();
    }

    /**
     * Returns a table to back our hash table. As per the comment
     * above, this table can be an array of Collection objects
     *
     * BE SURE TO CALL THIS FACTORY METHOD WHEN CREATING A TABLE SO
     * THAT ALL BUCKET TYPES ARE OF JAVA.UTIL.COLLECTION
     *
     * @param tableSize the size of the table to create
     */
    @SuppressWarnings("unchecked")
    private Collection<Node>[] createTable(int tableSize) {
        Collection<Node>[] table = new Collection[tableSize];
        for (int i = 0; i < tableSize; i++) {
            table[i] = createBucket();
        }
        return table;
    }

    // TODO: Implement the methods of the Map61B Interface below
    // Your code won't compile until you do so!

    @Override
    public void clear() {
        buckets = createTable(DEFAULT_INITIAL_SIZE);
        size = 0;
    }
    @Override
    public int size(){
        return size;
    }

    @Override
    public boolean containsKey(K key) {
        return getNode(key) != null;
    }

    @Override
    public V get(K key) {
        Node node = getNode(key);
        if (node == null) {
            return null;
        }
        return node.value;

    }

    private Node getNode(K key) {
        int bucketIndex = getIndex(key);
        return getNode(key, bucketIndex);
    }

    private int getIndex(K key){
        return getIndex(key, buckets);
    }


    private int getIndex(K key, Collection<Node>[] table) {
        int keyHashcode = key.hashCode();
        return Math.floorMod(keyHashcode, table.length);
    }

    private Node getNode(K key, int bucketIndex) {
        for (Node node : buckets[bucketIndex]) {
            if(node.key.equals(key)) {
                return node;
            }
        }
        return null;
    }

    @Override
    public V remove(K key) {
        int index = getIndex(key);
        Node node = getNode(key, index);
        if (node == null){
            return null;
        }
        size -= 1;
        buckets[index].remove(node);
        return node.value;
    }

    @Override
    public V remove(K key, V value) {
        int index = getIndex(key);
        Node node = getNode(key, index);
        if (node == null || !node.value.equals(value)){
            return null;
        }
        size -= 1;
        buckets[index].remove(node);
        return value;
    }

    @Override
    public Set<K> keySet() {
       HashSet<K> a = new HashSet<>();
       for(K key : this){
           a.add(key);
       }
       return a;
    }

    @Override
    public void put(K key, V value) {
        int bucketIndex = getIndex(key);
        Node node = getNode(key, bucketIndex);
        if(node != null){
            node.value = value;
            return;
        }
        node = createNode(key,value);
        buckets[bucketIndex].add(node);
        size += 1;
        if (hasReachedMaxLoad()) {
            resize(buckets.length * 2);
        }
    }


    private boolean hasReachedMaxLoad() {
        return (double) (size / buckets.length) > maxLoadFactor;
    }

    private void resize(int length){
        Collection<Node>[] newbucket = createTable(length);
        Iterator<Node> nodeIterator = new MyHashMapNodeIterator();
        while(nodeIterator.hasNext()){
            Node node = nodeIterator.next();
            int n = getIndex(node.key, newbucket);
            newbucket[n].add(node);
        }
        buckets = newbucket;
    }


    @Override
    public Iterator<K> iterator() {
        return new MyHashMapIterator();
    }

    private class MyHashMapIterator implements Iterator<K> {

        private final Iterator<Node> nodeIterator = new MyHashMapNodeIterator();

        public boolean hasNext() {
            return nodeIterator.hasNext();
        }

        public K next() {
            return nodeIterator.next().key;
        }
    }

    private class MyHashMapNodeIterator implements Iterator<Node>{
        private final Iterator<Collection<Node>> bucketsIterator = Arrays.stream(buckets).iterator();
        private Iterator<Node> currentBucketIterator;
        private int nodesLeft = size;

        public boolean hasNext(){
            return nodesLeft > 0;
        }

        public Node next(){
            if(currentBucketIterator == null || !currentBucketIterator.hasNext()){
                Collection<Node> currentBucket = bucketsIterator.next();
                while(currentBucket.size() == 0){
                    currentBucket = bucketsIterator.next();
                }
                currentBucketIterator = currentBucket.iterator();
            }
            nodesLeft -= 1;
            return currentBucketIterator.next();
        }
    }
}
