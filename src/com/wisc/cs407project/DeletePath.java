package com.wisc.cs407project;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import android.app.ActionBar;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.os.Environment;
import android.view.Gravity;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Toast;

public class DeletePath extends Activity {
	private ListView paths;
	private HashMap<String,String> fileName = new HashMap<String,String>();
	private Button changeDirectory, localButton;
	private DeletePath ref;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.chooser);
		localButton = (Button)findViewById(R.id.local_btn);
		changeDirectory = (Button)findViewById(R.id.changeDirectory_btn);
		changeDirectory.setVisibility(View.GONE);
		localButton.setVisibility(View.GONE);
		paths = (ListView) findViewById(R.id.listView1);
		ref = this;
		ActionBar actionBar = getActionBar();
	    actionBar.setDisplayHomeAsUpEnabled(true);
		LoadLocalPath();
	}
	
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
	    switch (item.getItemId()) {
	        case android.R.id.home:
	            // Back to parent activity
	            this.finish();
	            return true;
	        default:
	            return super.onOptionsItemSelected(item);
	    }
	}
	
	private void LoadLocalPath() {
		try {
			final List<String> pathList = new ArrayList<String>();
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
			final ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, pathList);
			paths.setAdapter(adapter);
			
			paths.setOnItemClickListener(new android.widget.AdapterView.OnItemClickListener() {
	            @Override
	            public void onItemClick(AdapterView<?> parent, View view, final int position, long id) {
	            	// AlertDialog.Builder
					AlertDialog.Builder builder = new AlertDialog.Builder(view.getContext());

					builder.setMessage("Are you sure you want to delete the selected path?").setTitle("Warning");

					// Add the buttons
					builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
						public void onClick(DialogInterface dialog, int id) {
							String item = pathList.get(position);
							
							// Remove path from storage
							String directoryPath = Environment.getExternalStorageDirectory().toString();
							final String filePath = directoryPath + getResources().getString(R.string.app_path_directory) + "/" + item + ".kml";
							File file = new File(filePath);
							boolean deleted = file.delete();
							
							if (deleted) {
								// Remove from list if deleted
								pathList.remove(position);
								adapter.notifyDataSetChanged();
								Toast toast = Toast.makeText(ref, item + " path deleted.", Toast.LENGTH_SHORT);
								toast.setGravity(Gravity.CENTER, 0, 0);
								View view2 = toast.getView();
								view2.setBackgroundResource(R.color.grey);
								toast.show();
							}
						}
					});
					builder.setNegativeButton("No", new DialogInterface.OnClickListener() {
						public void onClick(DialogInterface dialog, int id) {
							// Don't do anything
						}
					});

					// Create and show
					builder.create();
					builder.show();
					return;
	                
	            }
	        });
		} catch (Exception e) {
		}	
	}
}
