package com.wisc.cs407project.PathBuilder;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.util.Log;

import com.google.android.gms.maps.model.LatLng;

public class RouteJSONParser {
	/** Receives a JSONObject and returns a list of LatLngs */
	public ArrayList<LatLng> parse(JSONObject jObject){
		ArrayList<LatLng> route = new ArrayList<LatLng>() ;
		JSONArray jRoutes = null;
		JSONArray jLegs = null;
		JSONArray jSteps = null;

		try {

			jRoutes = jObject.getJSONArray("routes");
			jLegs = ( (JSONObject)jRoutes.get(0)).getJSONArray("legs");

			/** Traversing all legs */
			for(int j=0;j<jLegs.length();j++){
				jSteps = ( (JSONObject)jLegs.get(j)).getJSONArray("steps");

				/** Traversing all steps */
				for(int k=0;k<jSteps.length();k++){
					String polyline = "";
					polyline = (String)((JSONObject)((JSONObject)jSteps.get(k)).get("polyline")).get("points");
					ArrayList<LatLng> list = decodePoly(polyline);
					for (LatLng point : list) {
						route.add(point);
						Log.d("Building Route", "point added: " + point.latitude + ", " + point.longitude);
					}
				}
			}
		} catch (JSONException e) {
			e.printStackTrace();
		}catch (Exception e){
		}
		return route;
	}
	/**
	 * Method to decode polyline points
	 * Courtesy : http://jeffreysambells.com/2010/05/27/decoding-polylines-from-google-maps-direction-api-with-java
	 * */
	private ArrayList<LatLng> decodePoly(String encoded) {

		ArrayList<LatLng> poly = new ArrayList<LatLng>();
		int index = 0, len = encoded.length();
		int lat = 0, lng = 0;

		while (index < len) {
			int b, shift = 0, result = 0;
			do {
				b = encoded.charAt(index++) - 63;
				result |= (b & 0x1f) << shift;
				shift += 5;
			} while (b >= 0x20);
			int dlat = ((result & 1) != 0 ? ~(result >> 1) : (result >> 1));
			lat += dlat;

			shift = 0;
			result = 0;
			do {
				b = encoded.charAt(index++) - 63;
				result |= (b & 0x1f) << shift;
				shift += 5;
			} while (b >= 0x20);
			int dlng = ((result & 1) != 0 ? ~(result >> 1) : (result >> 1));
			lng += dlng;

			LatLng p = new LatLng((((double) lat / 1E5)),
					(((double) lng / 1E5)));
			poly.add(p);
		}

		return poly;
	}
}
