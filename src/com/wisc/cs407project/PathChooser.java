package com.wisc.cs407project;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileReader;
import java.io.InputStreamReader;
import java.net.URL;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

import org.apache.commons.validator.routines.UrlValidator;

import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseUser;
import com.wisc.cs407project.ParseObjects.StaticUtils;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.view.View;
import android.view.View.OnLongClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView.OnItemLongClickListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;

public class PathChooser extends Activity implements OnItemClickListener {
	private ListView paths;
	private PathChooser ref;
	private String currentDirectory;
	private static final String SETTINGSNAME = "ScaleSettings";
	private HashMap<String,String> fileName = new HashMap<String,String>();
	private boolean local;
	private Button changeDirectory, localButton;
	public static List<ParseObject> parsePaths;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.chooser);
		localButton = (Button)findViewById(R.id.local_btn);
		changeDirectory = (Button)findViewById(R.id.changeDirectory_btn);
		changeDirectory.setVisibility(View.GONE);
		localButton.setVisibility(View.VISIBLE);
		local = true;
		ref = this;
		paths = (ListView) findViewById(R.id.listView1);
		paths.setOnItemClickListener(this);
		paths.setOnItemLongClickListener(new OnItemLongClickListener(){
			@Override
			public boolean onItemLongClick(AdapterView<?> arg0, View arg1,
					int index, long arg3) {
				if(local){
					new UploadPathTask().execute(fileName.get((String) paths.getItemAtPosition(index)));
					return true;
				}
				return false;
			}});
		LoadLocalPath();
	}
	
	private class UploadPathTask extends AsyncTask<String, Void, String> {

		@Override
		protected String doInBackground(String... arg0) {
			try {
				String name = arg0[0];
				BufferedReader in = null;
				String directoryPath = Environment.getExternalStorageDirectory().toString() + getResources().getString(R.string.app_path_directory);
				in = new BufferedReader(new InputStreamReader(new FileInputStream(new File(directoryPath, arg0[0]))));
				
				String str;
				String fullFile = "";
				while ((str = in.readLine()) != null) {
					fullFile += str + "\n";
				}
				in.close();
				
				StaticUtils.CreatePath(fullFile, name);
				
				return fullFile;
			} catch (Exception e) {
			}
			return null;
		}
	}
	
	public void ChangeLocalClicked(View view)
	{
		local = !local;
		ArrayAdapter<String> adapter = new ArrayAdapter<String>(ref,
				android.R.layout.simple_list_item_1, new ArrayList<String>());
		fileName.clear();
		paths.setAdapter(adapter);
		if (local)
		{
			changeDirectory.setVisibility(View.GONE);
			localButton.setVisibility(View.VISIBLE);
			localButton.setText("Change To Online Paths");
			LoadLocalPath();
		}
		else
		{			
			//changeDirectory.setVisibility(View.VISIBLE);
			localButton.setText("Change To Local Paths");
			//SharedPreferences settings = getSharedPreferences(SETTINGSNAME, 0);
			//currentDirectory = settings.getString("pathDirectory", "");
			//currentDirectory = "http://pages.cs.wisc.edu/~hrycynia/cs407project/";
			//if (currentDirectory != "") {
				new LoadPathsTask().execute(currentDirectory);
			//}
		}
	}
	
	private void LoadLocalPath() {
		try {
			List<String> pathList = new ArrayList<String>();
			String directoryPath = Environment.getExternalStorageDirectory().toString() + getResources().getString(R.string.app_path_directory);
			
			File directory = new File(directoryPath);        
			File files[] = directory.listFiles();
			
			// Loop through all paths
			for (int i=0; i < files.length; i++) {
				String name = files[i].getName();
			    String temp = name.substring(0, name.length() - 4);
			    pathList.add(temp);
			    fileName.put(temp, name);
			}
			ArrayAdapter<String> adapter = new ArrayAdapter<String>(ref, android.R.layout.simple_list_item_1, pathList);
			paths.setAdapter(adapter);
		} catch (Exception e) {
		}	
	}

	public void ChangeDirectoryClicked(View view) {
		AlertDialog.Builder alert = new AlertDialog.Builder(this);

		alert.setTitle("Change Directory");
		alert.setMessage("Input the location to search for path files.");

		// Set an EditText view to get user input
		final EditText input = new EditText(this);
		input.setText(currentDirectory);
		alert.setView(input);

		alert.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface dialog, int whichButton) {
				currentDirectory = input.getText().toString();
				String location = getExternalFilesDir(null).getAbsolutePath() + "/dirs.txt";
				DirUtils.storeDir(location, currentDirectory);
				SharedPreferences settings = getSharedPreferences(SETTINGSNAME, 0);
				SharedPreferences.Editor editor = settings.edit();
				editor.putString("pathDirectory", currentDirectory);
				editor.commit();
				if (currentDirectory != "")
				{
					new LoadPathsTask().execute(currentDirectory);
				}
				else
				{
					LoadLocalPath();
				}
			}
		});

		alert.setNegativeButton("Cancel", null);

		alert.show();
	}

	public class LoadPathsTask extends AsyncTask<String, Void, List<String>> {

		@Override
		protected List<String> doInBackground(String... arg0) {
			List<String> pathList = new ArrayList<String>();
			pathList.add("Loading...");
			
			ParseQuery<ParseObject> query = ParseQuery.getQuery("PathFile");	
			query.findInBackground(new FindCallback<ParseObject>(){
				@Override
				public void done(List<ParseObject> list, ParseException e) {
					if(e == null){
						parsePaths = list;
						List<String> nameList = new ArrayList<String>();
						for(ParseObject object : list){
							nameList.add(object.getString("name"));
						}
						ArrayAdapter<String> adapter = new ArrayAdapter<String>(ref, android.R.layout.simple_list_item_1, nameList);
						paths.setAdapter(adapter);
					}
					else{
						Intent intent = new Intent(ref, Popup.class);
						intent.putExtra("title", "Error");
						intent.putExtra("text", "Couldn't load online paths");
						startActivity(intent);
					}
				}});
			
//			fileName.clear();
//			try {
//				String path = arg0[0];
//				if (!path.endsWith("/")) {
//					path += "/";
//				}
//				path += "Paths.txt";
//				BufferedReader in = null;
//				UrlValidator validator = new UrlValidator();
//				if(validator.isValid(path)) {
//					in = new BufferedReader(new InputStreamReader(new URL(path).openStream()));;
//				} else if(new File(path).exists()) {
//					in = new BufferedReader(new FileReader(path));
//				} else {
//					Intent intent = new Intent(ref, Popup.class);
//					intent.putExtra("title", "Error");
//					intent.putExtra("text", "Invalid Directory Location");
//					startActivity(intent);
//				}
//
//				String str;
//				while ((str = in.readLine()) != null) {
//					int split = str.lastIndexOf('\t');
//					if (split != -1)
//					{
//						String first = str.substring(0, split);
//						String second = str.substring(split+1);
//						pathList.add(first);
//						fileName.put(first, second);
//					}
//				}
//				in.close();
//			} catch (Exception e) {
//			}
			return pathList;
		}

		protected void onPostExecute(List<String> pathList) {
			ArrayAdapter<String> adapter = new ArrayAdapter<String>(ref, android.R.layout.simple_list_item_1, pathList);
			paths.setAdapter(adapter);
		}
	}

	@Override
	public void onItemClick(AdapterView<?> arg0, View arg1, int arg2, long arg3) {
		String path = "";
		if (!local) {
			path += currentDirectory;
			if (!path.endsWith("/")) {
				path += "/";
			}
		}
		
		// Send results for when path is chosen
		path += fileName.get((String) paths.getItemAtPosition(arg2));
		
		if(!local){
			path = parsePaths.get(arg2).getString("name");
		}
		Intent intent = new Intent();
		intent.putExtra("path", path);
		intent.putExtra("localPath", local);
		setResult(2, intent);
		finish();
	}
}
