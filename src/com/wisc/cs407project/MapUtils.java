package com.wisc.cs407project;

public class MapUtils {
	public static double getDistance(double lat1, double lon1, double lat2, double lon2) {
		int radius = 6371000; //earth's radius in meters
		double diffLat = Math.toRadians(lat2 - lat1);
		double diffLon = Math.toRadians(lon2 - lon1);
		//haversine formula
		double dist = 
				Math.pow(Math.sin(diffLat / 2), 2) + 
				Math.cos(Math.toRadians(lat2)) * 
				Math.cos(Math.toRadians(lat1)) * 
				Math.pow(Math.sin(diffLon / 2), 2); 
		dist =  radius * 2 * Math.atan2(Math.sqrt(dist), Math.sqrt(1-dist));
		return dist;
	}
}
