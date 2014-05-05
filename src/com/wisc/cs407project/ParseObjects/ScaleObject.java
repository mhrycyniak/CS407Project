package com.wisc.cs407project.ParseObjects;

import android.graphics.Bitmap;
import android.util.Log;

import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.parse.ParseObject;
import com.parse.ParseUser;

public class ScaleObject extends ScaleParseObject implements Comparable<ScaleObject>{
	private static String Text = "text";
	private static String Name = "name";
	private static String Percentage = "percentage";
	private static String ImageLocation = "imageLocation";
	
	public Marker marker;
	public Bitmap image;
	public LatLng position;
	public double distance;
	public boolean opened;
	
	// Added this field to be used with ScaleGenerator
		public Long comparativeValue;
		
		// NOTE: Only use this if you are sure both objects have comparativeValues and not just percentages.
		@Override
		public int compareTo(ScaleObject another) {
			return this.GetPercentage() > another.GetPercentage() ? 1 : -1;
		}
	
	public ScaleObject()
	{
		super();
	}

	public String Id(){
		return parseObject.getObjectId();
	}
	
	public ScaleObject(ParseObject object)
	{
		pullData(object);
	}
	
	public ScaleObject(String objectId)
	{
		super(objectId);
	}
	
	//note after calling set you need to call .push() for changes to be saved
	
	public void SetText(String text){
		parseObject.put(this.Text, text);
	}
	public String GetText(){
		return parseObject.getString(this.Text);
	}
	
	public void SetName(String name){
		parseObject.put(this.Name, name);
	}
	public String GetName(){
		return parseObject.getString(this.Name);
	}
	
	public void SetPercentage(double percent){
		parseObject.put(this.Percentage, percent);
	}
	public double GetPercentage(){
		return parseObject.getDouble(this.Percentage);
	}
	
	public void SetImageLocation(String imageLocation){
		parseObject.put(this.ImageLocation, imageLocation);
	}
	public String GetImageLocation(){
		return parseObject.getString(this.ImageLocation);
	}
}
