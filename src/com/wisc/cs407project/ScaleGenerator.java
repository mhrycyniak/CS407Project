package com.wisc.cs407project;

import java.util.ArrayList;
import java.util.Collections;

public class ScaleGenerator {

	private ArrayList<ScaleObject> members; // The scale members
	private Double maxComparativeValue; // Used for updating percentages
	private String scaleMetric;	// What is being compared
	private String scaleName;
	
	public ScaleGenerator() {
		members = new ArrayList<ScaleObject>();
	}
	
	public void loadScale() {
		//TODO
	}
	
	public void add(ScaleObject... objects) {
		boolean maxUpdate = false;
		for (ScaleObject object : objects) {
			members.add(object);
			// Flag a change in the max value
			if (object.comparativeValue > maxComparativeValue) {
				maxComparativeValue = object.comparativeValue;
				maxUpdate = true;
			}
		}
		// If max value changed, update each percentage
		if (maxUpdate) {
			for (ScaleObject object : members) {
				object.percentage = object.comparativeValue / maxComparativeValue;
			}
		}
		// Maintain sorted order
		sort();
	}
	
	public void remove(String... names) {
		for (String name : names) {
			// Check against each member
			for (int i = 0; i < members.size(); i++) {
				// If match, remove and adjust the loop index to avoid skipping the next member
				if (members.get(i).name.equals(name)) {
					members.remove(i);
					i--;
				}
			}
		}
	}
	
	public void sort() {
		Collections.sort(members);
	}
	
}
