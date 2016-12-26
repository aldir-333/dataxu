package dataxu.queue;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.BlockJUnit4ClassRunner;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.nullValue;
import static org.junit.Assert.assertThat;

@RunWith(BlockJUnit4ClassRunner.class)
public class MostRecentlyInsertedQueueTest {

  private MostRecentlyInsertedQueue<Integer> mostRecentlyInsertedQueue;

  @Before
  public void init() {
    mostRecentlyInsertedQueue = new MostRecentlyInsertedQueue<>(5);
  }

  private void fill() {
    mostRecentlyInsertedQueue.offer(10);
    mostRecentlyInsertedQueue.offer(20);
    mostRecentlyInsertedQueue.offer(30);
    mostRecentlyInsertedQueue.offer(40);
  }

  @Test
  public void offer() {
    fill();

    assertThat(mostRecentlyInsertedQueue.size(), is(4));

    assertThat(mostRecentlyInsertedQueue.offer(55), is(true));
    assertThat(mostRecentlyInsertedQueue.size(), is(5));

    assertThat(mostRecentlyInsertedQueue.offer(9), is(true));
    assertThat(mostRecentlyInsertedQueue.offer(124), is(true));
    assertThat(mostRecentlyInsertedQueue.size(), is(5));

  }

  @Test
  public void offer_null() {
    assertThat(mostRecentlyInsertedQueue.offer(null), is(false));
  }

  @Test
  public void poll() {
    fill();

    assertThat(mostRecentlyInsertedQueue.poll(), is(10));
    assertThat(mostRecentlyInsertedQueue.size(), is(3));

    assertThat(mostRecentlyInsertedQueue.poll(), is(20));
    assertThat(mostRecentlyInsertedQueue.poll(), is(30));
    assertThat(mostRecentlyInsertedQueue.size(), is(1));

    assertThat(mostRecentlyInsertedQueue.poll(), is(40));
    assertThat(mostRecentlyInsertedQueue.poll(), nullValue());
  }

  @Test
  public void peek() {
    fill();

    assertThat(mostRecentlyInsertedQueue.peek(), is(10));
    assertThat(mostRecentlyInsertedQueue.size(), is(4));

    mostRecentlyInsertedQueue.offer(55);
    assertThat(mostRecentlyInsertedQueue.peek(), is(10));
    assertThat(mostRecentlyInsertedQueue.size(), is(5));

    mostRecentlyInsertedQueue.offer(65);
    assertThat(mostRecentlyInsertedQueue.peek(), is(20));
    assertThat(mostRecentlyInsertedQueue.size(), is(5));

    mostRecentlyInsertedQueue.clear();
    assertThat(mostRecentlyInsertedQueue.peek(), nullValue());
  }

  @Test
  public void clear_and_isEmpty() {
    fill();

    assertThat(mostRecentlyInsertedQueue.isEmpty(), is(false));

    mostRecentlyInsertedQueue.clear();
    assertThat(mostRecentlyInsertedQueue.isEmpty(), is(true));
    assertThat(mostRecentlyInsertedQueue.size(), is(0));
  }

  @Test(expected = IllegalArgumentException.class)
  public void create_with_illegal_capacity() {
    new MostRecentlyInsertedQueue(0);
  }

}
