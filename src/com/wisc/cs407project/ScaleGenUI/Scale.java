package com.wisc.cs407project.ScaleGenUI;

import java.io.File;

import android.os.Bundle;
import android.os.Environment;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.util.Log;
import android.view.Menu;
import android.view.View;

import com.wisc.cs407project.DirChooser;
import com.wisc.cs407project.R;
import com.wisc.cs407project.RecordPath;
import com.wisc.cs407project.R.id;
import com.wisc.cs407project.R.layout;
import com.wisc.cs407project.R.menu;
import com.wisc.cs407project.R.string;

public class Scale extends Activity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.main);

		if (!appDirectorySetup()) {
			//TODO no storage access, possibly post warning message
		}
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.settings, menu);
		return true;
	}

	public void onButtonClick(final View v) {
		switch (v.getId()) {
		case R.id.walkPath:
			Intent walkIntent = new Intent(this, DirChooser.class);
			startActivity(walkIntent);
			break;
		case R.id.recordPath:
			Intent recordIntent = new Intent(this, RecordPath.class);
			startActivity(recordIntent);
			break;
		case R.id.scaleBuilder:
			//TODO may need to change this so back button doesn't re-open popup
			Intent builderIntent = new Intent(this, ScaleExplorerPopup.class);
			//startActivityForResult(builderIntent, R.id.TAG_BUILDER_IMGLOAD_ID);
			startActivity(builderIntent);
			
			/*
			AlertDialog.Builder builder = new AlertDialog.Builder(this);

			builder.setTitle("What would you like to do?");

			// Add the buttons
			builder.setPositiveButton("Edit Scale", new DialogInterface.OnClickListener() 
			{
				public void onClick(DialogInterface dialog, int id) 
				{
					//TODO Next thing: make a popup for this
					Intent builderIntent = new Intent(v.getContext(), ScaleBuilder.class);
					builderIntent.putExtra("Load", true);
					//builderIntent.putExtra("Path", path);
					startActivity(builderIntent);
					//Intent builderIntent = new Intent(v.getContext(), ExplorerPopup.class);
					//startActivity(builderIntent);
				}
			});
			builder.setNegativeButton("New Scale", new DialogInterface.OnClickListener() 
			{
				public void onClick(DialogInterface dialog, int id) 
				{
					Intent builderIntent = new Intent(v.getContext(), ScaleBuilder.class);
					builderIntent.putExtra("Load", false);
					startActivity(builderIntent);
				}
			});

			// Create and show
			builder.create();
			builder.show(); */
			break;
		default:
		}
	}

	public boolean appDirectorySetup() {
		// Check if mounted, get storage directory
		String extState = Environment.getExternalStorageState();
		if(!extState.equals(Environment.MEDIA_MOUNTED)) {
			Log.d("ERROR", "Storage not mounted");
			return false;
		}
		else {
			String basePath = Environment.getExternalStorageDirectory().toString();
			// Make sure there's an app directory
			String temp = basePath + "/" + getResources().getString(R.string.app_name);
			if (!(new File(temp).isDirectory())) {
				File tempFile = new File(temp);
				tempFile.mkdir();
			}
			// TODO only use if we decide to make saving images locally an option
			// Make sure it has an image directory
			//temp = basePath + getResources().getString(R.string.app_image_directory);
			//if (!(new File(temp).isDirectory())) {
			//	File tempFile = new File(temp);
			//	tempFile.mkdir();
			//	}
			// Make sure it has a scales directory
			temp = basePath + getResources().getString(R.string.app_scale_directory);
			if (!(new File(temp).isDirectory())) {
				File tempFile = new File(temp);
				tempFile.mkdir();
				}
			return true;
		}
	}
}