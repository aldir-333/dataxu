package dataxu.queue;

import java.util.AbstractQueue;
import java.util.Iterator;
import java.util.NoSuchElementException;

public class MostRecentlyInsertedQueue<E> extends AbstractQueue<E> {

  private Node first;
  private Node last;
  private int size;
  private int capacity;

  public MostRecentlyInsertedQueue(int capacity) {
    if (capacity <= 0) {
      throw new IllegalArgumentException("Capacity must be greater than 0");
    }
    this.capacity = capacity;
  }

  @Override
  public Iterator<E> iterator() {
    return new MostRecentlyInsertedQueueIterator();
  }

  @Override
  public int size() {
    return size;
  }

  @Override
  public boolean offer(E e) {
    if (e == null) {
      return false;
    }

    Node current = last;
    last = new Node(e);

    if (isEmpty()) {
      first = last;
      size++;
    } else if (size < capacity) {
      current.next = last;
      size++;
    } else {
      Node oldFirst = first;
      first = oldFirst.next;
      current.next = last;
    }
    return true;
  }

  @Override
  public E poll() {
    if (isEmpty()) {
      return null;
    }

    Node current = first;
    first = current.next;
    E e = current.value;
    current.value = null;
    size--;

    return e;
  }

  @Override
  public E peek() {
    if (isEmpty()) {
      return null;
    }
    return first.value;
  }

  private class MostRecentlyInsertedQueueIterator implements Iterator<E> {
    private Node current = first;

    @Override
    public boolean hasNext() {
      return current != null;
    }

    @Override
    public E next() {
      if (current == null) {
        throw new NoSuchElementException();
      }
      E value = current.value;
      current = current.next;

      return value;
    }

    @Override
    public void remove() {
      throw new UnsupportedOperationException("You can't remove elements during iterating.");
    }
  }

  private class Node {
    private E value;
    private Node next;

    Node(E value) {
      this.value = value;
    }
  }
}
