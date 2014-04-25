package com.wisc.cs407project;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileWriter;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;

import org.json.JSONObject;

import android.app.AlertDialog;
import android.app.Fragment;
import android.app.FragmentTransaction;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.location.Location;
import android.location.LocationManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
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
import com.google.android.gms.maps.GoogleMap.OnMapClickListener;
import com.google.android.gms.maps.GoogleMap.OnMarkerDragListener;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;
import com.wisc.cs407project.PathBuilder.PathState;
import com.wisc.cs407project.PathBuilder.RouteJSONParser;

public class RecordFragment extends Fragment {
	private GoogleMap map;
	private Button recordButton, drawButton, modeButton, saveButton, undoButton;
	private LocationManager locationMan;
	private RecordLocationListener locationLis;
	private boolean recording, validPath, drawing, markerPlaced, atLeastTwoPoints;
	private boolean inRecordMode = true;
	//private ArrayList<Polyline> pathLines = new ArrayList<Polyline>();
	private LatLng dragOffset;
	private PathState state;
	//// UNDO State Tracking ////
	//private ArrayList<LatLng> currentMarkerLoc, lastMarkerLoc;
	//private LatLng incrementalCurLoc, incrementalLastLoc;
	//private ArrayList<ArrayList<Polyline>> undoLinesList;
	//private ArrayList<ArrayList<LatLng>> undoMarkersList;
	//private ArrayList<Marker> currentMarkers;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		super.onCreateView(inflater, container, savedInstanceState);
		final View myFragmentView = inflater.inflate(R.layout.recordfragment, container, false);
		map = ((MapFragment) getFragmentManager().findFragmentById(R.id.mapRecord)).getMap();
		map.setMyLocationEnabled(true);
		mapClicked();

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
		drawButton = (Button) myFragmentView.findViewById(R.id.drawPathButton);
		drawClicked();
		modeButton = (Button) myFragmentView.findViewById(R.id.rfModeButton);
		modeButton.setVisibility(View.GONE);
		saveButton = (Button) myFragmentView.findViewById(R.id.rfSaveButton);
		saveButton.setVisibility(View.GONE);
		undoButton = (Button) myFragmentView.findViewById(R.id.rfUndoButton);
		undoButton.setVisibility(View.GONE);
		return myFragmentView;
	}

	@Override
	public void onDestroyView() {
		super.onDestroyView();
		Fragment fragment = (getFragmentManager().findFragmentById(R.id.mapRecord));
		// Avoids trying to commit after parent activity has been destroyed
		if (fragment.isResumed()) {
			FragmentTransaction ft = getActivity().getFragmentManager().beginTransaction();
			ft.remove(fragment);
			ft.commit();
		}
	}

	private void markerDragged() {
		map.setOnMarkerDragListener(new OnMarkerDragListener() {

			@Override
			public void onMarkerDrag(Marker marker) {
				// Ignore Google's stupid repositioning of the marker
				marker.setPosition(new LatLng(marker.getPosition().latitude - dragOffset.latitude, marker.getPosition().longitude - dragOffset.longitude));
				if (drawing && MapUtils.getDistance(state.currentState.leadingMarker.latitude, state.currentState.leadingMarker.longitude, marker.getPosition().latitude, marker.getPosition().longitude) > 10) {
					state.extendState(marker.getPosition(), marker);
				}
			}

			@Override
			public void onMarkerDragEnd(Marker marker) {
				if (drawing) {
					// Ignore Google's stupid repositioning of the marker
					marker.setPosition(state.currentState.leadingMarker);
				}
				else {
					state.refactor();
					String dirUrl = getDirectionsUrl(state.currentState.leadingMarker, new LatLng(marker.getPosition().latitude - dragOffset.latitude, marker.getPosition().longitude - dragOffset.longitude));
					// Draw route
					new DownloadTask().execute(dirUrl);
				}
			}

			@Override
			public void onMarkerDragStart(Marker marker) {
				if (drawing) {
					// Ignore Google's stupid repositioning of the marker
					dragOffset = new LatLng(marker.getPosition().latitude - state.currentState.leadingMarker.latitude, marker.getPosition().longitude - state.currentState.leadingMarker.longitude);
					marker.setPosition(state.currentState.leadingMarker);
					ArrayList<LatLng> newLine = new ArrayList<LatLng>();
					newLine.add(state.currentState.leadingMarker);
					state.addState(false, newLine, marker);
				} else {
					// Ignore Google's stupid repositioning of the marker
					dragOffset = new LatLng(marker.getPosition().latitude - state.currentState.leadingMarker.latitude, marker.getPosition().longitude - state.currentState.leadingMarker.longitude);
					marker.setPosition(state.currentState.leadingMarker);
					state.prepareForRefactor();
				}
			}
		});
	}

	private void mapClicked() {
		map.setOnMapClickListener(new OnMapClickListener() {
			@Override
			public void onMapClick(LatLng coords) {
				if (!inRecordMode) {
					// Drawing mode
					if (drawing) {
						if (!markerPlaced) {
							ArrayList<LatLng> newLine = new ArrayList<LatLng>();
							newLine.add(coords);
							state.addState(false, newLine, null);
							markerPlaced = true;
						}
					}
					// Connect mode
					else {
						if (markerPlaced) {
							String dirUrl = getDirectionsUrl(state.currentState.leadingMarker, coords);
							// Draw route
							new DownloadTask().execute(dirUrl);
						} else {
							ArrayList<LatLng> newLine = new ArrayList<LatLng>();
							newLine.add(coords);
							state.addState(false, newLine, null);
							markerPlaced = true;
						}
					}
				}
			}
		});
	}
