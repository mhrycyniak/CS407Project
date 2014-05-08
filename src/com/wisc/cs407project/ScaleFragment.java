package com.wisc.cs407project;

import android.app.Fragment;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.widget.Button;

import com.wisc.cs407project.ScaleGenUI.ScaleExplorerFragment;

public class ScaleFragment extends Fragment {
	
	private Button scaleBuilderButton;
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		super.onCreateView(inflater, container, savedInstanceState);
		final View myFragmentView = inflater.inflate(R.layout.scalefragment, container, false);
		
		// Grab Buttons and set onClickListeners
		scaleBuilderButton = (Button) myFragmentView.findViewById(R.id.scaleBuilder);
		recordClicked();
		
		return myFragmentView;
	}
	
	private void recordClicked() {
		scaleBuilderButton.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				//TODO may need to change this so back button doesn't re-open popup
				Intent builderIntent = new Intent(getActivity(), ScaleExplorerFragment.class);
				//startActivityForResult(builderIntent, R.id.TAG_BUILDER_IMGLOAD_ID);
				startActivity(builderIntent);
			}
		});
	}	
}
