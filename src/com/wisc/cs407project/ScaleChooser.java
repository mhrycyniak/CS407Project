package com.wisc.cs407project;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.InputStreamReader;
import java.io.FileReader;
import java.io.File;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.w3c.dom.Document;
import org.w3c.dom.NodeList;
import org.apache.commons.validator.routines.UrlValidator;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ListView;

import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.wisc.cs407project.R;

public class ScaleChooser extends Activity implements OnItemClickListener {
	private ListView scales;
	private ArrayAdapter<String> adapter;
	private ArrayList<com.wisc.cs407project.ParseObjects.Scale> loadedScales;
	private ScaleChooser ref;
	private String currentDirectory;
	private static final String SETTINGSNAME = "WalkSettings";
	private HashMap<String,String> fileName = new HashMap<String,String>();

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.chooser);
		ref = this;
		scales = (ListView) findViewById(R.id.listView1);
		scales.setOnItemClickListener(this);
		SharedPreferences settings = getSharedPreferences(SETTINGSNAME, 0);
		//currentDirectory = settings.getString("scaleDirectory", "");
		currentDirectory = "http://pages.cs.wisc.edu/~hrycynia/cs407project/";
	
		List<String> scaleList = new ArrayList<String>();
		loadedScales = new ArrayList<com.wisc.cs407project.ParseObjects.Scale>();
		adapter = new ArrayAdapter<String>(ref,
				android.R.layout.simple_list_item_1, scaleList);
		scales.setAdapter(adapter);
		
		ParseQuery<ParseObject> query = ParseQuery.getQuery(
				com.wisc.cs407project.ParseObjects.Scale.class.getSimpleName());
		query.findInBackground(new FindCallback<ParseObject>(){
			@Override
			public void done(List<ParseObject> scaleObjects, ParseException exc) {
				if(exc == null){
					adapter.clear();
					loadedScales.clear();
					for(ParseObject obj : scaleObjects){
						com.wisc.cs407project.ParseObjects.Scale scale = 
								new com.wisc.cs407project.ParseObjects.Scale(obj);
						loadedScales.add(scale);
						adapter.add(scale.GetName());
					};
				}
			}});
	}

	public void ChangeDirectoryClicked(View view) {
		AlertDialog.Builder alert = new AlertDialog.Builder(this);

		alert.setTitle("Change Directory");
		alert.setMessage("Input the location to search for scale files.");

		// Set an EditText view to get user input
		final EditText input = new EditText(this);
		input.setText(currentDirectory);
		alert.setView(input);

		alert.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface dialog, int whichButton) {
				currentDirectory = input.getText().toString();
				SharedPreferences settings = getSharedPreferences(SETTINGSNAME, 0);
				SharedPreferences.Editor editor = settings.edit();
				editor.putString("scaleDirectory", currentDirectory);
				editor.commit();
				String location = getExternalFilesDir(null).getAbsolutePath() + "/dirs.txt";
				DirUtils.storeDir(location, currentDirectory);
				
				//new LoadScalesTask().execute(currentDirectory);
			}
		});

		alert.setNegativeButton("Cancel", null);

		alert.show();
	}

	@Override
	public void onItemClick(AdapterView<?> arg0, View arg1, int index, long arg3) {
		Intent intent = new Intent();
		intent.putExtra("scaleItem", loadedScales.get(index).GetObjectId());
		setResult(1, intent);
		finish();
	}
}
