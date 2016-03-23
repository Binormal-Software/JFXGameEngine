package xyz.binormal;

import java.util.HashMap;

import javafx.scene.paint.Color;

public class JFXGameProperties {
	
	private HashMap<String, Object> settingsMap = new HashMap<String, Object>();
	
	public void set(String settingType, Object setting){
		settingsMap.put(settingType, setting);
	}
	
	public Object get(String settingType){
		
		if(settingsMap.get(settingType)==null){
			switch(settingType){// defaults
			case "Width": return 1280;
			case "Height": return 720;
			case "Title": return "Untitled Game";
			case "Background": return Color.WHITE;
			default: return null;
			}
		}else{
			return settingsMap.get(settingType);
		}
	}
	
	public void printValues(){
		System.out.println(settingsMap);
	}
	
}
