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

import com.parse.ParseObject;

public class DeleteScale extends Activity {
	private ListView scales;
	List<ParseObject> parseScales;
	private DeleteScale ref;
	private HashMap<String,String> fileName = new HashMap<String,String>();
	private Button localButton;
	private Button changeDirectory;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.chooser);
		localButton = (Button)findViewById(R.id.local_btn);
		changeDirectory = (Button)findViewById(R.id.changeDirectory_btn);
		changeDirectory.setVisibility(View.GONE);
		localButton.setVisibility(View.GONE);
		scales = (ListView) findViewById(R.id.listView1);
		ref = this;
		ActionBar actionBar = getActionBar();
	    actionBar.setDisplayHomeAsUpEnabled(true);
		loadLocalScales();
	}

	private void loadLocalScales() {
		try {
			final List<String> scaleList = new ArrayList<String>();
			String directoryPath = Environment.getExternalStorageDirectory().toString() + getResources().getString(R.string.app_scale_directory);
			
			File directory = new File(directoryPath);        
			File files[] = directory.listFiles();
			
			// Loop through all scales
			for (int i=0; i < files.length; i++) {
				String name = files[i].getName();
			    String temp = name.substring(0, name.length() - 4);
			    scaleList.add(temp);
			    fileName.put(temp, name);
			}
			final ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, scaleList);
			scales.setAdapter(adapter);
			
			scales.setOnItemClickListener(new android.widget.AdapterView.OnItemClickListener() {
	            @Override
	            public void onItemClick(AdapterView<?> parent, View view, final int position, long id) {
	            	// AlertDialog.Builder
					AlertDialog.Builder builder = new AlertDialog.Builder(view.getContext());

					builder.setMessage("Are you sure you want to delete the selected scale?").setTitle("Warning");

					// Add the buttons
					builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
						public void onClick(DialogInterface dialog, int id) {
							String item = scaleList.get(position);
							
							// Remove scale from storage
							String directoryPath = Environment.getExternalStorageDirectory().toString();
							final String filePath = directoryPath + getResources().getString(R.string.app_scale_directory) + "/" + item + ".xml";
							File file = new File(filePath);
							boolean deleted = file.delete();
							
							if (deleted) {
								// Remove from list if deleted
								scaleList.remove(position);
								adapter.notifyDataSetChanged();
								Toast toast = Toast.makeText(ref, item + " scale deleted.", Toast.LENGTH_SHORT);
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
}
