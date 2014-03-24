package com.wisc.cs407project.ScaleGenUI;

import java.io.File;

import com.wisc.cs407project.R;
import com.wisc.cs407project.R.layout;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Environment;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.LinearLayout;

public class ScaleExplorerPopup extends Activity {

	Activity activity;
	Button resume, newScale, loadLocal, loadOnline;
	LinearLayout localLoadLayout, onlineLoadLayout;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		super.onCreate(savedInstanceState);
		setContentView(R.layout.scale_explorer_popup);
		activity = this;

		// Resume Button, Related stuff, and Listener
		resume = (Button)findViewById(R.id.scaleExplorerResumeButton);
		// This is where a backup would be
		final String resumePath = Environment.getExternalStorageDirectory().toString() + "/" 
				+ getResources().getString(R.string.app_name) + "/"
				+ getResources().getString(R.string.resume_backup_filename);
		if (new File(resumePath).exists()) {
			resume.setOnClickListener(new View.OnClickListener() {
				public void onClick(View v) {
					Intent builderIntent = new Intent(v.getContext(), ScaleBuilder.class);
					builderIntent.putExtra("Load", true);
					builderIntent.putExtra("Path", resumePath);
					startActivity(builderIntent);
				}
			});
		} else {
			resume.setEnabled(false);
		}

		// New Button and Listener
		newScale = (Button)findViewById(R.id.scaleExplorerNewButton);
		newScale.setOnClickListener(new View.OnClickListener() {
			public void onClick(View v) {
				Intent builderIntent = new Intent(v.getContext(), ScaleBuilder.class);
				startActivity(builderIntent);
			}
		});
		
		// Load Lists/Searches, hidden by default
		localLoadLayout = (LinearLayout)findViewById(R.id.scaleExplorerLoadLocalLayout);
		localLoadLayout.setVisibility(View.GONE);
		//onlineLoadLayout... blah blah
		
		// Local Load Button and Listener
		loadLocal = (Button)findViewById(R.id.scaleExplorerLoadLocalButton);
		loadLocal.setOnClickListener(new View.OnClickListener() {
			public void onClick(View v) {
				//onlineLoadLayout.setVisibility(View.GONE);
				localLoadLayout.setVisibility(View.VISIBLE);
			}
		});
	}
}
