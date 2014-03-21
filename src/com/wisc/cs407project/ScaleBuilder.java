package com.wisc.cs407project;

import java.util.ArrayList;
import java.util.HashMap;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ListView;

public class ScaleBuilder extends Activity {

	// Monotony for the sake of clarity
	//public static final String KEY_NAME_LABEL = "nameLabel";
	public static final String KEY_NAME_EDIT = "nameEdit";
	public static final String KEY_COMPARABLE_LABEL = "comparableLabel";
	public static final String KEY_COMPARABLE_EDIT = "comparableEdit";
	public static final String KEY_UNITS = "units";
	//public static final String KEY_DESCRIPTION_LABEL = "descriptionLabel";
	public static final String KEY_DESCRIPTION_EDIT = "descriptionEdit";
	//public static final String KEY_IMAGE_LOCATION_LABEL = "imageLocationLabel";
	public static final String KEY_IMAGE_LOCATION_EDIT = "imageLocationEdit";
	
	ListView list;
	//View header;
	BuilderListAdapter adapter;
	String units;
	//private static LayoutInflater inflater = null;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.scale_builder);
		
		// Check intent to see if New or Editing
		
		// Testing
		units = "tests";
		ArrayList<HashMap<String, String>> data = new ArrayList<HashMap<String, String>>();
		for (int i = 0; i < 25; i++) {
			HashMap<String, String> map = new HashMap<String, String>();
			map.put(KEY_NAME_EDIT, "Name Edit Test " + Integer.toString(i));
			map.put(KEY_COMPARABLE_EDIT, "Comparable Edit Test " + Integer.toString(i));
			map.put(KEY_UNITS, units);
			map.put(KEY_DESCRIPTION_EDIT, "Description Edit Test " + Integer.toString(i));
			map.put(KEY_IMAGE_LOCATION_EDIT, "Image Location Edit Test " + Integer.toString(i));
			data.add(map);
		}
		
        list = (ListView)findViewById(R.id.builderList);
        // Set up the header
        //TODO put in parent layout so it doesn't scroll
        //inflater = (LayoutInflater)getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        //header = inflater.inflate(R.layout.builder_header, list, false);
        // Change header hear if needed
        //list.addHeaderView(header);
        // Set up the adapter
        adapter = new BuilderListAdapter(this, data);
        list.setAdapter(adapter);
	}

}
