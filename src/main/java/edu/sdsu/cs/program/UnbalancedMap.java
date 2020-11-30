package edu.sdsu.cs.program;

import java.util.LinkedList;
import java.util.Stack;

// class to be implemented with low level java code
public class UnbalancedMap<K extends Comparable<K>, V> implements IMap<K, V> {
    private static final int DEFAULT_TEST_SIZE = 100000;

    Node<K, V> root;
    int count = 0;

    private class Node<K, V> {
        K key;
        V value;
        Node<K, V> left, right;

        public Node(K key, V value) {
            this.key = key;
            this.value = value;
        }

        boolean contains(K k) {
            if (((Comparable<K>) this.key).compareTo(k) == 0)
                return true;
            else if (left != null && ((Comparable<K>) this.key).compareTo(k) > 0)
                return left.contains(k);
            else if (right != null)
                return right.contains(k);
            return false;
        }

        V getValue(K k) {
            if (((Comparable<K>) this.key).compareTo(k) == 0) {
                return this.value;
            } else if (((Comparable<K>) this.key).compareTo(k) > 0) {
                if (left == null)
                    return null;
                return left.getValue(k);
            } else {
                if (right == null)
                    return null;
                return right.getValue(k);
            }
        }

        K getKey(V v) {
            if (this.value.equals(v))
                return this.key;

            K res = null;
            if (left != null)
                res = left.getKey(v);

            if (res != null)
                return res;

            if (right != null)
                res = right.getKey(v);

            return res;
        }
    }

    public UnbalancedMap() {
        super();
        root = null;
    }

    public UnbalancedMap(IMap<K, V> map) {
        super();
        if (map != null) {
            Iterable<K> keys = map.keyset();
            for (K k : keys)
                this.add(k, map.getValue(k));
        }
    }

    @Override
    public boolean contains(K key) {
        return root == null ? false : root.contains(key);
    }

    @Override
    public boolean add(K key, V value) {

        if (root == null) {
            root = new Node<K, V>(key, value);
            count++;
            return true;
        }

        Node<K, V> parent = root;
        while (((Comparable<K>) key).compareTo(parent.key) != 0) {
            if (((Comparable<K>) key).compareTo(parent.key) > 0) {
                if (parent.right == null) {
                    parent.right = new Node<K, V>(key, value);
                    count++;
                    return true;
                }
                parent = parent.right;
            } else {
                if (parent.left == null) {
                    parent.left = new Node<K, V>(key, value);
                    count++;
                    return true;
                }
                parent = parent.left;
            }
        }
        return false;
    }

//    @Override
//    public V delete(K key) {
//        Node<K,V> current = root;
//        Node<K,V> previous = null;
//
//        while (current != null && current.key.compareTo(key) != 0) {
//            previous = current;
//
//            if (current.key.compareTo(key) > 0)
//                current = current.left;
//            else
//                current = current.right;
//        }
//
//        // key not found
//        if (current == null)
//            return null;
//
//        if (current.right == null || current.left == null) {
//            Node<K,V> newNode;
//
//            if (current.right == null)
//                newNode = current.left;
//            else
//                newNode = current.right;
//
//            if (previous == null)
//                return newNode.value;
//
//            if (current == previous.right)
//                previous.right = newNode;
//            else
//                previous.left = newNode;
//        } else {
//            Node<K,V> prev = null;
//            Node<K,V> temp = current.right;
//
//            while (temp.left != null) {
//                prev = temp;
//                temp = temp.left;
//            }
//
//            if (prev != null)
//                prev.left = temp.right;
//            else
//                current.right = temp.right;
//        }
//        count--;
//        return current.value;
//    }

    @Override
    public V delete(K key) {
        // TODO: Implement
        return null;
    }

    @Override
    public V getValue(K key) {
        return root == null ? null : root.getValue(key);
    }

    @Override
    public K getKey(V value) {
        return root == null ? null : root.getKey(value);
    }

    @Override
    public Iterable<K> getKeys(V value) {
        LinkedList<K> list = new LinkedList<>();
        getKeys(root, list, value);
        return list;
    }

    @Override
    public int size() {
        return this.count;
    }

    @Override
    public boolean isEmpty() {
        return this.size() == 0;
    }

    @Override
    public void clear() {
        this.root = null;
        this.count = 0;
    }

    @Override
    public Iterable<K> keyset() {
        LinkedList<K> list = new LinkedList<>();
        traverseKeys(root, list);
        return list;
    }

    private void getKeys(Node<K, V> node, LinkedList<K> list, V value) {
        if (node != null) {
            getKeys(node.left, list, value);
            if (node.value.equals(value))
                list.add(node.key);
            getKeys(node.right, list, value);
        }
    }

    private void traverseValues(Node<K, V> node, LinkedList<V> list) {
        if (node == null) {
            return;
        }
        Stack<Node> stack = new Stack<>();
        stack.push(node);

        while (!stack.empty()) {
            node = stack.pop();
            list.add(root.value);
            if (node.left != null)
                stack.push(node.left);
            if (node.right != null)
                stack.push(node.right);
        }
    }

    private void traverseKeys(Node<K, V> node, LinkedList<K> list) {
        if (node == null) {
            return;
        }
        Stack<Node> stack = new Stack<>();
        stack.push(node);

        while (!stack.empty()) {
            node = stack.pop();
            list.add(root.key);
            if (node.left != null)
                stack.push(node.left);
            if (node.right != null)
                stack.push(node.right);
        }
    }

    @Override
    public Iterable<V> values() {
        LinkedList<V> list = new LinkedList<>();
        traverseValues(root, list);
        return list;
    }
}
