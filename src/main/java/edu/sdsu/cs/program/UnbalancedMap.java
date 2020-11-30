package edu.sdsu.cs.program;

import java.util.Iterator;
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

        public Node<K, V> getChildWithKey(K key) {
            if (this.hasLeftChild() && this.left.key.equals(key)) {
                return this.left;
            } else if (this.hasRightChild() && this.right.key.equals(key)) {
                return this.right;
            } else {
                return null;
            }
        }

        private Node<K, V> smallest() {
            Node<K, V> smallest = this;
            while (smallest.hasLeftChild()) {
                smallest = smallest.left;
            }
            return smallest;
        }

        public Node<K, V> inOrderPredecessor() {
            Node<K, V> predecessor = this;
            if (predecessor.hasRightChild()) {
                return predecessor.right.smallest();
            }
            return null;
        }

        public boolean hasLeftChild() {
            return left != null;
        }

        public boolean hasRightChild() {
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

    private Node<K,V> getByKey(K key) {
        Node<K,V> p = root;
        while (p != null) {
            int cmp = p.key.compareTo(key);
            if (cmp < 0)
                p = p.right;
            else if (cmp > 0)
                p = p.left;
            else
                return p;
        }
        return null;
    }

    private Node<K,V> getByValue (V value) {
        Node<K,V> node = root;

        Stack<Node> stack = new Stack<>();
        stack.push(node);

        while (!stack.empty()) {
            node = stack.pop();

            if (node.value.equals(value)) {
                return node;
            }

            if (node.left != null)
                stack.push(node.left);
            if (node.right != null)
                stack.push(node.right);
        }
        return null;
    }

    @Override
    public boolean contains(K key) {
        if (root == null)
            return false;

        return getByKey(key) != null;
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
        if (!this.contains(key)) {
            return null;
        }

        if (isOnlyRoot(key)) {
            V value = root.value;
            root = null;
            count--;
            return value;
        }

        if (isRoot(key)) {
            V value = root.value;
            replaceRoot();
            count--;
            return value;

        }

        Node<K, V> parent = getParentNodeByKey(key);
        Node<K, V> active = parent.getChildWithKey(key);

        if (!active.hasLeftChild() && !active.hasRightChild()) {
            if (parent.left == active) {
                parent.left = null;
            } else {
                parent.right = null;
            }
        }

        if (active.hasLeftChild() && !active.hasRightChild()) {
            if (parent.left == active) {
                parent.left = active.left;
            } else {
                parent.right = active.left;
            }
        } else if (!active.hasLeftChild() && active.hasRightChild()) {
            if (parent.left == active) {
                parent.left = active.right;
            } else {
                parent.right = active.right;
            }
        } else if (active.hasLeftChild() && active.hasRightChild()) {
            Node<K, V> replacement = active.inOrderPredecessor();

            if (parent.left == active) {
                parent.left = replacement;
            } else {
                parent.right = replacement;
            }
            replacement.left = active.left;
            replacement.right = active.right;

        }

        count--;
        active.left = null;
        active.right = null;
        return active.value;
    }

    private Node<K, V> getParentNodeByKey(K key) {
        Node<K, V> active = root;
        Node<K, V> parent = root;
        int direction = key.compareTo(active.key);

        while (direction != 0) {
            parent = active;
            if (direction < 0) {
                active = active.left;
            } else if (direction > 0) {
                active = active.right;
            }
            direction = key.compareTo(active.key);
        }
        return parent;
    }

    private boolean isRoot(K key) {
        return (key.compareTo(root.key) == 0);
    }

    private boolean isOnlyRoot(K key) {
        return (isRoot(key) && size() == 1);
    }

    private void replaceRoot() {

        Node<K, V> replacement = root.inOrderPredecessor();
        Node<K, V> parent = replacement.getParentNode(root);
        if (parent.left == replacement) {
            parent.left = null;
        } else {
            parent.right = null;
        }
        replacement.left = root.left;
        replacement.right = root.right;
        root = replacement;
    }

    @Override
    public V getValue(K key) {
        if (root == null)
            return null;

        Node<K,V> foundNode = getByKey(key);
        if (foundNode == null)
            return null;
        return foundNode.value;
    }

    @Override
    public K getKey(V value) {
        if (root == null)
            return null;

        Node<K,V> foundNode = getByValue(value);
        if (foundNode == null)
            return null;
        return foundNode.key;
    }

    @Override
    public Iterable<K> getKeys(V value) {
        LinkedList<K> list = new LinkedList<>();
        traverseKeys(root, list, true, value);
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
        traverseKeys(root, list, false, null);
        return list;
    }

    private void traverseValues(Node<K, V> node, LinkedList<V> list, boolean filterWithkey, K key) {
        if (node == null) {
            return;
        }
        Stack<Node> stack = new Stack<>();
        stack.push(node);

        while (!stack.empty()) {
            node = stack.pop();

            if (filterWithkey) {
                if (node.key.equals(key)) {
                    list.add(node.value);
                }
            } else {
                list.add(node.value);
            }

            if (node.left != null)
                stack.push(node.left);
            if (node.right != null)
                stack.push(node.right);
        }
    }

    private void traverseKeys(Node<K, V> node, LinkedList<K> list, boolean filterWithValue, V value) {
        if (node == null) {
            return;
        }
        Stack<Node> stack = new Stack<>();
        stack.push(node);

        while (!stack.empty()) {
            node = stack.pop();

            if (filterWithValue) {
                if (node.value.equals(value))
                    list.add(node.key);
            } else {
                list.add(node.key);
            }

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
        traverseValues(root, list, false, null);
        return list;
    }
}
