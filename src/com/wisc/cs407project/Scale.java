package com.wisc.cs407project;

import android.app.ActionBar;
import android.app.ActionBar.Tab;
import java.io.File;

import android.os.Bundle;
import android.os.Environment;
import android.app.Activity;
import android.app.Fragment;
import android.app.FragmentTransaction;
import android.util.Log;
import android.view.Menu;


public class Scale extends Activity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.main);

		// ActionBar for tabs
		ActionBar actionbar = getActionBar();
		actionbar.setNavigationMode(ActionBar.NAVIGATION_MODE_TABS);

		// Create tabs and give them titles
		ActionBar.Tab walkTab = actionbar.newTab().setText(getString(R.string.walk_path));
		ActionBar.Tab recordTab = actionbar.newTab().setText(getString(R.string.record_path));
		ActionBar.Tab pathTab = actionbar.newTab().setText(getString(R.string.path_settings));
		ActionBar.Tab scaleTab = actionbar.newTab().setText(getString(R.string.scale_settings));

		// Create fragments
		WalkFragment walkFragment = new WalkFragment();
		RecordFragment recordFragment = new RecordFragment();
		PathFragment pathFragment = new PathFragment();
		ScaleFragment scaleFragment = new ScaleFragment();

		// Tab listener
		walkTab.setTabListener(new TabsListener(walkFragment));
		recordTab.setTabListener(new TabsListener(recordFragment));
		pathTab.setTabListener(new TabsListener(pathFragment));
		scaleTab.setTabListener(new TabsListener(scaleFragment));

		// Add tabs to ActionBar
		actionbar.addTab(walkTab);
		actionbar.addTab(recordTab);
		actionbar.addTab(pathTab);
		actionbar.addTab(scaleTab);
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


	class TabsListener implements ActionBar.TabListener {
		public Fragment fragment;

		public TabsListener(Fragment fragment) {
			this.fragment = fragment;
		}

		@Override
		public void onTabReselected(Tab tab, FragmentTransaction ft) {
		}

		@Override
		public void onTabSelected(Tab tab, FragmentTransaction ft) {
			ft.replace(R.id.fragment_container, fragment);
		}

		@Override
		public void onTabUnselected(Tab tab, FragmentTransaction ft) {
			ft.remove(fragment);
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
