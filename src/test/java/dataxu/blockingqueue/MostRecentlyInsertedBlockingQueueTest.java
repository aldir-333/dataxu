package dataxu.blockingqueue;

import com.jayway.awaitility.Duration;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.BlockJUnit4ClassRunner;

import java.util.Random;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.TimeUnit;

import static com.jayway.awaitility.Awaitility.await;
import static java.lang.Thread.sleep;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

@RunWith(BlockJUnit4ClassRunner.class)
public class MostRecentlyInsertedBlockingQueueTest {

  private BlockingQueue<Integer> queue;

  @Before
  public void setup() {
    queue = new MostRecentlyInsertedBlockingQueue<>(5);
  }

  @Test(expected = IllegalArgumentException.class)
  public void instantiatingWithNegativeCapacity_Should_ThrowException() {
    new MostRecentlyInsertedBlockingQueue(0);
  }

  @Test(timeout = 1600)
  public void pollWithTimeout_Should_ReturnNull_IfTimeElapses() throws InterruptedException {
    Integer poll = queue.poll(1500, TimeUnit.MILLISECONDS);
    assertNull(poll);
  }

  @Test
  public void take_from_empty_queue()
      throws InterruptedException {
    new Thread(() -> {
      await().atMost(Duration.TEN_SECONDS);
      for (int i = 2; i < 5; i++) {
        queue.offer(i);
      }
    }).start();

    int take1 = queue.take();
    int take2 = queue.take();
    int take3 = queue.take();

    assertEquals(2, take1);
    assertEquals(3, take2);
    assertEquals(4, take3);
  }

  @Test
  public void remaining_capacity_and_size() throws InterruptedException {
    assertEquals(5, queue.remainingCapacity());
    assertEquals(0, queue.size());

    queue.offer(1);
    queue.put(3);

    assertEquals(2, queue.size());
    assertEquals(3, queue.remainingCapacity());
  }

  @Test
  public void offer_take() throws InterruptedException {
    Runnable producer = () -> {
      Random rand = new Random();
      int res = 0;

      try {

        for (int i = 0; i < 3; i++) {
          res = rand.nextInt(200);
          System.out.println("Produced put: " + res);
          queue.put(res);
          Thread.sleep(200);
        }

        for (int i = 0; i < 3; i++) {
          res = rand.nextInt(350);
          System.out.println("Produced offer: " + res);
          queue.offer(res, 200, TimeUnit.MILLISECONDS);
          Thread.sleep(300);
        }

        res = rand.nextInt(350);
        System.out.println("Produced offer: " + res);
        queue.offer(res);

      } catch (InterruptedException e) {
        e.printStackTrace();
      }
    };


    Runnable consumer = () -> {
      try {
        for (int i = 0; i < 10; i++) {
          System.out.println("Consumed: " + queue.take());
        }
      } catch (InterruptedException e) {
        e.printStackTrace();
      }
    };

    new Thread(producer).start();
    new Thread(consumer).start();

    sleep(4000);
  }

}