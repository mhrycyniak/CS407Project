package com.wisc.cs407project;

import android.app.Application;
import android.util.Log;

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
	}
}