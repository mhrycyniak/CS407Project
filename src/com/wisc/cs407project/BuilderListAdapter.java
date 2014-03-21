package com.wisc.cs407project;

import java.util.ArrayList;
import java.util.HashMap;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

public class BuilderListAdapter extends BaseAdapter {

	private Activity activity;
    private ArrayList<HashMap<String, String>> data; // The Scale data, indexed by finals defined in ScaleBuilder
    private static LayoutInflater inflater = null;
    //public ImageLoader imageLoader; 
 
    public BuilderListAdapter(Activity a, ArrayList<HashMap<String, String>> d) {
        activity = a;
        data = d;
        inflater = (LayoutInflater)activity.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        //imageLoader=new ImageLoader(activity.getApplicationContext());
    }
	
	@Override
	public int getCount() {
		return data.size();
	}

	@Override
	public Object getItem(int index) {
		return data.get(index);
	}

	@Override
	public long getItemId(int index) {
		return index;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		BuilderListViewHolder holder;
		
		// Create the ListView item
		if (convertView == null) {
		convertView = inflater.inflate(R.layout.builder_row, parent, false);
		holder = new BuilderListViewHolder();
		
		//holder.nameLabel = (TextView) convertView.findViewById(R.id.builderRowNameLabel);
		holder.nameEdit = (EditText) convertView.findViewById(R.id.builderRowNameEdit);
		//holder.comparableLabel = (TextView) convertView.findViewById(R.id.builderRowComparableLabel);
		holder.comparableEdit = (EditText) convertView.findViewById(R.id.builderRowComparableEdit);
		holder.units = (TextView) convertView.findViewById(R.id.builderRowUnits);
		//holder.descriptionLabel = (TextView) convertView.findViewById(R.id.builderRowDescriptionLabel);
		holder.descriptionEdit = (EditText) convertView.findViewById(R.id.builderRowDescriptionEdit);
		//holder.imageLocationLabel = (TextView) convertView.findViewById(R.id.builderRowImageLocationLabel);
		holder.imageLocationEdit = (EditText) convertView.findViewById(R.id.builderRowImageLocationEdit);
		holder.image = (ImageView) convertView.findViewById(R.id.builderRowImage);
		
		// Delete button for each entry in the list, with Alert Dialog confirmation
		holder.deleteButton = (Button) convertView.findViewById(R.id.builderRowDelete);
		holder.deleteButton.setTag(position); // Pass along the index
		holder.deleteButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
            	
            	AlertDialog.Builder confirm = new AlertDialog.Builder(v.getContext());
            	String name = data.get((Integer)v.getTag()).get(ScaleBuilder.KEY_NAME_EDIT);
    			confirm.setTitle("Delete " + name);
    			confirm.setMessage("Are you sure?");
    			// A final variable is needed for passing the index into the onClick method for "Yes"
    			final int tempIndex = ((Integer)v.getTag()).intValue();
    			
    			// Add the buttons
    			confirm.setPositiveButton("Yes", new DialogInterface.OnClickListener() 
    			{
    				public void onClick(DialogInterface dialog, int id) 
    				{
    	            	data.remove(tempIndex);
    	            	notifyDataSetChanged();
    				}
    			});
    			confirm.setNegativeButton("No", new DialogInterface.OnClickListener() 
    			{
    				public void onClick(DialogInterface dialog, int id) 
    				{
    				}
    			});
    			// 3. Create and show
    			confirm.create().show();
            }
		});

		convertView.setTag(holder);
		}
		
		// Or recycle it
		else {
		holder = (BuilderListViewHolder) convertView.getTag();
		}

		// Set up the ViewHolder
		holder.position = position;
		//holder.nameLabel.setText(data.get(position).get(ScaleBuilder.KEY_NAME_LABEL));
		holder.nameEdit.setText(data.get(position).get(ScaleBuilder.KEY_NAME_EDIT));
		//holder.comparableLabel.setText(data.get(position).get(ScaleBuilder.KEY_COMPARABLE_LABEL));
		holder.comparableEdit.setText(data.get(position).get(ScaleBuilder.KEY_COMPARABLE_EDIT));
		holder.units.setText(data.get(position).get(ScaleBuilder.KEY_UNITS));
		//holder.descriptionLabel.setText(data.get(position).get(ScaleBuilder.KEY_DESCRIPTION_LABEL));
		holder.descriptionEdit.setText(data.get(position).get(ScaleBuilder.KEY_DESCRIPTION_EDIT));
		//holder.imageLocationLabel.setText(data.get(position).get(ScaleBuilder.KEY_IMAGE_LOCATION_LABEL));
		holder.imageLocationEdit.setText(data.get(position).get(ScaleBuilder.KEY_IMAGE_LOCATION_EDIT));
		// holder.image
		
		return convertView;
	}

}
