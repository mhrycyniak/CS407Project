package com.wisc.cs407project;

import android.os.Bundle;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.view.Menu;
import android.view.View;
import android.widget.Toast;

import com.wisc.cs407project.R;

public class Scale extends Activity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.main);
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
			AlertDialog.Builder builder = new AlertDialog.Builder(this /* Activity Context */);

			builder.setTitle("What would you like to do?");

			// Add the buttons
			builder.setPositiveButton("Edit Scale", new DialogInterface.OnClickListener() 
			{
				public void onClick(DialogInterface dialog, int id) 
				{
					Toast.makeText(getApplicationContext(), "WIP", Toast.LENGTH_LONG).show();
					//TODO
				}
			});
			builder.setNegativeButton("New Scale", new DialogInterface.OnClickListener() 
			{
				public void onClick(DialogInterface dialog, int id) 
				{
					Intent builderIntent = new Intent(v.getContext(), ScaleBuilder.class);
					startActivity(builderIntent);
				}
			});

			// 3. Create and show
			builder.create();
			builder.show();
			break;
		default:
		}
	}
}