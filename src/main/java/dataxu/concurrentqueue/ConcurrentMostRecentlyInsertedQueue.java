package dataxu.concurrentqueue;

import dataxu.queue.MostRecentlyInsertedQueue;

public class ConcurrentMostRecentlyInsertedQueue<E> extends MostRecentlyInsertedQueue<E> {

  public ConcurrentMostRecentlyInsertedQueue(int capacity) {
    super(capacity);
  }

  @Override
  public int size() {
    synchronized (this) {
      return super.size();
    }
  }

  @Override
  public boolean offer(E e) {
    synchronized (this) {
      return super.offer(e);
    }
  }

  @Override
  public E poll() {
    synchronized (this) {
      return super.poll();
    }
  }

  @Override
  public E peek() {
    synchronized (this) {
      return super.peek();
    }
  }

}
