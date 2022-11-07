package genericqueue;
import java.util.LinkedList;
import java.util.ListIterator;
import java.util.NoSuchElementException;

/**
 * The Class GenericQueue.
 *
 * @param <E> the element type
 */
public class GenericQueue<E> {
	
	/** The max queue size. */
	private final int MAX_QUEUE_SIZE;
	
	/** The queue. */
	private LinkedList<E> queue = new LinkedList<>();

	/**
	 * Instantiates a new generic queue with a default size of 20
	 */
	public GenericQueue() {	
		MAX_QUEUE_SIZE=20;
	}

	/**
	 * Instantiates a new generic queue of the specified size
	 *
	 * @param queueSize the queue size
	 */
	public GenericQueue(int queueSize) {	
		MAX_QUEUE_SIZE=queueSize;
	}

	/**
	 * Adds the element to the queue if possible
	 *
	 * @param o the element to add
	 * @return true, if successful
	 * @throws IllegalStateException the illegal state exception
	 */
	public boolean add (E o) throws IllegalStateException {
		if (queue.size() == MAX_QUEUE_SIZE) 
			throw new IllegalStateException("Add failed - Queue is full");
		queue.addLast(o);
		return true;
	}
	
	/**
	 * Removes the element at the head of the queue, it possible
	 *
	 * @return the e
	 * @throws NoSuchElementException the no such element exception
	 */
	public E remove() throws NoSuchElementException {
		if (isEmpty())
			throw new NoSuchElementException("No Element to be removed - Queue is empty");
		return(queue.removeFirst());
	}

	/**
	 * Element returns the element at the head of the queue without 
	 * removing it - if possible
	 *
	 * @return the e
	 * @throws NoSuchElementException the no such element exception
	 */
	public E element() throws NoSuchElementException {	
		if (isEmpty())
			throw new NoSuchElementException("No Element to be removed - Queue is empty");
		return(queue.getFirst());
	}
	
	/**
	 * Offer. - same functionality as add, but returns false if add not possible
	 *
	 * @param o the o
	 * @return true, if successful
	 */
	public boolean offer (E o) {
		if (queue.size() == MAX_QUEUE_SIZE)
			return false;
		queue.addLast(o);
		return true;
	}
	
	/**
	 * Poll. Same functionality as remove(), but returns null if queue is empty
	 *
	 * @return the e
	 */
	public E poll() {
		if (isEmpty()) 
			return null;
		return(queue.removeFirst());
	}

	/**
	 * Peek. Same functionality as element(), but returns  null if
	 * queue is empty.
	 *
	 * @return the e
	 */
	public E peek() {	
		if (isEmpty()) 
			return null;
		return(queue.getFirst());
	}
	
	/**
	 * Checks if is empty.
	 *
	 * @return true, if is empty
	 */
	public boolean isEmpty() {
		return (queue.isEmpty());
	}
	
	/**
	 * Size. Returns the number of entries currently in the queue
	 *
	 * @return the int
	 */
	public int size() {
		return (queue.size());
	}
	
	/**
	 * Gets the list iterator to provide non-destructive visibility
	 * to queue contents..
	 *
	 * @return the list iterator
	 */
	public ListIterator<E> getListIterator() {
		return queue.listIterator(0);
	}
	
	/**
	 * To string. 
	 *
	 * @return the string
	 */
	@Override
	public String toString() {
		String str = "queue: [";
		ListIterator<E> list = queue.listIterator(0);
		if (list != null) {
			while (list.hasNext()) {
				str += list.next();
				if (list.hasNext()) str += ",";
			}
		}
		str += "]";
		return str;
	}

}
