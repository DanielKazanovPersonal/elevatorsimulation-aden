
import building.Elevator;
import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.shape.Line;
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

	/** Danie's created variables */
	private final int WIDTH = 700;
	private final int HEIGHT = 700;
	
	/**
	 * Instantiates a new elevator simulation.
	 */
	public ElevatorSimulation() {
		controller = new ElevatorSimController(this);	
		NUM_FLOORS = controller.getNumFloors();
		NUM_ELEVATORS = controller.getNumElevators();
		currFloor = controller.getCurrentFloor();
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
		Scene scene = new Scene(borderPane, WIDTH, HEIGHT);
		
	    borderPane.setCenter(pane);
		borderPane.setBottom(hBox);
		primaryStage.setScene(scene);
		
		buttonSetup(hBox);
		floorSetup(pane);
		elevatorSetup(pane);
	}
	
	public void buttonSetup(HBox hBox) {
		Font font = new Font(25);
		
		Button run = new Button("Run");
		run.setFont(font);
		run.setPrefWidth(WIDTH / 3);
		run.setPrefHeight(HEIGHT / 9);
		
		Button step = new Button("Step: ");
		step.setFont(font);
		step.setPrefWidth(WIDTH / 3);
		step.setPrefHeight(HEIGHT / 9);
		
		Button log = new Button("Log");
		log.setFont(font);
		log.setPrefWidth(WIDTH / 3);
		log.setPrefHeight(HEIGHT / 9);
		
	    hBox.getChildren().addAll(run, step, log);
	}
	
	public void floorSetup(Pane pane) {
		Line[] lineArr = new Line[NUM_FLOORS];
		Text[] labelArr = new Text[NUM_FLOORS];
		int yLocation = HEIGHT - (HEIGHT / 8);
		
		for (int i = 0; i < NUM_FLOORS; i++) {
			lineArr[i] = new Line();
			labelArr[i] = new Text(WIDTH / 30, yLocation, i + 1 + "");
			
			lineArr[i].setStrokeWidth(15);
			lineArr[i].setStartX(WIDTH / 3);
			lineArr[i].setEndX(WIDTH);
			lineArr[i].setStartY(yLocation);
			lineArr[i].setEndY(yLocation);
			labelArr[i].setFont(Font.font("Tahoma", FontWeight.BOLD, 25));
			
			yLocation -= (HEIGHT - 75) / NUM_FLOORS;
			pane.getChildren().addAll(lineArr[i], labelArr[i]);
		}
	}
	
	public void elevatorSetup(Pane pane) {
		
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