// TODO make asynctasks not crash with screen flip
	private void undoLast() {
		state.revertState();
	}

	private void drawClicked() {
		drawButton.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				// Initialize the path drawing buttons, etc
				drawButton.setVisibility(View.GONE);
				recordButton.setVisibility(View.GONE);
				modeButton.setVisibility(View.VISIBLE);
				drawing = true;
				modeClicked();
				saveButton.setVisibility(View.VISIBLE);
				saveClicked();
				undoButton.setVisibility(View.VISIBLE);
				undoClicked();
				map.setMyLocationEnabled(false);
				inRecordMode = false;
				markerDragged();
				// Initialize (or reset) state trackers
				markerPlaced = false;
				state = new PathState(map);
			}
		});
	}

	private void undoClicked() {
		undoButton.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				undoLast();
			}
		});
	}

	private void modeClicked() {
		modeButton.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				if (!drawing) {
					modeButton.setText("Draw Mode");
					drawing = true;
					// Make sure leading marker is draggable
					if (state.currentLeadingMarker != null) {
						state.currentLeadingMarker.setDraggable(true);
					}
				}
				else {
					modeButton.setText("Connect Mode");
					drawing = false;
					// Don't allow refactoring if there's no path to refactor
					if (state.currentLaggingMarker == null) {
						if (state.currentLeadingMarker != null) {
							state.currentLeadingMarker.setDraggable(false);
						}
					}
				}
			}
		});
	}

	private void saveClicked() {
		saveButton.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				Log.d("Button", "save");

			}
		});
	}

	private void recordClicked() {	
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
						View view = toast.getView();
						view.setBackgroundResource(R.color.grey);
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

	private String getDirectionsUrl(LatLng origin,LatLng dest){
		// Origin
		String str_origin = "origin="+origin.latitude+","+origin.longitude;
		// Destination
		String str_dest = "destination="+dest.latitude+","+dest.longitude;
		// Sensor enabled
		String sensor = "sensor=false";
		// Walking mode
		String mode = "mode=walking";
		// Building the parameters to the web service
		String parameters = str_origin+"&"+str_dest+"&"+sensor+"&"+mode;
		// Output format
		String output = "json";
		// Building the url to the web service
		String url = "https://maps.googleapis.com/maps/api/directions/"+output+"?"+parameters;
		return url;
	}
	
    private class DownloadTask extends AsyncTask<String, Void, String>{
    	@Override
    	protected void onPreExecute() {
    		super.onPreExecute();
    		// TODO display waiting animation
    	}
        
        @Override
        protected String doInBackground(String... urlString) {
             String data = "";
            try{
            	InputStream inStream = null;
                HttpURLConnection urlConnection = null;
                try{
                    URL url = new URL(urlString[0]);
                    urlConnection = (HttpURLConnection) url.openConnection();
                    urlConnection.connect();
                    inStream = urlConnection.getInputStream();
                    BufferedReader bReader = new BufferedReader(new InputStreamReader(inStream));
                    StringBuffer sBuffer = new StringBuffer();
                    String line = "";
                    while( ( line = bReader.readLine()) != null){
                        sBuffer.append(line);
                    }
                    data = sBuffer.toString();
                    bReader.close();
                }catch(Exception e){
                    Log.d("Exception while downloading url", e.toString());
                }finally{
                    inStream.close();
                    urlConnection.disconnect();
                }
            }catch(Exception e){
                Log.d("Exception: DownloadTask",e.toString());
            }
            return data;
        }

        @Override
        protected void onPostExecute(String result) {
            super.onPostExecute(result);
 
            ParserTask parserTask = new ParserTask();
 
            // Invokes the thread for parsing the JSON data
            parserTask.execute(result);
        }
    }
    
    /** A class to parse the Google Places in JSON format */
    private class ParserTask extends AsyncTask<String, Integer, ArrayList<LatLng> >{
        @Override
        protected ArrayList<LatLng> doInBackground(String... jsonData) {
 
            JSONObject jObject;
            ArrayList<LatLng> route = null;
 
            try{
                jObject = new JSONObject(jsonData[0]);
                RouteJSONParser parser = new RouteJSONParser();
 
                // Starts parsing data
                route = parser.parse(jObject);
            }catch(Exception e){
                e.printStackTrace();
            }
            return route;
        }
 
        // Executes in UI thread, after the parsing process
        @Override
        protected void onPostExecute(ArrayList<LatLng> result) {
        	state.addState(true, result, null);
        }
    }
}
