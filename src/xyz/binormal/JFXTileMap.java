package xyz.binormal;

import java.awt.image.BufferedImage;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

import javax.imageio.ImageIO;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.xml.sax.SAXException;

import com.sun.javafx.geom.Rectangle;

import javafx.embed.swing.SwingFXUtils;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.image.Image;

public class JFXTileMap {

	private Image[] tileImage;
	private String[][][] mapMatrix;
	
	private List<Rectangle> hitboxList;
	private List<JFXItem> itemList;
	private List<JFXEvent> eventList;
	
	public double screenHeight;
	public double screenWidth;
	
	private double scrollX;
	private double scrollY;
	private double[] screenMargin;
	
	private int tileWidth;
	private int tileHeight;
	private int mapWidth;
	private int mapHeight;
	private int mapLayers;
	private int mapObjects; 
	private int tileSpacing;
	private int tileMargin;
	
	public JFXTileMap(String filePath) throws SAXException, IOException, ParserConfigurationException{
		
		
		System.out.println("Loading tmx..."); // LOAD TILE MAP
		
		File fXmlFile = new File(filePath);
        
		DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
		DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
		Document doc = dBuilder.parse(fXmlFile);
		doc.getDocumentElement().normalize();
		
		System.out.println("Root element :" + doc.getDocumentElement().getNodeName());
		
		Element eElement = (Element) doc.getElementsByTagName("map").item(0); // map data

	    tileWidth = Integer.parseInt(eElement.getAttribute("tilewidth"));
	    tileHeight = Integer.parseInt(eElement.getAttribute("tileheight"));
	    mapWidth = Integer.parseInt(eElement.getAttribute("width"));
	    mapHeight = Integer.parseInt(eElement.getAttribute("height"));


	    System.out.println("Map info: --------------");
	    System.out.println("tileWidth: " + tileWidth);
	    System.out.println("tileHeight: " + tileHeight);
	    System.out.println("mapWidth: " + mapWidth);
	    System.out.println("mapHeight: " + mapHeight);


	    eElement = (Element) doc.getElementsByTagName("tileset").item(0); // tileset data
	    
	    if(eElement.hasAttribute("spacing")){
	    	tileSpacing = Integer.parseInt(eElement.getAttribute("spacing"));
	    }else{
	    	tileSpacing = 0;
	    }
	    
	    if(eElement.hasAttribute("margin")){
	    	tileMargin = Integer.parseInt(eElement.getAttribute("margin"));
	    }else{
	    	tileMargin = 0;
	    }
	    
	    eElement = (Element) doc.getElementsByTagName("image").item(0);
	    String imageSource = eElement.getAttribute("source");
	    
	    mapLayers = doc.getElementsByTagName("data").getLength();
	    mapObjects = doc.getElementsByTagName("object").getLength();
	    
	    System.out.println("TileSet info: --------------");
	    System.out.println("imageSource: " + imageSource);
	    System.out.println("tileMargin: " + tileMargin);
	    System.out.println("tileSpacing: " + tileSpacing);
	    System.out.println("mapLayers: " + mapLayers);
	    System.out.println("mapObjects: " + mapObjects);
	    
	    mapMatrix = new String[mapLayers][][];
	    hitboxList = new ArrayList<Rectangle>();
	    itemList = new ArrayList<JFXItem>();
	    eventList = new ArrayList<JFXEvent>();
	    
	    for(int i = 0; i < mapObjects; i++){
	    	eElement = (Element) doc.getElementsByTagName("object").item(i);
	    	
	    	Rectangle objectBounds = new Rectangle(
	    			(int) Math.round(Double.parseDouble(eElement.getAttribute("x"))),
	    			(int) Math.round(Double.parseDouble(eElement.getAttribute("y"))),
	    			(int) Math.round(Double.parseDouble(eElement.getAttribute("width"))),
	    			(int) Math.round(Double.parseDouble(eElement.getAttribute("height"))) );
	    	
	    	switch(eElement.getAttribute("type")){

	    	case "hitbox":
	    		hitboxList.add(objectBounds);
	    		break;
	    		
	    	case "item":
	    		System.out.println("Adding item " + eElement.getAttribute("name"));
	    		JFXItem item = new JFXItem(eElement.getAttribute("name"), false);
	    		item.setBounds(objectBounds);
	    		itemList.add(item);
	    		break;
	    		
	    	case "event":
	    		
	    		JFXEvent event;
	    		
	    		
	    		switch(eElement.getAttribute("name")){
	    		
	    		case "spawn":
	    			
	    			event = new JFXEvent(objectBounds);
		    		eventList.add(event);
	    			
	    			break;
	    		
	    		default: 
	    			
	    			JFXItem takeItem = null;
		    		JFXItem giveItem = null;
		    		
		    		Node childNode = eElement.getChildNodes().item(1).getFirstChild();
		    		
		    		do{
		    			
		    			if (childNode.getNodeType() == Node.ELEMENT_NODE) { 

		    				Element cElement = (Element) childNode;             
		    				//System.out.println(cElement.getAttribute("name"));
		    				
		    				if(cElement.getAttribute("name").equals("takeItem")){
		    					takeItem = new JFXItem(cElement.getAttribute("value"), true);
		    				}
		    				if(cElement.getAttribute("name").equals("giveItem")){
		    					giveItem = new JFXItem(cElement.getAttribute("value"), true);
		    				}

		    			}   

		    			childNode = childNode.getNextSibling();
		    			
		    		}
		    	    while(childNode!=null);
		    		
		    		event = new JFXEvent(takeItem, giveItem);
		    		event.setBounds(objectBounds);
		    		eventList.add(event);
		    		
		    		break;
	    		
	    		
	    		}
	    		
	    		break;
	    		
	    	}
	    	
	    	
	    }
	    
	    
	    for(int i = 0; i < mapLayers; i++){
	    	
	    	StringReader sr = new StringReader(doc.getElementsByTagName("data").item(i).getTextContent());
		    BufferedReader CSVFile = new BufferedReader(sr);
		    
			LinkedList<String[]> rowList = new LinkedList<String[]>();
			String dataRow = CSVFile.readLine();
			
			while ((dataRow = CSVFile.readLine()) != null){
			    rowList.addLast(dataRow.split(","));
			}
			
			mapMatrix[i] = rowList.toArray(  new String[  rowList.size()  ][]  );
			System.out.println("Map matrix created from layer " + i + "; " + mapMatrix[0].length + "x" + mapMatrix[0][0].length); 
	    	
	    }
	    
	    
		////////////// LOAD TILE IMAGE DATA
		
		
		File file = new File("res/"+imageSource);
    	System.out.println(file.getAbsolutePath());
        FileInputStream fis = new FileInputStream(file);  
        BufferedImage image = ImageIO.read(fis); //reading the image file  
  
        int rows = image.getHeight() / tileHeight;
        int cols = image.getWidth() / tileWidth;
        int chunks = rows * cols;  
        int count = 1;  
        
        System.out.println("Reported tile dims are " + tileWidth + "x" + tileHeight);
        System.out.println("Reported tileSet size is " + cols + "x" + rows);
        
        tileImage = new Image[chunks + 1]; //Image array to hold image chunks  
        for (int y = 0; y < rows; y++) {  
            for (int x = 0; x < cols; x++) {   
            	
            	//System.out.println("Grabbing tile " + count + "; (column " + x + ", row " + y + ")");
                tileImage[count] = SwingFXUtils.toFXImage(image.getSubimage
                		( (x * (tileWidth + tileSpacing)) + tileMargin ,
                		( (y * (tileHeight + tileSpacing)) + tileMargin),
                		tileWidth, tileHeight), null);
                count++;	
            
            }  
        }  
        
        System.out.println("Splitting done"); 
			
	}
	
