package com.wisc.cs407project.PathBuilder;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileFilter;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.validator.routines.UrlValidator;

import com.wisc.cs407project.Popup;
import com.wisc.cs407project.R;

import android.app.Activity;
import android.app.FragmentManager;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.graphics.Point;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;
import android.view.Display;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.AdapterView.OnItemClickListener;

public class PathExplorerPopup extends Activity {
	Activity activity;
	Button resume, loadLocal, loadOnline, back, loadURL;
	LinearLayout localLoadLayout, onlineLoadLayout, ll;
	String path = "/";
	EditText currentDirectory, currentURL;
	ListView list, list2;
	ListAdapter adapter;
	String[] data;
	public static final String LAST_PATH_ADDRESS = "last_path_address";
	SharedPreferences settings;
	boolean isResuming, localPath;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		super.onCreate(savedInstanceState);
		setContentView(R.layout.path_explorer_popup);
		activity = this;

		// Set popup width to 3/4 screen width
		ll = (LinearLayout)findViewById(R.id.pathExplorerParentView);
		Display display = getWindowManager().getDefaultDisplay();
		Point size = new Point();
		display.getSize(size);
		int width = (3 * size.x) / 4;
		ViewGroup.LayoutParams params = ll.getLayoutParams();
		//MarginLayoutParams params = (MarginLayoutParams) ll.getLayoutParams();
		params.width = width;
		//params.setMargins(0, (size.x / 4), 0, 0);
		ll.setLayoutParams(params);
		ll.requestLayout();
		
		// Resume Button, Related stuff, and Listener
		resume = (Button)findViewById(R.id.pathExplorerResumeButton);
		// This is where a backup would be
		final String resumePath = Environment.getExternalStorageDirectory().toString() + "/" 
				+ getResources().getString(R.string.app_name) + "/"
				+ getResources().getString(R.string.resume_path_backup_filename);
		// If it's there, set up the resume button
		if (new File(resumePath).exists()) {
			resume.setOnClickListener(new View.OnClickListener() {
				public void onClick(View v) {
					isResuming = true;
					localPath = true;
					new LoadIndividualPathTask().execute(resumePath);
				}
			});
		}
		// Otherwise the resume button is disabled
		else {
			resume.setEnabled(false);
		}

		// These load lists are hidden by default
		localLoadLayout = (LinearLayout)findViewById(R.id.pathExplorerLoadLocalLayout);
		localLoadLayout.setVisibility(View.GONE);
		onlineLoadLayout = (LinearLayout)findViewById(R.id.pathExplorerLoadOnlineLayout);
		onlineLoadLayout.setVisibility(View.GONE);

