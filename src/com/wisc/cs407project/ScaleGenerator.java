package com.wisc.cs407project;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
import org.w3c.dom.Node;
import org.xml.sax.SAXException;

import android.util.Log;

public class ScaleGenerator {

	public ArrayList<ScaleObject> members; // The scale members
	public Long maxComparativeValue; // Used for updating percentages
	public String scaleMetric;	// What is being compared
	public String scaleName;

	public ScaleGenerator() {
		members = new ArrayList<ScaleObject>();
		maxComparativeValue = (long)0;
		scaleMetric = "";
		scaleName = "";
	}

	public void loadScale(String xmlScale) throws IOException, SAXException, ParserConfigurationException {
		//TODO clean and comment it better
		Document docScale;

		Log.d("", "loadScale; pre exceptions");
		//// The source of the thrown exceptions:
		DocumentBuilder docBuilder = DocumentBuilderFactory.newInstance().newDocumentBuilder();
		docScale = docBuilder.parse(new ByteArrayInputStream(xmlScale.getBytes()));
		//// They shouldn't occur though

		// Load header info
		NodeList scaleInfoList = docScale.getElementsByTagName("scaleInfo");
		Log.d("", "got to scaleInfo loading");
		if (scaleInfoList.getLength() > 0) {
			// Only the top (index 0) scaleInfo in an XML is used (there should be only one anyway)
			Element scaleInfo = (Element) scaleInfoList.item(0);
			NodeList child = scaleInfo.getChildNodes();
			
			// Load item info
			if (child != null && child.getLength() > 0) {
				for (int i = 0; i < child.getLength(); i++) {
					if (child.item(i).getNodeType() == Node.ELEMENT_NODE) {
						Element subChild = (Element) child.item(i);
						String nodeName = subChild.getNodeName();
						if (nodeName.equals("name")) {
							scaleName = subChild.getTextContent();
						} else if (nodeName.equals("units")) {
							scaleMetric = subChild.getTextContent();
						} else if (nodeName.equals("max")) {
							maxComparativeValue = convertToLong(subChild.getTextContent());
						}
					}
				}
			}
		}

		// Load scale items
		NodeList scaleItems = docScale.getElementsByTagName("scaleItem");
		for (int i = 0; i < scaleItems.getLength(); i++) {
			Element scaleItem = (Element) scaleItems.item(i);
			ScaleObject item = new ScaleObject();
			NodeList children = scaleItem.getChildNodes();
			
			// Load item info
			if (children != null && children.getLength() > 0) {
				for (int j = 0; j < children.getLength(); j++) {
					if (children.item(j).getNodeType() == Node.ELEMENT_NODE) {
						Element child = (Element) children.item(j);
						String nodeName = child.getNodeName();
						if (nodeName.equals("name")) {
							item.name = child.getTextContent();
							Log.d("", "loading name of a child, name is " + item.name);
						} else if (nodeName.equals("description")) {
							item.text = child.getTextContent();
						} else if (nodeName.equals("measurement")) {
							item.comparativeValue = convertToLong(child.getTextContent());
						} else if (nodeName.equals("percentage")) {
							item.percentage = convertToDouble(child.getTextContent());
						} else if(nodeName.equals("picture")) {
							item.imageLocation = child.getTextContent();
						} else if (nodeName.equals("local")) {
							if (child.getTextContent().equals("1") || child.getTextContent().equals("true")) {
								item.isImageLocal = true;
							}
						}
					}
				}
			}
			/* Fill in the comparative value if only a percentage was given. This is just for older
			 * XML scales, all of which will likely be replaced as development proceeds
			 */
			if (item.comparativeValue == null || item.comparativeValue.equals(0)) {
				item.comparativeValue = Double.doubleToLongBits(item.percentage);
			}
			// Add the item
			members.add(item);
		}
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
				object.percentage = Double.longBitsToDouble(object.comparativeValue / maxComparativeValue);
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

	public Double convertToDouble(String myString) {

		try {
			return Double.valueOf(myString);
		} catch (NumberFormatException e) {
			return (double)0;
		}
	}
	
	public Long convertToLong(String myString) {
		try {
			return Long.valueOf(myString);
		} catch (NumberFormatException e) {
			return (long)0;
		}
	}
}
