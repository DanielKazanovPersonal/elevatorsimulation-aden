import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;

import building.Building;
import myfileio.MyFileIO;

// TODO: Auto-generated Javadoc
/**
 * The Class ElevatorSimController.
 */
// TODO: Auto-generated Javadoc
public class ElevatorSimController {
	
	/**  Constant to specify the configuration file for the simulation. */
	private static final String SIM_CONFIG = "ElevatorSimConfig.csv";
	
	/**  Constant to make the Passenger queue contents visible after initialization. */
	private boolean PASSQ_DEBUG=false;
	
	/** The gui. */
	private ElevatorSimulation gui;
	
	/** The building. */
	private Building building;
	
	/** The fio. */
	private MyFileIO fio;

	/** The num floors. */
	private final int NUM_FLOORS;
	
	/** The num elevators. */
	private final int NUM_ELEVATORS;
	
	/** The num floors. */
	private int numFloors;
	
	/** The num elevators. */
	private int numElevators;
	
	/** The capacity. */
	private int capacity;
	
	/** The floor ticks. */
	private int floorTicks;
	
	/** The door ticks. */
	private int doorTicks;
	
	/** The pass per tick. */
	private int passPerTick;
	
	/** The testfile. */
	private String testfile;
	
	/** The logfile. */
	private String logfile;
	
	/** The step cnt. */
	private int stepCnt = 0;
	
	/** The end sim. */
	private boolean endSim = false;
	
	/** For information persistance across tick updates */
	private int elevatorStartFloor = 0;
		
	/**
	 * Instantiates a new elevator sim controller. 
	 * Reads the configuration file to configure the building and
	 * the elevator characteristics and also select the test
	 * to run. Reads the passenger data for the test to run to
	 * initialize the passenger queue in building...
	 *
	 * @param gui the gui
	 */
	public ElevatorSimController(ElevatorSimulation gui) {
		this.gui = gui;
		fio = new MyFileIO();
		// IMPORTANT: DO NOT CHANGE THE NEXT LINE!!! Update the config file itself
		// (ElevatorSimConfig.csv) to change the configuration or test being run.
		configSimulation(SIM_CONFIG);
		NUM_FLOORS = numFloors;
		NUM_ELEVATORS = numElevators;
		logfile = testfile.replaceAll(".csv", ".log");
		building = new Building(NUM_FLOORS,NUM_ELEVATORS,logfile);
		
		//TODO: YOU still need to configure the elevators in the building here....
		building.configElevators(numFloors, capacity, floorTicks, doorTicks, passPerTick);	
		initializePassengerData(testfile);
		

		
	}
	
	public ArrayList<Integer>[] getAllPassengerData() {
		return building.getFloorQueueData();
	}
	
	//TODO: Write methods to update the GUI display
	//      Needs to cover the Elevator state, Elevator passengers
	//      and queues for each floor, as well as the current time

	private int totalTicks = 0;

	public int getTotalTicks() {
		return totalTicks;
	}

	public int getCurrentFloor() {
		return 0; // HERE ------ IF WE DO ELEVATOR LIST IN BUILDING, THIS METHOD NEEDS TO TAKE IN WHICH ELEVATOR IS BEING USED
	}

	public String getTestName() {
		return ""; // NEED TO IMPLEMENT / ACTUALLY RETURN TEST
	}

	/**
	 * Config simulation. Reads the filename, and parses the
	 * parameters.
	 *
	 * @param filename the filename
	 */
	private void configSimulation(String filename) {
		File configFile = fio.getFileHandle(filename);
		try ( BufferedReader br = fio.openBufferedReader(configFile)) {
			String line;
			while ((line = br.readLine())!= null) {
				parseElevatorConfigData(line);
			}
			fio.closeFile(br);
		} catch (IOException e) { 
			System.err.println("Error in reading file: "+filename);
			e.printStackTrace();
		}
	}
	
