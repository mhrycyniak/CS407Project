package com.wisc.cs407project;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.StringReader;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import android.annotation.SuppressLint;
import android.app.Fragment;
import android.app.FragmentTransaction;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Point;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Display;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.GoogleMap.OnMarkerClickListener;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.model.BitmapDescriptor;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.PolylineOptions;
import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseFile;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.wisc.cs407project.ImageLoader.ImageLoader;

public class WalkFragment extends Fragment implements OnMarkerClickListener, LocationListener {
	private GoogleMap map;
	private Button pathButton;
	private Button scaleButton;
	private Button walkButton;
	private Button stopButton;
	private String scaleItem;
	private String pathURL;
	private boolean localPath;
	public List<ScaleObject> scaleItemList = new ArrayList<ScaleObject>();
	public LatLng startingPoint;
	public LatLng previousPoint;
	public boolean pathStarted = false;
	public double distanceTraveled = 0;
	public double distanceInterval;
	public Intent intent;
	//private ScaleLocationListener locationLis;
	private LocationManager locationMan;
	private WalkFragment ref;
	private NotificationManager mNotificationMgr;
	private boolean inFocus;
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		super.onCreateView(inflater, container, savedInstanceState);
		final View myFragmentView = inflater.inflate(R.layout.walkfragment, container, false);
		map = ((MapFragment) getFragmentManager().findFragmentById(R.id.mapWalk)).getMap();
		map.setMyLocationEnabled(true);
        map.setOnMarkerClickListener(this);
        ref = this;
        locationMan = (LocationManager) getActivity().getSystemService(Context.LOCATION_SERVICE);
        Location location = locationMan.getLastKnownLocation(LocationManager.GPS_PROVIDER);
		if (location != null) {
			map.moveCamera(CameraUpdateFactory.newLatLng(new LatLng(location.getLatitude(), location.getLongitude())));
			map.animateCamera(CameraUpdateFactory.zoomTo(15));
		}
        setHasOptionsMenu(true);
		
		     
		pathButton = (Button) myFragmentView.findViewById(R.id.choosePath);
		scaleButton = (Button) myFragmentView.findViewById(R.id.chooseScale);
		walkButton = (Button) myFragmentView.findViewById(R.id.walkPath);
		stopButton = (Button) myFragmentView.findViewById(R.id.stopWalk);
		onButtonClick();
		return myFragmentView;
	}
	
	@Override
	public void onDestroyView() {
		super.onDestroyView();
		Fragment fragment = (getFragmentManager().findFragmentById(R.id.mapWalk));
		// Avoids trying to commit after parent activity is already destroyed
		if (fragment.isResumed()) {
			FragmentTransaction ft = getActivity().getFragmentManager().beginTransaction();
			ft.remove(fragment);
			ft.commit();
		}
	}
	
	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		if (resultCode == 1) {
			Bundle extra = data.getExtras();
			scaleItem = extra.getString("scaleItem");			
			if (!scaleItem.isEmpty()) {				
				scaleButton.setBackgroundResource(R.color.green);
			}
			
			if (!scaleItem.isEmpty() && pathURL != null) {
				new LoadIndividualPathTask().execute(pathURL);
			}
		} else if (resultCode == 2) {
			Bundle extra = data.getExtras();
			pathURL = extra.getString("path");
			localPath = extra.getBoolean("localPath", false);
			if (pathURL != null) {
				pathButton.setBackgroundResource(R.color.green);
			}
			
			if (scaleItem != null && pathURL != null) {
				new LoadIndividualPathTask().execute(pathURL);
			}
		}
	}
	
	public void onButtonClick() {
		pathButton.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				Intent pathIntent = new Intent(getActivity(), PathChooser.class);
				startActivityForResult(pathIntent, 0);
			}
		});
		
		scaleButton.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				Intent scaleIntent = new Intent(getActivity(), ScaleChooser.class);
				startActivityForResult(scaleIntent, 0);
			}
		});
		
		walkButton.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				if (scaleItem != null && pathURL != null) {
					getActivity().findViewById(R.id.relativeLayout1).setVisibility(View.GONE);
					getActivity().findViewById(R.id.linearLayout1).setVisibility(View.VISIBLE);
					// Request location updates when walk path button is clicked
					locationMan.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 2, ref);
				} else if (scaleItem != null && pathURL == null) {
					Toast toast = Toast.makeText(getActivity(), "Please select a path.", Toast.LENGTH_SHORT);
					toast.setGravity(Gravity.CENTER, 0, 0);
					View view = toast.getView();
					view.setBackgroundResource(R.color.grey);
					toast.show();
				} else if (scaleItem == null && pathURL != null) {
					Toast toast = Toast.makeText(getActivity(), "Please select a scale.", Toast.LENGTH_SHORT);
					toast.setGravity(Gravity.CENTER, 0, 0);
					View view = toast.getView();
					view.setBackgroundResource(R.color.grey);
					toast.show();
				} else {
					Toast toast = Toast.makeText(getActivity(), "Please select a path and scale.", Toast.LENGTH_SHORT);
					toast.setGravity(Gravity.CENTER, 0, 0);
					View view = toast.getView();
					view.setBackgroundResource(R.color.grey);
					toast.show();
				}
			}
		});
		
		stopButton.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				locationMan.removeUpdates(ref);
				
				// Reset all variables for walk.
				pathStarted = false;
				scaleItem = null;
				pathURL = null;
				startingPoint = null;
				previousPoint = null;
				distanceTraveled = 0;
				distanceInterval = 0;
				
				// Clear map
				map.clear();
				getActivity().findViewById(R.id.linearLayout1).setVisibility(View.GONE);
				getActivity().findViewById(R.id.relativeLayout1).setVisibility(View.VISIBLE);
				
				// Change buttons back to default color
				scaleButton.setBackgroundResource(R.color.grey);
				pathButton.setBackgroundResource(R.color.grey);
				
				// Move to current location
				Location location = locationMan.getLastKnownLocation(LocationManager.GPS_PROVIDER);
				if (location != null) {
					map.moveCamera(CameraUpdateFactory.newLatLng(new LatLng(location.getLatitude(), location.getLongitude())));
					map.animateCamera(CameraUpdateFactory.zoomTo(15));
				}
			}
		});
	}

	private void populate(List<String> coordinates) {
        double maxLat = -90, maxLng = -180, minLat = 90, minLng = 180;
        try {
        	PolylineOptions path = new PolylineOptions();
        	List<Double> steps = new ArrayList<Double>();
        	List<LatLng> points = new ArrayList<LatLng>();
            double prevLat = 0;
            double prevLon = 0;
            double totalDist = 0;
            for (int i = 0; i < coordinates.size(); i++) {
                String coordText = coordinates.get(i);
                String[] coordinate = coordText.split(",");
                double currLat = Double.parseDouble(coordinate[1]);
                double currLon = Double.parseDouble(coordinate[0]);
                if (prevLat != 0 && prevLon != 0)
                {
                	double step = MapUtils.getDistance(prevLat, prevLon, currLat, currLon);
                	totalDist += step;
                	steps.add(step);
                }
            	prevLat = currLat;
            	prevLon = currLon;
                LatLng point = new LatLng(currLat, currLon);
            	if (currLat > maxLat) { maxLat = currLat; }
            	if (currLat < minLat) { minLat = currLat; }
            	if (currLon > maxLng) { maxLng = currLon; }
            	if (currLon < minLng) { minLng = currLon; }
                points.add(point);
                path.add(point);
            }
            startingPoint = points.get(0);
            BitmapDescriptor bitmapDescriptor = BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_GREEN);
            map.addMarker(new MarkerOptions().position(startingPoint).title("Starting Point").icon(bitmapDescriptor));
            distanceInterval = totalDist * 0.05;
            map.addPolyline(path);
            // add
            DocumentBuilder docBuilder = DocumentBuilderFactory.newInstance().newDocumentBuilder();
            			Document docScale = docBuilder.parse(new ByteArrayInputStream(scaleItem.getBytes()));
                        NodeList scaleItems = docScale.getElementsByTagName("scaleItem");
                        for (int i = 0; i < scaleItems.getLength(); i++) {
                        	NodeList children = scaleItems.item(i).getChildNodes();
                        	ScaleObject so = new ScaleObject();
                        	for (int j = 0; j < children.getLength(); j++)
                        	{
                        		Node child = children.item(j);
                        		String nodeName = child.getNodeName();
                        		if (nodeName.equals("name")) {
                        			so.name = child.getTextContent();
                        		}
                        		else if (nodeName.equals("description")) {
                        			so.text = child.getTextContent();
                        		}
                        		else if (nodeName.equals("percentage")) {
                        			so.percentage = Double.parseDouble(child.getTextContent());
                        		}
                        		else if (nodeName.equals("picture")) {
                        			so.imageLocation = child.getTextContent();
                        			new LoadImageTask().execute(so);
                        		}
                        	}
                        	if (so.name == null || so.text == null || so.percentage == null) {
            					Intent intent = new Intent(getActivity(), Popup.class);
            				intent.putExtra("title", "Invalid scale");
            					intent.putExtra("text", "A scale item is missing a name, text, or percentage tag.");
            					startActivityForResult(intent, 0);
                        	}
                        	else
                        	{
                        		double dist = so.percentage * totalDist;
                        		so.distance = dist;
                        		double cummDist = 0;
                        		int step = 0;
                        		while (step < steps.size() && cummDist + steps.get(step) < dist)
                        		{
                        			cummDist += steps.get(step);
                        			step++;
                        		}
                        		if (step != steps.size())
                        		{
                        			double percentageOfStep = (dist - cummDist)/steps.get(step);
                        			double lat = points.get(step).latitude + (points.get(step+1).latitude - points.get(step).latitude)*percentageOfStep;
                        			double lon = points.get(step).longitude + (points.get(step+1).longitude - points.get(step).longitude)*percentageOfStep;
                        			so.position = new LatLng(lat, lon);
                        			so.marker = map.addMarker(new MarkerOptions().position(so.position).title(so.name));
                        		}
                        		else
                        		{
                        			so.marker = map.addMarker(new MarkerOptions().position(points.get(i)).title(so.name));
                        			so.position = points.get(i);
                        		}
                        		scaleItemList.add(so);
                        	}
            
        		// Scale object not opened yet
        		so.opened = false;
            }
        }
        catch (Exception e) {
        	System.out.print(e.toString());
        }
        
        Location location = locationMan.getLastKnownLocation(LocationManager.GPS_PROVIDER);
        if (location != null) {
        	if (location.getLatitude() > maxLat) { maxLat = location.getLatitude(); }
        	if (location.getLatitude() < minLat) { minLat = location.getLatitude(); }
        	if (location.getLongitude() > maxLng) { maxLng = location.getLongitude(); }
        	if (location.getLongitude() < minLng) { minLng = location.getLongitude(); }
        }
        double latRange = maxLat - minLat, lngRange = maxLng - minLng;
        maxLat += latRange*0.05;
        minLat -= latRange*0.05;
        maxLng += lngRange*0.05;
        minLng -= lngRange*0.05;
        
        LatLngBounds calcBounds = new LatLngBounds(new LatLng(minLat, minLng), new LatLng(maxLat, maxLng));
        
        DisplayMetrics displaymetrics = new DisplayMetrics();
        getActivity().getWindowManager().getDefaultDisplay().getMetrics(displaymetrics);
        int height = displaymetrics.heightPixels;
        int width = displaymetrics.widthPixels;
        CameraUpdate camUpdate = CameraUpdateFactory.newLatLngBounds(calcBounds, width, height, 30);
		map.moveCamera(camUpdate);
	}

	@Override
	public boolean onMarkerClick(Marker marker) {
		for (ScaleObject so : scaleItemList)
		{
			if (marker.equals(so.marker))
			{
				//TODO is there a reason clicking on it when you're not near it shouldn't 
				//show the info?
				//if (so.distance - distanceTraveled < distanceInterval)
				{
					Intent intent = new Intent(getActivity(), Popup.class);
					intent.putExtra("title", so.name);			
					Popup.image = so.image;
					intent.putExtra("text", so.text);
					startActivity(intent);
					return true;
				}
				//break;
			}
		}
		return false;
	}
	
	private class LoadImageTask extends AsyncTask<ScaleObject, Void, String> {
	
		@Override
		protected String doInBackground(ScaleObject... arg0) {
			final ScaleObject object = arg0[0];
			if(object.imageLocation == null || object.imageLocation.equals("")){
				return null; //doesn't have an image
			}
  			try {
  				Display display = WalkFragment.this.getActivity().getWindowManager().getDefaultDisplay();
  				Point size = new Point();
  				display.getSize(size);
  				int width = size.x;
  				width = width / 2;
  				ImageLoader.REQUIRED_WIDTH = width;
  				ImageLoader.REQUIRED_HEIGHT = width;
  				object.image = ImageLoader.decodeFile(new File(object.imageLocation)); //TODO doesn't work
  				if(object.image != null)
  					return "";
  				
  				try{
  					URL url = new URL(object.imageLocation);
	 				InputStream in = url.openStream();
	 				BufferedInputStream buf = new BufferedInputStream(in);
	 				Bitmap myBitmap = BitmapFactory.decodeStream(buf);
	 				if (in != null) {
	 					in.close();
	 	        	}
	 				if (buf != null) {
	 					buf.close();
	 				}
	 				object.image = myBitmap;
	 				return "";
  				}catch(Exception e){
  					
  				}
  				
  				ParseQuery<ParseObject> query = ParseQuery.getQuery("Image");
  				query.whereEqualTo("scaleitem", object.name);
  				query.whereEqualTo("name", object.imageLocation.replaceAll("\\W _+", ""));
  				query.findInBackground(new FindCallback<ParseObject>(){
					@Override
					public void done(List<ParseObject> l, ParseException e) {
						try{
							if(e == null && l != null && l.size() > 0){
								ParseObject image = l.get(0);
								ParseFile file = image.getParseFile("file");
								byte[] data = file.getData();
								object.image = BitmapFactory.decodeByteArray(data, 0, data.length);
							}
							else{
								CouldntLoad(object);
							}
						}
						catch (Exception a){
							a.printStackTrace();
							CouldntLoad(object);
						}
					}});
				
  			} catch (Exception e) {
  				CouldntLoad(object);
  			}
  			return "";
  		}
		private void CouldntLoad(ScaleObject object){
			Intent intent = new Intent(WalkFragment.this.getActivity(), Popup.class);
			intent.putExtra("title", "Error");
			intent.putExtra("text", "Could not load image for "+object.name);
			startActivity(intent);
		}
  	}
	@Override
	public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
		super.onCreateOptionsMenu(menu, inflater);
		inflater.inflate(R.menu.mapsettings, menu);
	}
	
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		super.onOptionsItemSelected(item);
		switch(item.getItemId()) {
		case R.id.normalView:
			map.setMapType(GoogleMap.MAP_TYPE_NORMAL);
			return true;
		case R.id.satelliteView:
			map.setMapType(GoogleMap.MAP_TYPE_SATELLITE);
			return true;
		case R.id.hybridView:
			map.setMapType(GoogleMap.MAP_TYPE_HYBRID);
			return true;
		}
		return false;
	}
	
	@Override
	public void onDestroy(){
		super.onDestroy();
		locationMan.removeUpdates(this);
	} 
	
	@Override
	public void onPause() {
		super.onPause();
		inFocus = false;
	}
	
	@Override
	public void onResume() {
		super.onResume();
		inFocus = true;
	}

	private class LoadIndividualPathTask extends AsyncTask<String, Void, String> {

		@Override
		protected String doInBackground(String... arg0) {
			try {
				BufferedReader in = null;
				if (localPath) {
					// Paths directory
					String directoryPath = Environment.getExternalStorageDirectory().toString() + getResources().getString(R.string.app_path_directory);
					in = new BufferedReader(new InputStreamReader(new FileInputStream(new File(directoryPath, arg0[0]))));
				}
				else {
					ParseObject path = null;
					//Log.d("name", arg0[0]);
					for(ParseObject object : PathChooser.parsePaths){
						if(object.getString("name").equals(arg0[0])){
							path = object;
							// TODO change because bad if two paths have same name
							break;
						}
					}
					String data = new String(path.getParseFile("file").getData());
					localPath = !data.contains("<?xml version=\"1.0\" encoding=\"UTF-8\"?>");
					//Log.d("data", data);
					return data;
//					UrlValidator validator = new UrlValidator();
//					if(validator.isValid(arg0[0])) {
//						in = new BufferedReader(new InputStreamReader(new URL(arg0[0]).openStream()));;
//					} else if(new File(arg0[0]).exists()) {
//						in = new BufferedReader(new FileReader(arg0[0]));
//					} else {
//						Intent intent = new Intent(getActivity(), Popup.class);
//						intent.putExtra("title", "Error");
//						intent.putExtra("text", "Invalid Directory Location");
//						startActivity(intent);
//					}
				}
				String str;
				String fullFile = "";
				while ((str = in.readLine()) != null) {
					fullFile += str + "\n";
				}
				in.close();
				return fullFile;
			} catch (Exception e) {
			}
			return null;
		}

		protected void onPostExecute(String pathItem) {
			if (pathItem != null) {
				// Local paths are formatted differently
				if (localPath) {
					BufferedReader reader = new BufferedReader(new StringReader(pathItem));
					List<String> coordinates = new ArrayList<String>();
					String line = null;
					try {
						while((line = reader.readLine()) != null) {
							if (line != "") {
								coordinates.add(line);
							}
						}
						populate(coordinates);
					} catch (IOException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				} else {
					try {
						DocumentBuilder docBuilder = DocumentBuilderFactory.newInstance().newDocumentBuilder();
						Document doc = docBuilder.parse(new ByteArrayInputStream(pathItem.getBytes()));
						NodeList items = doc.getElementsByTagName("gx:coord");
						if (items.getLength() < 2) {
							items = doc.getElementsByTagName("coordinates");
							Log.d("num items", ""+items.getLength());
							for (int i = 0; i < items.getLength(); i++) {
								Log.d("item "+i, items.item(i).getFirstChild() == null ? "null" : items.item(i).getFirstChild().getNodeValue());
								if (items.item(i).getFirstChild()!= null && items.item(i).getFirstChild().getNodeValue().trim().contains("\n"))
								{
									String content = items.item(i).getFirstChild().getNodeValue().trim();
									List<String> coordinates = new ArrayList<String>();
									while (content.contains("\n")) {
										coordinates.add(content.substring(0, content.indexOf('\n')).trim());
										content = content.substring(content.indexOf('\n')+1);
									}
									coordinates.add(content);
									populate(coordinates);
									return;
								}
							}
							Intent intent = new Intent(getActivity(), Popup.class);
							intent.putExtra("title", "Error");
							intent.putExtra("text", "The path file does not contain a path.");
							startActivityForResult(intent, 0);
						}
						else {
							List<String> coordinates = new ArrayList<String>();
							for (int i =0; i < items.getLength(); i++) {
								coordinates.add(items.item(i).getTextContent().replace(" ",","));
							}
							populate(coordinates);
						}
					}
					catch (Exception e) {
						e.printStackTrace();
						Intent intent = new Intent(getActivity(), Popup.class);
						intent.putExtra("title", "Error");
						intent.putExtra("text", "The requested path is not in the correct format.");
						startActivityForResult(intent, 0);
					}
				}
			} else {				
				Intent intent = new Intent(getActivity(), Popup.class);
				intent.putExtra("title", "Error");
				intent.putExtra("text", "The requested path does not exist.");
				startActivityForResult(intent, 0);
			}
		}
	}

	@Override
	public void onLocationChanged(Location loc) {
		if (startingPoint != null) {
			if (!pathStarted) {
				if (MapUtils.getDistance(loc.getLatitude(), loc.getLongitude(), startingPoint.latitude, startingPoint.longitude) < 30) {
					// Display starting notification
					if (!inFocus) {
						startNotification();
					} else {
						Intent intent = new Intent(getActivity(), Popup.class);
						intent.putExtra("title", "Path started");
						intent.putExtra("text", "You have reached the paths starting point. Continue along the path to explore your chosen scale.");
						startActivity(intent);
					}
					previousPoint = new LatLng(loc.getLatitude(), loc.getLongitude());
					pathStarted = true;					
				}
			}
			else {
				distanceTraveled += MapUtils.getDistance(loc.getLatitude(), loc.getLongitude(), previousPoint.latitude, previousPoint.longitude);
				for (ScaleObject so : scaleItemList) {
					if (!so.opened && Math.abs(distanceTraveled - so.distance) < distanceInterval && MapUtils.getDistance(so.position.latitude, so.position.longitude, loc.getLatitude(), loc.getLongitude()) < 30) {
						so.opened = true;
						// Display notification that user has reached a scale when app is not focused.
						if (!inFocus) {
							scaleNotification(so.name);
						}
						Intent intent = new Intent(getActivity(), Popup.class);
						intent.putExtra("title", so.name);
						Popup.image = so.image;
						intent.putExtra("text", so.text);
						startActivity(intent);
					}
				}
			}
		}
		previousPoint = new LatLng(loc.getLatitude(), loc.getLongitude());
	}

	@Override
	public void onProviderDisabled(String provider) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onProviderEnabled(String provider) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onStatusChanged(String provider, int status, Bundle extras) {
		// TODO Auto-generated method stub
		
	}
	
	@SuppressLint("NewApi")
	private void startNotification() {
		// Prepare intent which is triggered if the notification is clicked.
		Intent intent = new Intent(getActivity(), Scale.class);
		intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
		PendingIntent contentIntent = PendingIntent.getActivity(getActivity(), 0, intent, 0);

		// Build notification
		Notification notification = new Notification.Builder(getActivity())
				.setContentTitle("Walk Through Time")
				.setContentText("You have reached the paths starting point. Click to view the map.")
				.setSmallIcon(R.drawable.ic_launcher)
				.setContentIntent(contentIntent)
				.setAutoCancel(true)
				.setDefaults(Notification.DEFAULT_SOUND | Notification.DEFAULT_VIBRATE | Notification.DEFAULT_LIGHTS)
				.build();


		// Display the notification
		mNotificationMgr = (NotificationManager) getActivity().getSystemService(Context.NOTIFICATION_SERVICE);
		mNotificationMgr.notify(0, notification);
	}
	
	@SuppressLint("NewApi")
	private void scaleNotification(String name) {
		// Prepare intent which is triggered if the notification is clicked.
		Intent intent = new Intent(getActivity(), Scale.class);
		intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
		PendingIntent contentIntent = PendingIntent.getActivity(getActivity(), 0, intent, 0);

		// Build notification
		Notification notification = new Notification.Builder(getActivity())
				.setContentTitle("Walk Through Time")
				.setContentText("You have reached the following scale: " + name + ". Click to view the map.")
				.setSmallIcon(R.drawable.ic_launcher)
				.setContentIntent(contentIntent)
				.setAutoCancel(true)
				.setDefaults(Notification.DEFAULT_SOUND | Notification.DEFAULT_VIBRATE | Notification.DEFAULT_LIGHTS)
				.build();


		// Display the notification
		mNotificationMgr = (NotificationManager) getActivity().getSystemService(Context.NOTIFICATION_SERVICE);
		mNotificationMgr.notify(1, notification);
	}
}
