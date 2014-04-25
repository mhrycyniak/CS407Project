package com.wisc.cs407project.ScaleGenUI;

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

import com.wisc.cs407project.ScaleObject;

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
		Document docScale;

		//// The source of the thrown exceptions:
		DocumentBuilder docBuilder = DocumentBuilderFactory.newInstance().newDocumentBuilder();
		docScale = docBuilder.parse(new ByteArrayInputStream(xmlScale.getBytes()));
		//// They shouldn't occur though

		// Load header info
		NodeList scaleInfoList = docScale.getElementsByTagName("scaleInfo");
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
						} else if (nodeName.equals("description")) {
							item.text = child.getTextContent();
						} else if (nodeName.equals("measurement")) {
							item.comparativeValue = convertToLong(child.getTextContent());
						} else if (nodeName.equals("percentage")) {
							item.percentage = convertToDouble(child.getTextContent());
						} else if(nodeName.equals("picture")) {
							item.imageLocation = child.getTextContent();
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
			/* Fill in the percentage if only a comparative value was given. This is more for the sake
			 * of where I'm at with testing at the moment and will probably not be used by the finished app.
			 */
			if (item.percentage == null || item.percentage.equals(0.0)) {
				item.percentage = ((double)item.comparativeValue / maxComparativeValue);
			}
			// Add the item
			members.add(item);
		}
	}

	public void add(ScaleObject... objects) {
		boolean maxUpdate = false;
		for (ScaleObject object : objects) {
			members.add(0, object);
			// Flag a change in the max value
			if (object.comparativeValue > maxComparativeValue) {
				maxComparativeValue = object.comparativeValue;
				maxUpdate = true;
			}
		}
		// If max value changed, update each percentage
		if (maxUpdate) {
			refactorMaxValue(maxComparativeValue);
		}
	}
	
	public void refactorMaxValue(long newMax) {
		maxComparativeValue = newMax;
		for (ScaleObject object : members) {
			object.percentage = ((double)object.comparativeValue / maxComparativeValue);
		}
	}
	
	public void refactorMaxValue() {
		long temp = (long)0;
		// Find the new max
		for (ScaleObject object : members) {
			if (object.comparativeValue > temp) {
				temp = object.comparativeValue;
			}
		}
		refactorMaxValue(temp);
	}
	
	public void addNew() {
		ScaleObject object = new ScaleObject();
		object.percentage = (double)0;
		object.comparativeValue = (long)0;
		add(object);
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
	
	public String getXML() {
		// We want scales stored in sorted order
		sort();
		String result = "<?xml version=\"1.0\" encoding=\"UTF-8\" ?>";
		result = result + "\n<scale>\n";
		result = result + getHeaderXML();
		for (ScaleObject object : members) {
			result = result + getObjectXML(object);
		}
		result = result + "</scale>\n";
		return result;
	}
	
	private String getHeaderXML() {
		String result = "\t<scaleInfo>\n";
		result = result + "\t\t<name>" + scaleName + "</name>\n";
		result = result + "\t\t<units>" + scaleMetric + "</units>\n";
		result = result + "\t\t<max>" + maxComparativeValue.toString() + "</max>\n";
		result = result + "\t</scaleInfo>\n";
		return result;
	}
	
	private String getObjectXML(ScaleObject object) {
		String result = "\n\t<scaleItem>\n";
		result = result + "\t\t<name>" + object.name + "</name>\n";
		result = result + "\t\t<description>" + object.text + "</description>\n";
		result = result + "\t\t<measurement>" + object.comparativeValue.toString() + "</measurement>\n";
		result = result + "\t\t<percentage>" + object.percentage.toString() + "</percentage>\n";
		result = result + "\t\t<picture>" + object.imageLocation + "</picture>\n";
		result = result + "\t</scaleItem>\n";
		return result;
	}

	public static Double convertToDouble(String myString) {

		try {
			return Double.valueOf(myString);
		} catch (NumberFormatException e) {
			return (double)0;
		}
	}
	
	public static Long convertToLong(String myString) {
		try {
			return Long.valueOf(myString);
		} catch (NumberFormatException e) {
			return (long)0;
		}
	}
}
