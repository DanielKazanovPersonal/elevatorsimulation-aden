
import java.util.ArrayList;

import building.Elevator;
import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Line;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.scene.text.Text;
import javafx.stage.Stage;


public class ElevatorSimulation extends Application {
	/** Instantiate the GUI fields */
	private ElevatorSimController controller;
	private final int NUM_FLOORS;
	private final int NUM_ELEVATORS;
	private int currFloor;
	private int passengers;
	
	public int getPassengers() {
		return passengers;
	}
	
	private int time;

	/** Local copies of the states for tracking purposes */
	private final int STOP = Elevator.STOP;
	private final int MVTOFLR = Elevator.MVTOFLR;
	private final int OPENDR = Elevator.OPENDR;
	private final int OFFLD = Elevator.OFFLD;
	private final int BOARD = Elevator.BOARD;
	private final int CLOSEDR = Elevator.CLOSEDR;
	private final int MV1FLR = Elevator.MV1FLR;

	/** Daniel's created variables */
	private Pane pane;
	private final int PANE_WIDTH = 700;
	private final int PANE_HEIGHT = 700;
	
	private final int ELEVATOR_WIDTH;
	private final int ELEVATOR_HEIGHT;
	private int ELEVATOR_X_POSITION = (PANE_WIDTH / 7);
	private int ELEVATOR_Y_POSITION = (PANE_HEIGHT / 2);
	
	/**
	 * Instantiates a new elevator simulation.
	 */
	public ElevatorSimulation() {
		controller = new ElevatorSimController(this);	
		NUM_FLOORS = controller.getNumFloors();
		NUM_ELEVATORS = controller.getNumElevators();
		currFloor = controller.getCurrentFloor();
		
		ELEVATOR_WIDTH = (PANE_WIDTH / 10);
		ELEVATOR_HEIGHT = (PANE_HEIGHT / NUM_FLOORS);
	}
	
	
	// TODO: Write this method
	public void endSimulation() {
		
	}
	
	// TODO: Write this method
	public void updateGUI() {
		
	}
	
	// TODO: Write this method
	public void updateElevator() {
		
	}

	/**
	 * Start.
	 *
	 * @param primaryStage the primary stage
	 * @throws Exception the exception
	 */
	@Override
	public void start(Stage primaryStage) throws Exception {
		// You need to design the GUI. Note that the test name should
		// appear in the Title of the window!!
		primaryStage.setTitle("Elevator Simulation - "+ controller.getTestName());
		primaryStage.show();
		primaryStage.setResizable(false);

		//TODO: Complete your GUI, including adding any helper methods.
		//      Meet the 30 line limit...
		mainSetup(primaryStage);
	}
	
	public void mainSetup(Stage primaryStage) {
		BorderPane borderPane = new BorderPane();
		pane = new Pane();
		HBox hBox = new HBox(3);
		Scene scene = new Scene(borderPane, PANE_WIDTH, PANE_HEIGHT);
		
	    borderPane.setCenter(pane);
		borderPane.setBottom(hBox);
		primaryStage.setScene(scene);
		
		buttonSetup(hBox);
		floorSetup();
		elevatorSetup();
		
		// TODO: change line below
//		elevatorMoveToFloor(pane, 6, body);
	}
	
	public void buttonSetup(HBox hBox) {
		Font font = new Font(25);
		
		Button run = new Button("Run");
		run.setFont(font);
		run.setPrefWidth(PANE_WIDTH / 3);
		run.setPrefHeight(PANE_HEIGHT / 9);
		run.setOnAction(e -> controller.stepSim());
		
		TextField step = new TextField("Step: " + time);
		step.setFont(font);
		step.setPrefWidth(PANE_WIDTH / 3);
		step.setPrefHeight(PANE_HEIGHT / 9);
		
		Button log = new Button("Log");
		log.setFont(font);
		log.setPrefWidth(PANE_WIDTH / 3);
		log.setPrefHeight(PANE_HEIGHT / 9);
		log.setOnAction(e -> controller.enableLogging());
		
	    hBox.getChildren().addAll(run, step, log);
	}
	
