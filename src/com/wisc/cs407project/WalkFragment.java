package com.wisc.cs407project;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.apache.commons.validator.routines.UrlValidator;
import org.w3c.dom.Document;
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
import android.graphics.Color;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.util.Log;
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
import com.parse.GetCallback;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.wisc.cs407project.ParseObjects.ScaleObject;

public class WalkFragment extends Fragment implements OnMarkerClickListener, LocationListener {
	private GoogleMap map;
	private Button pathButton;
	private Button scaleButton;
	private Button walkButton;
	private Button stopButton;
	private com.wisc.cs407project.ParseObjects.Scale scaleItem;
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
		FragmentTransaction ft = getActivity().getFragmentManager().beginTransaction();
		ft.remove(fragment);
		ft.commit();
	}
	
	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		if (resultCode == 1) {
			Bundle extra = data.getExtras();
			String id = extra.getString("scaleItem");			
			if (!id.isEmpty()) {
				ParseQuery<ParseObject> query = ParseQuery.getQuery(
						com.wisc.cs407project.ParseObjects.Scale.class.getSimpleName());
				query.getInBackground(id, new GetCallback<ParseObject>() {
				  public void done(ParseObject object, ParseException e) {
				    if (e == null) {
				      scaleItem = new com.wisc.cs407project.ParseObjects.Scale(object);
				      scaleItemList = scaleItem.GetObjects();
				    } else {
				      // something went wrong
				    }
				  }
				});
				scaleButton.setBackgroundColor(Color.GRAY);
			}
		} else if (resultCode == 2) {
			Bundle extra = data.getExtras();
			pathURL = extra.getString("path");
			localPath = extra.getBoolean("localPath", false);
			if (pathURL != null) {
				pathButton.setBackgroundColor(Color.GRAY);
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
					//locationLis = new ScaleLocationListener(ref);
					locationMan.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 2, ref);
					new LoadIndividualPathTask().execute(pathURL);
				} else if (scaleItem != null && pathURL == null) {
					Toast toast = Toast.makeText(getActivity(), "Please select a path.", Toast.LENGTH_SHORT);
					toast.setGravity(Gravity.CENTER, 0, 0);
					toast.show();
				} else if (scaleItem == null && pathURL != null) {
					Toast toast = Toast.makeText(getActivity(), "Please select a scale.", Toast.LENGTH_SHORT);
					toast.setGravity(Gravity.CENTER, 0, 0);
					toast.show();
				} else {
					Toast toast = Toast.makeText(getActivity(), "Please select a path and scale.", Toast.LENGTH_SHORT);
					toast.setGravity(Gravity.CENTER, 0, 0);
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
				
				//locationLis = null;
				// Clear map
				map.clear();
				getActivity().findViewById(R.id.linearLayout1).setVisibility(View.GONE);
				getActivity().findViewById(R.id.relativeLayout1).setVisibility(View.VISIBLE);
				
				// Need to change this
				scaleButton.setBackgroundResource(android.R.drawable.btn_default);
				pathButton.setBackgroundResource(android.R.drawable.btn_default);
				
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
            //DocumentBuilder docBuilder = DocumentBuilderFactory.newInstance().newDocumentBuilder();
			//Document docScale = docBuilder.parse(new ByteArrayInputStream(scaleItem.getBytes()));
            //NodeList scaleItems = docScale.getElementsByTagName("scaleItem");
           // ArrayList<ScaleObject> scaleItems = scaleItem.GetObjects();
            Log.d("scale items", ""+scaleItemList.size());
            
            for (int i = 0; i < scaleItemList.size(); i++) {
            	
            	ScaleObject so = scaleItemList.get(i);
            	new LoadImageTask().execute(so);
            	           	
            	Log.d("item", ""+i);
            	
            	double dist = so.GetPercentage() * totalDist;
        		so.distance = dist;
        		Log.d("dist", ""+so.distance);
        		
        		double cummDist = 0;
        		int step = 0;
        		while (step < steps.size() && cummDist + steps.get(step) < dist)
        		{
        			cummDist += steps.get(step);
        			step++;
        		}
        		Log.d("step", ""+step);
        		if (step != steps.size())
        		{
        			double percentageOfStep = (dist - cummDist)/steps.get(step);
        			double lat = points.get(step).latitude + (points.get(step+1).latitude - points.get(step).latitude)*percentageOfStep;
        			double lon = points.get(step).longitude + (points.get(step+1).longitude - points.get(step).longitude)*percentageOfStep;
        			so.position = new LatLng(lat, lon);
        			Log.d("point "+i+":", lat + ", " + lon);
        			so.marker = map.addMarker(new MarkerOptions().position(so.position).title(so.GetName()));
        		}
        		else
        		{
        			so.marker = map.addMarker(new MarkerOptions().position(points.get(i)).title(so.GetName()));
        			so.position = points.get(i);
        		}
        		//scaleItemList.add(so);
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
				if (so.distance - distanceTraveled < distanceInterval)
				{
					Intent intent = new Intent(getActivity(), Popup.class);
					intent.putExtra("title", so.GetName());
					intent.putExtra("image", so.image);
					intent.putExtra("text", so.GetText());
					startActivity(intent);
					return true;
				}
				break;
			}
		}
		return false;
	}
	
	private class LoadImageTask extends AsyncTask<ScaleObject, Void, String> {
	
		@Override
		protected String doInBackground(ScaleObject... arg0) {
			try {
				URL url = new URL(arg0[0].GetImageLocation());
				InputStream in = url.openStream();
				BufferedInputStream buf = new BufferedInputStream(in);
				Bitmap myBitmap = BitmapFactory.decodeStream(buf);
				if (in != null) {
                in.close();
            	}
					if (buf != null) {
                buf.close();
					}
					arg0[0].image = myBitmap;
			} catch (Exception e) {
			}
			return "";
		}
	}

	@Override
	public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
		inflater.inflate(R.menu.settings, menu);
		super.onCreateOptionsMenu(menu, inflater);
		menu.add(0, 1, 1, "New Scale");
		menu.add(0, 2, 2, "New Path");
	}
	
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		Intent newIntent;
		switch (item.getItemId()) {
		case 1 :
			getActivity().getIntent().removeExtra("pathItem"); 
			getActivity().getIntent().removeExtra("scaleItem"); 
			newIntent = new Intent(getActivity(), ScaleChooser.class);
			newIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
			startActivity(newIntent);
			return true;
		case 2 :
			getActivity().finish();
			return true;
		}
		return true;
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
		//locationLis = new ScaleLocationListener(ref);
		locationMan.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 2, this);
		inFocus = true;
	}

	private class LoadIndividualPathTask extends AsyncTask<String, Void, String> {

		@Override
		protected String doInBackground(String... arg0) {
			try {
				BufferedReader in = null;
				if (localPath) {
					in = new BufferedReader(new InputStreamReader(new FileInputStream(new File(getActivity().getFilesDir(), arg0[0]))));
				}
				else {
					UrlValidator validator = new UrlValidator();
					if(validator.isValid(arg0[0])) {
						in = new BufferedReader(new InputStreamReader(new URL(arg0[0]).openStream()));;
					} else if(new File(arg0[0]).exists()) {
						in = new BufferedReader(new FileReader(arg0[0]));
					} else {
						Intent intent = new Intent(getActivity(), Popup.class);
						intent.putExtra("title", "Error");
						intent.putExtra("text", "Invalid Directory Location");
						startActivity(intent);
					}
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
				try {
					DocumentBuilder docBuilder = DocumentBuilderFactory.newInstance().newDocumentBuilder();
					Document doc = docBuilder.parse(new ByteArrayInputStream(pathItem.getBytes()));
					NodeList items = doc.getElementsByTagName("gx:coord");
					if (items.getLength() < 2) {
						items = doc.getElementsByTagName("coordinates");
						for (int i = 0; i < items.getLength(); i++)
						{
							if (items.item(i).getFirstChild()!= null && items.item(i).getFirstChild().getNodeValue().trim().contains("\n"))
							{
								String content = items.item(i).getFirstChild().getNodeValue().trim();
								List<String> coordinates = new ArrayList<String>();
								while (content.contains("\n"))
								{
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
						for (int i =0; i < items.getLength(); i++)
						{
							coordinates.add(items.item(i).getTextContent().replace(" ",","));
						}
						populate(coordinates);
					}
				}
				catch (Exception e)
				{
					Intent intent = new Intent(getActivity(), Popup.class);
					intent.putExtra("title", "Error");
					intent.putExtra("text", "The requested path is not in the correct format.");
					startActivityForResult(intent, 0);
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
				for (com.wisc.cs407project.ParseObjects.ScaleObject so : scaleItemList) {
					if (!so.opened && Math.abs(distanceTraveled - so.distance) < distanceInterval && MapUtils.getDistance(so.position.latitude, so.position.longitude, loc.getLatitude(), loc.getLongitude()) < 30) {
						so.opened = true;
						// Display notification that user has reached a scale when app is not focused.
						if (!inFocus) {
							scaleNotification(so.GetName());
						}
						Intent intent = new Intent(getActivity(), Popup.class);
						intent.putExtra("title", so.GetName());
						intent.putExtra("image", so.image);
						intent.putExtra("text", so.GetText());
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
		//intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
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
		//intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
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
