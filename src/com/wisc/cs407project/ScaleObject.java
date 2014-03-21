package com.wisc.cs407project;

import android.graphics.Bitmap;

import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;

public class ScaleObject implements Comparable<ScaleObject> {
	public Marker marker;
	public String text;
	public String name;
	public Double percentage;
	public String imageLocation;
	public Bitmap image;
	public LatLng position;
	public double distance;
	public boolean opened;
	// Added these fields to be used with ScaleGenerator
	public Long comparativeValue;
	public boolean isImageLocal;
	
	// NOTE: Only use this if you are sure both objects have comparativeValues and not just percentages.
	@Override
	public int compareTo(ScaleObject another) {
		return comparativeValue.compareTo(another.comparativeValue);
	}
}
