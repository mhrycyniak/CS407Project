package com.wisc.cs407project;

import android.app.Activity;
import android.app.Fragment;
import android.location.Location;
import android.location.LocationListener;
import android.os.Bundle;

import com.google.android.gms.maps.model.LatLng;

public class RecordLocationListener extends Activity implements LocationListener {
	private LatLng previousPoint;
	private RecordFragment recordActivity;
	private String coordinates = "";
	private boolean isRecording = false;
	
	public RecordLocationListener(Fragment fragment) {
		recordActivity = (RecordFragment) fragment;
	}
	public void onLocationChanged(Location loc) {
		LatLng currentPoint = new LatLng(loc.getLatitude(), loc.getLongitude());
		if (isRecording) {
			coordinates += "\n"+currentPoint.longitude + ","+ currentPoint.latitude +",0";
			recordActivity.addEdge(previousPoint, currentPoint);
		}
		previousPoint = currentPoint;
	}
	
	@Override
	public void onProviderDisabled(String provider) {
	}
	
	@Override
	public void onProviderEnabled(String provider) {
		
	} 
	
	@Override
	public void onStatusChanged(String provider, int status, Bundle extras) {

	}
	
	public void StartRecording(){
		if (previousPoint != null) {
			coordinates += "\n"+previousPoint.longitude + ","+ previousPoint.latitude +",0";
		}
		isRecording = true;
	}
	
	public String StopRecording() {
		isRecording = false;
		return coordinates;
	}

}
