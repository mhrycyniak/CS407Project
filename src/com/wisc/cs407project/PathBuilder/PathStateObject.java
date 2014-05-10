package com.wisc.cs407project.PathBuilder;

import java.util.ArrayList;

import com.google.android.gms.maps.model.LatLng;

public class PathStateObject {
	public ArrayList<LatLng> mainLine;
	public LatLng leadingMarker;
	public LatLng laggingMarker;
	public boolean hasLaggingMarker;
	// This allows putting "connect mode" redraws into the undo stack
	public boolean hidden;
	
	public PathStateObject(boolean hasLaggingMarker, ArrayList<LatLng> line) {
		hidden = false;
		if (!line.isEmpty()) {
			mainLine = line;
			leadingMarker = mainLine.get(mainLine.size() - 1);
			if (hasLaggingMarker) {
				laggingMarker = mainLine.get(0);
				this.hasLaggingMarker = true;
			}
		}
	}
}
