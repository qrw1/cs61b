package bstmap;

import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

public class BSTMap<K extends Comparable<K>, V> implements Map61B<K, V> {
    private Node root;
    private int size = 0;

    private class Node{
        private K key;
        private V value;
        private Node left;
        private Node right;

        public Node(K k, V v){
            key = k;
            value = v;
        }

    }


    @Override
    public void clear(){
        root = null;
        size = 0;
    }

    @Override
    public boolean containsKey(K key){
        if(sch(root, key) != null){
            return true;
        }
        return false;
    }


    @Override
    public int size(){
        return size;
    }

    private Node sch(Node node, K key){
        if(node == null){
            return null;
        }
        if(node.key.equals(key)){
            return node;
        }
        int cmp = key.compareTo(node.key);
        if (cmp < 0) {
            return sch(node.left, key);
        } else if (cmp > 0) {
            return sch(node.right, key);
        }
        return null;
    }


    @Override
    public V get(K key){
        Node n = sch(root,key);
        if(n == null){
            return null;
        }
        return n.value;
    }

    @Override
    public void put(K key, V value) {
        root = put(root, key, value);
        size += 1;
    }

    private Node put(Node node, K key, V value){
        if(node == null) {
            return new Node(key, value);
        }else{
            int n = node.key.compareTo(key);
            if(n > 0){
                node.left = put(node.left, key ,value);
            } else if (n < 0) {
                node.right = put(node.right, key, value);
            }else{
                node.value = value;
            }
        }
        return node;
    }
    @Override
    public Iterator<K> iterator() {
        return keySet().iterator();
    }


    @Override
    public Set<K> keySet(){
        Set<K> keySet = new HashSet<>();
        keySetHelper(root, keySet);
        return keySet;
    }


    private void keySetHelper(Node o, Set<K> keySet) {
        if (o != null) {
            keySet.add(o.key);
            keySetHelper(o.left, keySet);
            keySetHelper(o.right, keySet);
        }
    }

    @Override
    public V remove(K key) {
        if(containsKey(key)){
            V ikey = get(key);
            root = remove(root, key);
            size -= 1;
            return ikey;
        }
        return null;
    }

    private Node remove(Node node, K key){
        if(node == null) {
            return null;
        }else{
            int n = node.key.compareTo(key);
            if(n > 0){
                node.left = remove(node.left, key);//将需要变化的一边进行迭代替换。
            } else if (n < 0) {
                node.right = remove(node.right, key);
            }else{
                if(node.right == null){
                    return node.left;
                }
                if(node.left == null){
                    return node.right;
                }
                Node originalNode = node;
                node = getMinChild(node.right);
                node.left = originalNode.left;
                node.right = remove(originalNode.right, node.key);
            }
        }
        return node;
    }

    private Node getMinChild(Node node){
        if (node.left == null) {
            return node;
        }
        return getMinChild(node.left);
    }


    @Override
    public V remove(K key, V value) {
        V ikey = get(key);
        if(containsKey(key) && ikey.equals(value)){
            root = remove(root, key);
            size -= 1;
            return ikey;
        }
        return null;
    }




}
