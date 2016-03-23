package xyz.binormal;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import javax.imageio.ImageIO;
import javafx.embed.swing.SwingFXUtils;
import javafx.scene.image.Image;

public class JFXAnimatedSprite {

	
	Image[] imgs;
    public double duration;
    
    private int rows;
    private int cols;
    private int chunks;
    
    public int chunkWidth;
    public int chunkHeight;
    
    
    
    public JFXAnimatedSprite(String imagePath, double duration) throws IOException{
    	this.duration = duration;
    	
    	//32x48 px
    	
    	File file = new File(imagePath);
    	System.out.println(file.getAbsolutePath());
        FileInputStream fis = new FileInputStream(file);  
        BufferedImage image = ImageIO.read(fis); //reading the image file  
  
        rows = 4; //You should decide the values for rows and cols variables  
        cols = 4;  
        chunks = rows * cols;  
  
        chunkWidth = image.getWidth() / cols; // determines the chunk width and height  
        chunkHeight = image.getHeight() / rows;  
        int count = 0;  
        
        System.out.println("Reported tile dims are " + chunkWidth + "x" + chunkHeight);
        
        imgs = new Image[chunks]; //Image array to hold image chunks  
        for (int y = 0; y < rows; y++) {  
            for (int x = 0; x < cols; x++) {   
            	
                imgs[count] = SwingFXUtils.toFXImage(image.getSubimage((x * chunkWidth), (y * chunkHeight), chunkWidth, chunkHeight), null);
                count++;	
            
            }  
        }  
        System.out.println("Splitting done");  
    }  
    	
    
    public Image getFrame(double time, int animationCycle ) //animationCycle- e.g. left, right up, etc.
    {   
		int index = (int)((time % ((cols) * duration)) / duration);
		index += (cols*animationCycle);
        return imgs[index];
        //return imgs[0];
    }
	
	
	
}
