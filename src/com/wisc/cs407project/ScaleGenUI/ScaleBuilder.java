package com.wisc.cs407project.ScaleGenUI;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.apache.commons.validator.routines.UrlValidator;
import org.w3c.dom.Document;
import org.w3c.dom.NodeList;

import com.wisc.cs407project.Popup;
import com.wisc.cs407project.R;
import com.wisc.cs407project.R.id;
import com.wisc.cs407project.R.layout;
import com.wisc.cs407project.R.string;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Rect;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.view.ViewTreeObserver.OnGlobalLayoutListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

public class ScaleBuilder extends Activity {

	//TODO ACTIVITY CRASHES WITH ORIENTATION CHANGE!!
	// Monotony for the sake of clarity
	public static final String KEY_NAME_EDIT = "nameEdit";
	public static final String KEY_COMPARABLE_LABEL = "comparableLabel";
	public static final String KEY_COMPARABLE_EDIT = "comparableEdit";
	public static final String KEY_UNITS = "units";
	public static final String KEY_DESCRIPTION_EDIT = "descriptionEdit";
	public static final String KEY_IMAGE_LOCATION_EDIT = "imageLocationEdit";

	ListView list;
	BuilderListAdapter adapter;
	private ScaleBuilder ref;
	String scaleText = "";
	ScaleGenerator scale;
	EditText headerName;
	EditText headerUnits;
	Button headerAdd, headerSort, headerSave;
	String loadedPath = "";


	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.scale_builder);
		ref = this;

		// Check intent to see if New or Editing
		Intent intent = getIntent();
		boolean loadNeeded = intent.getBooleanExtra("Load", false);
		
		if (loadNeeded) {
			loadedPath = intent.getStringExtra("Path");
			scale = new ScaleGenerator();

			//TODO make this dynamic, with menu
			String path = Environment.getExternalStorageDirectory().toString() + "/Pictures/Andrew/test_scale.xml";
			new LoadIndividualScaleTask().execute(path);

			// Find the header views
			// Header Name and Change Listener
			headerName = (EditText)findViewById(R.id.builderHeaderName);
			headerName.addTextChangedListener(new TextWatcher(){
		        public void afterTextChanged(Editable s) {
		        	// Avoid a potentially infinite change loop
		        	if (!scale.scaleName.equals(s)) {
		        		scale.scaleName = s.toString();
		        	}
		        }
		        public void beforeTextChanged(CharSequence s, int start, int count, int after){}
		        public void onTextChanged(CharSequence s, int start, int before, int count){}
		    });
			
			// Header Units and Change Listener
			headerUnits = (EditText)findViewById(R.id.builderHeaderUnits);
			headerUnits.addTextChangedListener(new TextWatcher(){
		        public void afterTextChanged(Editable s) {
		        	// Avoid a potentially infinite change loop
		        	if (!scale.scaleMetric.equals(s)) {
		        		scale.scaleMetric = s.toString();
		        		adapter.notifyDataSetChanged(); // Change the label in each list item as well
		        	}
		        }
		        public void beforeTextChanged(CharSequence s, int start, int count, int after){}
		        public void onTextChanged(CharSequence s, int start, int before, int count){}
		    });
			
			// Add Button
			headerAdd = (Button)findViewById(R.id.builderHeaderAddButton);
			headerAdd.setOnClickListener(new View.OnClickListener() {
				public void onClick(View v) {
					scale.addNew();
					adapter.notifyItemAdded();
					list.setSelection(0);
				}
			});
			
			// Add Button
			headerSort = (Button)findViewById(R.id.builderHeaderSortButton);
			headerSort.setOnClickListener(new View.OnClickListener() {
				public void onClick(View v) {
					scale.sort();
					adapter.notifyDataSetChanged();
				}
			});
			
			// Add Button
			headerSave = (Button)findViewById(R.id.builderHeaderSaveButton);
			headerSave.setOnClickListener(new View.OnClickListener() {
				public void onClick(View v) {
					// Let's just make sure
					adapter.notifyDataSetChanged();
					
					// Check access to device
					String extState = Environment.getExternalStorageState();
					if(!extState.equals(Environment.MEDIA_MOUNTED)) {
						//TODO make proper error here
						Log.e("ERROR", "Storage not mounted");
						return;
					}
					
					// Check if name is empty
					String scaleName = scale.scaleName;
					if (scaleName.equals("")) {
						//TODO make proper error here
						Log.e("ERROR", "Scale must have a name");
					}
					
					// Check if name is already used (by a file other than what was loaded)
					scaleName = scaleName + ".xml";
					String path = Environment.getExternalStorageDirectory().toString();
					path = path + getResources().getString(R.string.app_scale_directory) + "/" + scaleName;
					if ((!path.equals(loadedPath)) && (new File(path).exists())) {
						//TODO make overwrite confirmation
					}
					
					// Save (with Toast confirmation) (check warning flag, possibly)
					// DEFAULT IS TO OVERWRITE
					File saveFile = new File(path);
					FileOutputStream fos;
					String xmlFile = scale.getXML();
					//boolean warning = scale.xmlWarningFlag;
					byte[] data = xmlFile.getBytes();
					try {
					    fos = new FileOutputStream(saveFile);
					    fos.write(data);
					    fos.flush();
					    fos.close();
					} catch (FileNotFoundException e) {
					    // TODO handle exception
					} catch (IOException e) {
					    // TODO handle exception
					}

					Toast toast = Toast.makeText(getApplicationContext(), "File Saved" , Toast.LENGTH_LONG);
					toast.show();
				}
			});

		} else {
			// TODO load new scale
			list.setItemsCanFocus(true);

		}
	}

	@Override
	protected void onStop() {
	    super.onStop();
	    // TODO Save scale for Resume option
	}