	public void floorSetup() {
		Line[] lineArr = new Line[NUM_FLOORS];
		Text[] labelArr = new Text[NUM_FLOORS];
		int yLocation = PANE_HEIGHT - (PANE_HEIGHT / 8);
		
		for (int i = 0; i < NUM_FLOORS; i++) {
			lineArr[i] = new Line();
			labelArr[i] = new Text(PANE_WIDTH / 30, yLocation, i + 1 + "");
			
			lineArr[i].setStrokeWidth(15);
			lineArr[i].setStartX(PANE_WIDTH / 3);
			lineArr[i].setEndX(PANE_WIDTH);
			lineArr[i].setStartY(yLocation);
			lineArr[i].setEndY(yLocation);
			labelArr[i].setFont(Font.font("Tahoma", FontWeight.BOLD, 25));
			
			yLocation -= (PANE_HEIGHT - 75) / NUM_FLOORS;
			pane.getChildren().addAll(lineArr[i], labelArr[i]);
		}
	}
	
	public void elevatorSetup() {
		Rectangle[] rectangleArr = new Rectangle[NUM_ELEVATORS];
		Line[] lineArr = new Line[NUM_ELEVATORS];
		
		for (int i = 0; i < NUM_ELEVATORS; i++) {
			rectangleArr[i] = new Rectangle(ELEVATOR_X_POSITION, ELEVATOR_Y_POSITION, ELEVATOR_WIDTH, ELEVATOR_HEIGHT);
			
			lineArr[i] = new Line();
			lineArr[i].setStrokeWidth(3);
			lineArr[i].setStroke(Color.LIGHTGRAY);
			lineArr[i].setStartX(ELEVATOR_X_POSITION + (ELEVATOR_WIDTH / 2));
			lineArr[i].setEndX(ELEVATOR_X_POSITION + (ELEVATOR_WIDTH / 2));
			lineArr[i].setStartY(ELEVATOR_Y_POSITION);
			lineArr[i].setEndY(ELEVATOR_Y_POSITION + ELEVATOR_HEIGHT);
			
			Text passengers = new Text(ELEVATOR_X_POSITION, ELEVATOR_Y_POSITION - 10, "Passengers: " + this.passengers);
			passengers.setFont(Font.font("Tahoma", FontWeight.BOLD, 13));
			
			pane.getChildren().addAll(rectangleArr[i], lineArr[i], passengers);
		}
	}
	
	// TODO: Write this method
	public void elevatorOpenDoors() {
		// ASK --> Multiple elevator setup, need to PASS IN which elevator to open doors to
		// MAYBE --> Don't need to worry as will ALWAYS only have 1 elevator
	}
	
	// TODO: Write this method
	public void elevatorCloseDoors() {
		
	}
	
	// TODO: Not working, need to implement correctly
	public void elevatorMoveToFloor(int floor, Rectangle body) {
		for (int i = (int)body.getY(); i < (PANE_HEIGHT / floor); i++) {
			body.setY(body.getY() + i);
			pane.getChildren().remove(body);
			pane.getChildren().add(body);
		}
	}
	
	// TODO: Write this method
	public void passengersGroupSetup() {
		// in bulding call method called getAllPassengerData(); <-- array of arraylists (ints, 2D) Arraylist<int>[4] =
		// for (i = controller.stepCnt; i < step)
		// 	    controller.stepSim()
		// doesn't have to be smooth, can just pop near the elevator once the tick updates
		// draw all passengers
		
		ArrayList<Integer>[] passengerData = controller.getAllPassengerData();
		
		Circle[] circleArr = new Circle[passengerData[0].size()];
		Text[] textArr = new Text[passengerData[0].size()];
//		Triangle[] triangleArr = new Triangle[passengerData[0].size()];
		
		for (int i = 0; i < passengerData[0].size(); i++) {
			int numPeople = passengerData[0].get(i); // Number of people in  group
			int currFloor = passengerData[1].get(i); // Current floor
			int destFloor = passengerData[2].get(i); // Destination floor
			int politeness = passengerData[3].get(i); // Politeness (0 is impolite, 1 is polite)
			
			circleArr[i] = new Circle(PANE_WIDTH / 3 + (i * 10), PANE_HEIGHT / currFloor, PANE_HEIGHT / NUM_FLOORS);
			textArr[i] = new Text(PANE_WIDTH / 3 + (i * 10), PANE_HEIGHT / currFloor, numPeople + "");
			
			pane.getChildren().addAll(circleArr[i], textArr[i]);
		}
		
	}
	
	// TODO: Write this method
	public void passengersGroupMove(Pane pane) {
		// take care of ticks
	}
	
	/**
	 * The main method.
	 *
	 * @param args the arguments
	 */
	public static void main (String[] args) {
		Application.launch(args);
	}

}
