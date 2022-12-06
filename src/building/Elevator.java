package building;
import java.util.ArrayList;
import java.util.logging.FileHandler;
import java.util.logging.Level;
import java.util.logging.Logger;


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
	 private int boardedPassengers;

	@SuppressWarnings("unchecked")
	public Elevator(int numFloors,int capacity, int floorTicks, int doorTicks, int passPerTick) {		
		this.prevState = STOP;
		this.currState = STOP;
		this.timeInState = 0;
		this.currFloor = 0;
		passByFloor = new ArrayList[numFloors];
		
		for (int i = 0; i < numFloors; i++) 
			passByFloor[i] = new ArrayList<Passengers>(); 

		//TODO: Finish this constructor, adding configuration initialiation and
		//      initialization of any other private fields, etc.
	}
	
	public void clearPassengers(int floor) {
		passByFloor[floor].clear();
	}

	public ArrayList<Passengers> getAllPassengers() {
		ArrayList<Passengers> allPass = new ArrayList<>();
		for (int i = 0; i < passByFloor.length; i++) {
			for (int j = 0; j < passByFloor[i].size(); j++) {
				allPass.add(passByFloor[i].get(j));
			}
		}
		return allPass;
	}

	public void incrementTicks() {
		timeInState++;
	}

	public void addPassengers(Passengers p) {
		passByFloor[p.getDestFloor()].add(p);
	}	

	//TODO: Add Getter/Setters and any methods that you deem are required. Examples 
	//      include:
	//      1) moving the elevator
	//      2) closing the doors
	//      3) opening the doors
	//      and so on...
	
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
	
	public void updateCurrFloor(int newFloor) {
		prevFloor = currFloor;
		currFloor = newFloor;
	}
	
	public int getCapacity() {
		return this.capacity;
	}

	public int getBoardedPassengers() {
		return this.boardedPassengers;
	}

	public void setBoardedPassengers(int boardedPassengers) {
		this.boardedPassengers = boardedPassengers;
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

	public void updateCurrState(int newState) {
		prevState = currState;
		currState = newState;
		timeInState = 0;
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

	public void setDirection(int direction) {
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

	public void setDoorState(int doorState) {
		this.doorState = doorState;
	}

	public int getPassengers() {
		return this.passengers;
	}

	public void setPassengers(int passengers) {
		this.passengers = passengers;
	}

	public ArrayList<Passengers>[] getPassByFloor() {
		return this.passByFloor;
	}

	public void setPassByFloor(ArrayList<Passengers>[] passByFloor) {
		this.passByFloor = passByFloor;
	}

	public int getMoveToFloor() {
		return this.moveToFloor;
	}

	public void setMoveToFloor(int moveToFloor) {
		this.moveToFloor = moveToFloor;
	}

	public int getPostMoveToFloorDir() {
		return this.postMoveToFloorDir;
	}

	public void setPostMoveToFloorDir(int postMoveToFloorDir) {
		this.postMoveToFloorDir = postMoveToFloorDir;
	}

}
