package edu.sdsu.cs.program;

import java.util.LinkedList;
import java.util.Stack;

// class to be implemented with low level java code
public class UnbalancedMap<K extends Comparable<K>, V> implements IMap<K, V> {
    private static final int DEFAULT_TEST_SIZE = 100000;

    Node<K, V> root;
    int count = 0;

    private class Node<K extends Comparable<K>, V> {
        K key;
        V value;
        Node<K, V> left, right;

        public Node(K key, V value) {
            this.key = key;
            this.value = value;
        }

        public Node<K, V> getParentNode(Node<K, V> root) {
            Node<K, V> parent = root;
            int direction = 0;

            while (parent.left != this && parent.right != this) {
                direction = this.key.compareTo(parent.key);
                if (direction < 0) {
                    parent = parent.left;
                } else if (direction > 0) {
                    parent = parent.right;
                }
            }
            return parent;
        }

        public Node<K, V> belowByKey(K key) {
            if (isLeftChilded() && left.key.compareTo(key) == 0)
                return left;
            else if (isRightChilded() && right.key.compareTo(key) == 0)
                return right;
            else
                return null;
        }

        private Node<K, V> smallest() {
            Node<K, V> smallest = this;
            while (smallest.isLeftChilded()) {
                smallest = smallest.left;
            }
            return smallest;
        }

        public Node<K, V> pred_in_order() {
            Node<K, V> inorder = this;
            if (inorder.isRightChilded()) {
                return inorder.right.smallest();
            }
            return null;
        }

        public boolean isLeftChilded() {
            return left != null;
        }

        public boolean isRightChilded() {
            return right != null;
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

    @Override
    public V delete(K key) {
        if (!contains(key))
            return null;

        if (singleRoot(key)) {
            V v = root.value;
            root = null;
            count--;
            return v;
        }

        if (rootNode(key)) {
            V v = root.value;
            changeRoot();
            count--;
            return v;

        }

        Node<K, V> upper = upperByKey(key);
        Node<K, V> current = upper.belowByKey(key);

        if (!current.isLeftChilded() && !current.isRightChilded()) {
            if (upper.left == current) {
                upper.left = null;
            } else {
                upper.right = null;
            }
        }

        if (current.isLeftChilded() && !current.isRightChilded()) {
            if (upper.left == current)
                upper.left = current.left;
            else
                upper.right = current.left;
        } else if (!current.isLeftChilded() && current.isRightChilded()) {
            if (upper.left == current)
                upper.left = current.right;
            else
                upper.right = current.right;
        } else if (current.isLeftChilded() && current.isRightChilded()) {
            Node<K, V> nodetochangewith = current.pred_in_order();

            if (upper.left == current)
                upper.left = nodetochangewith;
            else
                upper.right = nodetochangewith;

            nodetochangewith.left = current.left;
            nodetochangewith.right = current.right;
        }

        count--;
        current.left = null;
        current.right = null;
        return current.value;
    }

    private Node<K, V> upperByKey(K key) {
        Node<K, V> current = root;
        Node<K, V> above = root;
        int comparison = ((K) key).compareTo(current.key);

        while (comparison != 0) {
            above = current;
            if (comparison < 0) {
                current = current.left;
            } else if (comparison > 0) {
                current = current.right;
            }
            comparison = ((K) key).compareTo(current.key);
        }
        return above;
    }

    private boolean rootNode(K key) {
        return (key.compareTo(root.key) == 0);
    }

    private boolean singleRoot(K key) {
        return (rootNode(key) && count == 1);
    }

    private void changeRoot() {

        Node<K, V> tobechangedwith = root.pred_in_order();
        Node<K, V> nodeparent = tobechangedwith.getParentNode(root);
        if (nodeparent.left == tobechangedwith) {
            nodeparent.left = null;
        } else {
            nodeparent.right = null;
        }
        tobechangedwith.left = root.left;
        tobechangedwith.right = root.right;
        root = tobechangedwith;
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
            list.add(node.value);
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
            list.add(node.key);
            if (node.left != null)
                stack.push(node.left);
            if (node.right != null)
                stack.push(node.right);
        }
    }

    private void getAll(Node<K, V> node, LinkedList<Node<K, V>> list) {
        if (node == null) {
            return;
        }
        Stack<Node> stack = new Stack<>();
        stack.push(node);

        while (!stack.empty()) {
            node = stack.pop();
            list.add(node);
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
