package com.wisc.cs407project;

import android.app.Activity;
import android.app.Fragment;
import android.content.Intent;
import android.location.Location;
import android.location.LocationListener;
import android.os.Bundle;

import com.google.android.gms.maps.model.LatLng;

public class ScaleLocationListener extends Activity implements LocationListener {
	//private LatLng previousPoint;
	private WalkFragment mapActivity;
	
	public ScaleLocationListener(Fragment fragment) {
		mapActivity = (WalkFragment) fragment;
	}
	
	@Override
	public void onLocationChanged(Location loc) {
		if (mapActivity.startingPoint != null) {
			if (!mapActivity.pathStarted) {
				if (MapUtils.getDistance(loc.getLatitude(), loc.getLongitude(), mapActivity.startingPoint.latitude, mapActivity.startingPoint.longitude) < 30) {
					mapActivity.previousPoint = new LatLng(loc.getLatitude(), loc.getLongitude());
					mapActivity.pathStarted = true;
					Intent intent = new Intent(mapActivity.getActivity(), Popup.class);
					intent.putExtra("title", "Path started");
					intent.putExtra("text", "You have reached the paths starting point. Continue along the path to explore your chosen scale.");
					mapActivity.startActivity(intent);
				}
			}
			else {
				mapActivity.distanceTraveled += MapUtils.getDistance(loc.getLatitude(), loc.getLongitude(), mapActivity.previousPoint.latitude, mapActivity.previousPoint.longitude);
				for (com.wisc.cs407project.ParseObjects.ScaleObject so : mapActivity.scaleItemList) {
					if (!so.opened && Math.abs(mapActivity.distanceTraveled - so.distance) < mapActivity.distanceInterval && MapUtils.getDistance(so.position.latitude, so.position.longitude, loc.getLatitude(), loc.getLongitude()) < 30) {
						so.opened = true;
						Intent intent = new Intent(mapActivity.getActivity(), Popup.class);
						intent.putExtra("title", so.GetName());
						Popup.image = so.image;
						intent.putExtra("text", so.GetText());
						mapActivity.startActivity(intent);
					}
				}
			}
		}
		mapActivity.previousPoint = new LatLng(loc.getLatitude(), loc.getLongitude());
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
	
}
