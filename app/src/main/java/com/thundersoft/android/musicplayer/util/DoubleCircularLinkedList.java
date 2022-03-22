package com.thundersoft.android.musicplayer.util;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import java.util.AbstractList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;

public class DoubleCircularLinkedList<T> extends AbstractList<T> {
    @Override
    public T get(int index) {
        return null;
    }

    @Override
    public int size() {
        return 0;
    }

    static class Node<T> {
        T data;
        Node<T> previous, next;

        public Node(T data) {
            this.data = data;
        }
    }


}
