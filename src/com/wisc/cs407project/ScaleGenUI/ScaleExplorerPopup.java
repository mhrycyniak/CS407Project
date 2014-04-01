package com.wisc.cs407project.ScaleGenUI;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileFilter;
import java.io.InputStreamReader;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.validator.routines.UrlValidator;

import com.wisc.cs407project.R;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.graphics.Point;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.Display;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.AdapterView.OnItemClickListener;

public class ScaleExplorerPopup extends Activity {

	Activity activity;
	Button resume, newScale, loadLocal, loadOnline, back, loadURL;
	LinearLayout localLoadLayout, onlineLoadLayout;
	String path = "/";
	EditText currentDirectory, currentURL;
	ListView list, list2;
	ListAdapter adapter;
	String[] data;
	boolean currentlyDisconnected;
	public static final String LAST_ADDRESS = "last_address";
	SharedPreferences settings;
	private BroadcastReceiver mConnReceiver;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		super.onCreate(savedInstanceState);
		setContentView(R.layout.scale_explorer_popup);
		activity = this;

		// Set popup width to 3/4 screen width
		LinearLayout ll = (LinearLayout)findViewById(R.id.scaleExplorerParentView);
		Display display = getWindowManager().getDefaultDisplay();
		Point size = new Point();
		display.getSize(size);
		int width = (3 * size.x) / 4;
		ViewGroup.LayoutParams params = ll.getLayoutParams();
		params.width = width;
		ll.setLayoutParams(params);
		ll.requestLayout();

		// Resume Button, Related stuff, and Listener
		resume = (Button)findViewById(R.id.scaleExplorerResumeButton);
		// This is where a backup would be
		final String resumePath = Environment.getExternalStorageDirectory().toString() + "/" 
				+ getResources().getString(R.string.app_name) + "/"
				+ getResources().getString(R.string.resume_backup_filename);
		// If it's there, set up the resume button
		if (new File(resumePath).exists()) {
			resume.setOnClickListener(new View.OnClickListener() {
				public void onClick(View v) {
					Intent builderIntent = new Intent(v.getContext(), ScaleBuilder.class);
					builderIntent.putExtra("Load", true);
					builderIntent.putExtra("Path", resumePath);
					startActivity(builderIntent);
				}
			});
		}
		// Otherwise the resume button is disabled
		else {
			resume.setEnabled(false);
		}

		// Create-New Button and Listener
		newScale = (Button)findViewById(R.id.scaleExplorerNewButton);
		newScale.setOnClickListener(new View.OnClickListener() {
			public void onClick(View v) {
				Intent builderIntent = new Intent(v.getContext(), ScaleBuilder.class);
				startActivity(builderIntent);
			}
		});

		// These load lists are hidden by default
		localLoadLayout = (LinearLayout)findViewById(R.id.scaleExplorerLoadLocalLayout);
		localLoadLayout.setVisibility(View.GONE);
		onlineLoadLayout = (LinearLayout)findViewById(R.id.scaleExplorerLoadOnlineLayout);
		onlineLoadLayout.setVisibility(View.GONE);

