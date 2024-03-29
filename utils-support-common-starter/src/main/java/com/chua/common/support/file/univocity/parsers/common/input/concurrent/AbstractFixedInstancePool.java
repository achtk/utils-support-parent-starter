package com.chua.common.support.file.univocity.parsers.common.input.concurrent;

import java.util.Arrays;

/**
 * A very simple object instance pool with a fixed size.
 *
 * <p> This is essentially an immutable circular queue. Elements are not added nor removed. Pointers to the head and tail of the queue identify what is the next available entry.
 * <p> Use {@link AbstractFixedInstancePool#allocate()} to get an available {@link Entry} from the pool. If all objects are allocated then the thread will block until an element is released.
 * <p> {@link AbstractFixedInstancePool#release(Entry)} releases an allocated {@link Entry} for reuse.
 *
 * @param <T> the class of objects stored in the instance pool
 * @author Univocity Software Pty Ltd - <a href="mailto:parsers@univocity.com">parsers@univocity.com</a>
 * @see Entry
 */
abstract class AbstractFixedInstancePool<T> {
	final Entry<T>[] instancePool;
	private final int[] instanceIndexes;
	private int head = 0;
	private int tail = 0;
	int count = 0;
	private int lastInstanceIndex = 0;

	/**
	 * Creates a new instance pool with the given size. Upon instantiation, the {@link AbstractFixedInstancePool#newInstance()} method will be called to fill in the instance pool, and the pool
	 * can then have its entries allocated for use (and reuse).
	 *
	 * @param size the size of the fixed instance pool.
	 */
	@SuppressWarnings("unchecked")
	AbstractFixedInstancePool(int size) {
		instancePool = new Entry[size];
		instanceIndexes = new int[size];
		Arrays.fill(instanceIndexes, -1);
		instancePool[0] = new Entry<T>(newInstance(), 0);
		instanceIndexes[0] = 0;
	}

	/**
	 * Creates a new instance of the given type of objects stored as entries of this instance pool
	 * This method is called in the constructor of this class for initialization of the instance array and must always return a new instance.
	 *
	 * @return returns a new instance to use in the pool
	 */
	protected abstract T newInstance();

	/**
	 * Retrieves the next available entry in this instance pool. Blocks until an entry becomes available (through {@link AbstractFixedInstancePool#release(Entry)}).
	 *
	 * @return the next available entry in this instance pool
	 */
	public synchronized Entry<T> allocate() {
		while (count == instancePool.length) {
			try {
				wait(50);
			} catch (InterruptedException e) {
				Thread.currentThread().interrupt();
				return new Entry<T>(newInstance(), -1);
			}
		}

		int index = instanceIndexes[head];
		if (index == -1) {
			index = ++lastInstanceIndex;
			instanceIndexes[index] = index;
			instancePool[index] = new Entry<T>(newInstance(), index);
		}
		Entry<T> out = instancePool[index];
		// instanceIndexes[head] = -1; //enable to print the queue's contents for debugging purposes
		head++;
		if (head == instancePool.length) {
			head = 0;
		}
		count++;
		return out;
	}

	/**
	 * Releases the given entry and makes it available for allocation (by {@link AbstractFixedInstancePool#allocate()})
	 *
	 * @param e the entry to be released and made available for reuse.
	 */
	public synchronized void release(Entry<T> e) {
		if (e.index != -1) {
			instanceIndexes[tail++] = e.index;
			if (tail == instancePool.length) {
				tail = 0;
			}
			count--;
		}
		notify();
	}
}
