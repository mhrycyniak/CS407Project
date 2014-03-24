package com.wisc.cs407project.ScaleGenUI;

import com.wisc.cs407project.R;
import com.wisc.cs407project.R.layout;

import android.app.Activity;
import android.os.Bundle;
import android.view.Window;

public class ScaleExplorerPopup extends Activity {
	
	Activity activity;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		super.onCreate(savedInstanceState);
		setContentView(R.layout.scale_explorer_popup);
		activity = this;
		
}
}