		// Local directory display
		currentDirectory = (EditText) findViewById(R.id.pathExplorerDirectory);
		// Local search list
		list = (ListView) findViewById(R.id.pathExplorerList);
		// Set up ListView listener
		list.setOnItemClickListener(new OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
				String item = ((String)list.getItemAtPosition(position));
				item = currentDirectory.getText() + item;
				localPath = true;
				new LoadDirectoryTask().execute(item);
			}
		});
		// Directory display's back button and listener
		back = (Button) findViewById(R.id.pathExplorerBackButton);
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
		loadLocal = (Button)findViewById(R.id.pathExplorerLoadLocalButton);
		loadLocal.setOnClickListener(new View.OnClickListener() {
			public void onClick(View v) {
				if (localLoadLayout.getVisibility() == View.GONE) { 
					onlineLoadLayout.setVisibility(View.GONE);
					localLoadLayout.setVisibility(View.VISIBLE);
				} else {
					localLoadLayout.setVisibility(View.GONE);
				}
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
			String betterPath = path + getResources().getString(R.string.app_path_directory);;
			Log.d("testing path directory...", betterPath);
			if (new File(betterPath).isDirectory()) {
				Log.d("changed path directory", "to default");
				path = betterPath;
			}
			new LoadDirectoryTask().execute(path);
		}

		// Address display
		currentURL = (EditText) findViewById(R.id.pathExplorerURL);
		// If a previous address has been used, go there automatically
		settings = getSharedPreferences(LAST_PATH_ADDRESS, MODE_PRIVATE);
		currentURL.setText(settings.getString(LAST_PATH_ADDRESS, ""));
		if (currentURL.getText().toString().equals("")) {
			currentURL.setText("http://pages.cs.wisc.edu/~hrycynia/cs407project/");
		}
		new LoadPathsTask().execute(currentURL.getText().toString());

		// Online paths list
		list2 = (ListView) findViewById(R.id.pathExplorerList2);
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
				localPath = false;
				new LoadIndividualPathTask().execute(path);
			}
		});
		// Load Button and its Listener
		loadURL = (Button) findViewById(R.id.pathExplorerLoadButton);
		loadURL.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View view) {
				new LoadPathsTask().execute(currentURL.getText().toString());
			}
		});

		// Online Load Button and Listener
		loadOnline = (Button)findViewById(R.id.pathExplorerLoadOnlineButton);
		loadOnline.setOnClickListener(new View.OnClickListener() {
			public void onClick(View v) {
				if (onlineLoadLayout.getVisibility() == View.GONE) { 
					onlineLoadLayout.setVisibility(View.VISIBLE);
					localLoadLayout.setVisibility(View.GONE);
				} else {
					onlineLoadLayout.setVisibility(View.GONE);
				}
			}
		});

		// Set popup list heights to 1/3 screen height
		display.getSize(size);
		int height = size.y / 3;
		ViewGroup.LayoutParams params2 = list.getLayoutParams();
		params2.height = height;
		list.setLayoutParams(params2);
		list.requestLayout();
		params2 = list2.getLayoutParams();
		params2.height = height;
		list2.setLayoutParams(params2);
		list2.requestLayout();
		
	}

	private class LoadDirectoryTask extends
	AsyncTask<String, Void, String[]> {

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
								((tempPath.length() > 4) && (tempPath.substring(tempPath.length() - 3).toLowerCase().equals("kml"))));
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
				// This shouldn't happen
				Log.e("ERROR", "Path is not a directory or an xml");
			}
			return null;
		}

		protected void onPostExecute(String[] filesAndDirectory) {
			// If a path was selected
			if (filesAndDirectory.length == 1) {
				new LoadIndividualPathTask().execute(filesAndDirectory[0]);
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

	private class LoadPathsTask extends AsyncTask<String, Void, String[]> {

		@Override
		protected String[] doInBackground(String... arg0) {
			List<String> pathList = new ArrayList<String>();
			try {
				String path = arg0[0];
				if (!path.endsWith("/")) {
					path += "/";
				}
				path += "Paths.txt";
				BufferedReader in;
				in = new BufferedReader(new InputStreamReader(new URL(path).openStream()));

				String str;
				while ((str = in.readLine()) != null) {
					int split = str.lastIndexOf('\t');
					if (split != -1)
					{
						String second = str.substring(split+1);
						pathList.add(second);
					}
				}
				in.close();
			} catch (IOException e) {
				// Abort
				Log.e("ERROR", "No connection or Failed to load Paths.txt");
			} catch (Exception e) {
				// Abort without error
				this.cancel(true);
			}
			// Plus 1 for passing URL to onPostExecute
			String[] result = new String[pathList.size() + 1];
			for (int i = 0; i < pathList.size(); i++) {
				result[i] = pathList.get(i);
			}
			// Pass URL
			result[pathList.size()] = arg0[0];

			return result;
		}

		protected void onPostExecute(String[] pathListAndDirectory) {
			// Store the URL
			SharedPreferences.Editor editor = settings.edit();
			editor.putString(LAST_PATH_ADDRESS, pathListAndDirectory[pathListAndDirectory.length - 1]);
			editor.commit();
			// Remove it
			String[] result = new String[pathListAndDirectory.length - 1];
			for (int i = 0; i < result.length; i++) {
				result[i] = pathListAndDirectory[i];
			}
			// Load list of paths
			adapter = new ArrayAdapter<String>(activity, R.layout.explorer_list_item, result);
			list2.setAdapter(adapter);
		}
	}

	@Override
	public void onConfigurationChanged (Configuration newConfig) {
		super.onConfigurationChanged(newConfig);

		// Reset size
		// Set popup width to 3/4 screen width
		ll = (LinearLayout)findViewById(R.id.pathExplorerParentView);
		Display display = getWindowManager().getDefaultDisplay();
		Point size = new Point();
		display.getSize(size);
		int width = (3 * size.x) / 4;
		ViewGroup.LayoutParams params = ll.getLayoutParams();
		params.width = width;
		ll.setLayoutParams(params);
		ll.requestLayout();
	}
	
	private class LoadIndividualPathTask extends
	AsyncTask<String, Void, String> {

		@Override
		protected String doInBackground(String... arg0) {
			try {
				BufferedReader in = null;
				UrlValidator validator = new UrlValidator();
				if(validator.isValid(arg0[0])) {
					in = new BufferedReader(new InputStreamReader(
							new URL(arg0[0]).openStream()));
				} else if(new File(arg0[0]).exists()) {
					in = new BufferedReader(new FileReader(arg0[0]));
				} else {
					Intent intent = new Intent(activity, Popup.class);
					intent.putExtra("title", "Error");
					intent.putExtra("text", "Invalid Directory Location");
					startActivity(intent);
				}

				String str;
				String fullFile = "";
				while ((str = in.readLine()) != null) {
					fullFile += str + "\n";
				}
				in.close();
				return fullFile;
			} catch (Exception e) {
			}
			return null;
		}

		protected void onPostExecute(String pathFile) {
			if (pathFile != null) {
				try {
					//Log.d("Load path intent sent", "file: " + pathFile);
					Intent drawPath = new Intent("LOAD_RECORD_FRAGMENT");
					drawPath.putExtra("kmlFile", pathFile);
					drawPath.putExtra("isLocalLoad", localPath);
					// TODO change this depending on if resuming or not
					if (isResuming) {
						drawPath.putExtra("isResumeLoad", true);
					} else {
						drawPath.putExtra("isResumeLoad", false);
					}
					LocalBroadcastManager.getInstance(activity).sendBroadcast(drawPath);
					PathExplorerPopup.this.finish();
				}
				catch (Exception e)
				{
					Intent intent = new Intent(activity, Popup.class);
					intent.putExtra("title", "Error");
					intent.putExtra("text", "The requested scale is not in the correct format.");
					e.printStackTrace();
					startActivity(intent);
				}
			} else {
				Intent intent = new Intent(activity, Popup.class);
				intent.putExtra("title", "Error");
				intent.putExtra("text", "The requested scale does not exist.");
				startActivity(intent);
			}
		}
	}
	
	@Override
	public void onBackPressed() {
		if (getFragmentManager().getBackStackEntryCount() == 0) {
			this.finish();
		} else {   
			getFragmentManager().popBackStack();
			Intent intent = new Intent("RESET_RECORD_FRAGMENT");
			LocalBroadcastManager.getInstance(this).sendBroadcast(intent);
		}
	}
}
