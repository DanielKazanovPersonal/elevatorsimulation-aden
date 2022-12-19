package building;
import java.io.BufferedWriter;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.ListIterator;
import java.util.logging.FileHandler;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.logging.SimpleFormatter;

import myfileio.MyFileIO;
import genericqueue.GenericQueue;

// TODO: Auto-generated Javadoc
/**
 * The Class Building.
 */
// TODO: Auto-generated Javadoc
public class Building {
	
	private final static int UP = 1;
	private final static int DOWN = -1;
	/** The Constant LOGGER. */
	private final static Logger LOGGER = Logger.getLogger(Building.class.getName());
	/**  The fh - used by LOGGER to write the log messages to a file. */
	private FileHandler fh;
	/**  The fio for writing necessary files for data analysis. */
	private MyFileIO fio;
	/**  File that will receive the information for data analysis. */
	private File passDataFile;
	/**  passSuccess holds all Passengers who arrived at their destination floor. */
	private ArrayList<Passengers> passSuccess;
	/**  gaveUp holds all Passengers who gave up and did not use the elevator. */
	private ArrayList<Passengers> gaveUp;
	/**  The number of floors - must be initialized in constructor. */
	private final int NUM_FLOORS;
	/**  The size of the up/down queues on each floor. */
	private final int FLOOR_QSIZE = 10;	
	/** passQ holds the time-ordered queue of Passengers, initialized at the start of the simulation. At the end of the simulation, the queue will be empty. */
	private GenericQueue<Passengers> passQ;
	/**  The size of the queue to store Passengers at the start of the simulation. */
	private final int PASSENGERS_QSIZE = 1000;	
	/**  The number of elevators - must be initialized in constructor. */
	private final int NUM_ELEVATORS;
	/** The floors. */
	public Floor[] floors;
	/** The elevators. */
	private Elevator[] elevators;
	/**  The Call Manager - it tracks calls for the elevator, analyzes them to answer questions and prioritize calls. */
	private CallManager callMgr;
	// Add any fields that you think you might need here...
	private static final String ELEVATOR_CONFIG_PATH = "ElevatorSimConfig.csv";
	
	/**
	 * Instantiates a new building.
	 *
	 * @param numFloors the num floors
	 * @param numElevators the num elevators
	 * @param logfile the logfile
	 */
	public Building(int numFloors, int numElevators,String logfile) {
		NUM_FLOORS = numFloors;
		NUM_ELEVATORS = numElevators;
		passQ = new GenericQueue<Passengers>(PASSENGERS_QSIZE);
		passSuccess = new ArrayList<Passengers>();
		gaveUp = new ArrayList<Passengers>();
		Passengers.resetStaticID();		
		initializeBuildingLogger(logfile);
		// passDataFile is where you will write all the results for those passengers who successfully
		// arrived at their destination and those who gave up...
		fio = new MyFileIO();
		passDataFile = fio.getFileHandle(logfile.replaceAll(".log","PassData.csv"));
		
		// create the floors, call manager and the elevator arrays
		// note that YOU will need to create and config each specific elevator...
		floors = new Floor[NUM_FLOORS];
		for (int i = 0; i < NUM_FLOORS; i++) {
			floors[i]= new Floor(FLOOR_QSIZE); 
		}
		callMgr = new CallManager(floors,NUM_FLOORS);
		elevators = new Elevator[NUM_ELEVATORS];

	}

	/**
	 * Called by the controller to retrieve data about passengers in the floor queues
	 * Author: RT
	 * Reviewer: BX, DK
	 * 
	 * @return an array of arraylists, where each arraylist is a list of a given passenger attribute
	 */
	public ArrayList<Integer>[] getFloorQueueData() {
		ArrayList<Integer>[] data = new ArrayList[4];
		for (int i = 0; i < 4; i++)
			data[i] = new ArrayList<Integer>();
		for (int i = 0; i < floors.length; i++) {
			ArrayList<Passengers> passengers = floors[i].getAllPassengers();
			for (int j = 0; j < passengers.size(); j++) {
				Passengers p = passengers.get(j);
				data[0].add(p.getNumPass());
				data[1].add(p.getOnFloor());
				data[2].add(p.getDestFloor());
				data[3].add(p.isPolite()? 1 : 0);				
			}
		}
		
		return data;
	}

	
	/**
	 * Gets the elevator state.
	 *
	 * @return the elevator state
	 */
	public int getElevatorState() {
		return(elevators[0].getCurrState());
	}

