package com.librarysystem.data;

import java.util.ArrayList;
import java.util.List;
import java.util.NoSuchElementException;

/**
 * A generic priority queue implementation using a binary min-heap.
 */
public class PriorityQueue<E extends Comparable<E>> {
    private final List<E> heap;

    public PriorityQueue() {
        this.heap = new ArrayList<>();
    }

    /**
     * Add element to priority queue
     */
    public void offer(E element) {
        heap.add(element);
        siftUp(heap.size() - 1);
    }

    /**
     * Remove and return the highest priority element
     */
    public E poll() {
        if (isEmpty()) {
            throw new NoSuchElementException("Priority queue is empty");
        }

        E root = heap.get(0);
        E last = heap.remove(heap.size() - 1);

        if (!heap.isEmpty()) {
            heap.set(0, last);
            siftDown(0);
        }

        return root;
    }

    /**
     * View the highest priority element without removing
     */
    public E peek() {
        if (isEmpty()) {
            throw new NoSuchElementException("Priority queue is empty");
        }
        return heap.get(0);
    }

    /**
     * Check if queue is empty
     */
    public boolean isEmpty() {
        return heap.isEmpty();
    }

    /**
     * Get size of queue
     */
    public int size() {
        return heap.size();
    }

    /**
     * Clear the queue
     */
    public void clear() {
        heap.clear();
    }

    /**
     * Sift up to maintain heap property
     */
    private void siftUp(int index) {
        while (index > 0) {
            int parentIndex = (index - 1) / 2;
            if (heap.get(index).compareTo(heap.get(parentIndex)) >= 0) {
                break;
            }
            swap(index, parentIndex);
            index = parentIndex;
        }
    }

    /**
     * Sift down to maintain heap property
     */
    private void siftDown(int index) {
        while (true) {
            int minIndex = index;
            int leftChild = 2 * index + 1;
            int rightChild = 2 * index + 2;

            if (leftChild < heap.size() && 
                heap.get(leftChild).compareTo(heap.get(minIndex)) < 0) {
                minIndex = leftChild;
            }

            if (rightChild < heap.size() && 
                heap.get(rightChild).compareTo(heap.get(minIndex)) < 0) {
                minIndex = rightChild;
            }

            if (minIndex != index) {
                swap(index, minIndex);
                index = minIndex;
            } else {
                break;
            }
        }
    }

    /**
     * Swap two elements
     */
    private void swap(int i, int j) {
        E temp = heap.get(i);
        heap.set(i, heap.get(j));
        heap.set(j, temp);
    }
}
