package com.wisc.cs407project.ParseObjects;

import java.io.BufferedInputStream;
import java.io.InputStream;
import java.net.URL;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Log;

import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.parse.GetDataCallback;
import com.parse.ParseException;
import com.parse.ParseFile;
import com.parse.ParseObject;
import com.parse.ParseUser;

public class ScaleObject extends ScaleParseObject implements Comparable<ScaleObject>{
	private static String Text = "text";
	private static String Name = "name";
	private static String Percentage = "percentage";
	private static String ImageLocation = "imageLocation";
	private static String Image = "imageId";
	
	public Marker marker;
	public LatLng position;
	public Bitmap image;
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
		if(parseObject != null)
			parseObject.put(this.Text, text);
	}
	public String GetText(){
		return parseObject.getString(this.Text);
	}
	
	public void SetName(String name){
		if(parseObject != null)
			parseObject.put(this.Name, name);
	}
	public String GetName(){
		return parseObject.getString(this.Name);
	}
	
	public void SetPercentage(double percent){
		if(parseObject != null)
			parseObject.put(this.Percentage, percent);
	}
	public double GetPercentage(){
		return parseObject.getDouble(this.Percentage);
	}
	
	public boolean updateImage = false;
	public void SetImage(ParseFile file){
		if(parseObject != null){
			updateImage = false;
			parseObject.put(Image, file);
		}
	}
	public void GetImage(){
		ParseFile file = parseObject.getParseFile(Image);
		
		if(file == null && !this.GetImageLocation().equals("")){
			try{
				URL url = new URL(GetImageLocation());
				InputStream in = url.openStream();
				BufferedInputStream buf = new BufferedInputStream(in);
				Bitmap myBitmap = BitmapFactory.decodeStream(buf);
				if (in != null) {
					in.close();
	        	}
				if (buf != null) {
					buf.close();
				}
				image = myBitmap;
			}
			catch(Exception e){
				e.printStackTrace();
			}
			return;
		}
		
		final ScaleObject o = this;
		file.getDataInBackground(new GetDataCallback(){

			@Override
			public void done(byte[] data, ParseException e) {
				if(e == null){
					Log.d("data length", ""+data.length);
					o.image = BitmapFactory.decodeByteArray(data, 0, data.length);
					Log.d("crash?", "no");
				}
				else{
					e.printStackTrace();
				}
			}});
	}
	
	public void SetImageLocation(String imageLocation){
		if(parseObject != null){
			updateImage = true;
			parseObject.put(this.ImageLocation, imageLocation);
		}
	}
	public String GetImageLocation(){
		String s = parseObject.getString(this.ImageLocation);
		return s == null ? "" : s;
	}
}
