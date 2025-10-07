package core.basesyntax;

import java.util.Objects;

public class MyHashMap<K, V> implements MyMap<K, V> {
    private static final int DEFAULT_INITIAL_CAPACITY = 1 << 4;
    private static final int MAXIMUM_CAPACITY = Integer.MAX_VALUE;
    private static final float DEFAULT_LOAD_FACTOR = 0.75f;
    private Node<K, V>[] mainList;
    private int tableCapacity;
    private int size;
    private int threshold;

    @SuppressWarnings("unchecked")
    public MyHashMap() {
        mainList = (Node<K, V>[]) new Node[DEFAULT_INITIAL_CAPACITY];
        tableCapacity = DEFAULT_INITIAL_CAPACITY;
        size = 0;
        threshold = (int) (DEFAULT_INITIAL_CAPACITY * DEFAULT_LOAD_FACTOR);
    }

    @Override
    public void put(K key, V value) {
        if (size >= threshold && tableCapacity < MAXIMUM_CAPACITY) {
            expandTable();
        }
        int index = hash(key);
        if (key == null) {
            Node<K, V> firstNode = mainList[0];
            if (firstNode == null) {
                mainList[0] = new Node<>(index, key, value, null);
                size++;
                return;
            }
            if (firstNode.key == null) {
                firstNode.value = value;
                return;
            }
            while (firstNode.next != null) {
                if (firstNode.key == null) {
                    firstNode.value = value;
                    return;
                }
                firstNode = firstNode.next;
            }
            if (firstNode.key == null) {
                firstNode.value = value;
                return;
            }
            firstNode.next = new Node<>(index, key, value, null);
            size++;
            return;
        }
        Node<K, V> newNode = new Node<>(index, key, value, null);
        if (mainList[index] != null) {
            Node<K, V> currentNode = mainList[index];
            if (Objects.equals(currentNode.key, newNode.key)) {
                currentNode.value = value;
                return;
            }
            while (currentNode.next != null) {
                currentNode = currentNode.next;
                if (Objects.equals(currentNode.key, newNode.key)) {
                    currentNode.value = value;
                    return;
                }
            }
            currentNode.next = newNode;
        } else {
            mainList[index] = newNode;
        }
        size++;
    }

    @Override
    public V getValue(K key) {
        int index = hash(key);
        Node<K, V> bucket = mainList[index];
        if (bucket == null) {
            return null;
        }
        if (Objects.equals(key, bucket.key)) {
            return bucket.value;
        }
        while (bucket.next != null) {
            bucket = bucket.next;
            if (Objects.equals(key, bucket.key)) {
                return bucket.value;
            }
        }
        return null;
    }

    @Override
    public int getSize() {
        return size;
    }

    private int hash(Object key) {
        return (key == null) ? 0 : (key.hashCode() % tableCapacity + tableCapacity) % tableCapacity;
    }

    @SuppressWarnings("unchecked")
    private void expandTable() {
        tableCapacity = tableCapacity << 1;
        threshold = (int) (DEFAULT_LOAD_FACTOR * tableCapacity);
        Node<K, V>[] newList = (Node<K, V>[]) new Node[tableCapacity];
        Node<K, V> oldTableNode;
        Node<K, V> newTableNode;
        int newTableIndex;
        for (Node<K, V> bucket : mainList) {
            if (bucket != null) {
                oldTableNode = bucket;
                newTableIndex = hash(oldTableNode.key);
                newTableNode = new Node<>(newTableIndex,
                        oldTableNode.key, oldTableNode.value, null);
                addToEndOfBucket(newTableNode, newTableIndex, newList);
                while (oldTableNode.next != null) {
                    oldTableNode = oldTableNode.next;
                    newTableIndex = hash(oldTableNode.key);
                    newTableNode = new Node<>(newTableIndex,
                            oldTableNode.key, oldTableNode.value, null);
                    addToEndOfBucket(newTableNode, newTableIndex, newList);
                }
            }
        }
        mainList = newList;
    }

    private void addToEndOfBucket(Node<K, V> newTableNode, int index, Node<K, V>[] newList) {
        Node<K, V> bucket = newList[index];
        if (bucket == null) {
            newList[index] = newTableNode;
        } else {
            while (bucket.next != null) {
                bucket = bucket.next;
            }
            bucket.next = newTableNode;
        }
    }

    private class Node<K, V> {
        private final int hash;
        private final K key;
        private V value;
        private Node<K, V> next;

        private Node(int hash, K key, V value, Node<K, V> next) {
            this.hash = hash;
            this.key = key;
            this.value = value;
            this.next = next;
        }

    }
}
