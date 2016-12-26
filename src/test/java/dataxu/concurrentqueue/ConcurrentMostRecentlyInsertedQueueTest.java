package dataxu.concurrentqueue;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.BlockJUnit4ClassRunner;

@RunWith(BlockJUnit4ClassRunner.class)
public class ConcurrentMostRecentlyInsertedQueueTest {

  private ConcurrentMostRecentlyInsertedQueue<Integer> queue;

  @Before
  public void setup() {
    queue = new ConcurrentMostRecentlyInsertedQueue<>(25);
  }

  @Test
  public void test_offer_poll_concurrently() {

    Runnable producer = () -> {
      try {
        for (int i = 0; i < 100; i++) {
          queue.offer(i);
          System.out.println("Produced: " + i);
        }
        Thread.sleep(100);
      } catch (InterruptedException e) {

      }
    };

    Runnable  consumer= () -> {
      while (true)
      {
        System.out.println("Consumed: " + queue.poll());
      }
    };

    Thread producerThread = new Thread(producer, "producer");
    Thread consumerThread = new Thread(consumer, "consumer");

    producerThread.start();
    consumerThread.start();
  }

}