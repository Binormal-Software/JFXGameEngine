package xyz.binormal;

import java.io.File;

import javafx.scene.image.Image;

public class JFXAnimatedFrames {

	
	public Image[] frames;
    public double duration;
    public int numberOfFrames;
    
    public JFXAnimatedFrames(String imagePath, double duration){
    	this.duration = duration;
    	
    	Image[] imageArray = new Image[100];
    	
    	int index = 0;
    	String imageName;
    	
    	try{
    		
    		do{
    			imageName = imagePath.split("#")[0] + (index) + imagePath.split("#")[1];
    			System.out.println("Looking for " + new File(imageName).getAbsolutePath());
    			imageArray[index] = new Image("file:" + new File(imageName).getAbsolutePath());
    			//imageArray[index] = new Image("file:X:/Documents & Files/Eclipse Projects/JFX SideScroller/res/walking (1).gif");
    			index++;
    			
    		}while(new File(imageName).exists());
    		
    		numberOfFrames = (index-1);
    		System.out.println(numberOfFrames + " frames found.");
    		
    		this.frames = imageArray;
    	}catch(Exception e){
    		System.err.println("Fatal error: Invalid path for image!");
    		e.printStackTrace();
    		System.exit(1);
    	}
    }
    
    
    public Image getFrame(double time)
    {   //or 32x48
        int index = (int)((time % (numberOfFrames * duration)) / duration);
        return frames[index];
    }
	
	
	
}
