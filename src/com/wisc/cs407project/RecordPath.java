package com.wisc.cs407project;

import java.io.File;
import java.io.FileWriter;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.PolylineOptions;
import com.wisc.cs407project.R;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

public class RecordPath extends FragmentActivity {
	private RecordPath ref;
	private GoogleMap map;
	private LocationManager locationMan;
	private RecordLocationListener locationLis;
	private boolean recording;
	private boolean validPath;
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.recordpath);
		ref = this;
		
		Fragment f = getSupportFragmentManager().findFragmentById(R.id.map);
		SupportMapFragment mf = (SupportMapFragment)f;
        map = mf.getMap();
        map.setMyLocationEnabled(true);
        
        // LocationClient is more efficient to use rather than LocationManager.
        locationMan = (LocationManager) this.getSystemService(LOCATION_SERVICE);
        Location location = locationMan.getLastKnownLocation(LocationManager.GPS_PROVIDER);
        if (location != null)
        {
         map.moveCamera(CameraUpdateFactory.newLatLng(new LatLng(location.getLatitude(), location.getLongitude())));
         map.animateCamera(CameraUpdateFactory.zoomTo(15));
        }
		//locationLis = new RecordLocationListener(this);
		locationMan.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 2, locationLis);
	}
	
	public void recordClicked(View view) {
		if (!recording)
		{
			Button recordButton = (Button)findViewById(R.id.recordbutton);
			recordButton.setText("Finish Recording");
			locationLis.StartRecording();
			recording = true;
		}
		else
		{
			if (!validPath)
			{
				Intent intent = new Intent(ref, Popup.class);
				intent.putExtra("title", "Warning");
				intent.putExtra("text", "The required minimum of two points has not yet been recorded.");
				startActivity(intent);
				return;
			}
			final String output = locationLis.StopRecording();
			AlertDialog.Builder alertBuilder = new AlertDialog.Builder(this);

			alertBuilder.setTitle("Creating the path file");
			
			LinearLayout layout = new LinearLayout(this);
			layout.setOrientation(LinearLayout.VERTICAL);
			// Set an EditText view to get user input
			final TextView firstMessage = new TextView(this);
			firstMessage.setText("Input the file name for this path file.");
			firstMessage.setTextSize(15);
			final EditText fileName = new EditText(this);
			final TextView secondMessage = new TextView(this);
			secondMessage.setText("Input a description of this path.");
			secondMessage.setTextSize(15);
			final EditText description = new EditText(this);
			layout.addView(firstMessage);
			layout.addView(fileName);
			layout.addView(secondMessage);
			layout.addView(description);
			alertBuilder.setView(layout);

			alertBuilder.setPositiveButton("Save", null);

			alertBuilder.setNegativeButton("Discard", new DialogInterface.OnClickListener() {
				public void onClick(DialogInterface dialog, int whichButton) {
					ref.finish();
				}
			});
			final AlertDialog alert = alertBuilder.create();
			alert.setOnShowListener(new DialogInterface.OnShowListener() {

			    @Override
			    public void onShow(DialogInterface dialog) {

			        Button b = alert.getButton(AlertDialog.BUTTON_POSITIVE);
			        b.setOnClickListener(new View.OnClickListener() {

			            @Override
			            public void onClick(View view) {
			            	if (fileName.getText().toString().isEmpty())
							{
								Intent intent = new Intent(ref, Popup.class);
								intent.putExtra("title", "Error");
								intent.putExtra("text", "File name cannot be left empty.");
								ref.startActivity(intent);
							}
			            	else if (description.getText().toString().isEmpty())
			            	{
								Intent intent = new Intent(ref, Popup.class);
								intent.putExtra("title", "Error");
								intent.putExtra("text", "Description cannot be left empty.");
								ref.startActivity(intent);
			            	}
			            	else
			            	{
								File file = new File(ref.getFilesDir(), fileName.getText().toString());
								if (file.exists())
								{
									Intent intent = new Intent(ref, Popup.class);
									intent.putExtra("title", "Error");
									intent.putExtra("text", "The file name provided already exists.");
									ref.startActivity(intent);
								}
								else
								{
									try {
										FileWriter paths = new FileWriter(new File(ref.getFilesDir(), "Paths.txt"), true);
										paths.write(description.getText().toString()+"\t"+fileName.getText().toString()+"\n");
										paths.flush();
										paths.close();
										FileWriter newPath = new FileWriter(new File(ref.getFilesDir(), fileName.getText().toString()));
										newPath.write("<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n"+
												"<kml xmlns=\"http://earth.google.com/kml/2.2\">\n"+
												"<Document>\n"+
												"<name>"+description.getText().toString()+"</name>\n"+
												"<Placemark>\n<name>"+description.getText().toString()+"</name>\n"+
												"<LineString>\n<tessellate>1</tessellate>\n<coordinates>"+
												output+
											    "</coordinates>\n</LineString>\n</Placemark>\n</Document>\n</kml>");
										newPath.flush();
										newPath.close();
										ref.finish();
										alert.dismiss();
										DirUtils.storeDir(getExternalFilesDir(null).getAbsolutePath() + "/dirs.txt", ref.getFilesDir().getAbsolutePath());
									}
									catch (Exception e) {
										
									}
								}
			            	}
			            }
			        });
			    }
			});
			alert.show();
		}
	}
	
	public void addEdge(LatLng last, LatLng current)
	{
		validPath = true;
		map.addPolyline(new PolylineOptions().add(last).add(current));
	}
}
