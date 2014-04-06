package com.wisc.cs407project.ScaleGenUI;

import com.wisc.cs407project.R;
import com.wisc.cs407project.ScaleObject;
import com.wisc.cs407project.ImageLoader.ImageLoader;
import com.wisc.cs407project.R.id;
import com.wisc.cs407project.R.layout;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Point;
import android.os.Environment;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.Display;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

public class BuilderListAdapter extends BaseAdapter {
	private Activity activity; // For reference
	private ScaleGenerator data; // The Scale data, indexed by finals defined in ScaleBuilder
	private static LayoutInflater inflater = null;
	public ImageLoader imageLoader;
	private boolean mediaIsMounted; // For enabling/disabling Browse buttons

	public BuilderListAdapter(Activity a, ScaleGenerator d) {
		activity = a;
		data = d;
		inflater = (LayoutInflater)activity.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		imageLoader = new ImageLoader(activity.getApplicationContext());
		
		// Check if mounted, to enable/disable Browse buttons
		String extState = Environment.getExternalStorageState();
		if(extState.equals(Environment.MEDIA_MOUNTED)) {
			mediaIsMounted = true;
		}
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
		final BuilderListViewHolder holder;

		// Create the ListView item
		if (convertView == null) {
			convertView = inflater.inflate(R.layout.builder_row, parent, false);
			holder = new BuilderListViewHolder();

			// Name and Change Listener
			holder.nameEdit = (EditText) convertView.findViewById(R.id.builderRowNameEdit);
			holder.nameEdit.addTextChangedListener(new TextWatcher(){
		        public void afterTextChanged(Editable s) {
		        	// Avoid a potentially infinite change loop
		        	if (!data.members.get(holder.position).name.equals(s)) {
		        		data.members.get(holder.position).name = s.toString();
		        	}
		        }
		        public void beforeTextChanged(CharSequence s, int start, int count, int after){}
		        public void onTextChanged(CharSequence s, int start, int before, int count){}
		    });
			
			// Measurement and Change Listeners
			holder.comparableEdit = (EditText) convertView.findViewById(R.id.builderRowComparableEdit);
			holder.comparableEdit.addTextChangedListener(new TextWatcher(){
		        public void afterTextChanged(Editable s) {
		        	// Avoid a potentially infinite change loop
		        	ScaleObject temp = data.members.get(holder.position);
		        	if (!temp.comparativeValue.equals(ScaleGenerator.convertToLong(s.toString()))) {
		        		temp.comparativeValue = ScaleGenerator.convertToLong(s.toString());
		        		if (temp.comparativeValue > data.maxComparativeValue){
		        			data.refactorMaxValue(temp.comparativeValue);
		        		}
		        		else {
		        			temp.percentage = ((double)temp.comparativeValue / data.maxComparativeValue);
		        		}
		        	}
		        }
		        public void beforeTextChanged(CharSequence s, int start, int count, int after){}
		        public void onTextChanged(CharSequence s, int start, int before, int count){}
		    });
			// Second listener to update ratios after change
			holder.comparableEdit.setOnEditorActionListener(new TextView.OnEditorActionListener() {
		        @Override
		        public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
		            if (actionId == EditorInfo.IME_ACTION_DONE) {
		            	notifyDataSetChanged();
		                return true;
		            }
		            return false;
		        }
		    });
			
			holder.units = (TextView) convertView.findViewById(R.id.builderRowUnits);
			
			// Description and Change Listener
			holder.descriptionEdit = (EditText) convertView.findViewById(R.id.builderRowDescriptionEdit);
			holder.descriptionEdit.addTextChangedListener(new TextWatcher(){
		        public void afterTextChanged(Editable s) {
		        	// Avoid a potentially infinite change loop
		        	if (!data.members.get(holder.position).text.equals(s)) {
		        		data.members.get(holder.position).text = s.toString();
		        	}
		        }
		        public void beforeTextChanged(CharSequence s, int start, int count, int after){}
		        public void onTextChanged(CharSequence s, int start, int before, int count){}
		    });
			
			// Image Location and Change Listener
			holder.imageLocationEdit = (EditText) convertView.findViewById(R.id.builderRowImageLocationEdit);
			holder.imageLocationEdit.addTextChangedListener(new TextWatcher(){
		        public void afterTextChanged(Editable s) {
		        	// Avoid a potentially infinite change loop
		        	if (!data.members.get(holder.position).imageLocation.equals(s)) {
		        		data.members.get(holder.position).imageLocation = s.toString();
		        	}
		        }
		        public void beforeTextChanged(CharSequence s, int start, int count, int after){}
		        public void onTextChanged(CharSequence s, int start, int before, int count){}
		    });
			
			// Measurement to Max ratio and image view
			holder.ratio = (TextView) convertView.findViewById(R.id.builderRowPercentage);
			holder.image = (ImageView) convertView.findViewById(R.id.builderRowImage);

			// Delete button for each entry in the list, with Alert Dialog confirmation
			holder.deleteButton = (Button) convertView.findViewById(R.id.builderRowDelete);
			holder.deleteButton.setOnClickListener(new View.OnClickListener() {
				public void onClick(View v) {

					AlertDialog.Builder confirm = new AlertDialog.Builder(v.getContext());
					//String name = data.members.get((Integer)v.getTag()).name;
					String name = data.members.get(holder.position).name;
					confirm.setTitle("Delete " + name);
					confirm.setMessage("Are you sure?");

					// Add the buttons
					confirm.setPositiveButton("Yes", new DialogInterface.OnClickListener() 
					{
						public void onClick(DialogInterface dialog, int id) 
						{
							// Make sure max value is changed if needed
							boolean newMaxNeeded = false;
							if (data.members.get(holder.position).comparativeValue.equals(data.maxComparativeValue)) {
								newMaxNeeded = true;
							}
							data.members.remove(holder.position);
							if (newMaxNeeded) {
								data.refactorMaxValue();
							}
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

			// Load Image button and Listener
			holder.loadButton = (Button) convertView.findViewById(R.id.builderRowImageLoadButton);
			holder.loadButton.setOnClickListener(new View.OnClickListener() {
				public void onClick(View v) {
					data.members.get(holder.position).imageLocation = holder.imageLocationEdit.getText().toString();
					notifyDataSetChanged();
				}
			});
			
			// Browse for Image button and Listener
			holder.browseButton = (Button) convertView.findViewById(R.id.builderRowImageBrowseButton);
			holder.browseButton.setOnClickListener(new View.OnClickListener() {
				public void onClick(View arg0) {
					// Open the explorer activity, passing it the position, noting that result is requested
					Intent builderIntent = new Intent(arg0.getContext(), ImageExplorerPopup.class);
					builderIntent.putExtra("position", holder.position);
					activity.startActivityForResult(builderIntent, R.id.TAG_BUILDER_IMGLOAD_ID);
				}
			});
			// Disable if media isn't mounted
			if(!mediaIsMounted) {
				holder.browseButton.setEnabled(false);
			}

			convertView.setTag(holder);
		}

		// Or recycle it
		else {
			holder = (BuilderListViewHolder) convertView.getTag();
			// Reset recycled ImageView to show nothing in case there's nothing to load below
			holder.image.setImageBitmap(null);
		}
		// Set up the ViewHolder
		holder.position = position;
		holder.nameEdit.setText(data.members.get(position).name);
		holder.comparableEdit.setText(data.members.get(position).comparativeValue.toString());
		holder.units.setText(data.scaleMetric);
		holder.descriptionEdit.setText(data.members.get(position).text);
		holder.imageLocationEdit.setText(data.members.get(position).imageLocation);
		String percentage = String.format("%f", data.members.get(position).percentage);
		holder.ratio.setText(percentage);

		// Tell the ImageLoader not to bother fully loading images bigger than the view
		Display display = activity.getWindowManager().getDefaultDisplay();
		Point size = new Point();
		display.getSize(size);
		int width = size.x;
		width = width / 2;
		imageLoader.setSize(width, width);
		imageLoader.DisplayImage(data.members.get(position).imageLocation, holder.image);

		return convertView;
	}

	public void notifyItemAdded() {
		notifyDataSetChanged();
	}
}
