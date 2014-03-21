package com.wisc.cs407project;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

public class BuilderListAdapter extends BaseAdapter {
//TODO clean and comment better
	private Activity activity;
	private ScaleGenerator data; // The Scale data, indexed by finals defined in ScaleBuilder
	private static LayoutInflater inflater = null;
	//public ImageLoader imageLoader; 

	public BuilderListAdapter(Activity a, ScaleGenerator d) {
		activity = a;
		data = d;
		inflater = (LayoutInflater)activity.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		//imageLoader=new ImageLoader(activity.getApplicationContext());
	}

	@Override
	public int getCount() {
		return data.members.size();
	}

	@Override
	public Object getItem(int index) {
		return data.members.get(index);
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

			holder.nameEdit = (EditText) convertView.findViewById(R.id.builderRowNameEdit);
			holder.comparableEdit = (EditText) convertView.findViewById(R.id.builderRowComparableEdit);
			holder.units = (TextView) convertView.findViewById(R.id.builderRowUnits);
			holder.descriptionEdit = (EditText) convertView.findViewById(R.id.builderRowDescriptionEdit);
			holder.imageLocationEdit = (EditText) convertView.findViewById(R.id.builderRowImageLocationEdit);
			holder.image = (ImageView) convertView.findViewById(R.id.builderRowImage);

			// Delete button for each entry in the list, with Alert Dialog confirmation
			holder.deleteButton = (Button) convertView.findViewById(R.id.builderRowDelete);
			holder.deleteButton.setTag(position); // Pass along the index
			holder.deleteButton.setOnClickListener(new View.OnClickListener() {
				public void onClick(View v) {

					AlertDialog.Builder confirm = new AlertDialog.Builder(v.getContext());
					String name = data.members.get((Integer)v.getTag()).name;
					confirm.setTitle("Delete " + name);
					confirm.setMessage("Are you sure?");
					// A final variable is needed for passing the index into the onClick method for "Yes"
					final int tempIndex = ((Integer)v.getTag()).intValue();

					// Add the buttons
					confirm.setPositiveButton("Yes", new DialogInterface.OnClickListener() 
					{
						public void onClick(DialogInterface dialog, int id) 
						{
							data.members.remove(tempIndex);
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
		Log.d("", "loading " + position);
		Log.d("", "name is " + data.members.get(position).name);
		// Set up the ViewHolder
		holder.position = position;
		holder.nameEdit.setText(data.members.get(position).name);
		holder.comparableEdit.setText(data.members.get(position).comparativeValue.toString());
		holder.units.setText(data.scaleMetric);
		holder.descriptionEdit.setText(data.members.get(position).text);
		holder.imageLocationEdit.setText(data.members.get(position).imageLocation);
		// holder.image

		return convertView;
	}

}
