package building;

import java.util.ArrayList;
import java.util.Arrays;

/**
 * The Class CallManager. This class models all of the calls on each floor,
 * and then provides methods that allow the building to determine what needs
 * to happen (ie, state transitions).
 */
public class CallManager {
	
	/** The floors. */
	private Floor[] floors;
	
	/** The num floors. */
	private final int NUM_FLOORS;
	
	/** The Constant UP. */
	private final static int UP = 1;
	
	/** The Constant DOWN. */
	private final static int DOWN = -1;
	
	/** The up calls array indicates whether or not there is a up call on each floor. */
	private boolean[] upCalls;
	
	/** The down calls array indicates whether or not there is a down call on each floor. */
	private boolean[] downCalls;
	
	/** The up call pending - true if any up calls exist */
	private boolean upCallPending;
	
	/** The down call pending - true if any down calls exit */
	private boolean downCallPending;
	
	//TODO: Add any additional fields here..
	
	/**
	 * Instantiates a new call manager.
	 *
	 * @param floors the floors
	 * @param numFloors the num floors
	 */
	public CallManager(Floor[] floors, int numFloors) {
		this.floors = floors;
		NUM_FLOORS = numFloors;
		upCalls = new boolean[NUM_FLOORS];
		downCalls = new boolean[NUM_FLOORS];
		upCallPending = false;
		downCallPending = false;
		
		//TODO: Initialize any added fields here
	}
	
	/**
	 * Update call status. This is an optional method that could be used to compute
	 * the values of all up and down call fields statically once per tick (to be
	 * more efficient, could only update when there has been a change to the floor queues -
	 * either passengers being added or being removed. The alternative is to dynamically
	 * recalculate the values of specific fields when needed.
	 */
	void updateCallStatus() {
		upCallPending = false;
		downCallPending = false;
		for (int i = 0; i < floors.length; i++) {
			upCalls[i] = floors[i].passGoingInDir(UP);
			downCalls[i] = floors[i].passGoingInDir(DOWN);
			if (upCalls[i]) upCallPending = true;
			if (downCalls[i]) downCallPending = true;
		}
	}

	/**
	 * Prioritize passenger calls from STOP STATE
	 *
	 * @param floor the floor the elevator is on
	 * @return the passengers
	 */
	Passengers prioritizePassengerCalls(Elevator e) {
		updateCallStatus();
		int floor = e.getCurrFloor();
		Passengers currFloorPass = checkCurrentFloor(e);
		if (currFloorPass != null) return currFloorPass;
		int numUpCalls = 0;
		for (int i = 0; i < floors.length; i++)
			if (upCalls[i]) numUpCalls++;
		int numDownCalls = 0;
		for (int i = 0; i < floors.length; i++)
			if (downCalls[i]) numDownCalls++;
		int highestDownCall = 0;
		for (int i = floors.length - 1; i >= 0; i--) {
			if (downCalls[i]) {
				highestDownCall = i;
				break;
			}
		}
		int lowestUpCall = floors.length - 1;
		for (int i = 0; i < floors.length; i++) {
			if (upCalls[i]) {
				lowestUpCall = i;
				break;
			}
		}
		if (numUpCalls > numDownCalls) {
			return floors[lowestUpCall].peekFloorQueue(UP);
		} else if (numUpCalls < numDownCalls) {
			return floors[highestDownCall].peekFloorQueue(DOWN);
		} else {
			if (Math.abs(lowestUpCall - floor) <= Math.abs(highestDownCall - floor)) {
				return floors[lowestUpCall].peekFloorQueue(UP);
			} else {
				return floors[highestDownCall].peekFloorQueue(DOWN);
			}
		}
	}
	
	/**
	 * Call prioritization and direction switching if the call is on the current floor
	 * 
	 * @param e elevator
	 * @return priority passenger group on this floor, or null if none
	 */
	Passengers checkCurrentFloor(Elevator e) {
		int floor = e.getCurrFloor();
		if (upCalls[floor] && !downCalls[floor]) {
			e.setDirection(UP);
			return floors[floor].peekFloorQueue(UP);
		} else if (downCalls[floor] && !upCalls[floor]) {
			e.setDirection(DOWN);
			return floors[floor].peekFloorQueue(DOWN);
		} else if (upCalls[floor] && downCalls[floor]){
			int downCallsBelowElevator = 0;
			int upCallsAboveElevator = 0;
			for (int i = 0; i < floor; i++)
				if (downCalls[i]) downCallsBelowElevator++;
			for (int i = floor + 1; i < floors.length; i++)
				if (upCalls[i]) upCallsAboveElevator++;
			if (upCallsAboveElevator >= downCallsBelowElevator) {
				e.setDirection(UP);
				return floors[floor].peekFloorQueue(UP);
			} else {
				e.setDirection(DOWN);
				return floors[floor].peekFloorQueue(DOWN);
			}
		}
		return null;
	}

