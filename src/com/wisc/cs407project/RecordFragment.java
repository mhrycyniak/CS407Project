package com.wisc.cs407project;

import java.io.File;
import java.io.FileWriter;

import android.app.AlertDialog;
import android.app.Fragment;
import android.app.FragmentTransaction;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.PolylineOptions;

public class RecordFragment extends Fragment {
	private GoogleMap map;
	private Button recordButton;
	private LocationManager locationMan;
	private RecordLocationListener locationLis;
	private boolean recording;
	private boolean validPath;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		super.onCreateView(inflater, container, savedInstanceState);
		final View myFragmentView = inflater.inflate(R.layout.recordfragment, container, false);
		map = ((MapFragment) getFragmentManager().findFragmentById(R.id.mapRecord)).getMap();
		map.setMyLocationEnabled(true);

		// LocationClient is more efficient to use rather than LocationManager.
		locationMan = (LocationManager) getActivity().getSystemService(Context.LOCATION_SERVICE);
		Location location = locationMan.getLastKnownLocation(LocationManager.GPS_PROVIDER);
		if (location != null) {
			map.moveCamera(CameraUpdateFactory.newLatLng(new LatLng(location.getLatitude(), location.getLongitude())));
			map.animateCamera(CameraUpdateFactory.zoomTo(15));
		}
		locationLis = new RecordLocationListener(this);
		locationMan.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 2, locationLis);
		
		recordButton = (Button) myFragmentView.findViewById(R.id.recordPath);
		recordClicked();
		return myFragmentView;
	}

	@Override
	public void onDestroyView() {
		super.onDestroyView();
		Fragment fragment = (getFragmentManager().findFragmentById(R.id.mapRecord));
		FragmentTransaction ft = getActivity().getFragmentManager().beginTransaction();
		ft.remove(fragment);
		ft.commit();
	}

	public void recordClicked() {	
		recordButton.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				if (!recording) {
					if (locationMan.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
						recordButton.setText("Finish Recording");
						locationLis.StartRecording();
						recording = true;
					} else {
						Toast toast = Toast.makeText(getActivity(), "Location not available right now.", Toast.LENGTH_SHORT);
						toast.setGravity(Gravity.CENTER, 0, 0);
						toast.show();
					}
					
				} else {
					// Build an alert dialog if the path is not valid.
					if (!validPath) {
						// AlertDialog.Builder
						AlertDialog.Builder builder = new AlertDialog.Builder(v.getContext());

						builder.setMessage("The required minimum of two points has not yet been recorded. Are you sure you want to stop recording?").setTitle("Warning");

						// Add the buttons
						builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
									public void onClick(DialogInterface dialog, int id) {
										// User wants to stop recording so clear map and reset text.
										map.clear();
										recordButton.setText("Record Path");
										recording = false;
										validPath = false;
										
										// Move to current location
										Location location = locationMan.getLastKnownLocation(LocationManager.GPS_PROVIDER);
										if (location != null) {
											map.moveCamera(CameraUpdateFactory.newLatLng(new LatLng(location.getLatitude(), location.getLongitude())));
											map.animateCamera(CameraUpdateFactory.zoomTo(15));
										}
									}
								});
						builder.setNegativeButton("No", new DialogInterface.OnClickListener() {
									public void onClick(DialogInterface dialog, int id) {
										// Do nothing if user still wants to continue recording.
									}
								});

						// Create and show
						builder.create();
						builder.show();
						return;
					}
					
					// Stop recording path.
					final String output = locationLis.StopRecording();
					AlertDialog.Builder alertBuilder = new AlertDialog.Builder(getActivity());

					alertBuilder.setTitle("Creating the path file");

					LinearLayout layout = new LinearLayout(getActivity());
					layout.setOrientation(LinearLayout.VERTICAL);

					// Set an EditText view to get user input
					final TextView firstMessage = new TextView(getActivity());
					firstMessage.setText("Input the file name for this path file.");
					firstMessage.setTextSize(15);
					final EditText fileName = new EditText(getActivity());
					final TextView secondMessage = new TextView(getActivity());
					secondMessage.setText("Input a description of this path.");
					secondMessage.setTextSize(15);
					final EditText description = new EditText(getActivity());
					layout.addView(firstMessage);
					layout.addView(fileName);
					layout.addView(secondMessage);
					layout.addView(description);
					alertBuilder.setView(layout);

					alertBuilder.setPositiveButton("Save", null);

					alertBuilder.setNegativeButton("Discard", new DialogInterface.OnClickListener() {
						public void onClick(DialogInterface dialog, int whichButton) {
							// Discard recorded path by clearing map and resetting text.
							map.clear();
							recordButton.setText("Record Path");
							recording = false;
							validPath = false;
							
							// Move to current location
							Location location = locationMan.getLastKnownLocation(LocationManager.GPS_PROVIDER);
							if (location != null) {
								map.moveCamera(CameraUpdateFactory.newLatLng(new LatLng(location.getLatitude(), location.getLongitude())));
								map.animateCamera(CameraUpdateFactory.zoomTo(15));
							}
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
									if (fileName.getText().toString().isEmpty()) {
										Intent intent = new Intent(getActivity(), Popup.class);
										intent.putExtra("title", "Error");
										intent.putExtra("text", "File name cannot be left empty.");
										getActivity().startActivity(intent);
									} else if (description.getText().toString().isEmpty()) {
										Intent intent = new Intent(getActivity(), Popup.class);
										intent.putExtra("title", "Error");
										intent.putExtra("text", "Description cannot be left empty.");
										getActivity().startActivity(intent);
									} else {
										File file = new File(getActivity().getFilesDir(), fileName.getText().toString());
										if (file.exists()) {
											Intent intent = new Intent(getActivity(), Popup.class);
											intent.putExtra("title", "Error");
											intent.putExtra("text", "The file name provided already exists.");
											getActivity().startActivity(intent);
										} else {
											try {
												FileWriter paths = new FileWriter(new File(getActivity().getFilesDir(),"Paths.txt"), true);
												paths.write(description.getText().toString() + "\t" + fileName.getText().toString() + "\n");
												paths.flush();
												paths.close();
												FileWriter newPath = new FileWriter(new File(getActivity().getFilesDir(),fileName.getText().toString()));
												newPath.write("<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n"
														+ "<kml xmlns=\"http://earth.google.com/kml/2.2\">\n"
														+ "<Document>\n"
														+ "<name>"
														+ description.getText().toString()
														+ "</name>\n"
														+ "<Placemark>\n<name>"
														+ description.getText().toString()
														+ "</name>\n"
														+ "<LineString>\n<tessellate>1</tessellate>\n<coordinates>"
														+ output
														+ "</coordinates>\n</LineString>\n</Placemark>\n</Document>\n</kml>");
												newPath.flush();
												newPath.close();
												getActivity().finish();
												alert.dismiss();
												DirUtils.storeDir(getActivity().getExternalFilesDir(null).getAbsolutePath() 
														+ "/dirs.txt", getActivity().getFilesDir().getAbsolutePath());
											} catch (Exception e) {

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
		});
	}

	public void addEdge(LatLng last, LatLng current) {
		validPath = true;
		map.addPolyline(new PolylineOptions().add(last).add(current));
	}
}
