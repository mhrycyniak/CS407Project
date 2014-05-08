package com.wisc.cs407project.ScaleGenUI;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileFilter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.validator.routines.UrlValidator;

import com.wisc.cs407project.R;

import android.app.Activity;
import android.app.Fragment;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.graphics.Point;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.Display;
import android.view.LayoutInflater;
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
import android.widget.ScrollView;
import android.widget.Toast;
import android.widget.AdapterView.OnItemClickListener;

public class ScaleExplorerFragment extends Fragment {
	Activity activity;
	Button resume, newScale, loadLocal, loadOnline, back, loadURL;
	LinearLayout localLoadLayout, onlineLoadLayout, ll;
	String path = "/";
	EditText currentDirectory, currentURL;
	ListView list, list2;
	ListAdapter adapter;
	String[] data;
	public static final String LAST_ADDRESS = "last_address";
	SharedPreferences settings;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		super.onCreateView(inflater, container, savedInstanceState);
		//setContentView(R.layout.scale_explorer_popup);
		final View myFragmentView = inflater.inflate(R.layout.scale_explorer_popup, container, false);
		activity = getActivity();

		// Set popup width to 3/4 screen width
		ll = (LinearLayout)myFragmentView.findViewById(R.id.scaleExplorerParentView);
		Display display = activity.getWindowManager().getDefaultDisplay();
		Point size = new Point();
		display.getSize(size);
		int width = (3 * size.x) / 4;
		ViewGroup.LayoutParams params = ll.getLayoutParams();
		params.width = width;
		ll.setLayoutParams(params);
		ll.requestLayout();
		
		// Resume Button, Related stuff, and Listener
		resume = (Button)myFragmentView.findViewById(R.id.scaleExplorerResumeButton);
		// This is where a backup would be
		final String resumePath = Environment.getExternalStorageDirectory().toString() + "/" 
				+ getResources().getString(R.string.app_name) + "/"
				+ getResources().getString(R.string.resume_scale_backup_filename);
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
		newScale = (Button)myFragmentView.findViewById(R.id.scaleExplorerNewButton);
		newScale.setOnClickListener(new View.OnClickListener() {
			public void onClick(View v) {
				Intent builderIntent = new Intent(v.getContext(), ScaleBuilder.class);
				startActivity(builderIntent);
			}
		});

		// These load lists are hidden by default
		localLoadLayout = (LinearLayout)myFragmentView.findViewById(R.id.scaleExplorerLoadLocalLayout);
		localLoadLayout.setVisibility(View.GONE);
		onlineLoadLayout = (LinearLayout)myFragmentView.findViewById(R.id.scaleExplorerLoadOnlineLayout);
		onlineLoadLayout.setVisibility(View.GONE);

		// Local directory display
		currentDirectory = (EditText) myFragmentView.findViewById(R.id.scaleExplorerDirectory);
		// Local search list
		list = (ListView) myFragmentView.findViewById(R.id.scaleExplorerList);
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
		back = (Button) myFragmentView.findViewById(R.id.scaleExplorerBackButton);
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
		loadLocal = (Button)myFragmentView.findViewById(R.id.scaleExplorerLoadLocalButton);
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
			String betterPath = path + getResources().getString(R.string.app_scale_directory);;
			if (new File(betterPath).isDirectory()) {
				path = betterPath;
			}
			new LoadDirectoryTask().execute(path);
		}

		// Address display
		currentURL = (EditText) myFragmentView.findViewById(R.id.scaleExplorerURL);
		// If a previous address has been used, go there automatically
		settings = activity.getSharedPreferences(LAST_ADDRESS, activity.MODE_PRIVATE);
		currentURL.setText(settings.getString(LAST_ADDRESS, ""));
		new LoadScalesTask().execute(currentURL.getText().toString());

		// Online scales list
		list2 = (ListView) myFragmentView.findViewById(R.id.scaleExplorerList2);
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
		loadURL = (Button) myFragmentView.findViewById(R.id.scaleExplorerLoadButton);
		loadURL.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View view) {
				new LoadScalesTask().execute(currentURL.getText().toString());
			}
		});

		// Online Load Button and Listener
		loadOnline = (Button)myFragmentView.findViewById(R.id.scaleExplorerLoadOnlineButton);
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
		params = list.getLayoutParams();
		params.height = height;
		list.setLayoutParams(params);
		list.requestLayout();
		params = list2.getLayoutParams();
		params.height = height;
		list2.setLayoutParams(params);
		list2.requestLayout();
		
		return myFragmentView;
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
				// This shouldn't happen
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
				adapter = new ArrayAdapter<String>(activity.getBaseContext(), R.layout.explorer_list_item, R.id.explorerTextItem, data);
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
						String second = str.substring(split+1);
						scaleList.add(second);
					}
				}
				in.close();
			} catch (IOException e) {
				// Abort
				Log.e("ERROR", "No connection");
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

	@Override
	public void onConfigurationChanged (Configuration newConfig) {
		super.onConfigurationChanged(newConfig);

		// Reset size
		// Set popup width to 3/4 screen width
		ll = (LinearLayout)getView().findViewById(R.id.scaleExplorerParentView);
		Display display = activity.getWindowManager().getDefaultDisplay();
		Point size = new Point();
		display.getSize(size);
		int width = (3 * size.x) / 4;
		ViewGroup.LayoutParams params = ll.getLayoutParams();
		params.width = width;
		ll.setLayoutParams(params);
		ll.requestLayout();
	}
}
