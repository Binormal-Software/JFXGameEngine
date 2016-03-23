package xyz.binormal;

import com.sun.javafx.geom.Rectangle;

public class JFXItem {

	private Rectangle bounds;
	private String itemName;
	private Boolean removable;
	
	public Boolean taken = false;
	
	public JFXItem(String name, Boolean removable){
		this.itemName = name;
		this.removable = removable;
	}
	
	public void setBounds(Rectangle bounds){
		this.bounds = bounds;
	}
	
	public void setName(String name){
		this.itemName = name;
	}
	
	public Rectangle getBounds(){
		return bounds;
	}
	
	public String getName(){
		return this.itemName;
	}
	
	public Boolean isRemovable(){
		return this.removable;
	}
	
	public void take(){
		taken = true;
	}
	
	
	
}
