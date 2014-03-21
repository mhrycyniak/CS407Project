package com.wisc.cs407project;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileReader;
import java.io.InputStreamReader;
import java.net.URL;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.apache.commons.validator.routines.UrlValidator;
import org.w3c.dom.Document;
import org.w3c.dom.NodeList;

import android.app.Activity;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.widget.EditText;
import android.widget.ListView;

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
	String units;
	private ScaleBuilder ref;
	String scaleText = "";
	ScaleGenerator scale;


	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.scale_builder);
		ref = this;

		// Check intent to see if New or Editing
		Intent intent = getIntent();
		boolean loadNeeded = intent.getBooleanExtra("Load", false);

		if (loadNeeded) {
			scale = new ScaleGenerator();

			//TODO make this dynamic, with menu
			String path = Environment.getExternalStorageDirectory().toString() + "/Pictures/Andrew/test_scale.xml";
			new LoadIndividualScaleTask().execute(path);

			// TODO make this dynamic (maybe need adapter in the async)
			// Load header
			EditText headerName = (EditText)findViewById(R.id.builderHeaderName);
			EditText headerUnits = (EditText)findViewById(R.id.builderHeaderUnits);
			headerName.setText(scale.scaleName);
			headerUnits.setText(scale.scaleMetric);
			units = scale.scaleMetric;
		} else {
			// TODO load new scale
		}
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
							Log.d("", scaleText);
							scale.loadScale(scaleText);
							Log.d("", "scale loaded, now notify change");
							list = (ListView)findViewById(R.id.list);

							// Set up the adapter
							adapter = new BuilderListAdapter(ref, scale);
							list.setAdapter(adapter);
							//adapter.notifyDataSetChanged();
						} catch (Exception e) {
							Log.d("", "ERROR");
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
}