	/**
	 * Parses the elevator simulation config file to configure the simulation:
	 * number of floors and elevators, the actual test file to run, and the
	 * elevator characteristics.
	 *
	 * @param line the line
	 * @throws IOException Signals that an I/O exception has occurred.
	 */
	private void parseElevatorConfigData(String line) throws IOException {
		String[] values = line.split(",");
		if (values[0].equals("numFloors")) {
			numFloors = Integer.parseInt(values[1]);
		} else if (values[0].equals("numElevators")) {
			numElevators = Integer.parseInt(values[1]);
		} else if (values[0].equals("passCSV")) {
			testfile = values[1];
		} else if (values[0].equals("capacity")) {
			capacity = Integer.parseInt(values[1]);
		} else if (values[0].equals("floorTicks")) {
			floorTicks = Integer.parseInt(values[1]);
		} else if (values[0].equals("doorTicks")) {
			doorTicks = Integer.parseInt(values[1]);
		} else if (values[0].equals("passPerTick")) {
			passPerTick = Integer.parseInt(values[1]);
		}
	}
	
	/**
	 * Initialize passenger data. Reads the supplied filename,
	 * and for each passenger group, identifies the pertinent information
	 * and adds it to the passengers queue in Building...
	 *
	 * @param filename the filename
	 */
	private void initializePassengerData(String filename) {
		boolean firstLine = true;
		File passInput = fio.getFileHandle(filename);
		try (BufferedReader br = fio.openBufferedReader(passInput)) {
			String line;
			while ((line = br.readLine())!= null) {
				if (firstLine) {
					firstLine = false;
					continue;
				}
				parsePassengerData(line);
			}
			fio.closeFile(br);
		} catch (IOException e) { 
			System.err.println("Error in reading file: "+filename);
			e.printStackTrace();
		}
		if (PASSQ_DEBUG) building.dumpPassQ();
	}	
	
	/**
	 * Parses the line of passenger data into tokens, and 
	 * passes those values to the building to be added to the
	 * passenger queue
	 *
	 * @param line the line of passenger input data
	 */
	private void parsePassengerData(String line) {
		int time=0, numPass=0,fromFloor=0, toFloor=0;
		boolean polite = true;
		int wait = 1000;
		String[] values = line.split(",");
		for (int i = 0; i < values.length; i++) {
			switch (i) {
				case 0 : time      = Integer.parseInt(values[i]); break;
				case 1 : numPass   = Integer.parseInt(values[i]); break;
				case 2 : fromFloor   = Integer.parseInt(values[i]); break;
				case 3 : toFloor  = Integer.parseInt(values[i]); break;
				case 5 : wait      = Integer.parseInt(values[i]); break;
				case 4 : polite = "TRUE".equalsIgnoreCase(values[i]); break;
			}
		}
		building.addPassengersToQueue(time,numPass,fromFloor,toFloor,polite,wait);	
	}
	
	/**
	 * Enable logging. A pass-through from the GUI to building
	 */
	public void enableLogging() {
		building.enableLogging();
	}
	
	// TODO: Write any other helper methods that you may need to access data from the building...

	public boolean isPASSQ_DEBUG() {
		return this.PASSQ_DEBUG;
	}

	public boolean getPASSQ_DEBUG() {
		return this.PASSQ_DEBUG;
	}

	public void setPASSQ_DEBUG(boolean PASSQ_DEBUG) {
		this.PASSQ_DEBUG = PASSQ_DEBUG;
	}

	public ElevatorSimulation getGui() {
		return this.gui;
	}

	public void setGui(ElevatorSimulation gui) {
		this.gui = gui;
	}
	public void setBuilding(Building building) {
		this.building = building;
	}

	public MyFileIO getFio() {
		return this.fio;
	}

	public void setFio(MyFileIO fio) {
		this.fio = fio;
	}

	public int getNUM_FLOORS() {
		return this.NUM_FLOORS;
	}


	public int getNUM_ELEVATORS() {
		return this.NUM_ELEVATORS;
	}


	public int getNumFloors() {
		return this.numFloors;
	}

