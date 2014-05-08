package com.wisc.cs407project;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.FilenameFilter;
import java.io.InputStreamReader;
import java.io.FileReader;
import java.io.File;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.w3c.dom.Document;
import org.w3c.dom.NodeList;
import org.apache.commons.validator.routines.UrlValidator;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import com.wisc.cs407project.R;
import com.wisc.cs407project.PathChooser.LoadPathsTask;

public class ScaleChooser extends Activity implements OnItemClickListener {
	private ListView scales;
	private ScaleChooser ref;
	private String currentDirectory;
	private static final String SETTINGSNAME = "WalkSettings";
	private HashMap<String,String> fileName = new HashMap<String,String>();
	private Button localButton;
	private Button changeDirectory;
	private boolean local = true;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.chooser);
		localButton = (Button)findViewById(R.id.local_btn);
		changeDirectory = (Button)findViewById(R.id.changeDirectory_btn);
		changeDirectory.setVisibility(View.GONE);
		localButton.setVisibility(View.VISIBLE);
		ref = this;
		scales = (ListView) findViewById(R.id.listView1);
		scales.setOnItemClickListener(this);
		SharedPreferences settings = getSharedPreferences(SETTINGSNAME, 0);
		//currentDirectory = settings.getString("scaleDirectory", "");
		//currentDirectory = "http://pages.cs.wisc.edu/~hrycynia/cs407project/";
		currentDirectory = Environment.getExternalStorageDirectory().toString() +
				getResources().getString(R.string.app_scale_directory) + "/";
		if (currentDirectory != "") {
			new LoadScalesTask().execute(currentDirectory);
		} else {
			ChangeDirectoryClicked(null);
		}
	}
	
	public void ChangeLocalClicked(View view)
	{
		local = !local;
		ArrayAdapter<String> adapter = new ArrayAdapter<String>(ref,
				android.R.layout.simple_list_item_1, new ArrayList<String>());
		fileName.clear();
		scales.setAdapter(adapter);
		if (local)
		{
			localButton.setText("Change To Online Paths");			
		}
		else
		{			
			localButton.setText("Change To Local Paths");
		}
		new LoadScalesTask().execute(currentDirectory);
	}

	public void ChangeDirectoryClicked(View view) {
		AlertDialog.Builder alert = new AlertDialog.Builder(this);

		alert.setTitle("Change Directory");
		alert.setMessage("Input the location to search for scale files.");

		// Set an EditText view to get user input
		final EditText input = new EditText(this);
		input.setText(currentDirectory);
		alert.setView(input);

		alert.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface dialog, int whichButton) {
				currentDirectory = input.getText().toString();
				SharedPreferences settings = getSharedPreferences(SETTINGSNAME, 0);
				SharedPreferences.Editor editor = settings.edit();
				editor.putString("scaleDirectory", currentDirectory);
				editor.commit();
				String location = getExternalFilesDir(null).getAbsolutePath() + "/dirs.txt";
				DirUtils.storeDir(location, currentDirectory);

				new LoadScalesTask().execute(currentDirectory);
			}
		});

		alert.setNegativeButton("Cancel", null);

		alert.show();
	}

	private class LoadScalesTask extends AsyncTask<String, Void, List<String>> {

		@Override
		protected List<String> doInBackground(String... arg0) {
			List<String> scaleList = new ArrayList<String>();
			fileName.clear();
			try {
				File directory = new File(arg0[0]);
				File[] xmlFiles = directory.listFiles(new FilenameFilter(){
					@Override
					public boolean accept(File dir, String filename) {
						return filename.substring(filename.length()-4).equals(".xml");
					}});
				for(File file : xmlFiles){					
					scaleList.add(file.getName());
					fileName.put(file.getName(), file.getName());
				}
				
			} catch (Exception e) {
				e.printStackTrace();
			}
			return scaleList;
		}

		protected void onPostExecute(List<String> scaleList) {
			ArrayAdapter<String> adapter = new ArrayAdapter<String>(ref,
					android.R.layout.simple_list_item_1, scaleList);
			scales.setAdapter(adapter);
		}
	}

	@Override
	public void onItemClick(AdapterView<?> arg0, View arg1, int arg2, long arg3) {
		String path = currentDirectory;
		if (!path.endsWith("/")) {
			path += "/";
		}
		path += fileName.get((String) scales.getItemAtPosition(arg2));
		new LoadIndividualScaleTask().execute(path);
	}

	private class LoadIndividualScaleTask extends AsyncTask<String, Void, String> {

		@Override
		protected String doInBackground(String... arg0) {
			try {
				BufferedReader in = null;
				UrlValidator validator = new UrlValidator();
				if(validator.isValid(arg0[0])) {
					in = new BufferedReader(new InputStreamReader(new URL(arg0[0]).openStream()));
				} else if(new File(arg0[0]).exists()) {
					in = new BufferedReader(new FileReader(arg0[0]));
				} else {
					Intent intent = new Intent(ref, Popup.class);
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

		protected void onPostExecute(String scaleItem) {
			if (scaleItem != null) {
				try {
					DocumentBuilder docBuilder = DocumentBuilderFactory.newInstance().newDocumentBuilder();
					Document doc = docBuilder.parse(new ByteArrayInputStream(scaleItem.getBytes()));
					NodeList items = doc.getElementsByTagName("scaleItem");
					if (items.getLength() < 2)
					{
						Intent intent = new Intent(ref, Popup.class);
						intent.putExtra("title", "Error");
						intent.putExtra("text", "The requested scale does not contain enough scale items.");
						startActivity(intent);
					}
					else {
						Intent intent = new Intent();
						intent.putExtra("scaleItem", scaleItem);
						setResult(1, intent);
						finish();
					}
				}
				catch (Exception e)
				{
					Intent intent = new Intent(ref, Popup.class);
					intent.putExtra("title", "Error");
					intent.putExtra("text", "The requested scale is not in the correct format.");
					startActivity(intent);
				}
			} else {
				Intent intent = new Intent(ref, Popup.class);
				intent.putExtra("title", "Error");
				intent.putExtra("text", "The requested scale does not exist.");
				startActivity(intent);
			}
		}
	}
}