package xyz.binormal;

import javafx.scene.Group;
import javafx.scene.Node;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Text;

public class SampleGame extends JFXGameEngine {
	
	Text fps;
	Group group;
	
	public static void main(String[] args) {
		
		
		gameProperties.set("Width", 500);
		gameProperties.set("Height", 500);
		gameProperties.set("Background", Color.LIGHTBLUE);
		gameProperties.set("Title", "Sample Game");
		
		
		launch(args);
	}

	
	@Override
	protected void initialize() {
		
		group = new Group();
		
		fps = new Text();
		fps.setY(25);
		
		Rectangle r = new Rectangle(100, 100, Color.WHITE);
		
		
		group.getChildren().addAll(r, fps);
		rootNode().add(group);
		
		
	}

	@Override
	protected void update(double tpf) {
		
		fps.setText("I'm moving!! :D \r\n fps: " + Math.round(1/tpf) );
        
        group.setTranslateX(200 + (150 * Math.cos(runningTime())));
        group.setTranslateY(200 + (150 * Math.sin(runningTime())));
	}

}
