package com.wisc.cs407project;

import java.util.ArrayList;
import java.util.List;

import android.app.ActionBar;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;

public class Settings extends Activity {
	private ListView options;
	private Button localButton, changeDirectory;
	private Settings ref;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.chooser);
		localButton = (Button)findViewById(R.id.local_btn);
		changeDirectory = (Button)findViewById(R.id.changeDirectory_btn);
		changeDirectory.setVisibility(View.GONE);
		localButton.setVisibility(View.GONE);
		options = (ListView) findViewById(R.id.listView1);
		ref = this;
		ActionBar actionBar = getActionBar();
	    actionBar.setDisplayHomeAsUpEnabled(true);
	    loadOptions();
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
	
	public void loadOptions() {
		try {
			final List<String> optionsList = new ArrayList<String>();
			optionsList.add("Delete Path");
			optionsList.add("Delete Scale");
			
			final ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, optionsList);
			options.setAdapter(adapter);
			
			options.setOnItemClickListener(new android.widget.AdapterView.OnItemClickListener() {
	            @Override
	            public void onItemClick(AdapterView<?> parent, View view, final int position, long id) {
	            	if (position == 0) {
	            		Intent removePath = new Intent(ref, DeletePath.class);
	        			startActivity(removePath);
	            	} else {
	            		Intent removeScale = new Intent(ref, DeleteScale.class);
	        			startActivity(removeScale);
	            	}
	            }
	        });
		} catch (Exception e) {
		}
	}
}