	/**
	 * Gets the elevator curr floor.
	 *
	 * @return the elevator curr floor
	 */
	public int getElevatorCurrFloor() {
		return(elevators[0].getCurrFloor());
	}

	/**
	 * Gets the elevator direction.
	 *
	 * @return the elevator direction
	 */
	public int getElevatorDirection() {
		return(elevators[0].getDirection());
	}
	
	public int getElevatorTimeInState() {
		return elevators[0].getTimeInState();
	}
	
	public int getPassengersInElevator() {
		return elevators[0].getPassengers();
	}

	/**
	 * Configures the elevators with the passed in parameters
	 * 
	 * @param numFloors the number of floors in the building
	 * @param capacity the maximum number of passengers in an elevator
	 * @param floorTicks the number of ticks to move between floors
	 * @param doorTicks the number of ticks to open/close the door completely
	 * @param passPerTick maximum number of passengers that can board/offload in a tick
	 */
	public void configElevators(int numFloors,int capacity, int floorTicks, int doorTicks, int passPerTick) {
		for (int i = 0; i < elevators.length; i++) {
			elevators[i] = new Elevator(numFloors, capacity, floorTicks, doorTicks, passPerTick);
		}
	}
	
	/**
	 * Called before each update tick to move passengers from the main queue into the floor queues
	 * Author: RT
	 * Reviewer: BX, DK
	 * 
	 * @param timeSinceSimStart time since the start of the simulation
	 */
	public void updateFloorQueues(int timeSinceSimStart) {
		if (!passQ.isEmpty()) {
			while (!passQ.isEmpty() && passQ.peek().getTime() == timeSinceSimStart) {
				Passengers p = passQ.poll();
				logCalls(timeSinceSimStart, p.getNumPass(), p.getOnFloor(), p.getDirection(), p.getId());
				floors[p.getOnFloor()].addPassenger(p, p.getDirection());
			}
		}
	}


	/**
	 * Whether or not the elevator's state changed from the previous tick
	 * Author: RT
	 * Reviewer: BX, DK
	 * 
	 * @param e elevator to check
	 * @return whether or not the state has changed
	 */
	private boolean elevatorStateChanged(Elevator e) {
		return e.getCurrState() != e.getPrevState();
	}
	
	/**
	 * Calculates next state after stop state
	 * Author: RT
	 * Reviewer: BX, DK
	 * 
	 * @param time time since simulation started
	 * @param elevator elevator to modify
	 * @return the next state
	 */
	private int currStateStop(int time, Elevator elevator) {
		if (!callMgr.callPending()) {
			return Elevator.STOP;
		} else {
			Passengers priorityGrp = callMgr.prioritizePassengerCalls(elevator.getCurrFloor());
			if (priorityGrp.getOnFloor() == elevator.getCurrFloor()) {
				elevator.setDirection(priorityGrp.getDirection());
				return Elevator.OPENDR;
			}
			elevator.setPostMoveToFloorDir(priorityGrp.getDestFloor() - priorityGrp.getOnFloor() > 0? UP : DOWN);
			elevator.setStartFloor(elevator.getCurrFloor());
			elevator.setTargetFloor(priorityGrp.getOnFloor());
			elevator.setDirection((priorityGrp.getOnFloor() - elevator.getCurrFloor()) / Math.abs(priorityGrp.getOnFloor() - elevator.getCurrFloor()));
			
			return Elevator.MVTOFLR;
		}
	}
	
	/**
	 * Calculates next state after mvtoflr state
	 * Author: RT
	 * Reviewer: BX, DK
	 * 
	 * @param time time since simulation started
	 * @param elevator elevator to modify
	 * @return the next state
	 */
	private int currStateMvToFlr(int time, Elevator elevator) {
		int startFloor = elevator.getStartFloor();
		int endFloor = elevator.getTargetFloor();
		int ticksPerFloor = elevator.getTicksPerFloor();

		elevator.incrementTicks();
		int timeInState = elevator.getTimeInState();
		if ((double) timeInState / ticksPerFloor == Math.abs(endFloor - startFloor)) {
			elevator.setDirection(elevator.getPostMoveToFloorDir());
			elevator.updateCurrFloor(endFloor);
			return Elevator.OPENDR;
		} else {
			if (timeInState % ticksPerFloor == 0) {
				int floor = elevator.getCurrFloor();
				int dir = elevator.getDirection();
				elevator.updateCurrFloor(floor + dir);
				logElevatorStateChanged(time + 1, elevator.getPrevState(), elevator.getCurrState(), floor, floor + dir);

			}
			return Elevator.MVTOFLR;
		}
	}
	
