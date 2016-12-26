# dataxu
MostRecentlyInsertedQueue

Implement a class called MostRecentlyInsertedQueue which implements the interface java.util.Queue<E>. To reduce the amount of boilerplate code you have to write you may extend java.util.AbstractQueue<E>. 
The purpose of this queue is to store the N most recently inserted elements. The queue should have the following properties:

The queue implements the interface java.util.Queue<E>
The queue is bounded in size. The total capacity of the queue must be passed into the constructor.
New elements are added to the tail of the queue
The queue is traversed from head to tail
The queue must always accept new elements. If the queue is already full (Queue#size() == capacity), the oldest element that was inserted (the head) should be evicted, and then the new element can be added at the tail.

The following code demonstrates the desired behavior:

The primary evaluation criteria are correctness in the behavior of the queue as specified in the problem description, design, and clarity.
Secondary criteria include performance and memory efficiency.

Bonus #1: Implement ConcurrentMostRecentlyInsertedQueue, a thread-safe version of MostRecentlyInsertedQueue

Bonus #2: Implement MostRecentlyInsertedBlockingQueue, a thread-safe variant of MostRecentlyInsertedQueue that implements java.util.concurrent.BlockingQueue<E>
