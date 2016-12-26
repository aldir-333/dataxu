package dataxu.blockingqueue;

import java.util.Collection;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.ReentrantLock;

import dataxu.queue.MostRecentlyInsertedQueue;

public class MostRecentlyInsertedBlockingQueue<E> extends MostRecentlyInsertedQueue<E>
    implements BlockingQueue<E> {

  private final int capacity;

  private final AtomicInteger count = new AtomicInteger();

  private Node head;

  private Node last;

  private final ReentrantLock takeLock = new ReentrantLock();

  private final Condition notEmpty = takeLock.newCondition();

  private final ReentrantLock putLock = new ReentrantLock();

  private final Condition notFull = putLock.newCondition();

  public MostRecentlyInsertedBlockingQueue(int capacity) {
    super(capacity);
    this.capacity = capacity;
    last = head = new Node(null);
  }

  private void signalNotEmpty() {
    takeLock.lock();
    try {
      notEmpty.signal();
    } finally {
      takeLock.unlock();
    }
  }

  private void signalNotFull() {
    putLock.lock();
    try {
      notFull.signal();
    } finally {
      putLock.unlock();
    }
  }

  private E dequeue() {
    Node current = head;
    Node first = current.next;
    current.next = current;
    head = first;
    E x = first.value;
    first.value = null;
    return x;
  }

  @Override
  public int size() {
    return count.get();
  }

  @Override
  public int remainingCapacity() {
    return capacity - count.get();
  }

  @Override
  public void put(E e) throws InterruptedException {
    if (e == null) {
      throw new NullPointerException();
    }

    int counter;
    Node node = new Node(e);
    putLock.lockInterruptibly();
    try {
      while (count.get() == capacity) {
        notFull.await();
      }
      last = last.next = node;
      counter = count.getAndIncrement();
      if (counter + 1 < capacity) {
        notFull.signal();
      }
    } finally {
      putLock.unlock();
    }
    if (counter == 0) {
      signalNotEmpty();
    }
  }

  @Override
  public boolean offer(E e, long timeout, TimeUnit unit)
      throws InterruptedException {

    if (e == null) {
      throw new NullPointerException();
    }

    long nanos = unit.toNanos(timeout);
    int counter;
    putLock.lockInterruptibly();
    try {
      while (count.get() == capacity) {
        if (nanos <= 0) {
          return false;
        }
        nanos = notFull.awaitNanos(nanos);
      }
      last = last.next = new Node(e);
      counter = count.getAndIncrement();
      if (counter + 1 < capacity) {
        notFull.signal();
      }
    } finally {
      putLock.unlock();
    }
    if (counter == 0) {
      signalNotEmpty();
    }
    return true;
  }

  @Override
  public boolean offer(E e) {
    if (e == null) {
      throw new NullPointerException();
    }
    if (count.get() == capacity) {
      return false;
    }
    int counter = 0;
    Node node = new Node(e);
    putLock.lock();
    try {
      if (count.get() < capacity) {
        last = last.next = node;
        counter = count.getAndIncrement();
        if (counter + 1 < capacity) {
          notFull.signal();
        }
      }
    } finally {
      putLock.unlock();
    }
    if (counter == 1) {
      signalNotEmpty();
    }
    return counter >= 1;
  }

  @Override
  public E take() throws InterruptedException {
    E x;
    int counter;
    takeLock.lockInterruptibly();
    try {
      while (count.get() == 0) {
        notEmpty.await();
      }
      x = dequeue();
      counter = count.getAndDecrement();
      if (counter > 1) {
        notEmpty.signal();
      }
    } finally {
      takeLock.unlock();
    }
    if (counter == capacity) {
      signalNotFull();
    }
    return x;
  }

  @Override
  public E poll(long timeout, TimeUnit unit) throws InterruptedException {
    E x;
    int counter;
    long nanos = unit.toNanos(timeout);
    takeLock.lockInterruptibly();
    try {
      while (count.get() == 0) {
        if (nanos <= 0) {
          return null;
        }
        nanos = notEmpty.awaitNanos(nanos);
      }
      x = dequeue();
      counter = count.getAndDecrement();
      if (counter > 1) {
        notEmpty.signal();
      }
    } finally {
      takeLock.unlock();
    }
    if (counter == capacity)
      signalNotFull();
    return x;
  }

  @Override
  public E poll() {
    if (count.get() == 0) {
      return null;
    }
    E x = null;
    int counter = 0;

    takeLock.lock();
    try {
      if (count.get() > 0) {
        x = dequeue();
        counter = count.getAndDecrement();
        if (counter > 1) {
          notEmpty.signal();
        }
      }
    } finally {
      takeLock.unlock();
    }
    if (counter == capacity) {
      signalNotFull();
    }
    return x;
  }

  @Override
  public E peek() {
    if (count.get() == 0) {
      return null;
    }
    takeLock.lock();
    try {
      Node first = head.next;
      return first == null ? null : first.value;
    } finally {
      takeLock.unlock();
    }
  }

  @Override
  public int drainTo(Collection<? super E> c) {
    throw new UnsupportedOperationException();
  }

  @Override
  public int drainTo(Collection<? super E> c, int maxElements) {
    throw new UnsupportedOperationException();
  }

  private class Node {
    private E value;
    private Node next;

    Node(E value) {
      this.value = value;
    }
  }

}
