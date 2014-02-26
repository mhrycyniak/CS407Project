package com.wisc.cs407project;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.List;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ListView;

public class DirChooser extends Activity implements OnItemClickListener {
	private ListView dirs;
	private DirChooser ref;
	private String currentDirectory;
	private Integer currentPosition;
	private List<String> menu;
	private static final String SETTINGSNAME = "ScaleSettings";

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.chooser);
		ref = this;
		dirs = (ListView) findViewById(R.id.listView1);
		dirs.setOnItemClickListener(this);
		SharedPreferences settings = getSharedPreferences(SETTINGSNAME, 0);
		currentDirectory = settings.getString("scaleDirectory", "");
		loadMenu();
	}
	
	private void loadMenu()
	{
		menu = new ArrayList<String>();

		try {
			String dirFile = getExternalFilesDir(null).getAbsolutePath() + "/dirs.txt";
			File f = new File(dirFile);
			if(!f.exists()) {
				f.createNewFile();				
			} else {
				BufferedReader br = new BufferedReader(new FileReader(dirFile));
				String line;
				while( (line = br.readLine()) != null) {
					line = line.trim();
					if(!line.isEmpty()) {
						if(line.equals(currentDirectory)) {
							currentPosition = menu.size();
						}
						menu.add(line.trim());
					} 
				}	
			}
			
			if(menu.size() > 1) {
				ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, menu);
				dirs.setAdapter(adapter);
			} else {
				Intent intent = new Intent(ref, ScaleChooser.class);
				startActivity(intent);
			}

		} catch(Exception e) {}
			
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
				Intent intent = new Intent(ref, ScaleChooser.class);
				startActivity(intent);
			}
		});

		alert.setNegativeButton("Cancel", null);

		alert.show();
	}
	
	@Override
	public void onItemClick(AdapterView<?> arg0, View arg1, int position, long arg3) {
		SharedPreferences settings = getSharedPreferences(SETTINGSNAME, 0);
		SharedPreferences.Editor editor = settings.edit();
		editor.putString("scaleDirectory", menu.get(position));
		editor.commit();
		Intent intent = new Intent(ref, ScaleChooser.class);
		startActivity(intent);
	}
	

}
