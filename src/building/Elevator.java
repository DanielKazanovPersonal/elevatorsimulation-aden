package building;
import java.util.ArrayList;
import java.util.logging.FileHandler;
import java.util.logging.Level;
import java.util.logging.Logger;
// before liveshare

/**
 * @author 
 * This class will represent an elevator, and will contain
 * configuration information (capacity, speed, etc) as well
 * as state information - such as stopped, direction, and count
 * of passengers targeting each floor...
 */
public class Elevator {
	/** Elevator State Variables - These are visible publicly */
	public final static int STOP = 0;
	public final static int MVTOFLR = 1;
	public final static int OPENDR = 2;
	public final static int OFFLD = 3;
	public final static int BOARD = 4;
	public final static int CLOSEDR = 5;
	public final static int MV1FLR = 6;

	/** Default configuration parameters for the elevator. These should be
	 *  updated in the constructor.
	 */
	private int capacity = 15;				// The number of PEOPLE the elevator can hold
	private int ticksPerFloor = 5;			// The time it takes the elevator to move between floors
	private int ticksDoorOpenClose = 2;  	// The time it takes for doors to go from OPEN <=> CLOSED
	private int passPerTick = 3;            // The number of PEOPLE that can enter/exit the elevator per tick
	
	/** Finite State Machine State Variables */
	private int currState;		// current state
	private int prevState;      // prior state
	private int prevFloor;      // prior floor
	private int currFloor;      // current floor
	private int direction;      // direction the Elevator is traveling in.

	private int timeInState;    // represents the time in a given state
	                            // reset on state entry, used to determine if
	                            // state has completed or if floor has changed
	                            // *not* used in all states 

	private int doorState;      // used to model the state of the doors - OPEN, CLOSED
	                            // or moving

	
	private int passengers;  	// the number of people in the elevator
	
	private ArrayList<Passengers>[] passByFloor;  // Passengers to exit on the corresponding floor

	private int moveToFloor;	// When exiting the STOP state, this is the floor to move to without
	                            // stopping.
	
	private int postMoveToFloorDir; // This is the direction that the elevator will travel AFTER reaching
	                                // the moveToFloor in MVTOFLR state.
	private int transferredPassengers; //represents the passengers getting on or off the elevator

	//for the moveToFloor method
	private int targetFloor;
	private int startFloor;
	
	private boolean atCapLastTick;
	
	@SuppressWarnings("unchecked")
	public Elevator(int numFloors,int capacity, int floorTicks, int doorTicks, int passPerTick) {		
		this.prevState = STOP;
		this.currState = STOP;
		this.timeInState = 0;
		this.currFloor = 0;
		passByFloor = new ArrayList[numFloors];
		
		for (int i = 0; i < numFloors; i++) 
			passByFloor[i] = new ArrayList<Passengers>();
		direction = 1; //up by default
	}
	
	/**
	 * Removes all passengers with a given destination floor
	 * @param floor floor to be cleared
	 * Author: BX
	 * Reviewer: __
	 * 
	 */
	public void clearPassengers(int floor) {
		int passGettingOff = 0;
		for (Passengers p : passByFloor[floor]) {
			passGettingOff += p.getNumPass();
		}
		passByFloor[floor].clear();
		passengers -= passGettingOff;
	}

	/**
	 * Returns a list of all of the passengers in the elevator
	 * Author: BX
	 * Reviewer: __
	 * 
	 * @return an arraylist of every passenger group in the elevator
	 */
	public ArrayList<Passengers> getAllPassengers() {
		ArrayList<Passengers> allPass = new ArrayList<>();
		for (int i = 0; i < passByFloor.length; i++) {
			for (int j = 0; j < passByFloor[i].size(); j++) {
				allPass.add(passByFloor[i].get(j));
			}
		}
		return allPass;
	}

	/**
	 * Increase the time in state by 1
	 */
	public void incrementTicks() {
		timeInState++;
	}

	/**
	 * Adds a passenger group to the elevator
	 * Author: BX
	 * Reviewer: __
	 * @param p passenger group
	 */
	public void addPassengers(Passengers p) {
		passByFloor[p.getDestFloor()].add(p);
		passengers += p.getNumPass();
	}
	
