package com.wisc.cs407project;

import com.wisc.cs407project.ParseObjects.*;
import android.app.Application;

import com.parse.Parse;
import com.parse.ParseObject;

public class Cs407Application extends Application {
	
	@Override
	public void onCreate() {
		super.onCreate();
		
		Parse.initialize(this, "8TQsLVtRLfrd8pXDogooaSQaN3hrYJm7QjsoE4fR", 
				"3hRgVRboZYCxFkgZcVFXvAd89phKWlwAXEbJpNPx");
		//ParseObject testObject = new ParseObject("AnotherTestObject");
		//testObject.put("foo", "bar");
		//testObject.saveInBackground();
		
		//StaticUtils.CreatePlanetScale();
//		StaticUtils.CreatePath(StaticUtils.WashAve, "Path down Washington Ave.kml");
//		StaticUtils.CreatePath(StaticUtils.Lap, "A Lap.kml");
//		StaticUtils.CreatePath(StaticUtils.route2, "Route 2.kml");
//		StaticUtils.CreatePath(StaticUtils.samplePath, "Sample Path.kml");
//		StaticUtils.CreatePath(StaticUtils.straightPath, "A Straight Path.kml");
		
	}	
}