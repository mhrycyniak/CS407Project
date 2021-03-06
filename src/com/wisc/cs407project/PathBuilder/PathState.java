package com.wisc.cs407project.PathBuilder;

import java.util.ArrayList;

import android.graphics.Color;
import android.util.Log;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;

public class PathState {
	public enum MarkerType {DRAW, CONNECT}
	public ArrayList<PathStateObject> stateList;
	public PathStateObject currentState;
	private Polyline currentLeadingLine;
	private Polyline currentLaggingLine;
	private GoogleMap refMap;
	public Marker currentLeadingMarker, currentLaggingMarker;
	
	public PathState(GoogleMap map) {
		stateList = new ArrayList<PathStateObject>();
		refMap = map;
	}
	
	public void addState(boolean hasLaggingMarker, ArrayList<LatLng> coords, Marker maintainLeading, boolean inDrawMode) {
		if (coords.isEmpty()) {
			return;
		}
		Log.d("state added", "f");
		// Clear current state display
		clearCurrent(maintainLeading);
		ArrayList<LatLng> newLeadLine = new ArrayList<LatLng>();
		
		// Add Lagging Line if present
		if (!stateList.isEmpty()) {
			ArrayList<LatLng> newLagLine = new ArrayList<LatLng>();
			for (PathStateObject previousState : stateList) {
				if (!previousState.hidden) {
					newLagLine.addAll(previousState.mainLine);
				}
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
			if (!inDrawMode) {
				currentLeadingMarker = refMap.addMarker(new MarkerOptions()
				.position(currentState.leadingMarker)
				.icon(BitmapDescriptorFactory.fromResource(com.wisc.cs407project.R.drawable.draggable_marker))
				.draggable(true));
			} else {
				currentLeadingMarker = refMap.addMarker(new MarkerOptions()
				.position(currentState.leadingMarker)
				.icon(BitmapDescriptorFactory.fromResource(com.wisc.cs407project.R.drawable.drawable_marker))
				.draggable(true));
			}
		}
	}
	
	public void revertState(boolean inDrawMode) {
		Log.d("state removed", "f");
		clearCurrent(null);
		// If we're at the initial state
		if (stateList.isEmpty()) {
			return;
		}
		stateList.remove(stateList.size() - 1);
		// If we revert to the initial state
		if (stateList.isEmpty()) {
			currentState = null;
			return;
		}
		// Otherwise, re-add previousState
		PathStateObject previousState = stateList.remove(stateList.size() - 1);
		addState(previousState.hasLaggingMarker, previousState.mainLine, null, inDrawMode);
	}
	
	public void extendState(LatLng coords, Marker maintainLeading, boolean inDrawMode) {
		// If we're at the initial state (we shouldn't be)
		if (stateList.isEmpty()) {
			return;
		}
		clearCurrent(maintainLeading);
		PathStateObject previousState = stateList.remove(stateList.size() - 1);
		ArrayList<LatLng> newMainLine = previousState.mainLine;
		newMainLine.add(coords);
		addState(previousState.hasLaggingMarker, newMainLine, maintainLeading, inDrawMode);
	}
	
	private void clearCurrent(Marker maintainLeading) {
		if (currentLeadingLine != null){
			currentLeadingLine.remove();
			currentLeadingLine = null;
		}
		if (currentLaggingLine != null){
			currentLaggingLine.remove();
			currentLaggingLine = null;
		}
		if (currentLeadingMarker != null && maintainLeading == null){
			currentLeadingMarker.remove();
			currentLeadingMarker = null;
		}
		if (currentLaggingMarker != null){
			currentLaggingMarker.remove();
			currentLaggingMarker = null;
		}
	}
	
	public void prepareForRefactor() {
		if (currentLeadingLine != null) currentLeadingLine.remove();
	}
	
	// Returns true if a refactored route should be fetched
	public boolean refactor(boolean inDrawMode) {
		if (stateList.size() > 1) {
			if (currentLaggingLine != null) currentLaggingLine.remove();
			if (currentLeadingMarker != null) currentLeadingMarker.remove();
			if (currentLaggingMarker != null) currentLaggingMarker.remove();
			makeHidden(stateList.size() - 1);
			currentState = stateList.get(stateList.size() - 2);
			return true;
		} else if (stateList.size() == 1 && currentLaggingMarker != null) {
			ArrayList<LatLng> newLine = new ArrayList<LatLng>();
			newLine.add(currentLaggingMarker.getPosition());
			if (currentLaggingLine != null) currentLaggingLine.remove();
			if (currentLeadingMarker != null) currentLeadingMarker.remove();
			currentLaggingMarker.remove();
			makeHidden(stateList.size() - 1);
			this.addState(false, newLine, null, inDrawMode);
			return true;
		} else {
			if (currentLaggingLine != null) currentLaggingLine.remove();
			if (currentLeadingMarker != null) currentLeadingMarker.remove();
			if (currentLaggingMarker != null) currentLaggingMarker.remove();
			makeHidden(stateList.size() - 1);
			return false;
		}
	}
	
	public void modeChange(MarkerType changeTo) {
		if (currentLeadingMarker != null) {
			currentLeadingMarker.remove();
			if (changeTo == MarkerType.CONNECT) {
				currentLeadingMarker = refMap.addMarker(new MarkerOptions()
				.position(currentState.leadingMarker)
				.icon(BitmapDescriptorFactory.fromResource(com.wisc.cs407project.R.drawable.draggable_marker))
				.draggable(true));
			} else {
				currentLeadingMarker = refMap.addMarker(new MarkerOptions()
				.position(currentState.leadingMarker)
				.icon(BitmapDescriptorFactory.fromResource(com.wisc.cs407project.R.drawable.drawable_marker))
				.draggable(true));
			}
		}
	}
	
	public void makeHidden(int index) {
		if (stateList.size() > index) {
			stateList.get(index).hidden = true;
		}
	}
	
	public void makeUnhidden(int index) {
		if (stateList.size() > index) {
			stateList.get(index).hidden = false;
		}
	}
	
	public String getPath() {
		String result = "";
		if (stateList.isEmpty()) {
			return result;
		}
		LatLng previousAdded = null;
		for(PathStateObject leg : stateList) {
			if (!leg.mainLine.isEmpty() && !leg.hidden) {
				for (LatLng point : leg.mainLine) {
					if(previousAdded == null || (previousAdded != null && !previousAdded.equals(point))) {
						result += "\n"+ point.longitude + ","+ point.latitude +",0";
					}
				}
			}
		}
		return result;
	}
}
