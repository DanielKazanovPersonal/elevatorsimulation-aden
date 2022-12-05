
import building.Elevator;
import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
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
	
	public void update() {
		
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
		Pane pane = new Pane();
		HBox hBox = new HBox(3);
		Scene scene = new Scene(borderPane, PANE_WIDTH, PANE_HEIGHT);
		
	    borderPane.setCenter(pane);
		borderPane.setBottom(hBox);
		primaryStage.setScene(scene);
		
		buttonSetup(hBox);
		floorSetup(pane);
		Rectangle body = elevatorSetup(pane);
		
		// TODO: change line below
		elevatorMoveToFloor(pane, 6, body);
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
	
	public void floorSetup(Pane pane) {
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
	
	//TODO: implement to work with multiple elevators
	public Rectangle elevatorSetup(Pane pane) {
		Rectangle body = new Rectangle(ELEVATOR_X_POSITION, ELEVATOR_Y_POSITION, ELEVATOR_WIDTH, ELEVATOR_HEIGHT);
		Line line = new Line();
		line.setStrokeWidth(3);
		line.setStroke(Color.LIGHTGRAY);
		line.setStartX(ELEVATOR_X_POSITION + (ELEVATOR_WIDTH / 2));
		line.setEndX(ELEVATOR_X_POSITION + (ELEVATOR_WIDTH / 2));
		line.setStartY(ELEVATOR_Y_POSITION);
		line.setEndY(ELEVATOR_Y_POSITION + ELEVATOR_HEIGHT);
		
		Text passengers = new Text(ELEVATOR_X_POSITION, ELEVATOR_Y_POSITION - 10, "Passengers: " + this.passengers);
		passengers.setFont(Font.font("Tahoma", FontWeight.BOLD, 13));
		
		pane.getChildren().addAll(body, line, passengers);
		return body;
	}
	
	// TODO: Write this method
	public void elevatorOpenDoors() {
		
	}
	
	// TODO: Not working, need to implement correctly
	public void elevatorMoveToFloor(Pane pane, int floor, Rectangle body) {
		for (int i = (int)body.getY(); i < (PANE_HEIGHT / floor); i++) {
			body.setY(body.getY() + i);
			pane.getChildren().remove(body);
			pane.getChildren().add(body);
		}
	}
	
	public void passengersGroupSetup(Pane pane) {
		
	}
	
	public void passengersGroupMove(Pane pane) {
		
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
