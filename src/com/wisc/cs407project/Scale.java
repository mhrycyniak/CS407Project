package com.wisc.cs407project;

import android.app.ActionBar;
import android.app.ActionBar.Tab;
import android.app.Activity;
import android.app.Fragment;
import android.app.FragmentTransaction;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.Menu;
import android.view.View;
import android.widget.Toast;

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
}