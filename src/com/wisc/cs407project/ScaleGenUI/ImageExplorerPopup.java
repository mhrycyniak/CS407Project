package com.wisc.cs407project.ScaleGenUI;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileFilter;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.apache.commons.validator.routines.UrlValidator;
import org.w3c.dom.Document;
import org.w3c.dom.NodeList;

import com.wisc.cs407project.Popup;
import com.wisc.cs407project.R;
import com.wisc.cs407project.R.id;
import com.wisc.cs407project.R.layout;
import com.wisc.cs407project.R.string;
import com.wisc.cs407project.ImageLoader.ImageLoader;

import android.app.Activity;
import android.content.Intent;
import android.content.res.Configuration;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Point;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.Display;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.TextView;

public class ImageExplorerPopup extends Activity {

	Button open, cancel, back;
	ListView list;
	EditText currentDirectory;
	ImageView preview;
	ArrayAdapter<String> adapter;
	String path = "/";
	String currentImage;
	String[] data;
	Activity activity;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		super.onCreate(savedInstanceState);
		setContentView(R.layout.image_explorer_popup);
		activity = this;

		// Open Button and its Listener
		open = (Button) findViewById(R.id.imageExplorerOpenButton);
		open.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View view) {
				// If image is selected, return its location as a result of the Activity
				if (currentImage != null) {
					Intent result = activity.getIntent();
					result.putExtra("imageLoc", currentImage);
					activity.setResult(RESULT_OK, result);
					finish();
				}
			}
		});

		// Cancel Button and its Listener
		cancel = (Button) findViewById(R.id.imageExplorerCancelButton);
		cancel.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View view) {
				finish();
			}
		});

		preview = (ImageView) findViewById(R.id.imageExplorerPreview);

		// Back Button and its Listener
		back = (Button) findViewById(R.id.imageExplorerBackButton);
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

		currentDirectory = (EditText) findViewById(R.id.imageExplorerDirectory);
		list = (ListView) findViewById(R.id.imageExplorerList);
		// Set ListView listener
		list.setOnItemClickListener(new OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
				String item = ((String)list.getItemAtPosition(position));
				item = currentDirectory.getText() + item;
				new LoadDirectoryTask().execute(item);
			}
		});

		//Set popup height to approx 1/3 of screen
		Display display = getWindowManager().getDefaultDisplay();
		Point size = new Point();
		display.getSize(size);
		int height = size.y / 3;
		ViewGroup.LayoutParams params = list.getLayoutParams();
		params.height = height;
		list.setLayoutParams(params);
		list.requestLayout();
		ViewGroup.LayoutParams params2 = preview.getLayoutParams();
		params2.height = height;
		preview.setLayoutParams(params2);
		preview.requestLayout();

		// Check if mounted, get storage directory
		String extState = Environment.getExternalStorageState();
		if(!extState.equals(Environment.MEDIA_MOUNTED)) {
			// This really shouldn't happen, since Browse button is disabled when nothing is mounted
			Intent intent = new Intent(activity, Popup.class);
			intent.putExtra("title", "Error");
			intent.putExtra("text", "No External Storage Mounted");
			startActivity(intent);
			activity.finish();
			Log.e("ERROR", "Storage not mounted");
		}
		else {
			// Try to set path to start with app directory
			path = Environment.getExternalStorageDirectory().toString();
			// TODO make this the Image directory if we add that option
			String betterPath = path + "/" + getResources().getString(R.string.app_name);;
			if (new File(betterPath).isDirectory()) {
				path = betterPath;
			}
			//currentDirectory.setText(path + "/");
			new LoadDirectoryTask().execute(path);
		}
	}


	private class LoadDirectoryTask extends
	AsyncTask<String, Void, String[]> {

		@Override
		protected String[] doInBackground(String... arg0) {
			// If path a directory
			if (new File(arg0[0]).isDirectory()) {
				File directory = new File(arg0[0]);

				// Filter through only directories and supported image types
				FileFilter filterDirectoriesOnly = new FileFilter() {
					public boolean accept(File file) {
						return (file.isDirectory() || checkExtension(file.getPath()));
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
			// If path is an image
			else if (new File(arg0[0]).exists()) {
				String[] result = new String[1];
				result[0] = arg0[0];
				return result;
			}
			// This shouldn't happen
			return null;
		}

		protected void onPostExecute(String[] filesAndDirectory) {
			if (filesAndDirectory == null) {
				// This shouldn't happen
				Log.e("ERROR", "Path is not an image or a directory");
			}
			// If an image was selected
			if (filesAndDirectory.length == 1) {
				Bitmap image = ImageLoader.decodeFile(new File(filesAndDirectory[0]));
				if (image != null) {
					preview.setImageBitmap(image);
					currentImage = filesAndDirectory[0];
				}
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
				Log.d("set path to: ", filesAndDirectory[filesAndDirectory.length - 1]);

				// Update the ListView and clear the preview
				preview.setImageBitmap(null);
				currentImage = null;
				data = filesOnly;
				adapter = new ArrayAdapter<String>(getBaseContext(), R.layout.explorer_list_item, R.id.explorerTextItem, data);
				list.setAdapter(adapter);
			}
		}
	}

	public boolean checkExtension(String path) {
		String ext3 = "";
		String ext4 = "";
		if (path.length() > 4) {
			ext3 = path.substring(path.length() - 3).toLowerCase();
			if (path.length() > 5) {
				ext4 = path.substring(path.length() - 4).toLowerCase();
			}
		}
		if (ext3.equals("png") || ext3.equals("bmp") || ext3.equals("jpg") || ext3.equals("gif") || ext4.equals("jpeg")) {
			return true;
		} else {
			return false;
		}
	}

	@Override
	public void onConfigurationChanged (Configuration newConfig) {
		super.onConfigurationChanged(newConfig);

		// Reset size
		//Set popup height to approx 1/3 of screen
		Display display = getWindowManager().getDefaultDisplay();
		Point size = new Point();
		display.getSize(size);
		int height = 2 * (size.y / 5);
		ViewGroup.LayoutParams params = list.getLayoutParams();
		params.height = height;
		list.setLayoutParams(params);
		list.requestLayout();
		ViewGroup.LayoutParams params2 = preview.getLayoutParams();
		params2.height = height;
		preview.setLayoutParams(params2);
		preview.requestLayout();
	}
}
