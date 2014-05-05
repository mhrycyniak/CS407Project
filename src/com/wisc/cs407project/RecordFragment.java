package com.wisc.cs407project;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;

import org.json.JSONObject;

import android.app.AlertDialog;
import android.app.Fragment;
import android.app.FragmentTransaction;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.location.Location;
import android.location.LocationManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Environment;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.GoogleMap.OnMapClickListener;
import com.google.android.gms.maps.GoogleMap.OnMarkerDragListener;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.PolylineOptions;
import com.wisc.cs407project.PathBuilder.PathState;
import com.wisc.cs407project.PathBuilder.Route;
import com.wisc.cs407project.PathBuilder.RouteJSONParser;

public class RecordFragment extends Fragment {
	private GoogleMap map;
	private EditText fromText, toText;
	private LinearLayout searchBox;
	private Button recordButton, drawButton, modeButton, saveButton, undoButton, locationButton, goButton;
	private LocationManager locationMan;
	private RecordLocationListener locationLis;
	private boolean recording, validPath, drawing, markerPlaced, locationOpen, warningsShown;//, atLeastTwoPoints;
	private boolean inRecordMode = true;

	private LatLng dragOffset;
	private PathState state;
	private String builtPath;

	// Our handler for received Intents.
	private BroadcastReceiver mMessageReceiver = new BroadcastReceiver() {
		@Override
		public void onReceive(Context context, Intent intent) {
			// Reset everything
			resetFragment();
		}
	};

	private void resetFragment() {
		// Reset everything
		map.clear();
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

		inRecordMode = false;
		drawing = false;
		markerPlaced = false;
		recording = false;
		validPath = false;

		recordButton.setText("Record Path");
		recordButton.setVisibility(View.VISIBLE);
		recordClicked();
		drawButton.setVisibility(View.VISIBLE);
		drawClicked();
		modeButton.setVisibility(View.GONE);
		saveButton.setVisibility(View.GONE);
		undoButton.setVisibility(View.GONE); 
		locationButton.setVisibility(View.GONE);
		searchBox.setVisibility(View.GONE);
	}

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
		locationButton = (Button) myFragmentView.findViewById(R.id.rfLocationButton);
		locationButton.setVisibility(View.GONE);
		goButton = (Button) myFragmentView.findViewById(R.id.rfLocationGoButton);
		fromText = (EditText) myFragmentView.findViewById(R.id.rfFromEditText);
		toText = (EditText) myFragmentView.findViewById(R.id.rfToEditText);
		searchBox = (LinearLayout) myFragmentView.findViewById(R.id.rfSearchBox);
		searchBox.setVisibility(View.GONE);