	public void setBounds(double[] startPosition, double screenWidth, double screenHeight, double xMargin, double yMargin){
		
		scrollX = (screenWidth/2d) - startPosition[0];
		scrollY = (screenHeight/2d) - startPosition[1];
		
		this.screenHeight = screenHeight;
		this.screenWidth = screenWidth;
		
		screenMargin = new double[2];
		screenMargin[0] = xMargin;
		screenMargin[1] = yMargin;
	}
	
	public void drawMap(GraphicsContext gc, int startLayer, int endLayer){
		
		for(int i = startLayer; i < endLayer; i++){
			for (int y = 0; y < mapHeight; y++) {
				for(int x = 0; x < mapWidth; x++){
					//System.out.println("Drawing tile @ (column " + x + ", row " + y + ") to " + (Integer.parseInt( mapMatrix[x][y] )) );
					gc.drawImage(tileImage[ Integer.parseInt( mapMatrix[i][y][x])], (tileWidth*x)+scrollX+screenMargin[0], (tileWidth*y+scrollY+screenMargin[1]));
				}
			}
		}
	}
	
	public void scrollMap(double walkingSpeed, int walkingDirection){
	
		switch(getDirection(walkingDirection)){
		case "DOWN": scrollY -= walkingSpeed; break;
		case "LEFT": scrollX += walkingSpeed; break;
		case "RIGHT": scrollX -= walkingSpeed; break;
		case "UP": scrollY += walkingSpeed; break;
		}
		
	}

	public double[] getPlayerLocation(){
		double[] location =  new double[]{(-scrollX + (screenWidth/2d)),(-scrollY + (screenHeight/2d))};
		//System.out.println(location[0] + "," + location[1]);
		//System.out.println(screenWidth + "x" + screenHeight);
		return location;
	}
	
	public List<JFXItem> getItems(){
		return this.itemList;
	}
	
	public List<JFXEvent> getEvents(){
		return this.eventList;
	}
	
	public List<Rectangle> getHitboxes(){
		return this.hitboxList;
	}
	
	public void setPlayerLocation(double x, double y){
		scrollX = -x + (screenWidth/2d);
		scrollY = -y + (screenHeight/2d);
	}
	
	public double[] getMapSizeInPixels(){
		return new double[]{(this.mapWidth*this.tileWidth),
				(this.mapHeight*this.tileHeight)};
	}
	
	public void resolutionChange(String dimension, double delta){
		
		if (dimension.toLowerCase().equals("x")){
			screenMargin[0] += delta/2d;
			
		}else if (dimension.toLowerCase().equals("y")){
			screenMargin[1] += delta/2d;
			
		}
		
		
		
		
	}
	
	public double[] getDistanceToEdge(){
		return new double[]{
				this.screenWidth/2d + this.screenMargin[0],
				this.screenHeight/2d + this.screenMargin[1],
				(this.mapWidth*this.tileWidth) -(this.screenWidth/2d + this.screenMargin[0]),
				(this.mapHeight*this.tileHeight) - (this.screenHeight/2d + this.screenMargin[1])};
	}
		
	private String getDirection(int walkingDirection){
		switch(walkingDirection){
		
		case 0: return "DOWN";
		case 1: return "LEFT";
		case 2: return "RIGHT";
		case 3: return "UP";
		default: return null;
		
		}
	}
	
}