		// Local directory display
		currentDirectory = (EditText) findViewById(R.id.scaleExplorerDirectory);
		// Local search list
		list = (ListView) findViewById(R.id.scaleExplorerList);
		// Set up ListView listener
		list.setOnItemClickListener(new OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
				String item = ((String)list.getItemAtPosition(position));
				item = currentDirectory.getText() + item;
				new LoadDirectoryTask().execute(item);
			}
		});
		// Directory display's back button and listener
		back = (Button) findViewById(R.id.scaleExplorerBackButton);
		back.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View view) {
				String item = currentDirectory.getText().toString();
				// Stop at root
				if (item.length() > 1) {
					// Pop off "/currentDirectory/"
					item = item.substring(0, item.lastIndexOf("/"));
					item = item.substring(0, item.lastIndexOf("/"));
					// Root is "/" not ""
					if (item.equals("")) {
						item = "/";
					}
					new LoadDirectoryTask().execute(item);
				}
			}
		});

		// Load-local Button and Listener
		loadLocal = (Button)findViewById(R.id.scaleExplorerLoadLocalButton);
		loadLocal.setOnClickListener(new View.OnClickListener() {
			public void onClick(View v) {
				onlineLoadLayout.setVisibility(View.GONE);
				localLoadLayout.setVisibility(View.VISIBLE);
			}
		});
		
		// Check if mounted, disable if not, otherwise load the default directory into the list
		String extState = Environment.getExternalStorageState();
		if(!extState.equals(Environment.MEDIA_MOUNTED)) {
			loadLocal.setEnabled(false);
		}
		else {
			// Try to set path to start with app directory
			path = Environment.getExternalStorageDirectory().toString();
			String betterPath = path + getResources().getString(R.string.app_scale_directory);;
			if (new File(betterPath).isDirectory()) {
				path = betterPath;
			}
			new LoadDirectoryTask().execute(path);
			Log.d("initial execute; path: ", path);
		}

		// Address display
		currentURL = (EditText) findViewById(R.id.scaleExplorerURL);
		// If a previous address has been used, go there automatically
		settings = getSharedPreferences(LAST_ADDRESS, MODE_PRIVATE);
        currentURL.setText(settings.getString(LAST_ADDRESS, ""));
        new LoadScalesTask().execute(currentURL.getText().toString());
		
        // Online scales list
		list2 = (ListView) findViewById(R.id.scaleExplorerList2);
		// Set ListView listener
		list2.setOnItemClickListener(new OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
				String item = ((String)list2.getItemAtPosition(position));
				String path = currentURL.getText().toString();
				if (!path.endsWith("/")) {
					path += "/";
				}
				path += item;
				Intent builderIntent = new Intent(activity, ScaleBuilder.class);
				builderIntent.putExtra("Load", true);
				builderIntent.putExtra("Path", path);
				startActivity(builderIntent);
			}
		});
		// Load Button and its Listener
		loadURL = (Button) findViewById(R.id.scaleExplorerLoadButton);
		loadURL.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View view) {
				new LoadScalesTask().execute(currentURL.getText().toString());
			}
		});

		// Online Load Button and Listener
		loadOnline = (Button)findViewById(R.id.scaleExplorerLoadOnlineButton);
		loadOnline.setOnClickListener(new View.OnClickListener() {
			public void onClick(View v) {
				onlineLoadLayout.setVisibility(View.VISIBLE);
				localLoadLayout.setVisibility(View.GONE);
			}
		});
		// Check if connected
		ConnectivityManager connManager = (ConnectivityManager) getSystemService(CONNECTIVITY_SERVICE);
	    NetworkInfo mWifi = connManager.getNetworkInfo(ConnectivityManager.TYPE_WIFI);
	    NetworkInfo mMobile = connManager.getNetworkInfo(ConnectivityManager.TYPE_MOBILE);
	    // If not, disable the button
	    if (!mWifi.isAvailable() && !mMobile.isAvailable()) {
	        loadOnline.setEnabled(false);
	    }

		// Set popup list heights to 1/3 screen height
		display.getSize(size);
		int height = size.y / 3;
		params = list.getLayoutParams();
		params.height = height;
		list.setLayoutParams(params);
		list.requestLayout();
		params = list2.getLayoutParams();
		params.height = height;
		list2.setLayoutParams(params);
		list2.requestLayout();
	}
	
	@Override
	public void onResume() {
		super.onResume();
		// Listen for network changes, adjust Load from Online button accordingly
		mConnReceiver = new BroadcastReceiver() {
			@Override
			public void onReceive(Context context, Intent intent) {
				boolean noConnectivity = intent.getBooleanExtra(ConnectivityManager.EXTRA_NO_CONNECTIVITY, false);
				// do application-specific task(s) based on the current network state, such
				if (noConnectivity) {
					currentlyDisconnected = true;
					onlineLoadLayout.setEnabled(false);
				} else if (!noConnectivity) {
					currentlyDisconnected = false;
					onlineLoadLayout.setEnabled(true);
				}
			}
		};
		registerReceiver(mConnReceiver, new IntentFilter(ConnectivityManager.CONNECTIVITY_ACTION));
	}
	
	@Override
	public void onPause() {
		super.onPause();
		unregisterReceiver(mConnReceiver);
	}

	private class LoadDirectoryTask extends AsyncTask<String, Void, String[]> {

		@Override
		protected String[] doInBackground(String... arg0) {
			// If path a directory
			if (new File(arg0[0]).isDirectory()) {
				File directory = new File(arg0[0]);

				// Filter through only directories and xmls
				FileFilter filterDirectoriesOnly = new FileFilter() {
					public boolean accept(File file) {
						String tempPath = file.getPath();
						return (file.isDirectory() || 
								((tempPath.length() > 4) && (tempPath.substring(tempPath.length() - 3).toLowerCase().equals("xml"))));
					}
				};
				// Use the filter to get children
				File[] children = directory.listFiles(filterDirectoriesOnly);
				// Add 1 to pass along the directory, another to signal that result is a directory (see onpostexecute)
				String[] result = new String[children.length + 2];
				// Format the file names
				for (int i = 0; i < children.length; i++) {
					result[i+1] = children[i].getName();
				}
				// Add the directory (offsetting for the confusing directory flag)
				if (arg0[0].equals("/")) {
					result[children.length + 1] = arg0[0]; // Root is "/" not "//"
				} else {
					result[children.length + 1] = arg0[0] + "/";
				}
				return result;
			} 
			// If path is an xml
			else if (new File(arg0[0]).exists()) {
				String[] result = new String[1];
				result[0] = arg0[0];
				return result;
			} else {
				//TODO
				Log.e("ERROR", "Path is not a directory or an xml");
			}
			return null;
		}

		protected void onPostExecute(String[] filesAndDirectory) {
			// If a scale was selected
			if (filesAndDirectory.length == 1) {
				Intent builderIntent = new Intent(activity, ScaleBuilder.class);
				builderIntent.putExtra("Load", true);
				builderIntent.putExtra("Path", filesAndDirectory[0]);
				startActivity(builderIntent);
			}
			// If a directory was selected (there will be more than one String in the array)
			else if (filesAndDirectory != null) {
				// Pull out the files
				String[] filesOnly = new String[filesAndDirectory.length - 2];
				for (int i = 0; i < filesOnly.length; i++) {
					// The "i+1" ignores the "this is a directory" flag
					filesOnly[i] = filesAndDirectory[i+1];
				}
				// Pull out the directory
				currentDirectory.setText(filesAndDirectory[filesAndDirectory.length - 1]);

				// Update the ListView and clear the preview
				data = filesOnly;
				adapter = new ArrayAdapter<String>(getBaseContext(), R.layout.explorer_list_item, R.id.explorerTextItem, data);
				list.setAdapter(adapter);
			}
		}
	}
	
	private class LoadScalesTask extends AsyncTask<String, Void, String[]> {

		@Override
		protected String[] doInBackground(String... arg0) {
			List<String> scaleList = new ArrayList<String>();
			try {
				String path = arg0[0];
				if (!path.endsWith("/")) {
					path += "/";
				}
				path += "Scales.txt";
				BufferedReader in;
				in = new BufferedReader(new InputStreamReader(new URL(path).openStream()));
				
				String str;
				while ((str = in.readLine()) != null) {
					int split = str.lastIndexOf('\t');
					if (split != -1)
					{
						String first = str.substring(0, split);
						String second = str.substring(split+1);
						scaleList.add(second);
					}
				}
				in.close();
			} catch (Exception e) {
				// Abort without error
				this.cancel(true);
			}
			// Plus 1 for passing URL to onPostExecute
			String[] result = new String[scaleList.size() + 1];
			for (int i = 0; i < scaleList.size(); i++) {
				result[i] = scaleList.get(i);
			}
			// Pass URL
			result[scaleList.size()] = arg0[0];
		    
			return result;
		}

		protected void onPostExecute(String[] scaleListAndDirectory) {
			// Store the URL
			SharedPreferences.Editor editor = settings.edit();
		    editor.putString(LAST_ADDRESS, scaleListAndDirectory[scaleListAndDirectory.length - 1]);
		    editor.commit();
		    // Remove it
		    String[] result = new String[scaleListAndDirectory.length - 1];
		    for (int i = 0; i < result.length; i++) {
		    	result[i] = scaleListAndDirectory[i];
		    }
		    // Load list of scales
			adapter = new ArrayAdapter<String>(activity, R.layout.explorer_list_item, result);
			list2.setAdapter(adapter);
		}
	}
}
