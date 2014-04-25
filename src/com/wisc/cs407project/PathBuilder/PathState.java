package com.wisc.cs407project.PathBuilder;

import java.util.ArrayList;

import android.graphics.Color;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;
import com.wisc.cs407project.MapUtils;

public class PathState {
	private ArrayList<PathStateObject> stateList;
	public PathStateObject currentState;
	private Polyline currentLeadingLine;
	private Polyline currentLaggingLine;
	private GoogleMap refMap;
	public Marker currentLeadingMarker, currentLaggingMarker;
	
	public PathState(GoogleMap map) {
		stateList = new ArrayList<PathStateObject>();
		refMap = map;
	}
	
	public void addState(boolean hasLaggingMarker, ArrayList<LatLng> coords, Marker maintainLeading) {
		if (coords.isEmpty()) {
			return;
		}
		// Clear current state display
		clearCurrent(maintainLeading);
		ArrayList<LatLng> newLeadLine = new ArrayList<LatLng>();
		
		// Add Lagging Line if present
		if (!stateList.isEmpty()) {
			ArrayList<LatLng> newLagLine = new ArrayList<LatLng>();
			for (PathStateObject previousState : stateList) {
				newLagLine.addAll(previousState.mainLine);
			}
			currentLaggingLine = refMap.addPolyline(new PolylineOptions()
			.addAll(newLagLine)
			.color(Color.BLACK));
			// Make sure paths are connected
			if (newLagLine.size() > 0 && !coords.get(0).equals(newLagLine.get(newLagLine.size() - 1))) {
				newLeadLine.add(newLagLine.get(newLagLine.size() - 1));
			}
		}
		// Add the Leading Line
		newLeadLine.addAll(coords);
		currentLeadingLine = refMap.addPolyline(new PolylineOptions()
		.addAll(newLeadLine)
		.color(Color.BLUE));
		
		currentState = new PathStateObject(hasLaggingMarker, coords);
		stateList.add(currentState);
		
		// Add the markers
		if (hasLaggingMarker) {
			currentLaggingMarker = refMap.addMarker(new MarkerOptions()
            .position(currentState.laggingMarker)
            .draggable(false));
		}
		if (maintainLeading == null) {
			currentLeadingMarker = refMap.addMarker(new MarkerOptions()
			.position(currentState.leadingMarker)
			.draggable(true));
		}
	}
	
	public void revertState() {
		// If we're at the initial state
		if (stateList.isEmpty()) {
			return;
		}
		clearCurrent(null);
		stateList.remove(stateList.size() - 1);
		// If we revert to the initial state
		if (stateList.isEmpty()) {
			return;
		}
		// Otherwise, re-add previousState
		PathStateObject previousState = stateList.remove(stateList.size() - 1);
		addState(previousState.hasLaggingMarker, previousState.mainLine, null);
	}
	
	public void extendState(LatLng coords, Marker maintainLeading) {
		// If we're at the initial state (we shouldn't be)
		if (stateList.isEmpty()) {
			return;
		}
		clearCurrent(maintainLeading);
		PathStateObject previousState = stateList.remove(stateList.size() - 1);
		ArrayList<LatLng> newMainLine = previousState.mainLine;
		newMainLine.add(coords);
		addState(previousState.hasLaggingMarker, newMainLine, maintainLeading);
	}
	
	private void clearCurrent(Marker maintainLeading) {
		if (currentLeadingLine != null) currentLeadingLine.remove();
		if (currentLaggingLine != null) currentLaggingLine.remove();
		if (currentLeadingMarker != null && maintainLeading == null) currentLeadingMarker.remove();
		if (currentLaggingMarker != null) currentLaggingMarker.remove();
	}
	
	public void prepareForRefactor() {
		if (currentLeadingLine != null) currentLeadingLine.remove();
	}
	
	public void refactor() {
		if (currentLaggingLine != null) currentLaggingLine.remove();
		if (currentLeadingMarker != null) currentLeadingMarker.remove();
		if (currentLaggingMarker != null) currentLaggingMarker.remove();
		stateList.remove(stateList.size() - 1);
		currentState = stateList.get(stateList.size() - 1);
	}
}
