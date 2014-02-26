package com.wisc.cs407project;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
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
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ListView;
import com.wisc.cs407project.R;

public class ScaleChooser extends Activity implements OnItemClickListener {
	private ListView scales;
	private ScaleChooser ref;
	private String currentDirectory;
	private static final String SETTINGSNAME = "ScaleSettings";
	private HashMap<String,String> fileName = new HashMap<String,String>();

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.chooser);
		ref = this;
		scales = (ListView) findViewById(R.id.listView1);
		scales.setOnItemClickListener(this);
		SharedPreferences settings = getSharedPreferences(SETTINGSNAME, 0);
		currentDirectory = settings.getString("scaleDirectory", "");
		if (currentDirectory != "") {
			new LoadScalesTask().execute(currentDirectory);
		}
		else
		{
			ChangeDirectoryClicked(null);
		}
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
				SharedPreferences settings = getSharedPreferences(SETTINGSNAME,
						0);
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
				String path = arg0[0];
				if (!path.endsWith("/")) {
					path += "/";
				}
				path += "Scales.txt";
				BufferedReader in;
				UrlValidator validator = new UrlValidator();
				if(validator.isValid(path)) {
					in = new BufferedReader(new InputStreamReader(
							new URL(path).openStream()));
				} else {
					in = new BufferedReader(new FileReader(path));
				}
	
				String str;
				while ((str = in.readLine()) != null) {
					int split = str.lastIndexOf('\t');
					if (split != -1)
					{
						String first = str.substring(0, split);
						String second = str.substring(split+1);
						scaleList.add(first);
						fileName.put(first, second);
					}
				}
				in.close();
			} catch (Exception e) {
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

	private class LoadIndividualScaleTask extends
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
					DocumentBuilder docBuilder = DocumentBuilderFactory
							.newInstance().newDocumentBuilder();
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
						Intent intent = new Intent(ref, PathChooser.class);
						intent.putExtra("scaleItem", scaleItem);
						startActivity(intent);
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