	/**
	 * Updates the current floor of the elevator
	 * @param newFloor new floor of the elevator
	 * Author: BX
	 * Reviewer: __
	 */
	public void updateCurrFloor(int newFloor) {
		prevFloor = currFloor;
		currFloor = newFloor;
	}
	/**
	 * Updates the current state of the elevator
	 * @param newState new state of the elevator
	 * Author: BX
	 * Reviewer: __
	 */
	public void updateCurrState(int newState) {
		prevState = currState;
		currState = newState;
		if (newState != prevState)
			timeInState = 0;
	}

	/**
	 * Gets a list of all passengers with a given destination floor
	 * 
	 * @param floor destination floor
	 * @return list of passengers with destination floor of floor
	 */
	public ArrayList<Passengers> getPassWithDestFloor(int floor) {
		return passByFloor[floor];
	}
	
	/**
	 * Flips the direction of the elevator
	 */
	void flipDirections() {
		direction *= -1;
	}
	
	/**
	 * Opens the door by 1 tick
	 */
	void openDoor() {
		doorState += 1;
	}
	
	/**
	 * Closes the door by 1 tick
	 */
	void closeDoor() {
		doorState -= 1;
	}
	
	public int getPrevState() {
		return this.prevState;
	}

	public int getCurrState() {
		return this.currState;
	}
	
	public int getPrevFloor() {
		return this.prevFloor;
	}

	public int getCurrFloor() {
		return this.currFloor;
	}
	
	public int getCapacity() {
		return this.capacity;
	}

	public int getBoardedPassengers() {
		return this.transferredPassengers;
	}

	public void setBoardedPassengers(int boardedPassengers) {
		this.transferredPassengers = boardedPassengers;
	}

	public int getOffloadedPassengers() {
		return this.transferredPassengers;
	}
	
	public void setOffloadedPassengers(int offloadedPassengers) {
		this.transferredPassengers = offloadedPassengers;
	}

	public int getTicksPerFloor() {
		return this.ticksPerFloor;
	}

	public int getTicksDoorOpenClose() {
		return this.ticksDoorOpenClose;
	}

	public int getPassPerTick() {
		return this.passPerTick;
	}

	
	public void setCapacity(int capacity) {
		this.capacity = capacity;
	}
	public void setTicksPerFloor(int ticksPerFloor) {
		this.ticksPerFloor = ticksPerFloor;
	}
	public void setTicksDoorOpenClose(int ticksDoorOpenClose) {
		this.ticksDoorOpenClose = ticksDoorOpenClose;
	}
	public void setPassPerTick(int passPerTick) {
		this.passPerTick = passPerTick;
	}
	public void setCurrState(int currState) {
		this.currState = currState;
	}
	public void setPrevState(int prevState) {
		this.prevState = prevState;
	}
	public void setPrevFloor(int prevFloor) {
		this.prevFloor = prevFloor;
	}
	public void setCurrFloor(int currFloor) {
		this.currFloor = currFloor;
	}

	public int getDirection() {
		return this.direction;
	}
	
	void setDirection(int direction) {
		this.direction = direction;
	}

	public int getTimeInState() {
		return this.timeInState;
	}

	public void setTimeInState(int timeInState) {
		this.timeInState = timeInState;
	}

	public int getDoorState() {
		return this.doorState;
	}

	public int getPassengers() {
		return this.passengers;
	}

	public void setPassengers(int passengers) {
		this.passengers = passengers;
	}

	public int getPostMoveToFloorDir() {
		return this.postMoveToFloorDir;
	}

	public void setPostMoveToFloorDir(int postMoveToFloorDir) {
		this.postMoveToFloorDir = postMoveToFloorDir;
	}

	public int getTargetFloor() {
		return targetFloor;
	}
	
	public void setTargetFloor(int targetFloor) {
		this.targetFloor = targetFloor;
	}
	
	public int getStartFloor() {
		return startFloor;
	}
	
	public void setStartFloor(int startFloor) {
		this.startFloor = startFloor;
	}
	
	boolean atCapacityLastTick() {
		return atCapLastTick;
	}
	
	void setCapacityFlag(boolean flag) {
		atCapLastTick = flag;
	}
}