// TODO clean this up and (maybe) modify the error popups etc.

	private class LoadIndividualScaleTask extends
	AsyncTask<String, Void, String> {

		@Override
		protected String doInBackground(String... arg0) {
			try {
				BufferedReader in = null;
				UrlValidator validator = new UrlValidator();
				if(validator.isValid(arg0[0])) {
					in = new BufferedReader(new InputStreamReader(
							new URL(arg0[0]).openStream()));
				} else if(new File(arg0[0]).exists()) {
					in = new BufferedReader(new FileReader(arg0[0]));
				} else {
					Intent intent = new Intent(ref, Popup.class);
					intent.putExtra("title", "Error");
					intent.putExtra("text", "Invalid Directory Location");
					startActivity(intent);
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

		protected void onPostExecute(String scaleItem) {
			if (scaleItem != null) {
				try {
					DocumentBuilder docBuilder = DocumentBuilderFactory
							.newInstance().newDocumentBuilder();
					Document doc = docBuilder.parse(new ByteArrayInputStream(scaleItem.getBytes()));
					NodeList items = doc.getElementsByTagName("scaleItem");
					if (items.getLength() < 2)
					{
						Intent intent = new Intent(ref, Popup.class);
						intent.putExtra("title", "Error");
						intent.putExtra("text", "The requested scale does not contain enough scale items.");
						startActivity(intent);
					}
					else {
						scaleText = scaleItem;
						try {
							scale.loadScale(scaleText);
							list = (ListView)findViewById(R.id.list);
							list.setItemsCanFocus(true);

							// Set up the adapter
							adapter = new BuilderListAdapter(ref, scale);
							list.setAdapter(adapter);
							// Load header
							headerName.setText(scale.scaleName);
							headerUnits.setText(scale.scaleMetric);
							
						} catch (Exception e) {
							Log.d("", "ERROR: in ScaleGenerator.loadScale()");
							e.printStackTrace();
						}
					}
				}
				catch (Exception e)
				{
					Intent intent = new Intent(ref, Popup.class);
					intent.putExtra("title", "Error");
					intent.putExtra("text", "The requested scale is not in the correct format.");
					startActivity(intent);
				}
			} else {
				Intent intent = new Intent(ref, Popup.class);
				intent.putExtra("title", "Error");
				intent.putExtra("text", "The requested scale does not exist.");
				startActivity(intent);
			}
		}
	}
	
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
	    // Check which request we're responding to
	    if (requestCode == R.id.TAG_BUILDER_IMGLOAD_ID) {
	        // Make sure the request was successful
	        if (resultCode == RESULT_OK) {
	            String imageLocation = data.getStringExtra("imageLoc");
	            int position = data.getIntExtra("position", -1);
	            if (position >= 0) {
	            	scale.members.get(position).imageLocation = imageLocation;
	            	adapter.notifyDataSetChanged();
	            }
	        }
	    }
	}
}