	/**
	 * Decide whether or not to change directions based on calls and elevator destinations
	 * 
	 * @param e the elevator object
	 * @return whether or not to change directions
	 */
	boolean changeDirection(Elevator e) {
		updateCallStatus();
		int currFloor = e.getCurrFloor();
		int currDir = e.getDirection();
		if (currDir == UP) {
			if (currFloor == floors.length - 1) return true;
			if (e.getPassengers() == 0) {
				for (int i = currFloor + 1; i < floors.length; i++)
					if (callOnFloor(i)) return false;
				if (callOnFloor(currFloor))
					if (upCalls[currFloor]) return false;
				return true;
			} else {
				for (Passengers p : e.getAllPassengers())
					if (p.getDestFloor() > currFloor) return false;
			}
		} else {
			if (currFloor == 0) return true;
			if (e.getPassengers() == 0) {
				for (int i = 0; i < currFloor; i++) {
					if (callOnFloor(i))
						return false;
				}
				if (callOnFloor(currFloor))
					if (downCalls[currFloor]) return false;
				return true;
			} else {
				for (Passengers p : e.getAllPassengers())
					if (p.getDestFloor() < currFloor) return false;
				return true;
			}
		}
		return false;
	}
	
	/**
	 * Determines whether or not to change the direction of the elevator after the offload case
	 * 
	 * @param e elevator
	 * @return whether or not direction should change
	 */
	boolean changeDirectionAfterOffload(Elevator e) {
		updateCallStatus();
		if (e.getPassengers() == 0) {
			if (e.getDirection() == UP) {
				for (int i = e.getCurrFloor() + 1; i < floors.length; i++)
					if (callOnFloor(i)) return false;
				if (callOnFloor(e.getCurrFloor(), UP)) return false;
			} else {
				for (int i = 0; i < e.getCurrFloor(); i++)
					if (callOnFloor(i)) return false;
				if (callOnFloor(e.getCurrFloor(), DOWN)) return false;
			}
			if (callOnFloor(e.getCurrFloor(), e.getDirection() * -1)) return true;
		}
		return false;
	}
	
	/**
	 * Determines whether or not there is a call in a given direction from a certain floor
	 * 
	 * @param direction direction of call search
	 * @param currFloor floor to start search from
	 * @return whether or not there is a call in the direction of the elevator
	 */
	boolean callInDirection(int direction, int currFloor) {
		if (direction == UP) {
			for (int i = currFloor + 1; i < floors.length; i++) {
				if (callOnFloor(i, direction)) return true;
			}
		} else {
			for (int i = 0; i < currFloor; i++) {
				if (callOnFloor(i, direction)) return true;
			}
		}
		
		return false;
	}

	/**
	 * Whether or not there is a call pending
	 * 
	 * @return if there is a call pending on some floor
	 */
	boolean callPending() {
		updateCallStatus();
		return upCallPending || downCallPending;
	}
	
	/**
	 * Whether or not there is a call on a floor
	 * 
	 * @param floor floor to scan
	 * @return up or down call on given floor
	 */
	boolean callOnFloor(int floor) {
		updateCallStatus();
		return upCalls[floor] || downCalls[floor];
	}
	
	/**
	 * Whether or not there is a call in a certain direction on a floor
	 * 
	 * @param floor floor to scan
	 * @param elevatorDirection direction to be checked
	 * @return if there is a call on the given floor in the given direction
	 */
	boolean callOnFloor(int floor, int elevatorDirection) {
		updateCallStatus();
		return (elevatorDirection == UP)? upCalls[floor] : downCalls[floor];
	}
	
	//TODO: might wanna move this into the building
	boolean callerIsPolite(int floor, int elevatorDirection) {
		updateCallStatus();
		if (floors[floor].peekFloorQueue(elevatorDirection).isPolite()) return true;
		floors[floor].peekFloorQueue(elevatorDirection).setPolite(true);
		return false;
	}
	
	/**
	 * Finds the lowest up call
	 * @return the lowest up call
	 */
	int getLowestUpCall() {
		for (int i = 0; i < floors.length; i++) {
			if (upCalls[i]) return i;
		}
		return floors.length - 1;
	}
	
	/**
	 * Finds the highest down call
	 * @return highest down call
	 */
	int getHighestDownCall() {
		for (int i = floors.length - 1; i >= 0; i--) {
			if (downCalls[i]) return i;
		}
		
		return 0;
	}
}