	public void setNumFloors(int numFloors) {
		this.numFloors = numFloors;
	}

	public int getNumElevators() {
		return this.numElevators;
	}

	public void setNumElevators(int numElevators) {
		this.numElevators = numElevators;
	}

	public int getCapacity() {
		return this.capacity;
	}

	public void setCapacity(int capacity) {
		this.capacity = capacity;
	}

	public int getFloorTicks() {
		return this.floorTicks;
	}

	public void setFloorTicks(int floorTicks) {
		this.floorTicks = floorTicks;
	}

	public int getDoorTicks() {
		return this.doorTicks;
	}

	public void setDoorTicks(int doorTicks) {
		this.doorTicks = doorTicks;
	}

	public int getPassPerTick() {
		return this.passPerTick;
	}

	public void setPassPerTick(int passPerTick) {
		this.passPerTick = passPerTick;
	}

	public String getTestfile() {
		return this.testfile;
	}

	public void setTestfile(String testfile) {
		this.testfile = testfile;
	}

	public String getLogfile() {
		return this.logfile;
	}

	public void setLogfile(String logfile) {
		this.logfile = logfile;
	}

	public int getStepCnt() {
		return this.stepCnt;
	}

	public void setStepCnt(int stepCnt) {
		this.stepCnt = stepCnt;
	}

	public boolean isEndSim() {
		return this.endSim;
	}

	public boolean getEndSim() {
		return this.endSim;
	}

	public void setEndSim(boolean endSim) {
		this.endSim = endSim;
	}
	public void setTotalTicks(int totalTicks) {
		this.totalTicks = totalTicks;
	}
	
 	/**
	 * Step sim. See the comments below for the functionality you
	 * must implement......
	 */
	public void stepSim() {
 		// DO NOT MOVE THIS - YOU MUST INCREMENT TIME FIRST!
		stepCnt++;
		if (!building.endSim(stepCnt)) {
			building.updateFloorQueues(stepCnt);
			building.updateElevator(stepCnt);
		}

		// TODO: Write the rest of this method
		// If simulation is not completed (not all passengers have been processed
		// or elevator(s) are not all in STOP state), then
		// 		1) check for arrival of any new passengers
		// 		2) update the elevator
		// 		3) update the GUI
		//  else 
		//    	1) update the GUI
		//		2) close the logs
		//		3) process the passenger results
		//		4) send endSimulation to the GUI to stop ticks.
		
		if (gui == null) return;
		
		
//		if (gui.getPassengers() != 0) { // || CODE_CODE_CODE
			// check for arrival of new passengers
			if (building.getElevatorState() == 1 || building.getElevatorState() == 6) { // 1 is movetofloor and 6 is move1floor
				gui.elevatorMoveToFloor(elevatorStartFloor);
//				gui.passengersGroupSetup();
				return;
			} else {
				elevatorStartFloor = building.getElevatorCurrFloor();
			}
			
			if (building.getElevatorState() == 2) { // open door
				gui.elevatorOpenDoors();
			} else if (building.getElevatorState() == 5) { // close door
				gui.elevatorClosedDoors();                 }
//			} else if (building.getElevatorState() == 3) { // off load
//				gui.setPassengers(gui.getPassengers() - building.passGoingToFloorOnElevator());
//			} else if (building.getElevatorState() == 4) { // board state
//				gui.passengersGroupMove();
//			}
//		} else {
			gui.passengersGroupSetup();
			building.closeLogs(stepCnt);
			// process the passenger results
			gui.endSimulation();
		}
//	}
	
	public int getElevatorDirection() {
		return building.getElevatorDirection();
	}

	/**
	 * Gets the building. ONLY USED FOR JUNIT TESTING - YOUR GUI SHOULD NOT ACCESS THIS!.
	 *
	 * @return the building
	 */
	Building getBuilding() {
		return building;
	}
	
	int getTimeInState() {
		return building.getElevatorTimeInState();
	}

	int getElevatorState() {
		return building.getElevatorState();
	}
}
