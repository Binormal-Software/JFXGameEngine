package xyz.binormal;

import javafx.animation.AnimationTimer;
import javafx.application.Application;
import javafx.collections.ObservableList;
import javafx.scene.Group;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;
import javafx.stage.Stage;

public abstract class JFXGameEngine extends Application {

	public final String TITLE = "Binormal JFX Game Engine";
	public final String VERSION = "1.0.0A";
	
	
	protected static JFXGameProperties gameProperties = new JFXGameProperties();
	protected Scene gameScene;
	
	private Group root = new Group();
	private Canvas canvas;
	protected GraphicsContext gc;
	
	private long startNanoTime = System.nanoTime();
	private long lastNanoTime = System.nanoTime();
	

	@Override
	public void start(Stage primaryStage) throws Exception {
		
		Runtime.getRuntime().addShutdownHook(new Thread(new Runnable() {
		    public void run() {
		    	close();
		    }
		}));
		
		try{

			System.out.println("Starting " + TITLE + " " + VERSION);
			System.out.println("Launching '" + (String)gameProperties.get("Title") + "' with properties as follow:");
			gameProperties.printValues();

			canvas = new Canvas((int) gameProperties.get("Width"), (int) gameProperties.get("Height"));
			canvas.widthProperty().bind(primaryStage.widthProperty());
			canvas.heightProperty().bind(primaryStage.heightProperty());
			
			gc = canvas.getGraphicsContext2D();
			root.getChildren().add(canvas);
			
			gameScene = new Scene(root, 
					(int)gameProperties.get("Width"), 
					(int)gameProperties.get("Height"),
					(Color)gameProperties.get("Background"));

			primaryStage.setTitle((String)gameProperties.get("Title"));
			primaryStage.setScene(gameScene);
			primaryStage.show();
			
		}catch(NullPointerException | ClassCastException e){
			System.err.println("Error: Some necessary gameProperties were left undefined or defined as the wrong type.");
			System.exit(1);
			
		}catch(Exception e){
			System.err.println("Error launching engine: " + e);
			System.exit(1);
		}

		initialize();
		
		new AnimationTimer()
		    {
		        public void handle(long currentTime)
		        {
		            double tpf = (currentTime - lastNanoTime) / 1000000000.0; 
		            update(tpf);
		            lastNanoTime = currentTime;
		        }
		    }.start();
		
	}

	protected abstract void initialize();
	
	protected abstract void update(double tpf);

	protected Object getInstance(){
		return this;
	}

	protected ObservableList<Node> rootNode(){
		return this.root.getChildren();
	}

	protected double runningTime(){
		return ((lastNanoTime - startNanoTime)/1000000000.0) ;
	}

	private void close(){
		System.out.println("Exiting game engine...");
		// close resources
		System.out.println("Done. Goodbye!");
	}
	
}
