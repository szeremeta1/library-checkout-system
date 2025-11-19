package com.librarysystem.data;

import java.util.*;

/**
 * A generic doubly-linked list implementation.
 * Supports insertion, deletion, and traversal operations.
 */
public class LinkedList<E> implements Iterable<E> {
    private Node<E> head;
    private Node<E> tail;
    private int size;

    public LinkedList() {
        this.head = null;
        this.tail = null;
        this.size = 0;
    }

    /**
     * Add element to the end of the list
     */
    public void add(E element) {
        Node<E> newNode = new Node<>(element);
        if (isEmpty()) {
            head = tail = newNode;
        } else {
            tail.next = newNode;
            newNode.prev = tail;
            tail = newNode;
        }
        size++;
    }

    /**
     * Add element at specific index
     */
    public void add(int index, E element) {
        if (index < 0 || index > size) {
            throw new IndexOutOfBoundsException("Index: " + index + ", Size: " + size);
        }
        if (index == size) {
            add(element);
            return;
        }

        Node<E> newNode = new Node<>(element);
        Node<E> node = getNode(index);

        newNode.next = node;
        newNode.prev = node.prev;

        if (node.prev != null) {
            node.prev.next = newNode;
        } else {
            head = newNode;
        }
        node.prev = newNode;
        size++;
    }

    /**
     * Remove element at index
     */
    public E remove(int index) {
        if (index < 0 || index >= size) {
            throw new IndexOutOfBoundsException("Index: " + index + ", Size: " + size);
        }

        Node<E> node = getNode(index);
        E element = node.element;

        if (node.prev != null) {
            node.prev.next = node.next;
        } else {
            head = node.next;
        }

        if (node.next != null) {
            node.next.prev = node.prev;
        } else {
            tail = node.prev;
        }

        size--;
        return element;
    }

    /**
     * Remove first occurrence of element
     */
    public boolean remove(E element) {
        for (int i = 0; i < size; i++) {
            if (getNode(i).element.equals(element)) {
                remove(i);
                return true;
            }
        }
        return false;
    }

    /**
     * Get element at index
     */
    public E get(int index) {
        if (index < 0 || index >= size) {
            throw new IndexOutOfBoundsException("Index: " + index + ", Size: " + size);
        }
        return getNode(index).element;
    }

    /**
     * Check if list contains element
     */
    public boolean contains(E element) {
        return indexOf(element) != -1;
    }

    /**
     * Get index of element
     */
    public int indexOf(E element) {
        for (int i = 0; i < size; i++) {
            if (getNode(i).element.equals(element)) {
                return i;
            }
        }
        return -1;
    }

    /**
     * Get size of list
     */
    public int size() {
        return size;
    }

    /**
     * Check if list is empty
     */
    public boolean isEmpty() {
        return size == 0;
    }

    /**
     * Clear the list
     */
    public void clear() {
        head = tail = null;
        size = 0;
    }

    /**
     * Get node at index
     */
    private Node<E> getNode(int index) {
        if (index < size / 2) {
            Node<E> node = head;
            for (int i = 0; i < index; i++) {
                node = node.next;
            }
            return node;
        } else {
            Node<E> node = tail;
            for (int i = size - 1; i > index; i--) {
                node = node.prev;
            }
            return node;
        }
    }

    @Override
    public Iterator<E> iterator() {
        return new Iterator<E>() {
            private Node<E> current = head;

            @Override
            public boolean hasNext() {
                return current != null;
            }

            @Override
            public E next() {
                if (!hasNext()) {
                    throw new NoSuchElementException();
                }
                E element = current.element;
                current = current.next;
                return element;
            }
        };
    }

    /**
     * Inner class for linked list nodes
     */
    private static class Node<E> {
        E element;
        Node<E> next;
        Node<E> prev;

        Node(E element) {
            this.element = element;
        }
    }
}