	/**
	 * Calculates next state after opendr state
	 * Author: RT
	 * Reviewer: BX, DK
	 * 
	 * @param time time since simulation started
	 * @param elevator elevator to modify
	 * @return the next state
	 */
	private int currStateOpenDr(int time, Elevator elevator) {
		int doorState = elevator.getDoorState();
		int maxDoorTicks = elevator.getTicksDoorOpenClose();
		
		elevator.updateCurrFloor(elevator.getCurrFloor());		
		elevator.openDoor();
		
		if (doorState + 1 < maxDoorTicks) {
			return Elevator.OPENDR;
		} else if (elevator.getPassWithDestFloor(elevator.getCurrFloor()).size() == 0){
			return Elevator.BOARD;
		} else {
			return Elevator.OFFLD;
		}
	}
	
	/**
	 * Calculates next state after offld state
	 * Author: RT
	 * Reviewer: BX, DK
	 * 
	 * @param time time since simulation started
	 * @param elevator elevator to modify
	 * @return the next state
	 */
	private int currStateOffLd(int time, Elevator elevator) {
		elevator.incrementTicks();

		int floor = elevator.getCurrFloor();
		
		if (elevator.getTimeInState() == 1) {
			ArrayList<Passengers> offloaded = elevator.getPassWithDestFloor(floor);
			int passengerCnt = 0;
			for (Passengers p : offloaded)
				passengerCnt += p.getNumPass();
			for (int i = 0; i < offloaded.size(); i++)
				logArrival(time, offloaded.get(i).getNumPass(), floor, offloaded.get(i).getId());
			elevator.clearPassengers(floor);
			elevator.setOffloadedPassengers(passengerCnt);
		}
		
		int offloaded = elevator.getOffloadedPassengers();
		if (Math.ceil((double)offloaded / elevator.getPassPerTick()) <= elevator.getTimeInState()) {
			if (callMgr.changeDirectionAfterOffload(elevator.getDirection(), floor, elevator.getPassengers())) elevator.flipDirections();			
			return callMgr.callOnFloor(floor, elevator.getDirection())? Elevator.BOARD : Elevator.CLOSEDR;
		} else {
			return Elevator.OFFLD;
		}
	}
	
	/**
	 * Calculates next state after board state
	 * Author: RT
	 * Reviewer: BX, DK
	 * 
	 * @param time time since simulation started
	 * @param elevator elevator to modify
	 * @return the next state
	 */
	private int currStateBoard(int time, Elevator elevator) {
		if (elevator.getTimeInState() == 0) {
			elevator.setBoardedPassengers(0);
			elevator.setCapacityFlag(false);
		}
		int passengersPerTick = elevator.getPassPerTick();
		
		elevator.incrementTicks();
		int timeInState = elevator.getTimeInState();
		if (!elevator.atCapacityLastTick())
			attemptPassengerBoard(elevator, time);
		if (Math.ceil((double) elevator.getBoardedPassengers() / passengersPerTick) <= timeInState) {
			return Elevator.CLOSEDR;
		} else {
			return Elevator.BOARD;
		}
	}
	
	/**
	 * Attempt to board the next passenger on the floor
	 * Author: RT
	 * Reviewer: BX, DK
	 * 
	 * @param e elevator
	 * @param time time since sim started
	 */
	private void attemptPassengerBoard(Elevator e, int time) {
		int floor = e.getCurrFloor();
		int dir = e.getDirection();
		while (floors[floor].passGoingInDir(dir)) {
			int boardedPassengers = e.getBoardedPassengers();
			Passengers frontGrp = floors[floor].peekFloorQueue(dir);
			if (frontGrp.getWaitTime() + frontGrp.getTime() < time) {
				logGiveUp(time, frontGrp.getNumPass(), frontGrp.getOnFloor(), frontGrp.getDirection(), frontGrp.getId());
				floors[floor].removeFirstPassInQ(dir);
			} else if (e.getCapacity() >= e.getPassengers() + frontGrp.getNumPass()) {
				e.setBoardedPassengers(boardedPassengers + frontGrp.getNumPass());
				floors[floor].removeFirstPassInQ(dir);
				e.addPassengers(frontGrp);
				logBoard(time, frontGrp.getNumPass(), frontGrp.getOnFloor(), frontGrp.getDirection(), frontGrp.getId());
			} else {
				callMgr.callerIsPolite(floor, dir); //sets skipped passengers to polite to avoid call dupes
				logSkip(time, frontGrp.getNumPass(), frontGrp.getOnFloor(), frontGrp.getDirection(), frontGrp.getId());
				e.setCapacityFlag(true);
				break;
			}
		}
	}
	
