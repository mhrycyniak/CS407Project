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
	public Route parse(JSONObject jObject){
		ArrayList<LatLng> routeList = new ArrayList<LatLng>();
		Route route = new Route();
		JSONArray jRoutes = null;
		JSONArray jLegs = null;
		JSONArray jSteps = null;
		//JSONArray jStatus = null;

		try {
			//JSONArray jWarnings = jObject.getJSONArray("warnings");
			//for (int i = 0; i < jWarnings.length(); i++) {
			//	String warning = (String) jWarnings.get(i);
			//	Log.d("warning " + i + ": ", warning);
			//}
			String status = (String) jObject.get("status");
			route.status = status;
			Log.d("status: ", status);
			//String status = (String)jStatus.get(0);
			if (status.equals("NOT_FOUND")) {
				return null;
			}
			
			jRoutes = jObject.getJSONArray("routes");
			jLegs = ( (JSONObject)jRoutes.get(0)).getJSONArray("legs");

			String copyrights = ((JSONObject)jRoutes.get(0)).getString("copyrights");
			route.copyrights = copyrights;
			Log.d("copyrights: ", copyrights);
			JSONArray jWarnings = ((JSONObject)jRoutes.get(0)).getJSONArray("warnings");
			ArrayList<String> warnings = new ArrayList<String>();
			if (!jWarnings.isNull(0)) {
				for (int i = 0; i < jWarnings.length(); i++) {
					String warning = jWarnings.getString(i);
					Log.d("warning: ", warning);
					warnings.add(warning);
				}
            }
			route.warnings = warnings;
			
			// Traverse legs
			for(int j=0;j<jLegs.length();j++){
				jSteps = ( (JSONObject)jLegs.get(j)).getJSONArray("steps");

				// Traverse steps
				for(int k=0;k<jSteps.length();k++){
					String polyline = "";
					polyline = (String)((JSONObject)((JSONObject)jSteps.get(k)).get("polyline")).get("points");
					ArrayList<LatLng> list = decodePoly(polyline);
					for (LatLng point : list) {
						routeList.add(point);
						//Log.d("Building Route", "point added: " + point.latitude + ", " + point.longitude);
					}
				}
			}
		} catch (JSONException e) {
			e.printStackTrace();
		}catch (Exception e){
		}
		route.route = routeList;
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
