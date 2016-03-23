package xyz.binormal;

import com.sun.javafx.geom.Rectangle;

public class JFXEvent {

	private Rectangle bounds;
	private JFXItem takeItem;
	private JFXItem giveItem;
	private String eventType;
	private Boolean eventDone = false;
	
	public JFXEvent(JFXItem takeItem, JFXItem giveItem){
		this.takeItem = takeItem;
		this.giveItem = giveItem;
		this.eventType = this.itemEvent();
	}
	
	public JFXEvent(Rectangle bounds){
		this.bounds = bounds;
		this.eventType = this.spawnEvent();
	}
	
	public void setBounds(Rectangle bounds){
		this.bounds = bounds;
	}
	
	public Rectangle getBounds(){
		return bounds;
	}
	
	public JFXItem getTakeItem(){
		return this.takeItem;
	}
	
	public JFXItem getGiveItem(){
		return this.giveItem;
	}
	
	public String getEventType(){
		return this.eventType;
	}
	
	public String itemEvent(){
		return "item";
	}
	
	public void doEvent(){
		this.eventDone = true;
	}
	
	public Boolean eventDone(){
		return this.eventDone;
	}
	
	public String spawnEvent(){
		return "spawn";
	}
}
