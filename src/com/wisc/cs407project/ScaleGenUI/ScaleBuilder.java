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
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Rect;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.text.Editable;
import android.text.InputFilter;
import android.text.SpannableStringBuilder;
import android.text.Spanned;
import android.text.TextWatcher;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.ViewTreeObserver.OnGlobalLayoutListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

public class ScaleBuilder extends Activity {
	// Monotony for the sake of clarity
	public static final String KEY_NAME_EDIT = "nameEdit";
	public static final String KEY_COMPARABLE_LABEL = "comparableLabel";
	public static final String KEY_COMPARABLE_EDIT = "comparableEdit";
	public static final String KEY_UNITS = "units";
	public static final String KEY_DESCRIPTION_EDIT = "descriptionEdit";
	public static final String KEY_IMAGE_LOCATION_EDIT = "imageLocationEdit";

	ListView list;
	BuilderListAdapter adapter;
	private Activity ref;
	String scaleText = "";
	String path;
	ScaleGenerator scale;
	EditText headerName;
	EditText headerUnits;
	Button headerAdd, headerSort, headerSave;
	String loadedPath = "";
	boolean cancelSave = false;
	boolean holdForDialog = false;


	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.scale_builder);
		ref = this;

		// Check intent to see if New or Editing
		Intent intent = getIntent();
		boolean loadNeeded = intent.getBooleanExtra("Load", false);
		
		scale = new ScaleGenerator();
		
		if (loadNeeded) {
			loadedPath = intent.getStringExtra("Path");
			new LoadIndividualScaleTask().execute(loadedPath);
		} else {
			// Set up list and adapter
			list = (ListView)findViewById(R.id.list);
			list.setItemsCanFocus(true);
			adapter = new BuilderListAdapter(ref, scale);
			list.setAdapter(adapter);
		}
		
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
			// Filter the EditText to allow only some chars (since it's used as a filename)
			InputFilter filter = new InputFilter() {
			    @Override
			    public CharSequence filter(CharSequence source, int start, int end,
			            Spanned dest, int dstart, int dend) {

			        if (source instanceof SpannableStringBuilder) {
			            SpannableStringBuilder sourceAsSpannableBuilder = (SpannableStringBuilder)source;
			            for (int i = end - 1; i >= start; i--) { 
			                char currentChar = source.charAt(i);
			                 if (!Character.isLetterOrDigit(currentChar) && !Character.isSpaceChar(currentChar)) {    
			                     sourceAsSpannableBuilder.delete(i, i+1);
			                 }     
			            }
			            return source;
			        } else {
			            StringBuilder filteredStringBuilder = new StringBuilder();
			            for (int i = start; i < end; i++) { 
			                char currentChar = source.charAt(i);
			                if (Character.isLetterOrDigit(currentChar) || Character.isSpaceChar(currentChar)) {    
			                    filteredStringBuilder.append(currentChar);
			                }     
			            }
			            return filteredStringBuilder.toString();
			        }
			    }
			};
			headerName.setFilters(new InputFilter[]{filter});
			
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
					if (!scale.members.isEmpty()) {
						scale.sort();
						adapter.notifyDataSetChanged();
					}
				}
			});
			
			// Add Button
			headerSave = (Button)findViewById(R.id.builderHeaderSaveButton);
			headerSave.setOnClickListener(new View.OnClickListener() {
				public void onClick(View v) {
					// Check access to device
					String extState = Environment.getExternalStorageState();
					if(!extState.equals(Environment.MEDIA_MOUNTED)) {
						// This shouldn't be possible
						Log.e("ERROR", "Storage not mounted");
						return;
					}
					
					// Check if name is empty
					String scaleName = scale.scaleName;
					if (scaleName.equals("")) {
						Intent intent = new Intent(ref, Popup.class);
						intent.putExtra("title", "Error");
						intent.putExtra("text", "No Scale Name Entered");
						startActivity(intent);
						Log.e("ERROR", "Scale must have a name");
						return;
					}
					
					// Check if name is already used (by a file other than what was loaded)
					// Confirm overwrite, save here if confirmed
					scaleName = scaleName + ".xml";
					path = Environment.getExternalStorageDirectory().toString();
					path = path + getResources().getString(R.string.app_scale_directory) + "/" + scaleName;
					if ((!path.equals(loadedPath)) && (new File(path).exists())) {
						
						AlertDialog.Builder builder = new AlertDialog.Builder(ref);

						builder.setTitle("A file by this name already exists. Do you want to overwrite it?");

						// Add the buttons
						builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() 
						{
							public void onClick(DialogInterface dialog, int id) 
							{
								dialog.dismiss();
								new SaveScaleTask().execute(path);
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
					}
					new SaveScaleTask().execute(path);
				}
			});
			// Check if mounted, to enable/disable Save button
			String extState = Environment.getExternalStorageState();
			if(!extState.equals(Environment.MEDIA_MOUNTED)) {
				headerSave.setEnabled(false);
			}
	}

	@Override
	protected void onStop() {
	    super.onStop();
	    // Check access to device
		String extState = Environment.getExternalStorageState();
		if(!extState.equals(Environment.MEDIA_MOUNTED)) {
			Log.e("ERROR: Save on Stop", "Storage not mounted");
			return;
		}
		
		// Build the save path
		String path = Environment.getExternalStorageDirectory().toString();
		path = path + "/" + getResources().getString(R.string.app_name) + "/"
				+ getResources().getString(R.string.resume_backup_filename);
		
		new SaveScaleTask().execute(path);
	}

	private class SaveScaleTask extends
	AsyncTask<String, Void, String> {

		@Override
		protected String doInBackground(String... arg0) {
			File saveFile = new File(arg0[0]);
			FileOutputStream fos;
			String xmlFile = scale.getXML();
			byte[] data = xmlFile.getBytes();
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
				Toast toast = Toast.makeText(getApplicationContext(), filename + " saved", Toast.LENGTH_LONG);
				toast.setGravity(Gravity.CENTER, 0, 0);
				View view = toast.getView();
				view.setBackgroundResource(R.color.grey);
				toast.show();
			}
		}
	}
	
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
				catch (Exception e)
				{
					Intent intent = new Intent(ref, Popup.class);
					intent.putExtra("title", "Error");
					intent.putExtra("text", "The requested scale is not in the correct format.");
					e.printStackTrace();
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