	/**
	 * Calculates next state after closedr state
	 * Author: RT
	 * Reviewer: BX, DK
	 * 
	 * @param time time since simulation started
	 * @param elevator elevator to modify
	 * @return the next state
	 */
	private int currStateCloseDr(int time, Elevator elevator) {
		int doorState = elevator.getDoorState();
		int currFloor = elevator.getCurrFloor();
		int dir = elevator.getDirection();

		elevator.closeDoor();

		if (callMgr.callOnFloor(currFloor, dir) && !callMgr.callerIsPolite(currFloor, dir))
			return Elevator.OPENDR;
		if (doorState - 1 > 0) {
			if (callMgr.changeDirection(currFloor, dir, elevator.getAllPassengers())) elevator.flipDirections();
			return Elevator.CLOSEDR;
		} else if (elevator.getPassengers() == 0) {
			if (!callMgr.callPending()) {
				return Elevator.STOP;
			} else if (callMgr.callOnFloor(currFloor, dir) && !callMgr.callInDirection(dir, currFloor)) {
				return Elevator.OPENDR;
			} else {
				return Elevator.MV1FLR;
			}
		} else {
			if (callMgr.changeDirection(currFloor, dir, elevator.getAllPassengers())) elevator.flipDirections();
			return Elevator.MV1FLR;
		}
	}
	
	/**
	 * Calculates next state after mv1flr state
	 * Author: RT
	 * Reviewer: BX, DK
	 * 
	 * @param time time since simulation started
	 * @param elevator elevator to modify
	 * @return the next state
	 */
	private int currStateMv1Flr(int time, Elevator elevator) {
		elevator.incrementTicks();
		int dir = elevator.getDirection();
		int floor = elevator.getCurrFloor();
		if (elevator.getTimeInState() % elevator.getTicksPerFloor() == 0) {
			elevator.updateCurrFloor(floor + dir);
			floor += dir;			
			ArrayList<Passengers> passengers = elevator.getAllPassengers();
			for (Passengers p : passengers) {
				if (p.getDestFloor() == floor)
					return Elevator.OPENDR;
			}
			if (callMgr.callOnFloor(floor)) {
				if (elevator.getPassengers() == 0) {
					if ((dir == UP && callMgr.getHighestDownCall() == floor) || (dir == DOWN && callMgr.getLowestUpCall() == floor)) {
						if (callMgr.changeDirection(floor, dir, passengers)) 
							elevator.flipDirections();
						return Elevator.OPENDR;
					}
				}
				if (callMgr.callOnFloor(floor, dir)) 
					return Elevator.OPENDR;
			}
			logElevatorStateChanged(time + 1, elevator.getPrevState(), elevator.getCurrState(), floor - dir, floor);
		}
		if (callMgr.changeDirection(floor, dir, elevator.getAllPassengers()))
			elevator.flipDirections();
		return Elevator.MV1FLR;
	}
	
	/**
	 * Whether or not the simulation should end
	 * Author: RT
	 * Reviewer: BX, DK
	 * 
	 * @param time time since sim started
	 * @return whether or not the simulation should end
	 */
	public boolean endSim(int time) {
		for (Floor f : floors)
			if (!f.isEmpty())
				return false;
		for (Elevator e : elevators)
			if (e.getCurrState() != Elevator.STOP)
				return false;
		if (!passQ.isEmpty()) return false;
		updateElevator(time);
		return true;
	}
	
	/**
	 * Adds a passenger to the building's queue.
	 * Author: RT
	 * Reviewer: BX, DK
	 *
	 * @param time the time
	 * @param numPass the num pass
	 * @param fromFloor the from floor
	 * @param toFloor the to floor
	 * @param polite the polite
	 * @param wait the wait
	 */
	public void addPassengersToQueue(int time, int numPass, int fromFloor, int toFloor, boolean polite, int wait) {
		passQ.add(new Passengers(time, numPass, fromFloor, toFloor, polite, wait));
	}
	
