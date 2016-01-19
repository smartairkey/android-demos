package com.example.dmitry.accelbattery.utils;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

/**
 * Created by dmitry on 17.01.16.
 */
public class CircularBuffer<T> {
    private final LinkedList<T> elements;
    private final int size;

    public CircularBuffer(int size) {
        this.size = size;
        elements = new LinkedList<>();
    }

    public int getSize() {
        return elements.size();
    }

    public T getLast() {
        if (isEmpty()) {
            return null;
        }
        return elements.getLast();
    }

    public List<T> getAll() {
        return new ArrayList<>(elements);
    }

    public T get(int i) {
        return elements.get(i);
    }

    public boolean isEmpty() {
        return elements.isEmpty();
    }

    public void add(T element) {
        if (isFull()) {
            elements.removeFirst();
        }

        elements.add(element);
    }

    public boolean isFull() {
        return elements.size() == size;
    }

    public void clear() {
        elements.clear();
    }
}