		return myFragmentView;
	}

	@Override
	public void onStop() {
		// Unregister since the activity is about to be closed.
		LocalBroadcastManager.getInstance(getActivity()).unregisterReceiver(mMessageReceiver);
		super.onStop();
	}

	@Override
	public void onResume() {
		// Register to receive messages.
		LocalBroadcastManager.getInstance(getActivity()).registerReceiver(mMessageReceiver,
				new IntentFilter("RESET_RECORD_FRAGMENT"));
		super.onResume();
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
				if (!locationOpen) {
					// Ignore Google's stupid repositioning of the marker
					marker.setPosition(new LatLng(marker.getPosition().latitude - dragOffset.latitude, marker.getPosition().longitude - dragOffset.longitude));
					if (drawing && MapUtils.getDistance(state.currentState.leadingMarker.latitude, state.currentState.leadingMarker.longitude, marker.getPosition().latitude, marker.getPosition().longitude) > 10) {
						state.extendState(marker.getPosition(), marker);
					}
				}
			}

			@Override
			public void onMarkerDragEnd(Marker marker) {
				if (!locationOpen) {
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
			}

			@Override
			public void onMarkerDragStart(Marker marker) {
				if (!locationOpen) {
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
			}
		});
	}

	private void mapClicked() {
		map.setOnMapClickListener(new OnMapClickListener() {
			@Override
			public void onMapClick(LatLng coords) {
				if (!inRecordMode && !locationOpen) {
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
		if (locationOpen) {
			locationButton.setBackgroundColor(getResources().getColor(R.color.grey));
			searchBox.setVisibility(View.GONE);
			locationOpen=false;
		}
		if (state.currentState != null && state.currentState.leadingMarker != null) {
			map.moveCamera(CameraUpdateFactory.newLatLng(state.currentState.leadingMarker));
			map.animateCamera(CameraUpdateFactory.zoomTo(15));
		} else {
			Location location = locationMan.getLastKnownLocation(LocationManager.GPS_PROVIDER);
			if (location != null) {
				map.moveCamera(CameraUpdateFactory.newLatLng(new LatLng(location.getLatitude(), location.getLongitude())));
				map.animateCamera(CameraUpdateFactory.zoomTo(15));
			}
			markerPlaced = false;
		}
	}

	private void goClicked() {
		goButton.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				if (markerPlaced && !fromText.getText().toString().equals(Double.toString(state.currentState.leadingMarker.latitude) + "," + Double.toString(state.currentState.leadingMarker.longitude))) {
					AlertDialog.Builder builder = new AlertDialog.Builder(v.getContext());

					builder.setMessage("You've altered the \"From\" location. Retreiving this route will overwrite any path you've already built. Would you like to proceed?").setTitle("Warning");

					// Add the buttons
					builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
						public void onClick(DialogInterface dialog, int id) {
							// reset state and map, get route
							map.clear();
							state = new PathState(map);
							fromText.setText(replaceSpaces(fromText.getText().toString()));
							toText.setText(replaceSpaces(toText.getText().toString()));
							String origin = fromText.getText().toString();
							String dest = toText.getText().toString();
							String dirUrl = getDirectionsUrl(origin, dest);
							InputMethodManager imm = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
							imm.hideSoftInputFromWindow(toText.getWindowToken(), 0);
							// Draw route
							new DownloadTask().execute(dirUrl);
							markerPlaced = true;
						}
					});
					builder.setNegativeButton("No", new DialogInterface.OnClickListener() {
						public void onClick(DialogInterface dialog, int id) {
							// reset the from location text
							fromText.setText(Double.toString(state.currentState.leadingMarker.latitude) + "," + Double.toString(state.currentState.leadingMarker.longitude));
						}
					});

					// Create and show
					builder.create();
					builder.show();
					return;
				} else {
					fromText.setText(replaceSpaces(fromText.getText().toString()));
					toText.setText(replaceSpaces(toText.getText().toString()));
					String origin = fromText.getText().toString();
					String dest = toText.getText().toString();
					String dirUrl = getDirectionsUrl(origin, dest);
					InputMethodManager imm = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
					imm.hideSoftInputFromWindow(toText.getWindowToken(), 0);
					// Draw route
					new DownloadTask().execute(dirUrl);
					markerPlaced = true;
				}
			}
		});
	}

	private void locationClicked() {
		locationButton.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				if (locationOpen) {
					locationButton.setBackgroundColor(getResources().getColor(R.color.grey));
					searchBox.setVisibility(View.GONE);
					locationOpen=false;
				} else {
					if (markerPlaced) {
						fromText.setText(Double.toString(state.currentState.leadingMarker.latitude) + "," + Double.toString(state.currentState.leadingMarker.longitude));
						map.moveCamera(CameraUpdateFactory.newLatLng(state.currentState.leadingMarker));
						map.animateCamera(CameraUpdateFactory.zoomTo(15));
					} else {
						fromText.setText("");
					}
					locationButton.setBackgroundColor(getResources().getColor(R.color.green));
					searchBox.setVisibility(View.VISIBLE);
					toText.setText("");
					locationOpen=true;
				}
			}
		});
	}

	private void drawClicked() {
		drawButton.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {

				FragmentTransaction ft = getActivity().getFragmentManager().beginTransaction();
				ft.addToBackStack(null).commit();
				// TODO add "what to do when pressed"


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
				locationButton.setVisibility(View.VISIBLE);
				locationClicked();
				goClicked();
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
				builtPath = state.getPath();
				if (state.stateList.size() < 2 || builtPath == null || builtPath.equals("")) {
					Intent intent = new Intent(getActivity(), Popup.class);
					intent.putExtra("title", "Warning");
					intent.putExtra("text", "The required minimum of two points has not yet been recorded.");
					startActivity(intent);
				}
				else {
					save(builtPath);
				}
			}
		});
	}

	private void recordClicked() {	
		recordButton.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				if (!recording) {
					if (locationMan.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
						FragmentTransaction ft = getActivity().getFragmentManager().beginTransaction();
						ft.addToBackStack(null).commit();

						recordButton.setText("Finish Recording");
						drawButton.setVisibility(View.GONE);
						locationLis.StartRecording();
						recording = true;
						inRecordMode = true;
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

						builder.setMessage("The required minimum of two points has not yet been recorded. Do you want to discard the path?").setTitle("Warning");

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
					save(output);

				}
			}
		});
	}
	//////////////////////////////////////////////////////////////
	private void save(final String fileContents) {
		AlertDialog.Builder alertBuilder = new AlertDialog.Builder(getActivity());

		alertBuilder.setTitle("Creating the path file");

		LinearLayout layout = new LinearLayout(getActivity());
		layout.setOrientation(LinearLayout.VERTICAL);

		// Set an EditText view to get user input
		final TextView firstMessage = new TextView(getActivity());
		firstMessage.setText("Input the file name for this path file.");
		firstMessage.setTextSize(15);
		final EditText fileName = new EditText(getActivity());
		layout.addView(firstMessage);
		layout.addView(fileName);
		alertBuilder.setView(layout);

		alertBuilder.setPositiveButton("Save", null);

		alertBuilder.setNegativeButton("Discard", new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface dialog, int whichButton) {
				// Discard recorded path by clearing map and resetting text.
				// TODO resetting needs to be fixed here
				if (inRecordMode) {
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
						} else {
							String directoryPath = Environment.getExternalStorageDirectory().toString();
							final String filePath = directoryPath + getResources().getString(R.string.app_path_directory) + "/" + fileName.getText().toString() + ".kml";
							File file = new File(filePath);
							if (file.exists()) {

								AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
								builder.setTitle("A file by this name already exists. Do you want to overwrite it?");
								// Add the buttons
								builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() 
								{
									public void onClick(DialogInterface dialog, int id) 
									{
										new SavePathTask().execute(filePath);
										dialog.dismiss();
										alert.dismiss();
									}
								});
								builder.setNegativeButton("No", new DialogInterface.OnClickListener() 
								{
									public void onClick(DialogInterface dialog, int id) 
									{
										dialog.dismiss();
									}
								});

								// Create and show
								builder.create();
								builder.show(); 

								return;
							} else {
								new SavePathTask().execute(filePath);
								alert.dismiss();
								return;
							}
						}
					}
				});
			}
		});
		alert.show();
	} ////////////////////////////////////////////////////

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

	private String getDirectionsUrl(String origin, String dest) {
		// Origin
		String str_origin = "origin="+origin;
		// Destination
		String str_dest = "destination="+dest;
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
		Log.d("search url", url);
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
			Log.d("result string", data);
			return data;
		}

		@Override
		protected void onPostExecute(String result) {
			super.onPostExecute(result);
			ParserTask parserTask = new ParserTask();
			parserTask.execute(result);
		}
	}

	/** A class to parse the Google Places in JSON format */
	private class ParserTask extends AsyncTask<String, Integer, Route >{
		@Override
		protected Route doInBackground(String... jsonData) {

			JSONObject jObject;
			Route route = null;

			try{
				jObject = new JSONObject(jsonData[0]);
				RouteJSONParser parser = new RouteJSONParser();

				// Starts parsing data
				route = parser.parse(jObject);
			} catch(Exception e){
				e.printStackTrace();
			}
			return route;
		}

		// Executes in UI thread, after the parsing process
		@Override
		protected void onPostExecute(Route result) {
			if (result == null || !result.status.equals("OK")) {
				Intent intent = new Intent(getActivity(), Popup.class);
				intent.putExtra("title", "Route Not Found");
				intent.putExtra("text", "Try adjusting your search parameters.");
				startActivity(intent);
			} else {
				state.addState(true, result.route, null);
				if (state.currentState.leadingMarker != null && state.currentState.laggingMarker != null) {
					LatLngBounds.Builder llb = new LatLngBounds.Builder();
					for (LatLng point : result.route) {
						llb.include(point);
					}
					LatLngBounds bounds = llb.build();
					//Change the padding as per needed
					CameraUpdate cu = CameraUpdateFactory.newLatLngBounds(bounds, 100);
					map.animateCamera(cu);
				}
				if (locationOpen) {
					locationButton.setBackgroundColor(getResources().getColor(R.color.grey));
					searchBox.setVisibility(View.GONE);
					locationOpen=false;
				}
				validPath = true;
				// Display copyrights and warnings
				String copyrights = result.copyrights + "\n";
				String warnings = "";
				if (result.warnings != null && !result.warnings.isEmpty()) {
					warnings = "Warnings:\n";
					for (int i = 0; i < result.warnings.size(); i++) {
						warnings = warnings + (i+1) + ". " + result.warnings.get(i) + "\n";
					}
				}
				// Display Copyrights and Warnings only once per program run
				if (!warningsShown) {
				final Toast toast = Toast.makeText(getActivity(), copyrights + warnings, Toast.LENGTH_SHORT);
				toast.setGravity(Gravity.BOTTOM, 0, saveButton.getHeight() + 20);
				View view = toast.getView();
				view.setBackgroundResource(R.color.grey);
				//toast.show();
				new CountDownTimer(9000, 1000) {
				    public void onTick(long millisUntilFinished) {toast.show();}
				    public void onFinish() {toast.show();}
				}.start();
				warningsShown = true;
				}
			}
		}
	}

	private class SavePathTask extends
	AsyncTask<String, Void, String> {

		@Override
		protected String doInBackground(String... arg0) {
			File saveFile = new File(arg0[0]);
			FileOutputStream fos;
			byte[] data = builtPath.getBytes();
			try {
				fos = new FileOutputStream(saveFile);
				fos.write(data);
				fos.flush();
				fos.close();
			} catch (FileNotFoundException e) {
				Log.e("SaveScaleTask", "FileNotFoundException");
			} catch (IOException e) {
				Log.e("SaveScaleTask", "IOException");
			}

			String filename = saveFile.getName();
			// Don't display Toast if this is the onStop() save
			if (arg0[0].equals(Environment.getExternalStorageDirectory().toString() + "/" 
					+ getResources().getString(R.string.app_name) + "/"
					+ getResources().getString(R.string.resume_backup_filename))) {
				return null;
			}
			return filename;
		}
		protected void onPostExecute(String filename) {
			if (filename != null) {
				Toast toast = Toast.makeText(getActivity(), filename + " saved", Toast.LENGTH_LONG);
				toast.setGravity(Gravity.CENTER, 0, 0);
				View view = toast.getView();
				view.setBackgroundResource(R.color.grey);
				toast.show();
			}
		}
	}

	private String replaceSpaces(String input) {
		return input.replace(' ', '+');
	}
}