	// DO NOT CHANGE ANYTHING BELOW THIS LINE:
	/**
	 * Initialize building logger. Sets formating, file to log to, and
	 * turns the logger OFF by default
	 *
	 * @param logfile the file to log information to
	 */
	void initializeBuildingLogger(String logfile) {
		System.setProperty("java.util.logging.SimpleFormatter.format","%4$-7s %5$s%n");
		LOGGER.setLevel(Level.OFF);
		try {
			fh = new FileHandler(logfile);
			LOGGER.addHandler(fh);
			SimpleFormatter formatter = new SimpleFormatter();
			fh.setFormatter(formatter);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}	
	
	/**
	 * Update elevator - this is called AFTER time has been incremented.
	 * -  Logs any state changes, if the have occurred,
	 * -  Calls appropriate method based upon currState to perform
	 *    any actions and calculate next state...
	 *
	 * @param time the time
	 */
	// YOU WILL NEED TO CODE ANY MISSING METHODS IN THE APPROPRIATE CLASSES...
	public void updateElevator(int time) {
		for (Elevator lift: elevators) {
			if (elevatorStateChanged(lift))
				logElevatorStateChanged(time,lift.getPrevState(),lift.getCurrState(),lift.getPrevFloor(),lift.getCurrFloor());
			switch (lift.getCurrState()) {
				case Elevator.STOP: lift.updateCurrState(currStateStop(time,lift)); break;
				case Elevator.MVTOFLR: lift.updateCurrState(currStateMvToFlr(time,lift)); break;
				case Elevator.OPENDR: lift.updateCurrState(currStateOpenDr(time,lift)); break;
				case Elevator.OFFLD: lift.updateCurrState(currStateOffLd(time,lift)); break;
				case Elevator.BOARD: lift.updateCurrState(currStateBoard(time,lift)); break;
				case Elevator.CLOSEDR: lift.updateCurrState(currStateCloseDr(time,lift)); break;
				case Elevator.MV1FLR: lift.updateCurrState(currStateMv1Flr(time,lift)); break;
			}
		}
	}

	/**
	 * Process passenger data. Do NOT change this - it simply dumps the 
	 * collected passenger data for successful arrivals and give ups. These are
	 * assumed to be ArrayLists...
	 */
	public void processPassengerData() {
		
		try {
			BufferedWriter out = fio.openBufferedWriter(passDataFile);
			out.write("ID,Number,From,To,WaitToBoard,TotalTime\n");
			for (Passengers p : passSuccess) {
				String str = p.getId()+","+p.getNumPass()+","+(p.getOnFloor()+1)+","+(p.getDestFloor()+1)+","+
				             (p.getBoardTime() - p.getTime())+","+(p.getTimeArrived() - p.getTime())+"\n";
				out.write(str);
			}
			for (Passengers p : gaveUp) {
				String str = p.getId()+","+p.getNumPass()+","+(p.getOnFloor()+1)+","+(p.getDestFloor()+1)+","+
				             p.getWaitTime()+",-1\n";
				out.write(str);
			}
			fio.closeFile(out);
		} 
		catch (IOException e) {
			e.printStackTrace();
		}
	}

	/**
	 * Enable logging. Prints the initial configuration message.
	 * For testing, logging must be enabled BEFORE the run starts.
	 */
	public void enableLogging() {
		LOGGER.setLevel(Level.INFO);
		for (Elevator el:elevators)
			logElevatorConfig(el.getCapacity(),el.getTicksPerFloor(), el.getTicksDoorOpenClose(), el.getPassPerTick(), el.getCurrState(), el.getCurrFloor());
	}
	
	/**
	 * Close logs, and pause the timeline in the GUI.
	 *
	 * @param time the time
	 */
	public void closeLogs(int time) {
		if (LOGGER.getLevel() == Level.INFO) {
			logEndSimulation(time);
			fh.flush();
			fh.close();
		}
	}
	
	/**
	 * Prints the state.
	 *
	 * @param state the state
	 * @return the string
	 */
	private String printState(int state) {
		String str = "";
		
		switch (state) {
			case Elevator.STOP: 		str =  "STOP   "; break;
			case Elevator.MVTOFLR: 		str =  "MVTOFLR"; break;
			case Elevator.OPENDR:   	str =  "OPENDR "; break;
			case Elevator.CLOSEDR:		str =  "CLOSEDR"; break;
			case Elevator.BOARD:		str =  "BOARD  "; break;
			case Elevator.OFFLD:		str =  "OFFLD  "; break;
			case Elevator.MV1FLR:		str =  "MV1FLR "; break;
			default:					str =  "UNDEF  "; break;
		}
		return(str);
	}
	
	/**
	 * Dump passQ contents. Debug hook to view the contents of the passenger queue...
	 */
	public void dumpPassQ() {
		ListIterator<Passengers> passengers = passQ.getListIterator();
		if (passengers != null) {
			System.out.println("Passengers Queue:");
			while (passengers.hasNext()) {
				Passengers p = passengers.next();
				System.out.println(p);
			}
		}
	}


	/**
	 * Log elevator config.
	 *
	 * @param capacity the capacity
	 * @param ticksPerFloor the ticks per floor
	 * @param ticksDoorOpenClose the ticks door open close
	 * @param passPerTick the pass per tick
	 * @param state the state
	 * @param floor the floor
	 */
	private void logElevatorConfig(int capacity, int ticksPerFloor, int ticksDoorOpenClose, int passPerTick, int state, int floor) {
		LOGGER.info("CONFIG:   Capacity="+capacity+"   Ticks-Floor="+ticksPerFloor+"   Ticks-Door="+ticksDoorOpenClose+
				    "   Ticks-Passengers="+passPerTick+"   CurrState=" + (printState(state))+"   CurrFloor="+(floor+1));
	}
		
	/**
	 * Log elevator state changed.
	 *
	 * @param time the time
	 * @param prevState the prev state
	 * @param currState the curr state
	 * @param prevFloor the prev floor
	 * @param currFloor the curr floor
	 */
	private void logElevatorStateChanged(int time, int prevState, int currState, int prevFloor, int currFloor) {
		LOGGER.info("Time="+time+"   Prev State: " + printState(prevState) + "   Curr State: "+printState(currState)
		+"   PrevFloor: "+(prevFloor+1) + "   CurrFloor: " + (currFloor+1));
	}
	
	/**
	 * Log arrival.
	 *
	 * @param time the time
	 * @param numPass the num pass
	 * @param floor the floor
	 * @param id the id
	 */
	private void logArrival(int time, int numPass, int floor,int id) {
		LOGGER.info("Time="+time+"   Arrived="+numPass+" Floor="+ (floor+1)
		+" passID=" + id);						
	}
	
	/**
	 * Log calls.
	 *
	 * @param time the time
	 * @param numPass the num pass
	 * @param floor the floor
	 * @param dir the dir
	 * @param id the id
	 */
	private void logCalls(int time, int numPass, int floor, int dir, int id) {
		LOGGER.info("Time="+time+"   Called="+numPass+" Floor="+ (floor +1)
				+" Dir="+((dir>0)?"Up":"Down")+"   passID=" + id);
	}
	
	/**
	 * Log give up.
	 *
	 * @param time the time
	 * @param numPass the num pass
	 * @param floor the floor
	 * @param dir the dir
	 * @param id the id
	 */
	private void logGiveUp(int time, int numPass, int floor, int dir, int id) {
		LOGGER.info("Time="+time+"   GaveUp="+numPass+" Floor="+ (floor+1) 
				+" Dir="+((dir>0)?"Up":"Down")+"   passID=" + id);				
	}

	/**
	 * Log skip.
	 *
	 * @param time the time
	 * @param numPass the num pass
	 * @param floor the floor
	 * @param dir the dir
	 * @param id the id
	 */
	private void logSkip(int time, int numPass, int floor, int dir, int id) {
		LOGGER.info("Time="+time+"   Skip="+numPass+" Floor="+ (floor+1) 
				+" Dir="+((dir>0)?"Up":"Down")+"   passID=" + id);				
	}
	
	/**
	 * Log board.
	 *
	 * @param time the time
	 * @param numPass the num pass
	 * @param floor the floor
	 * @param dir the dir
	 * @param id the id
	 */
	private void logBoard(int time, int numPass, int floor, int dir, int id) {
		LOGGER.info("Time="+time+"   Board="+numPass+" Floor="+ (floor+1) 
				+" Dir="+((dir>0)?"Up":"Down")+"   passID=" + id);				
	}
	
	/**
	 * Log end simulation.
	 *
	 * @param time the time
	 */
	private void logEndSimulation(int time) {
		LOGGER.info("Time="+time+"   Detected End of Simulation");
	}
}
