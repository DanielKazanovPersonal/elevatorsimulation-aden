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
//		System.out.println(Arrays.toString(upCalls));
//		System.out.println(Arrays.toString(downCalls));
		
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
		
//		System.out.println(numUpCalls + ", " + numDownCalls + ", " + lowestUpCall + ", " + highestDownCall + ", " + e.getCurrFloor());
		
		if (numUpCalls > numDownCalls) {
//			System.out.println("1");
			return floors[lowestUpCall].peekFloorQueue(UP);
		} else if (numUpCalls < numDownCalls) {
//			System.out.println("2");
			return floors[highestDownCall].peekFloorQueue(DOWN);
		} else {
			if (Math.abs(lowestUpCall - floor) <= Math.abs(highestDownCall - floor)) {
//				System.out.println("3");
				return floors[lowestUpCall].peekFloorQueue(UP);
			} else {
//				System.out.println("4");
				return floors[highestDownCall].peekFloorQueue(DOWN);
			}
		}
	}
	
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

	//TODO: Write any additional methods here. Things that you might consider:
	//      1. pending calls - are there any? only up? only down?
	//      2. is there a call on the current floor in the current direction
	//      3. How many up calls are pending? how many down calls are pending? 
	//      4. How many calls are pending in the direction that the elevator is going
	//      5. Should the elevator change direction?
	//
	//      These are an example - you may find you don't need some of these, or you may need more...
	
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
//		System.out.println("initial dir " + currDir);
		
		if (currDir == UP) {
//			System.out.print("going up, ");
			if (currFloor == floors.length - 1) return true;

			if (e.getPassengers() == 0) {
				for (int i = currFloor + 1; i < floors.length; i++) {
					if (callOnFloor(i)) {
//						System.out.println("calls above");
						return false;
					}
				}
				
				if (callOnFloor(currFloor)) {
					if (upCalls[currFloor]) return false;
				}
				
//				System.out.println("no calls above");
				
				return true;
			} else {
//				System.out.println("have passengers, ");
				for (Passengers p : e.getAllPassengers()) {
					if (p.getDestFloor() > currFloor) return false;
				}
//				return true;
			}
		} else {
//			System.out.print("going down, ");
			if (currFloor == 0) return true;

			if (e.getPassengers() == 0) {
				for (int i = 0; i < currFloor; i++) {
					if (callOnFloor(i)) {
//						System.out.println("call below");
						return false;
					}
				}
				
				if (callOnFloor(currFloor)) {
					if (downCalls[currFloor]) return false;
				}
				
//				System.out.println("no calls below");
				return true;
			} else {
//				System.out.println("have passengers, ");
				for (Passengers p : e.getAllPassengers()) {
					if (p.getDestFloor() < currFloor) return false;
				}
				return true;
			}
		}
		
		return false;
	}
	
	boolean changeDirectionAfterOffload(Elevator e) {
		updateCallStatus();
		//changing direction logic
		//if elevator is empty
		//	if no calls on this floor or floors in the direction of movement
		//	and call on this floor in the opposite direction

		if (e.getPassengers() == 0) {
			if (e.getDirection() == UP) {
				for (int i = e.getCurrFloor() + 1; i < floors.length; i++) {
					if (callOnFloor(i)) return false;
				}
				//if call on current floor in the direction of movement
				if (callOnFloor(e.getCurrFloor(), UP)) return false;
			} else {
				for (int i = 0; i < e.getCurrFloor(); i++) {
					if (callOnFloor(i)) return false;
				}
				//if call on current floor in the direction of movement
				if (callOnFloor(e.getCurrFloor(), DOWN)) return false;
			}
			
			//if you have reached here, then there are no calls in the current direction of the elevator
			//if there is a call on the current floor in the opposite direction
			if (callOnFloor(e.getCurrFloor(), e.getDirection() * -1)) return true;
		}
		
		return false;
	}
	
	//note -- excludes the current floor of the elevator
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
	
	//might not need this tbh
	boolean changeDirectionAfterCloseDoor(Elevator e) {
		//if no passengers
		
		//if no calls in floors in the current direction
		//and calls in floors in opposite direction
		
		return false;
	}
	
	boolean callPending() {
		updateCallStatus();
		return upCallPending || downCallPending;
	}
	
	boolean callOnFloor(int floor) {
		updateCallStatus();
		return upCalls[floor] || downCalls[floor];
	}
	
	boolean callOnFloor(int floor, int elevatorDirection) {
		updateCallStatus();
//		System.out.println(Arrays.toString(upCalls));
//		System.out.println(Arrays.toString(downCalls));
		return (elevatorDirection == UP)? upCalls[floor] : downCalls[floor];
	}
	
	boolean callerIsPolite(int floor, int elevatorDirection) {
		updateCallStatus();
		return floors[floor].peekFloorQueue((elevatorDirection == UP)? UP : DOWN).getPolite();
	}
}
