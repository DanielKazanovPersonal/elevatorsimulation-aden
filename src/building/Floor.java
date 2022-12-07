package building;
import java.util.ArrayList;
// ListIterater can be used to look at the contents of the floor queues for 
// debug/display purposes...
import java.util.ListIterator;

import genericqueue.GenericQueue;

// TODO: Auto-generated Javadoc
/**
 * The Class Floor. This class provides the up/down queues to hold
 * Passengers as they wait for the Elevator.
 */
public class Floor {
	/**  Constant for representing direction. */
	private static final int UP = 1;
	private static final int DOWN = -1;

	/** The queues to represent Passengers going UP or DOWN */	
	private GenericQueue<Passengers> down;
	private GenericQueue<Passengers> up;

	public Floor(int qSize) {
		down = new GenericQueue<Passengers>(qSize);
		up = new GenericQueue<Passengers>(qSize);
	}
	
	// TODO: Write the helper methods needed for this class. 
	// You probably will only be accessing one queue at any
	// given time based upon direction - you could choose to 
	// account for this in your methods.

	public boolean passGoingInDir(int dir) {
		if (dir > 0) {
			if (up.isEmpty()) {
				return false;
			}
			return true;
		} else {
			if (down.isEmpty()) {
				return false;
			}
			return true;	
		}
	}

	// gets and removes first passenger in q
	public Passengers removeFirstPassInQ(int direction) {

		if (direction > 0) {
			return up.poll();
		} else {
			return down.poll();
		}
	}

	public void addPassenger(Passengers p, int direction) { // can always come back to change return value
		if (direction == UP) {
			up.add(p);
		} else {
			down.add(p);
		}
	}

	public boolean isEmpty() {
		if (down.isEmpty() && up.isEmpty()) return true;
		return false;
	}

	// same as removeFirstPassInQ wo the removal
	public Passengers peekFloorQueue(int dir) {
		if (dir > 0) {
			return up.peek();
		} else {
			return down.peek();
		}
	}
	
	ArrayList<Passengers> getAllPassengers() {
		ArrayList<Passengers> passengers = new ArrayList<Passengers>();
		
		ListIterator<Passengers> q;
		q = up.getListIterator();
		if (q != null) {
			while (q.hasNext()) {
				passengers.add(q.next());
			}
		}
		q = down.getListIterator();
		if (q != null) {
			while (q.hasNext()) {
				passengers.add(q.next());
			}
		}
		
		return passengers;
	}

	/**
	 * Queue string. This method provides visibility into the queue
	 * contents as a string. What exactly you would want to visualize 
	 * is up to you
	 *
	 * @param dir determines which queue to look at
	 * @return the string of queue contents
	 */
	String queueString(int dir) {
		String str = "";
		ListIterator<Passengers> list;
		list = (dir == UP) ?up.getListIterator() : down.getListIterator();
		if (list != null) {
			while (list.hasNext()) {
				// choose what you to add to the str here.
				// Example: str += list.next().getNumPass();
				if (list.hasNext()) str += ",";
			}
		}
		return str;	
	}
	
	
}
