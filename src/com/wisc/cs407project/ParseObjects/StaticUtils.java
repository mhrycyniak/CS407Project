package com.wisc.cs407project.ParseObjects;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.List;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.w3c.dom.Document;
import org.w3c.dom.NodeList;

import com.parse.ParseException;
import com.parse.ParseFile;
import com.parse.ParseObject;
import com.parse.SaveCallback;
import com.wisc.cs407project.PathChooser;
import com.wisc.cs407project.Popup;
import com.wisc.cs407project.R;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Environment;
import android.util.Log;


public class StaticUtils {
	
	public static final String SecondLapContent = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>\r\n" + 
			"<kml xmlns=\"http://earth.google.com/kml/2.2\">\r\n" + 
			"<Document>\r\n" + 
			"  <name>Second Lap</name>\r\n" + 
			"  <description><![CDATA[walking\r\n" + 
			"\r\n" + 
			"Created by Google My Tracks on Android.]]></description>\r\n" + 
			"  <Style id=\"style1\">\r\n" + 
			"    <IconStyle>\r\n" + 
			"      <Icon>\r\n" + 
			"        <href>http://maps.google.com/mapfiles/kml/paddle/grn-circle_maps.png</href>\r\n" + 
			"      </Icon>\r\n" + 
			"    </IconStyle>\r\n" + 
			"  </Style>\r\n" + 
			"  <Style id=\"style1\">\r\n" + 
			"    <LineStyle>\r\n" + 
			"      <color>800000FF</color>\r\n" + 
			"      <width>5</width>\r\n" + 
			"    </LineStyle>\r\n" + 
			"  </Style>\r\n" + 
			"  <Style id=\"style1\">\r\n" + 
			"    <IconStyle>\r\n" + 
			"      <Icon>\r\n" + 
			"        <href>http://maps.google.com/mapfiles/kml/paddle/red-circle_maps.png</href>\r\n" + 
			"      </Icon>\r\n" + 
			"    </IconStyle>\r\n" + 
			"  </Style>\r\n" + 
			"  <Placemark>\r\n" + 
			"    <name>Second Lap (Start)</name>\r\n" + 
			"    <Snippet></Snippet>\r\n" + 
			"    <description><![CDATA[]]></description>\r\n" + 
			"    <styleUrl>#style1</styleUrl>\r\n" + 
			"    <ExtendedData>\r\n" + 
			"      <Data name=\"&gt;_androidId\">\r\n" + 
			"        <value>1386804384214.2652</value>\r\n" + 
			"      </Data>\r\n" + 
			"    </ExtendedData>\r\n" + 
			"    <Point>\r\n" + 
			"      <coordinates>-89.375618,43.081455,0.000000</coordinates>\r\n" + 
			"    </Point>\r\n" + 
			"  </Placemark>\r\n" + 
			"  <Placemark>\r\n" + 
			"    <name>Second Lap</name>\r\n" + 
			"    <Snippet></Snippet>\r\n" + 
			"    <description><![CDATA[]]></description>\r\n" + 
			"    <styleUrl>#style1</styleUrl>\r\n" + 
			"    <ExtendedData>\r\n" + 
			"      <Data name=\"&gt;_androidId\">\r\n" + 
			"        <value>1386804384426.1284</value>\r\n" + 
			"      </Data>\r\n" + 
			"    </ExtendedData>\r\n" + 
			"    <LineString>\r\n" + 
			"      <tessellate>1</tessellate>\r\n" + 
			"      <coordinates>\r\n" + 
			"        -89.375618,43.081455,0.000000\r\n" + 
			"        -89.375557,43.081329,0.000000\r\n" + 
			"        -89.375259,43.081127,0.000000\r\n" + 
			"        -89.374680,43.081673,0.000000\r\n" + 
			"        -89.373787,43.082226,0.000000\r\n" + 
			"        -89.373604,43.082371,0.000000\r\n" + 
			"        -89.374107,43.082829,0.000000\r\n" + 
			"        -89.374481,43.083096,0.000000\r\n" + 
			"        -89.375870,43.082062,0.000000\r\n" + 
			"        -89.376251,43.081837,0.000000\r\n" + 
			"        -89.376366,43.081779,0.000000\r\n" + 
			"      </coordinates>\r\n" + 
			"    </LineString>\r\n" + 
			"  </Placemark>\r\n" + 
			"  <Placemark>\r\n" + 
			"    <name>Second Lap (End)</name>\r\n" + 
			"    <Snippet>Created by Google My Tracks on Android.Name: Second LapActivity type: walkingDes</Snippet>\r\n" + 
			"    <description><![CDATA[Created by <a href=\"http://www.google.com/mobile/mytracks\" target=\"_blank\">Google My Tracks</a> on Android.<p>Name: Second Lap<br>Activity type: walking<br>Description: -<br>Total distance: 0.63 km (0.4 mi)<br>Total time: 05:53<br>Moving time: 05:52<br>Average speed: 6.47 km/h (4.0 mi/h)<br>Average moving speed: 6.48 km/h (4.0 mi/h)<br>Max speed: 8.51 km/h (5.3 mi/h)<br>Average pace: 9:17 min/km (14:56 min/mi)<br>Average moving pace: 9:16 min/km (14:55 min/mi)<br>Fastest pace: 7:03 min/km (11:21 min/mi)<br>Max elevation: 248 m (814 ft)<br>Min elevation: 235 m (770 ft)<br>Elevation gain: 8 m (27 ft)<br>Max grade: 9 %<br>Min grade: -12 %<br>Recorded: 12/11/2013 4:27PM<br><img border=\"0\" src=\"http://chart.apis.google.com/chart?&amp;chs=600x350&amp;cht=lxy&amp;chtt=Elevation&amp;chxt=x,y&amp;chxr=0,0,0,0|1,700.0,900.0,25&amp;chco=009A00&amp;chm=B,00AA00,0,0,0&amp;chg=100000,12.5,1,0&amp;chd=e:AAAABfCcCpDgDqEoE4F4GEG6HGIAIMJEJOKGKULNLbMbMoNpN1OsO2PuP5QvQ6R4SDTCTNUJUVVOVaWRWcXbXmYgYpZoZ0awa8b1cCc.dLeGeSfKfVgLgWhOhXiSiejdjlkekqlmlymum7n2oCo.pJqDqNrFrRsMsYtVteuYujvdvrwgwtxhxyyzy.zw0J1H1W2N2q3Q3z4047566C6-7G7.8N9B9S-H-V.L.a....,a6a6fdjTlnm-n9ojpBo3opoSn6ngnKmymdmKl5lnlWlEk0kikRkWkcj-jRijh4hKgff0fXe8emeUeDdzdpdedQdAczcjcTcCb1bobdbSbHa9ayanafaXaPaHZ.Z3ZvZnZfZOY-YxYmYZYOYJYDX-X4XzXuXrXmXjXgXeXbXVXQXIXAW4WwWoWlWiWgWdWgWlWoWwW9XTXrYJYjZBZcZ0aKaia.bfcFcodLdreLe0e0\"><br>]]></description>\r\n" + 
			"    <styleUrl>#style1</styleUrl>\r\n" + 
			"    <ExtendedData>\r\n" + 
			"      <Data name=\"&gt;_androidId\">\r\n" + 
			"        <value>1386804384573.5528</value>\r\n" + 
			"      </Data>\r\n" + 
			"    </ExtendedData>\r\n" + 
			"    <Point>\r\n" + 
			"      <coordinates>-89.375732,43.081429,0.000000</coordinates>\r\n" + 
			"    </Point>\r\n" + 
			"  </Placemark>\r\n" + 
			"</Document>\r\n" + 
			"</kml>\r\n" + 
			"";
	
	public static String Lap = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>\r\n" + 
			"<kml xmlns=\"http://earth.google.com/kml/2.2\">\r\n" + 
			"<Document>\r\n" + 
			"  <name>12/11/2013 10:57AM</name>\r\n" + 
			"  <description><![CDATA[walking\r\n" + 
			"\r\n" + 
			"Created by Google My Tracks on Android.]]></description>\r\n" + 
			"  <Style id=\"style1\">\r\n" + 
			"    <IconStyle>\r\n" + 
			"      <Icon>\r\n" + 
			"        <href>http://maps.google.com/mapfiles/kml/paddle/grn-circle_maps.png</href>\r\n" + 
			"      </Icon>\r\n" + 
			"    </IconStyle>\r\n" + 
			"  </Style>\r\n" + 
			"  <Style id=\"style1\">\r\n" + 
			"    <LineStyle>\r\n" + 
			"      <color>800000FF</color>\r\n" + 
			"      <width>5</width>\r\n" + 
			"    </LineStyle>\r\n" + 
			"  </Style>\r\n" + 
			"  <Style id=\"style1\">\r\n" + 
			"    <IconStyle>\r\n" + 
			"      <Icon>\r\n" + 
			"        <href>http://maps.google.com/mapfiles/kml/paddle/red-circle_maps.png</href>\r\n" + 
			"      </Icon>\r\n" + 
			"    </IconStyle>\r\n" + 
			"  </Style>\r\n" + 
			"  <Placemark>\r\n" + 
			"    <name>12/11/2013 10:57AM (Start)</name>\r\n" + 
			"    <Snippet></Snippet>\r\n" + 
			"    <description><![CDATA[]]></description>\r\n" + 
			"    <styleUrl>#style1</styleUrl>\r\n" + 
			"    <ExtendedData>\r\n" + 
			"      <Data name=\"&gt;_androidId\">\r\n" + 
			"        <value>1386781599373.2794</value>\r\n" + 
			"      </Data>\r\n" + 
			"    </ExtendedData>\r\n" + 
			"    <Point>\r\n" + 
			"      <coordinates>-89.375908,43.081371,0.000000</coordinates>\r\n" + 
			"    </Point>\r\n" + 
			"  </Placemark>\r\n" + 
			"  <Placemark>\r\n" + 
			"    <name>12/11/2013 10:57AM</name>\r\n" + 
			"    <Snippet></Snippet>\r\n" + 
			"    <description><![CDATA[]]></description>\r\n" + 
			"    <styleUrl>#style1</styleUrl>\r\n" + 
			"    <ExtendedData>\r\n" + 
			"      <Data name=\"&gt;_androidId\">\r\n" + 
			"        <value>1386781599636.2114</value>\r\n" + 
			"      </Data>\r\n" + 
			"    </ExtendedData>\r\n" + 
			"    <LineString>\r\n" + 
			"      <tessellate>1</tessellate>\r\n" + 
			"      <coordinates>\r\n" + 
			"        -89.411794,43.077478,0.000000\r\n" + 
			"        -89.412684,43.077474,0.000000\r\n" + 
			"        -89.412705,43.077854,0.000000\r\n" + 
			"        -89.413376,43.077872,0.000000\r\n" + 
			"        -89.413757,43.077877,0.000000\r\n" + 
			"        -89.413768,43.078221,0.000000\r\n" + 
			"        -89.414894,43.078229,0.000000\r\n" + 
			"      </coordinates>\r\n" + 
			"    </LineString>\r\n" + 
			"  </Placemark>\r\n" + 
			"  <Placemark>\r\n" + 
			"    <name>12/11/2013 10:57AM (End)</name>\r\n" + 
			"    <Snippet>Created by Google My Tracks on Android.Name: 12/11/2013 10:57AMActivity type: wa</Snippet>\r\n" + 
			"    <description><![CDATA[Created by <a href=\"http://www.google.com/mobile/mytracks\" target=\"_blank\">Google My Tracks</a> on Android.<p>Name: 12/11/2013 10:57AM<br>Activity type: walking<br>Description: -<br>Total distance: 0.61 km (0.4 mi)<br>Total time: 06:09<br>Moving time: 06:06<br>Average speed: 5.93 km/h (3.7 mi/h)<br>Average moving speed: 5.98 km/h (3.7 mi/h)<br>Max speed: 7.64 km/h (4.7 mi/h)<br>Average pace: 10:07 min/km (16:17 min/mi)<br>Average moving pace: 10:02 min/km (16:09 min/mi)<br>Fastest pace: 7:52 min/km (12:39 min/mi)<br>Max elevation: 253 m (830 ft)<br>Min elevation: 230 m (754 ft)<br>Elevation gain: 25 m (82 ft)<br>Max grade: 23 %<br>Min grade: -3 %<br>Recorded: 12/11/2013 10:57AM<br><img border=\"0\" src=\"http://chart.apis.google.com/chart?&amp;chs=600x350&amp;cht=lxy&amp;chtt=Elevation&amp;chxt=x,y&amp;chxr=0,0,0,0|1,700.0,900.0,25&amp;chco=009A00&amp;chm=B,00AA00,0,0,0&amp;chg=100000,12.5,1,0&amp;chd=e:AAAMAMAMATBCBZCJCnDdDxEtE-F9GKHKHeIWIiJcJnKnKwLsL2M4NCOEOPPQPaQYQiRgRqSjStToTxUwU5V7WHXDXOYJYUZPZaacaqbibwcpc0dwd6e5fFgCgLhJhSiTiejgjskrk2l2mCnEnPoLoXpQpbqYqhrcrmsns1t2uBvAvKwHwSxUxhyXyozqz2011B1y2K283V4Q4g5f5n6o6y718B8-9M-J-S.Q.a.-.-,E3EWEKEFD0EKEuFqGnHmIfJlKrL1M6NuOUO1PTPuQHQdQxRERVSGS5TsUfVXWIWwXNXjXzX-YAX7XuXeXTXNXIXDXAW9W7W9XAXDXFXFXFXFXDW9W7W4W4W4W7W4W4W4W4W4W4W7W9W9W9W9W4WzWtWoWoWoWoWlWlWiWgWdWaWYWaWdWgWiWlWtW1XFXYXwYLY2ZpaqbydDeWfshFiTjjknlqmlndoDoppBpWphph\"><br>]]></description>\r\n" + 
			"    <styleUrl>#style1</styleUrl>\r\n" + 
			"    <ExtendedData>\r\n" + 
			"      <Data name=\"&gt;_androidId\">\r\n" + 
			"        <value>1386781599904.5960</value>\r\n" + 
			"      </Data>\r\n" + 
			"    </ExtendedData>\r\n" + 
			"    <Point>\r\n" + 
			"      <coordinates>-89.411794,43.077478,0.000000</coordinates>\r\n" + 
			"    </Point>\r\n" + 
			"  </Placemark>\r\n" + 
			"</Document>\r\n" + 
			"</kml>\r\n" + 
			"";
	
	public static String route2 = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>\r\n" + 
			"<kml xmlns=\"http://earth.google.com/kml/2.2\">\r\n" + 
			"<Document>\r\n" + 
			"  <name>Walking directions to University Ave</name>\r\n" + 
			"  <description><![CDATA[]]></description>\r\n" + 
			"  <Style id=\"style3\">\r\n" + 
			"    <IconStyle>\r\n" + 
			"      <Icon>\r\n" + 
			"        <href></href>\r\n" + 
			"      </Icon>\r\n" + 
			"    </IconStyle>\r\n" + 
			"  </Style>\r\n" + 
			"  <Style id=\"style1\">\r\n" + 
			"    <LineStyle>\r\n" + 
			"      <color>73FF0000</color>\r\n" + 
			"      <width>5</width>\r\n" + 
			"    </LineStyle>\r\n" + 
			"  </Style>\r\n" + 
			"  <Style id=\"style2\">\r\n" + 
			"    <IconStyle>\r\n" + 
			"      <Icon>\r\n" + 
			"        <href></href>\r\n" + 
			"      </Icon>\r\n" + 
			"    </IconStyle>\r\n" + 
			"  </Style>\r\n" + 
			"  <Placemark>\r\n" + 
			"    <name>From: E Gorham St</name>\r\n" + 
			"    <styleUrl>#style3</styleUrl>\r\n" + 
			"    <Point>\r\n" + 
			"      <coordinates>-89.378761,43.083920,0.000000</coordinates>\r\n" + 
			"    </Point>\r\n" + 
			"  </Placemark>\r\n" + 
			"  <Placemark>\r\n" + 
			"    <name>Walking directions to University Ave</name>\r\n" + 
			"    <styleUrl>#style1</styleUrl>\r\n" + 
			"    <ExtendedData>\r\n" + 
			"      <Data name=\"_SnapToRoads\">\r\n" + 
			"        <value>true</value>\r\n" + 
			"      </Data>\r\n" + 
			"    </ExtendedData>\r\n" + 
			"    <LineString>\r\n" + 
			"      <tessellate>1</tessellate>\r\n" + 
			"      <coordinates>\r\n" + 
			"        <gx:coord>-89.378761 43.083920 0.000000</gx:coord>\r\n" + 
			"        <gx:coord>-89.384003 43.080219 0.000000</gx:coord>\r\n" + 
			"        <gx:coord>-89.384003 43.080219 0.000000</gx:coord>\r\n" + 
			"        <gx:coord>-89.384125 43.076221 0.000000</gx:coord>\r\n" + 
			"        <gx:coord>-89.384125 43.076221 0.000000</gx:coord>\r\n" + 
			"        <gx:coord>-89.384247 43.076183 0.000000</gx:coord>\r\n" + 
			"        <gx:coord>-89.386108 43.074879 0.000000</gx:coord>\r\n" + 
			"        <gx:coord>-89.386208 43.074734 0.000000</gx:coord>\r\n" + 
			"        <gx:coord>-89.386208 43.074734 0.000000</gx:coord>\r\n" + 
			"        <gx:coord>-89.393440 43.074841 0.000000</gx:coord>\r\n" + 
			"        <gx:coord>-89.393440 43.074841 0.000000</gx:coord>\r\n" + 
			"        <gx:coord>-89.395821 43.073193 0.000000</gx:coord>\r\n" + 
			"        <gx:coord>-89.395821 43.073193 0.000000</gx:coord>\r\n" + 
			"        <gx:coord>-89.400558 43.073269 0.000000</gx:coord>\r\n" + 
			"      </coordinates>\r\n" + 
			"    </LineString>\r\n" + 
			"  </Placemark>\r\n" + 
			"  <Placemark>\r\n" + 
			"    <name>To: University Ave</name>\r\n" + 
			"    <styleUrl>#style2</styleUrl>\r\n" + 
			"    <Point>\r\n" + 
			"      <coordinates>-89.400558,43.073269,0.000000</coordinates>\r\n" + 
			"    </Point>\r\n" + 
			"  </Placemark>\r\n" + 
			"</Document>\r\n" + 
			"</kml>\r\n" + 
			"";
	
	public static String samplePath = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>\r\n" + 
			"<kml xmlns=\"http://www.opengis.net/kml/2.2\"\r\n" + 
			"xmlns:gx=\"http://www.google.com/kml/ext/2.2\"\r\n" + 
			"xmlns:atom=\"http://www.w3.org/2005/Atom\">\r\n" + 
			"<Document>\r\n" + 
			"<open>1</open>\r\n" + 
			"<visibility>1</visibility>\r\n" + 
			"<name><![CDATA[11/4/2013 12:13PM]]></name>\r\n" + 
			"<atom:author><atom:name><![CDATA[Created by Google My Tracks on Android.]]></atom:name></atom:author>\r\n" + 
			"<Style id=\"track\">\r\n" + 
			"<LineStyle><color>7f0000ff</color><width>4</width></LineStyle>\r\n" + 
			"<IconStyle>\r\n" + 
			"<scale>1.3</scale>\r\n" + 
			"<Icon><href>http://earth.google.com/images/kml-icons/track-directional/track-0.png</href></Icon>\r\n" + 
			"</IconStyle>\r\n" + 
			"</Style>\r\n" + 
			"<Style id=\"start\"><IconStyle>\r\n" + 
			"<scale>1.3</scale>\r\n" + 
			"<Icon><href>http://maps.google.com/mapfiles/kml/paddle/grn-circle.png</href></Icon>\r\n" + 
			"<hotSpot x=\"32\" y=\"1\" xunits=\"pixels\" yunits=\"pixels\"/>\r\n" + 
			"</IconStyle></Style>\r\n" + 
			"<Style id=\"end\"><IconStyle>\r\n" + 
			"<scale>1.3</scale>\r\n" + 
			"<Icon><href>http://maps.google.com/mapfiles/kml/paddle/red-circle.png</href></Icon>\r\n" + 
			"<hotSpot x=\"32\" y=\"1\" xunits=\"pixels\" yunits=\"pixels\"/>\r\n" + 
			"</IconStyle></Style>\r\n" + 
			"<Style id=\"statistics\"><IconStyle>\r\n" + 
			"<scale>1.3</scale>\r\n" + 
			"<Icon><href>http://maps.google.com/mapfiles/kml/pushpin/ylw-pushpin.png</href></Icon>\r\n" + 
			"<hotSpot x=\"20\" y=\"2\" xunits=\"pixels\" yunits=\"pixels\"/>\r\n" + 
			"</IconStyle></Style>\r\n" + 
			"<Style id=\"waypoint\"><IconStyle>\r\n" + 
			"<scale>1.3</scale>\r\n" + 
			"<Icon><href>http://maps.google.com/mapfiles/kml/pushpin/blue-pushpin.png</href></Icon>\r\n" + 
			"<hotSpot x=\"20\" y=\"2\" xunits=\"pixels\" yunits=\"pixels\"/>\r\n" + 
			"</IconStyle></Style>\r\n" + 
			"<Schema id=\"schema\">\r\n" + 
			"<gx:SimpleArrayField name=\"power\" type=\"int\">\r\n" + 
			"<displayName><![CDATA[Power (W)]]></displayName>\r\n" + 
			"</gx:SimpleArrayField>\r\n" + 
			"<gx:SimpleArrayField name=\"cadence\" type=\"int\">\r\n" + 
			"<displayName><![CDATA[Cadence (rpm)]]></displayName>\r\n" + 
			"</gx:SimpleArrayField>\r\n" + 
			"<gx:SimpleArrayField name=\"heart_rate\" type=\"int\">\r\n" + 
			"<displayName><![CDATA[Heart rate (bpm)]]></displayName>\r\n" + 
			"</gx:SimpleArrayField>\r\n" + 
			"</Schema>\r\n" + 
			"<Placemark>\r\n" + 
			"<name><![CDATA[11/4/2013 12:13PM (Start)]]></name>\r\n" + 
			"<description><![CDATA[]]></description>\r\n" + 
			"<TimeStamp><when>2013-11-04T18:13:30.998Z</when></TimeStamp>\r\n" + 
			"<styleUrl>#start</styleUrl>\r\n" + 
			"<Point>\r\n" + 
			"<coordinates>-89.397546,43.073842,288.0</coordinates>\r\n" + 
			"</Point>\r\n" + 
			"</Placemark>\r\n" + 
			"<Placemark id=\"tour\">\r\n" + 
			"<name><![CDATA[11/4/2013 12:13PM]]></name>\r\n" + 
			"<description><![CDATA[]]></description>\r\n" + 
			"<styleUrl>#track</styleUrl>\r\n" + 
			"<ExtendedData>\r\n" + 
			"<Data name=\"type\"><value><![CDATA[walking]]></value></Data>\r\n" + 
			"</ExtendedData>\r\n" + 
			"<gx:MultiTrack>\r\n" + 
			"<altitudeMode>absolute</altitudeMode>\r\n" + 
			"<gx:interpolate>1</gx:interpolate>\r\n" + 
			"<gx:Track>\r\n" + 
			"<when>2013-11-04T18:13:30.998Z</when>\r\n" + 
			"<gx:coord>-89.397546 43.073842 288.0</gx:coord>\r\n" + 
			"<when>2013-11-04T18:13:31.998Z</when>\r\n" + 
			"<gx:coord>-89.397539 43.073762 284.0</gx:coord>\r\n" + 
			"<when>2013-11-04T18:13:32.999Z</when>\r\n" + 
			"<gx:coord>-89.397529 43.073674 279.0</gx:coord>\r\n" + 
			"<when>2013-11-04T18:13:34.004Z</when>\r\n" + 
			"<gx:coord>-89.397517 43.073612 279.0</gx:coord>\r\n" + 
			"<when>2013-11-04T18:13:34.991Z</when>\r\n" + 
			"<gx:coord>-89.397502 43.073569 279.0</gx:coord>\r\n" + 
			"<when>2013-11-04T18:13:36.991Z</when>\r\n" + 
			"<gx:coord>-89.397468 43.073491 273.0</gx:coord>\r\n" + 
			"<when>2013-11-04T18:13:37.999Z</when>\r\n" + 
			"<gx:coord>-89.397446 43.073451 272.0</gx:coord>\r\n" + 
			"<when>2013-11-04T18:13:40.999Z</when>\r\n" + 
			"<gx:coord>-89.39739 43.073379 268.0</gx:coord>\r\n" + 
			"<when>2013-11-04T18:13:42.003Z</when>\r\n" + 
			"<gx:coord>-89.397371 43.073358 265.0</gx:coord>\r\n" + 
			"<when>2013-11-04T18:13:51.022Z</when>\r\n" + 
			"<gx:coord>-89.397294 43.073318 260.0</gx:coord>\r\n" + 
			"<when>2013-11-04T18:13:52.013Z</when>\r\n" + 
			"<gx:coord>-89.397297 43.073319 260.0</gx:coord>\r\n" + 
			"<when>2013-11-04T18:14:04.022Z</when>\r\n" + 
			"<gx:coord>-89.397314 43.073319 258.0</gx:coord>\r\n" + 
			"<when>2013-11-04T18:14:05.020Z</when>\r\n" + 
			"<gx:coord>-89.397319 43.073324 259.0</gx:coord>\r\n" + 
			"<when>2013-11-04T18:14:15.947Z</when>\r\n" + 
			"<gx:coord>-89.397332 43.073411 260.0</gx:coord>\r\n" + 
			"<when>2013-11-04T18:14:16.008Z</when>\r\n" + 
			"<gx:coord>-89.397333 43.073417 260.0</gx:coord>\r\n" + 
			"<when>2013-11-04T18:14:29.006Z</when>\r\n" + 
			"<gx:coord>-89.397376 43.073495 259.0</gx:coord>\r\n" + 
			"<when>2013-11-04T18:14:30.007Z</when>\r\n" + 
			"<gx:coord>-89.397377 43.073511 259.0</gx:coord>\r\n" + 
			"<when>2013-11-04T18:14:36.156Z</when>\r\n" + 
			"<gx:coord>-89.39741 43.073593 258.0</gx:coord>\r\n" + 
			"<when>2013-11-04T18:14:37.026Z</when>\r\n" + 
			"<gx:coord>-89.397418 43.073606 258.0</gx:coord>\r\n" + 
			"<when>2013-11-04T18:14:42.011Z</when>\r\n" + 
			"<gx:coord>-89.39747 43.073668 257.0</gx:coord>\r\n" + 
			"<when>2013-11-04T18:14:43.015Z</when>\r\n" + 
			"<gx:coord>-89.397474 43.073689 260.0</gx:coord>\r\n" + 
			"<when>2013-11-04T18:14:49.011Z</when>\r\n" + 
			"<gx:coord>-89.397514 43.073769 266.0</gx:coord>\r\n" + 
			"<when>2013-11-04T18:14:50.018Z</when>\r\n" + 
			"<gx:coord>-89.397526 43.073779 267.0</gx:coord>\r\n" + 
			"<when>2013-11-04T18:14:56.017Z</when>\r\n" + 
			"<gx:coord>-89.397598 43.073836 271.0</gx:coord>\r\n" + 
			"<when>2013-11-04T18:14:57.018Z</when>\r\n" + 
			"<gx:coord>-89.39761 43.073847 272.0</gx:coord>\r\n" + 
			"<when>2013-11-04T18:15:03.002Z</when>\r\n" + 
			"<gx:coord>-89.397642 43.073919 272.0</gx:coord>\r\n" + 
			"<when>2013-11-04T18:15:04.005Z</when>\r\n" + 
			"<gx:coord>-89.397653 43.073931 272.0</gx:coord>\r\n" + 
			"<when>2013-11-04T18:15:10.886Z</when>\r\n" + 
			"<gx:coord>-89.397707 43.073999 272.0</gx:coord>\r\n" + 
			"<when>2013-11-04T18:15:11.017Z</when>\r\n" + 
			"<gx:coord>-89.397711 43.074013 272.0</gx:coord>\r\n" + 
			"<when>2013-11-04T18:15:15.997Z</when>\r\n" + 
			"<gx:coord>-89.397736 43.07409 271.0</gx:coord>\r\n" + 
			"<when>2013-11-04T18:15:17.001Z</when>\r\n" + 
			"<gx:coord>-89.397739 43.074104 271.0</gx:coord>\r\n" + 
			"<when>2013-11-04T18:15:21.995Z</when>\r\n" + 
			"<gx:coord>-89.397738 43.074178 270.0</gx:coord>\r\n" + 
			"<when>2013-11-04T18:15:22.996Z</when>\r\n" + 
			"<gx:coord>-89.397738 43.074197 270.0</gx:coord>\r\n" + 
			"<when>2013-11-04T18:15:29.008Z</when>\r\n" + 
			"<gx:coord>-89.397763 43.074279 269.0</gx:coord>\r\n" + 
			"<when>2013-11-04T18:15:30.008Z</when>\r\n" + 
			"<gx:coord>-89.397764 43.074294 268.0</gx:coord>\r\n" + 
			"<when>2013-11-04T18:15:33.018Z</when>\r\n" + 
			"<gx:coord>-89.397737 43.074357 267.0</gx:coord>\r\n" + 
			"<when>2013-11-04T18:15:34.014Z</when>\r\n" + 
			"<gx:coord>-89.397725 43.074379 267.0</gx:coord>\r\n" + 
			"<when>2013-11-04T18:15:37.014Z</when>\r\n" + 
			"<gx:coord>-89.397679 43.074442 266.0</gx:coord>\r\n" + 
			"<when>2013-11-04T18:15:38.015Z</when>\r\n" + 
			"<gx:coord>-89.397666 43.074461 266.0</gx:coord>\r\n" + 
			"<when>2013-11-04T18:15:42.018Z</when>\r\n" + 
			"<gx:coord>-89.397619 43.07453 266.0</gx:coord>\r\n" + 
			"<when>2013-11-04T18:15:43.019Z</when>\r\n" + 
			"<gx:coord>-89.397607 43.074545 266.0</gx:coord>\r\n" + 
			"<when>2013-11-04T18:15:49.025Z</when>\r\n" + 
			"<gx:coord>-89.397577 43.07463 264.0</gx:coord>\r\n" + 
			"<when>2013-11-04T18:15:50.028Z</when>\r\n" + 
			"<gx:coord>-89.397576 43.074641 264.0</gx:coord>\r\n" + 
			"<when>2013-11-04T18:15:57.017Z</when>\r\n" + 
			"<gx:coord>-89.39758 43.074729 264.0</gx:coord>\r\n" + 
			"<when>2013-11-04T18:15:58.015Z</when>\r\n" + 
			"<gx:coord>-89.397576 43.074742 264.0</gx:coord>\r\n" + 
			"<when>2013-11-04T18:16:02.024Z</when>\r\n" + 
			"<gx:coord>-89.397516 43.074805 264.0</gx:coord>\r\n" + 
			"<when>2013-11-04T18:16:03.032Z</when>\r\n" + 
			"<gx:coord>-89.397478 43.074824 264.0</gx:coord>\r\n" + 
			"<when>2013-11-04T18:16:06.013Z</when>\r\n" + 
			"<gx:coord>-89.397384 43.074877 263.0</gx:coord>\r\n" + 
			"<when>2013-11-04T18:16:07.009Z</when>\r\n" + 
			"<gx:coord>-89.397361 43.074893 263.0</gx:coord>\r\n" + 
			"<when>2013-11-04T18:16:16.010Z</when>\r\n" + 
			"<gx:coord>-89.397248 43.074923 266.0</gx:coord>\r\n" + 
			"<when>2013-11-04T18:16:17.018Z</when>\r\n" + 
			"<gx:coord>-89.397237 43.074921 266.0</gx:coord>\r\n" + 
			"<when>2013-11-04T18:16:22.015Z</when>\r\n" + 
			"<gx:coord>-89.397166 43.074982 270.0</gx:coord>\r\n" + 
			"<when>2013-11-04T18:16:23.019Z</when>\r\n" + 
			"<gx:coord>-89.39715 43.074995 271.0</gx:coord>\r\n" + 
			"<when>2013-11-04T18:16:30.947Z</when>\r\n" + 
			"<gx:coord>-89.397031 43.075005 273.0</gx:coord>\r\n" + 
			"<when>2013-11-04T18:16:30.987Z</when>\r\n" + 
			"<gx:coord>-89.397015 43.075002 273.0</gx:coord>\r\n" + 
			"<when>2013-11-04T18:16:35.984Z</when>\r\n" + 
			"<gx:coord>-89.396926 43.075012 275.0</gx:coord>\r\n" + 
			"<when>2013-11-04T18:16:36.009Z</when>\r\n" + 
			"<gx:coord>-89.39689 43.075011 276.0</gx:coord>\r\n" + 
			"<when>2013-11-04T18:16:38.991Z</when>\r\n" + 
			"<gx:coord>-89.3968 43.075009 278.0</gx:coord>\r\n" + 
			"<ExtendedData>\r\n" + 
			"<SchemaData schemaUrl=\"#schema\">\r\n" + 
			"</SchemaData>\r\n" + 
			"</ExtendedData>\r\n" + 
			"</gx:Track>\r\n" + 
			"</gx:MultiTrack>\r\n" + 
			"</Placemark>\r\n" + 
			"<Placemark>\r\n" + 
			"<name><![CDATA[11/4/2013 12:13PM (End)]]></name>\r\n" + 
			"<description><![CDATA[Created by Google My Tracks on Android.\r\n" + 
			"\r\n" + 
			"Name: 11/4/2013 12:13PM\r\n" + 
			"Activity type: walking\r\n" + 
			"Description: -\r\n" + 
			"Total distance: 0.31 km (0.2 mi)\r\n" + 
			"Total time: 03:08\r\n" + 
			"Moving time: 02:54\r\n" + 
			"Average speed: 5.92 km/h (3.7 mi/h)\r\n" + 
			"Average moving speed: 6.39 km/h (4.0 mi/h)\r\n" + 
			"Max speed: 7.01 km/h (4.4 mi/h)\r\n" + 
			"Average pace: 10:08 min/km (16:19 min/mi)\r\n" + 
			"Average moving pace: 9:24 min/km (15:07 min/mi)\r\n" + 
			"Fastest pace: 8:34 min/km (13:47 min/mi)\r\n" + 
			"Max elevation: 268 m (879 ft)\r\n" + 
			"Min elevation: 265 m (868 ft)\r\n" + 
			"Elevation gain: 4 m (14 ft)\r\n" + 
			"Max grade: 6 %\r\n" + 
			"Min grade: -4 %\r\n" + 
			"Recorded: 11/4/2013 12:13PM\r\n" + 
			"]]></description>\r\n" + 
			"<TimeStamp><when>2013-11-04T18:16:38.991Z</when></TimeStamp>\r\n" + 
			"<styleUrl>#end</styleUrl>\r\n" + 
			"<Point>\r\n" + 
			"<coordinates>-89.3968,43.075009,278.0</coordinates>\r\n" + 
			"</Point>\r\n" + 
			"</Placemark>\r\n" + 
			"</Document>\r\n" + 
			"</kml>\r\n" + 
			"";
	
	public static String straightPath = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>\r\n" + 
			"<kml xmlns=\"http://www.opengis.net/kml/2.2\"\r\n" + 
			"xmlns:gx=\"http://www.google.com/kml/ext/2.2\"\r\n" + 
			"xmlns:atom=\"http://www.w3.org/2005/Atom\">\r\n" + 
			"<Document>\r\n" + 
			"<open>1</open>\r\n" + 
			"<visibility>1</visibility>\r\n" + 
			"<name><![CDATA[11/9/2013 8:58AM]]></name>\r\n" + 
			"<atom:author><atom:name><![CDATA[Created by Google My Tracks on Android.]]></atom:name></atom:author>\r\n" + 
			"<Style id=\"track\">\r\n" + 
			"<LineStyle><color>7f0000ff</color><width>4</width></LineStyle>\r\n" + 
			"<IconStyle>\r\n" + 
			"<scale>1.3</scale>\r\n" + 
			"<Icon><href>http://earth.google.com/images/kml-icons/track-directional/track-0.png</href></Icon>\r\n" + 
			"</IconStyle>\r\n" + 
			"</Style>\r\n" + 
			"<Style id=\"start\"><IconStyle>\r\n" + 
			"<scale>1.3</scale>\r\n" + 
			"<Icon><href>http://maps.google.com/mapfiles/kml/paddle/grn-circle.png</href></Icon>\r\n" + 
			"<hotSpot x=\"32\" y=\"1\" xunits=\"pixels\" yunits=\"pixels\"/>\r\n" + 
			"</IconStyle></Style>\r\n" + 
			"<Style id=\"end\"><IconStyle>\r\n" + 
			"<scale>1.3</scale>\r\n" + 
			"<Icon><href>http://maps.google.com/mapfiles/kml/paddle/red-circle.png</href></Icon>\r\n" + 
			"<hotSpot x=\"32\" y=\"1\" xunits=\"pixels\" yunits=\"pixels\"/>\r\n" + 
			"</IconStyle></Style>\r\n" + 
			"<Style id=\"statistics\"><IconStyle>\r\n" + 
			"<scale>1.3</scale>\r\n" + 
			"<Icon><href>http://maps.google.com/mapfiles/kml/pushpin/ylw-pushpin.png</href></Icon>\r\n" + 
			"<hotSpot x=\"20\" y=\"2\" xunits=\"pixels\" yunits=\"pixels\"/>\r\n" + 
			"</IconStyle></Style>\r\n" + 
			"<Style id=\"waypoint\"><IconStyle>\r\n" + 
			"<scale>1.3</scale>\r\n" + 
			"<Icon><href>http://maps.google.com/mapfiles/kml/pushpin/blue-pushpin.png</href></Icon>\r\n" + 
			"<hotSpot x=\"20\" y=\"2\" xunits=\"pixels\" yunits=\"pixels\"/>\r\n" + 
			"</IconStyle></Style>\r\n" + 
			"<Schema id=\"schema\">\r\n" + 
			"<gx:SimpleArrayField name=\"power\" type=\"int\">\r\n" + 
			"<displayName><![CDATA[Power (W)]]></displayName>\r\n" + 
			"</gx:SimpleArrayField>\r\n" + 
			"<gx:SimpleArrayField name=\"cadence\" type=\"int\">\r\n" + 
			"<displayName><![CDATA[Cadence (rpm)]]></displayName>\r\n" + 
			"</gx:SimpleArrayField>\r\n" + 
			"<gx:SimpleArrayField name=\"heart_rate\" type=\"int\">\r\n" + 
			"<displayName><![CDATA[Heart rate (bpm)]]></displayName>\r\n" + 
			"</gx:SimpleArrayField>\r\n" + 
			"</Schema>\r\n" + 
			"<Placemark>\r\n" + 
			"<name><![CDATA[11/9/2013 8:58AM (Start)]]></name>\r\n" + 
			"<description><![CDATA[]]></description>\r\n" + 
			"<TimeStamp><when>2013-11-09T14:59:20.434Z</when></TimeStamp>\r\n" + 
			"<styleUrl>#start</styleUrl>\r\n" + 
			"<Point>\r\n" + 
			"<coordinates>-89.37578,43.08129,232.0</coordinates>\r\n" + 
			"</Point>\r\n" + 
			"</Placemark>\r\n" + 
			"<Placemark id=\"tour\">\r\n" + 
			"<name><![CDATA[11/9/2013 8:58AM]]></name>\r\n" + 
			"<description><![CDATA[]]></description>\r\n" + 
			"<styleUrl>#track</styleUrl>\r\n" + 
			"<ExtendedData>\r\n" + 
			"<Data name=\"type\"><value><![CDATA[biking]]></value></Data>\r\n" + 
			"</ExtendedData>\r\n" + 
			"<gx:MultiTrack>\r\n" + 
			"<altitudeMode>absolute</altitudeMode>\r\n" + 
			"<gx:interpolate>1</gx:interpolate>\r\n" + 
			"<gx:Track>\r\n" + 
			"<when>2013-11-09T14:59:20.434Z</when>\r\n" + 
			"<gx:coord>-89.37578 43.08129 232.0</gx:coord>\r\n" + 
			"<when>2013-11-09T14:59:34.004Z</when>\r\n" + 
			"<gx:coord>-89.375741 43.081335 235.0</gx:coord>\r\n" + 
			"<when>2013-11-09T14:59:35.002Z</when>\r\n" + 
			"<gx:coord>-89.37574 43.081334 236.0</gx:coord>\r\n" + 
			"<when>2013-11-09T14:59:40.179Z</when>\r\n" + 
			"<gx:coord>-89.375748 43.081328 236.0</gx:coord>\r\n" + 
			"<when>2013-11-09T14:59:41.004Z</when>\r\n" + 
			"<gx:coord>-89.375762 43.081331 236.0</gx:coord>\r\n" + 
			"<when>2013-11-09T14:59:44.020Z</when>\r\n" + 
			"<gx:coord>-89.375844 43.08135 236.0</gx:coord>\r\n" + 
			"<when>2013-11-09T14:59:45.017Z</when>\r\n" + 
			"<gx:coord>-89.375879 43.081364 236.0</gx:coord>\r\n" + 
			"<when>2013-11-09T14:59:47.902Z</when>\r\n" + 
			"<gx:coord>-89.375962 43.081409 237.0</gx:coord>\r\n" + 
			"<when>2013-11-09T14:59:48.021Z</when>\r\n" + 
			"<gx:coord>-89.376004 43.081442 237.0</gx:coord>\r\n" + 
			"<when>2013-11-09T14:59:49.014Z</when>\r\n" + 
			"<gx:coord>-89.376046 43.081485 237.0</gx:coord>\r\n" + 
			"<when>2013-11-09T14:59:50.029Z</when>\r\n" + 
			"<gx:coord>-89.376088 43.081541 237.0</gx:coord>\r\n" + 
			"<when>2013-11-09T14:59:51.626Z</when>\r\n" + 
			"<gx:coord>-89.376141 43.081611 237.0</gx:coord>\r\n" + 
			"<when>2013-11-09T14:59:52.024Z</when>\r\n" + 
			"<gx:coord>-89.376167 43.081646 237.0</gx:coord>\r\n" + 
			"<when>2013-11-09T14:59:52.613Z</when>\r\n" + 
			"<gx:coord>-89.376167 43.081646 237.0</gx:coord>\r\n" + 
			"<when>2013-11-09T14:59:53.635Z</when>\r\n" + 
			"<gx:coord>-89.376229 43.081725 237.0</gx:coord>\r\n" + 
			"<when>2013-11-09T14:59:54.621Z</when>\r\n" + 
			"<gx:coord>-89.376264 43.081777 237.0</gx:coord>\r\n" + 
			"<when>2013-11-09T14:59:55.039Z</when>\r\n" + 
			"<gx:coord>-89.376282 43.081817 237.0</gx:coord>\r\n" + 
			"<when>2013-11-09T14:59:56.123Z</when>\r\n" + 
			"<gx:coord>-89.376307 43.081895 238.0</gx:coord>\r\n" + 
			"<when>2013-11-09T14:59:57.039Z</when>\r\n" + 
			"<gx:coord>-89.376307 43.081966 238.0</gx:coord>\r\n" + 
			"<when>2013-11-09T14:59:58.035Z</when>\r\n" + 
			"<gx:coord>-89.37629 43.082045 238.0</gx:coord>\r\n" + 
			"<when>2013-11-09T14:59:59.649Z</when>\r\n" + 
			"<gx:coord>-89.376245 43.08215 238.0</gx:coord>\r\n" + 
			"<when>2013-11-09T15:00:00.628Z</when>\r\n" + 
			"<gx:coord>-89.376183 43.082221 238.0</gx:coord>\r\n" + 
			"<when>2013-11-09T15:00:01.214Z</when>\r\n" + 
			"<gx:coord>-89.376112 43.082279 238.0</gx:coord>\r\n" + 
			"<when>2013-11-09T15:00:02.048Z</when>\r\n" + 
			"<gx:coord>-89.376014 43.082357 238.0</gx:coord>\r\n" + 
			"<when>2013-11-09T15:00:02.713Z</when>\r\n" + 
			"<gx:coord>-89.376014 43.082357 238.0</gx:coord>\r\n" + 
			"<when>2013-11-09T15:00:03.048Z</when>\r\n" + 
			"<gx:coord>-89.375889 43.082436 238.0</gx:coord>\r\n" + 
			"<when>2013-11-09T15:00:04.645Z</when>\r\n" + 
			"<gx:coord>-89.375736 43.082528 238.0</gx:coord>\r\n" + 
			"<when>2013-11-09T15:00:05.627Z</when>\r\n" + 
			"<gx:coord>-89.375605 43.082577 239.0</gx:coord>\r\n" + 
			"<when>2013-11-09T15:00:06.047Z</when>\r\n" + 
			"<gx:coord>-89.375515 43.082608 239.0</gx:coord>\r\n" + 
			"<when>2013-11-09T15:00:07.146Z</when>\r\n" + 
			"<gx:coord>-89.375351 43.082668 239.0</gx:coord>\r\n" + 
			"<when>2013-11-09T15:00:07.739Z</when>\r\n" + 
			"<gx:coord>-89.375351 43.082668 239.0</gx:coord>\r\n" + 
			"<when>2013-11-09T15:00:08.047Z</when>\r\n" + 
			"<gx:coord>-89.375242 43.082722 240.0</gx:coord>\r\n" + 
			"<when>2013-11-09T15:00:09.046Z</when>\r\n" + 
			"<gx:coord>-89.37514 43.082778 240.0</gx:coord>\r\n" + 
			"<when>2013-11-09T15:00:10.045Z</when>\r\n" + 
			"<gx:coord>-89.375056 43.082833 241.0</gx:coord>\r\n" + 
			"<when>2013-11-09T15:00:11.049Z</when>\r\n" + 
			"<gx:coord>-89.374981 43.082887 241.0</gx:coord>\r\n" + 
			"<when>2013-11-09T15:00:12.764Z</when>\r\n" + 
			"<gx:coord>-89.374909 43.082938 241.0</gx:coord>\r\n" + 
			"<when>2013-11-09T15:00:13.046Z</when>\r\n" + 
			"<gx:coord>-89.374837 43.082984 241.0</gx:coord>\r\n" + 
			"<when>2013-11-09T15:00:14.049Z</when>\r\n" + 
			"<gx:coord>-89.374767 43.083029 241.0</gx:coord>\r\n" + 
			"<when>2013-11-09T15:00:15.041Z</when>\r\n" + 
			"<gx:coord>-89.374698 43.083074 242.0</gx:coord>\r\n" + 
			"<when>2013-11-09T15:00:16.046Z</when>\r\n" + 
			"<gx:coord>-89.374629 43.08312 242.0</gx:coord>\r\n" + 
			"<when>2013-11-09T15:00:17.148Z</when>\r\n" + 
			"<gx:coord>-89.374558 43.083166 242.0</gx:coord>\r\n" + 
			"<when>2013-11-09T15:00:18.050Z</when>\r\n" + 
			"<gx:coord>-89.374498 43.083208 242.0</gx:coord>\r\n" + 
			"<when>2013-11-09T15:00:19.051Z</when>\r\n" + 
			"<gx:coord>-89.374428 43.083255 242.0</gx:coord>\r\n" + 
			"<when>2013-11-09T15:00:20.048Z</when>\r\n" + 
			"<gx:coord>-89.37436 43.083303 243.0</gx:coord>\r\n" + 
			"<when>2013-11-09T15:00:21.048Z</when>\r\n" + 
			"<gx:coord>-89.374295 43.083351 243.0</gx:coord>\r\n" + 
			"<when>2013-11-09T15:00:22.792Z</when>\r\n" + 
			"<gx:coord>-89.374224 43.083402 243.0</gx:coord>\r\n" + 
			"<when>2013-11-09T15:00:23.649Z</when>\r\n" + 
			"<gx:coord>-89.374145 43.083458 243.0</gx:coord>\r\n" + 
			"<when>2013-11-09T15:00:24.626Z</when>\r\n" + 
			"<gx:coord>-89.374087 43.083499 243.0</gx:coord>\r\n" + 
			"<when>2013-11-09T15:00:25.827Z</when>\r\n" + 
			"<gx:coord>-89.374018 43.083552 243.0</gx:coord>\r\n" + 
			"<when>2013-11-09T15:00:26.044Z</when>\r\n" + 
			"<gx:coord>-89.373981 43.083581 244.0</gx:coord>\r\n" + 
			"<when>2013-11-09T15:00:27.143Z</when>\r\n" + 
			"<gx:coord>-89.373894 43.083649 244.0</gx:coord>\r\n" + 
			"<when>2013-11-09T15:00:27.815Z</when>\r\n" + 
			"<gx:coord>-89.373894 43.083649 244.0</gx:coord>\r\n" + 
			"<when>2013-11-09T15:00:28.645Z</when>\r\n" + 
			"<gx:coord>-89.373808 43.083717 244.0</gx:coord>\r\n" + 
			"<when>2013-11-09T15:00:29.625Z</when>\r\n" + 
			"<gx:coord>-89.373744 43.083762 244.0</gx:coord>\r\n" + 
			"<when>2013-11-09T15:00:30.843Z</when>\r\n" + 
			"<gx:coord>-89.373668 43.083813 244.0</gx:coord>\r\n" + 
			"<when>2013-11-09T15:00:31.040Z</when>\r\n" + 
			"<gx:coord>-89.373629 43.083837 244.0</gx:coord>\r\n" + 
			"<when>2013-11-09T15:00:32.050Z</when>\r\n" + 
			"<gx:coord>-89.37354 43.083898 244.0</gx:coord>\r\n" + 
			"<when>2013-11-09T15:00:33.052Z</when>\r\n" + 
			"<gx:coord>-89.373463 43.083952 244.0</gx:coord>\r\n" + 
			"<when>2013-11-09T15:00:34.049Z</when>\r\n" + 
			"<gx:coord>-89.373395 43.083998 243.0</gx:coord>\r\n" + 
			"<when>2013-11-09T15:00:35.050Z</when>\r\n" + 
			"<gx:coord>-89.373332 43.084039 244.0</gx:coord>\r\n" + 
			"<when>2013-11-09T15:00:36.047Z</when>\r\n" + 
			"<gx:coord>-89.37327 43.08408 244.0</gx:coord>\r\n" + 
			"<when>2013-11-09T15:00:37.845Z</when>\r\n" + 
			"<gx:coord>-89.37321 43.084122 244.0</gx:coord>\r\n" + 
			"<when>2013-11-09T15:00:38.047Z</when>\r\n" + 
			"<gx:coord>-89.373147 43.084165 244.0</gx:coord>\r\n" + 
			"<when>2013-11-09T15:00:39.050Z</when>\r\n" + 
			"<gx:coord>-89.373086 43.084206 243.0</gx:coord>\r\n" + 
			"<when>2013-11-09T15:00:40.046Z</when>\r\n" + 
			"<gx:coord>-89.373026 43.084246 243.0</gx:coord>\r\n" + 
			"<when>2013-11-09T15:00:41.049Z</when>\r\n" + 
			"<gx:coord>-89.372964 43.084287 243.0</gx:coord>\r\n" + 
			"<when>2013-11-09T15:00:42.050Z</when>\r\n" + 
			"<gx:coord>-89.3729 43.084328 243.0</gx:coord>\r\n" + 
			"<when>2013-11-09T15:00:43.047Z</when>\r\n" + 
			"<gx:coord>-89.372832 43.084368 243.0</gx:coord>\r\n" + 
			"<when>2013-11-09T15:00:44.647Z</when>\r\n" + 
			"<gx:coord>-89.372759 43.084417 243.0</gx:coord>\r\n" + 
			"<when>2013-11-09T15:00:46.044Z</when>\r\n" + 
			"<gx:coord>-89.37268 43.084472 244.0</gx:coord>\r\n" + 
			"<when>2013-11-09T15:00:47.149Z</when>\r\n" + 
			"<gx:coord>-89.372604 43.084525 243.0</gx:coord>\r\n" + 
			"<when>2013-11-09T15:00:48.148Z</when>\r\n" + 
			"<gx:coord>-89.372535 43.084571 244.0</gx:coord>\r\n" + 
			"<when>2013-11-09T15:00:49.644Z</when>\r\n" + 
			"<gx:coord>-89.372452 43.084626 244.0</gx:coord>\r\n" + 
			"<when>2013-11-09T15:00:50.627Z</when>\r\n" + 
			"<gx:coord>-89.372389 43.084665 244.0</gx:coord>\r\n" + 
			"<when>2013-11-09T15:00:51.217Z</when>\r\n" + 
			"<gx:coord>-89.372334 43.084702 245.0</gx:coord>\r\n" + 
			"<when>2013-11-09T15:00:52.946Z</when>\r\n" + 
			"<gx:coord>-89.372258 43.084752 245.0</gx:coord>\r\n" + 
			"<when>2013-11-09T15:00:53.152Z</when>\r\n" + 
			"<gx:coord>-89.372172 43.084807 246.0</gx:coord>\r\n" + 
			"<when>2013-11-09T15:00:54.645Z</when>\r\n" + 
			"<gx:coord>-89.372087 43.084861 246.0</gx:coord>\r\n" + 
			"<when>2013-11-09T15:00:55.626Z</when>\r\n" + 
			"<gx:coord>-89.372029 43.084903 246.0</gx:coord>\r\n" + 
			"<when>2013-11-09T15:00:56.204Z</when>\r\n" + 
			"<gx:coord>-89.371984 43.084937 246.0</gx:coord>\r\n" + 
			"<when>2013-11-09T15:00:57.047Z</when>\r\n" + 
			"<gx:coord>-89.371904 43.084999 246.0</gx:coord>\r\n" + 
			"<when>2013-11-09T15:00:58.044Z</when>\r\n" + 
			"<gx:coord>-89.371831 43.085055 246.0</gx:coord>\r\n" + 
			"<when>2013-11-09T15:00:59.050Z</when>\r\n" + 
			"<gx:coord>-89.371766 43.085106 247.0</gx:coord>\r\n" + 
			"<when>2013-11-09T15:01:00.047Z</when>\r\n" + 
			"<gx:coord>-89.371702 43.08515 247.0</gx:coord>\r\n" + 
			"<when>2013-11-09T15:01:01.038Z</when>\r\n" + 
			"<gx:coord>-89.371637 43.085194 247.0</gx:coord>\r\n" + 
			"<when>2013-11-09T15:01:02.037Z</when>\r\n" + 
			"<gx:coord>-89.371573 43.08524 247.0</gx:coord>\r\n" + 
			"<when>2013-11-09T15:01:03.049Z</when>\r\n" + 
			"<gx:coord>-89.371508 43.085288 247.0</gx:coord>\r\n" + 
			"<when>2013-11-09T15:01:04.046Z</when>\r\n" + 
			"<gx:coord>-89.371443 43.085336 247.0</gx:coord>\r\n" + 
			"<when>2013-11-09T15:01:05.051Z</when>\r\n" + 
			"<gx:coord>-89.371376 43.085384 247.0</gx:coord>\r\n" + 
			"<when>2013-11-09T15:01:07.224Z</when>\r\n" + 
			"<gx:coord>-89.371294 43.085443 247.0</gx:coord>\r\n" + 
			"<when>2013-11-09T15:01:07.644Z</when>\r\n" + 
			"<gx:coord>-89.371258 43.085467 247.0</gx:coord>\r\n" + 
			"<when>2013-11-09T15:01:08.627Z</when>\r\n" + 
			"<gx:coord>-89.371185 43.085517 247.0</gx:coord>\r\n" + 
			"<when>2013-11-09T15:01:09.918Z</when>\r\n" + 
			"<gx:coord>-89.371102 43.085573 247.0</gx:coord>\r\n" + 
			"<when>2013-11-09T15:01:11.123Z</when>\r\n" + 
			"<gx:coord>-89.371066 43.085596 247.0</gx:coord>\r\n" + 
			"<when>2013-11-09T15:01:11.149Z</when>\r\n" + 
			"<gx:coord>-89.370971 43.085661 247.0</gx:coord>\r\n" + 
			"<when>2013-11-09T15:01:12.645Z</when>\r\n" + 
			"<gx:coord>-89.370885 43.085723 247.0</gx:coord>\r\n" + 
			"<when>2013-11-09T15:01:13.040Z</when>\r\n" + 
			"<gx:coord>-89.370846 43.085752 246.0</gx:coord>\r\n" + 
			"<when>2013-11-09T15:01:14.646Z</when>\r\n" + 
			"<gx:coord>-89.37076 43.085816 246.0</gx:coord>\r\n" + 
			"<when>2013-11-09T15:01:15.247Z</when>\r\n" + 
			"<gx:coord>-89.370715 43.08585 246.0</gx:coord>\r\n" + 
			"<when>2013-11-09T15:01:16.130Z</when>\r\n" + 
			"<gx:coord>-89.37065 43.085897 246.0</gx:coord>\r\n" + 
			"<when>2013-11-09T15:01:17.047Z</when>\r\n" + 
			"<gx:coord>-89.370575 43.08595 246.0</gx:coord>\r\n" + 
			"<when>2013-11-09T15:01:18.050Z</when>\r\n" + 
			"<gx:coord>-89.370502 43.085999 245.0</gx:coord>\r\n" + 
			"<when>2013-11-09T15:01:19.046Z</when>\r\n" + 
			"<gx:coord>-89.37043 43.086044 245.0</gx:coord>\r\n" + 
			"<when>2013-11-09T15:01:20.043Z</when>\r\n" + 
			"<gx:coord>-89.370361 43.086088 245.0</gx:coord>\r\n" + 
			"<when>2013-11-09T15:01:21.145Z</when>\r\n" + 
			"<gx:coord>-89.370284 43.086136 246.0</gx:coord>\r\n" + 
			"<when>2013-11-09T15:01:22.046Z</when>\r\n" + 
			"<gx:coord>-89.370213 43.086177 245.0</gx:coord>\r\n" + 
			"<when>2013-11-09T15:01:23.646Z</when>\r\n" + 
			"<gx:coord>-89.370121 43.086234 246.0</gx:coord>\r\n" + 
			"<when>2013-11-09T15:01:24.046Z</when>\r\n" + 
			"<gx:coord>-89.370077 43.086261 246.0</gx:coord>\r\n" + 
			"<when>2013-11-09T15:01:25.047Z</when>\r\n" + 
			"<gx:coord>-89.369996 43.086311 246.0</gx:coord>\r\n" + 
			"<when>2013-11-09T15:01:26.148Z</when>\r\n" + 
			"<gx:coord>-89.369924 43.086359 246.0</gx:coord>\r\n" + 
			"<when>2013-11-09T15:01:27.047Z</when>\r\n" + 
			"<gx:coord>-89.369857 43.086407 246.0</gx:coord>\r\n" + 
			"<when>2013-11-09T15:01:28.046Z</when>\r\n" + 
			"<gx:coord>-89.369793 43.086456 246.0</gx:coord>\r\n" + 
			"<when>2013-11-09T15:01:29.047Z</when>\r\n" + 
			"<gx:coord>-89.369729 43.086505 246.0</gx:coord>\r\n" + 
			"<when>2013-11-09T15:01:30.049Z</when>\r\n" + 
			"<gx:coord>-89.369668 43.086556 246.0</gx:coord>\r\n" + 
			"<when>2013-11-09T15:01:31.045Z</when>\r\n" + 
			"<gx:coord>-89.369604 43.086607 245.0</gx:coord>\r\n" + 
			"<when>2013-11-09T15:01:32.055Z</when>\r\n" + 
			"<gx:coord>-89.369538 43.086658 246.0</gx:coord>\r\n" + 
			"<when>2013-11-09T15:01:33.057Z</when>\r\n" + 
			"<gx:coord>-89.369473 43.086709 245.0</gx:coord>\r\n" + 
			"<when>2013-11-09T15:01:34.084Z</when>\r\n" + 
			"<gx:coord>-89.369406 43.086759 245.0</gx:coord>\r\n" + 
			"<when>2013-11-09T15:01:35.056Z</when>\r\n" + 
			"<gx:coord>-89.369339 43.086804 246.0</gx:coord>\r\n" + 
			"<when>2013-11-09T15:01:36.189Z</when>\r\n" + 
			"<gx:coord>-89.369265 43.086851 246.0</gx:coord>\r\n" + 
			"<when>2013-11-09T15:01:37.757Z</when>\r\n" + 
			"<gx:coord>-89.369187 43.086906 246.0</gx:coord>\r\n" + 
			"<when>2013-11-09T15:01:38.050Z</when>\r\n" + 
			"<gx:coord>-89.369152 43.086931 246.0</gx:coord>\r\n" + 
			"<when>2013-11-09T15:01:39.738Z</when>\r\n" + 
			"<gx:coord>-89.369057 43.087 246.0</gx:coord>\r\n" + 
			"<when>2013-11-09T15:01:40.200Z</when>\r\n" + 
			"<gx:coord>-89.369009 43.087032 246.0</gx:coord>\r\n" + 
			"<when>2013-11-09T15:01:41.058Z</when>\r\n" + 
			"<gx:coord>-89.368935 43.087083 245.0</gx:coord>\r\n" + 
			"<when>2013-11-09T15:01:41.208Z</when>\r\n" + 
			"<gx:coord>-89.368935 43.087083 245.0</gx:coord>\r\n" + 
			"<when>2013-11-09T15:01:42.755Z</when>\r\n" + 
			"<gx:coord>-89.368843 43.087147 246.0</gx:coord>\r\n" + 
			"<when>2013-11-09T15:01:43.058Z</when>\r\n" + 
			"<gx:coord>-89.368807 43.087172 246.0</gx:coord>\r\n" + 
			"<when>2013-11-09T15:01:44.755Z</when>\r\n" + 
			"<gx:coord>-89.368708 43.087237 246.0</gx:coord>\r\n" + 
			"<when>2013-11-09T15:01:45.146Z</when>\r\n" + 
			"<gx:coord>-89.368662 43.087267 246.0</gx:coord>\r\n" + 
			"<when>2013-11-09T15:01:46.051Z</when>\r\n" + 
			"<gx:coord>-89.368584 43.087323 246.0</gx:coord>\r\n" + 
			"<when>2013-11-09T15:01:47.059Z</when>\r\n" + 
			"<gx:coord>-89.368509 43.087377 246.0</gx:coord>\r\n" + 
			"<when>2013-11-09T15:01:48.057Z</when>\r\n" + 
			"<gx:coord>-89.368441 43.087428 246.0</gx:coord>\r\n" + 
			"<when>2013-11-09T15:01:49.056Z</when>\r\n" + 
			"<gx:coord>-89.368375 43.087475 246.0</gx:coord>\r\n" + 
			"<when>2013-11-09T15:01:50.057Z</when>\r\n" + 
			"<gx:coord>-89.368313 43.087519 247.0</gx:coord>\r\n" + 
			"<when>2013-11-09T15:01:51.257Z</when>\r\n" + 
			"<gx:coord>-89.368244 43.087566 247.0</gx:coord>\r\n" + 
			"<when>2013-11-09T15:01:52.054Z</when>\r\n" + 
			"<gx:coord>-89.368187 43.087605 247.0</gx:coord>\r\n" + 
			"<when>2013-11-09T15:01:53.062Z</when>\r\n" + 
			"<gx:coord>-89.36812 43.087653 247.0</gx:coord>\r\n" + 
			"<when>2013-11-09T15:01:54.056Z</when>\r\n" + 
			"<gx:coord>-89.368052 43.087698 247.0</gx:coord>\r\n" + 
			"<when>2013-11-09T15:01:55.058Z</when>\r\n" + 
			"<gx:coord>-89.367984 43.087739 248.0</gx:coord>\r\n" + 
			"<when>2013-11-09T15:01:56.058Z</when>\r\n" + 
			"<gx:coord>-89.367918 43.087778 248.0</gx:coord>\r\n" + 
			"<when>2013-11-09T15:01:57.059Z</when>\r\n" + 
			"<gx:coord>-89.367857 43.08782 248.0</gx:coord>\r\n" + 
			"<when>2013-11-09T15:01:58.057Z</when>\r\n" + 
			"<gx:coord>-89.367799 43.087862 248.0</gx:coord>\r\n" + 
			"<when>2013-11-09T15:01:59.782Z</when>\r\n" + 
			"<gx:coord>-89.367743 43.087905 248.0</gx:coord>\r\n" + 
			"<when>2013-11-09T15:02:00.054Z</when>\r\n" + 
			"<gx:coord>-89.367689 43.087947 247.0</gx:coord>\r\n" + 
			"<when>2013-11-09T15:02:01.058Z</when>\r\n" + 
			"<gx:coord>-89.367634 43.087988 247.0</gx:coord>\r\n" + 
			"<when>2013-11-09T15:02:02.756Z</when>\r\n" + 
			"<gx:coord>-89.367567 43.088033 247.0</gx:coord>\r\n" + 
			"<when>2013-11-09T15:02:04.875Z</when>\r\n" + 
			"<gx:coord>-89.367473 43.088089 247.0</gx:coord>\r\n" + 
			"<when>2013-11-09T15:02:05.170Z</when>\r\n" + 
			"<gx:coord>-89.367411 43.088123 248.0</gx:coord>\r\n" + 
			"<when>2013-11-09T15:02:08.018Z</when>\r\n" + 
			"<gx:coord>-89.367317 43.088168 248.0</gx:coord>\r\n" + 
			"<when>2013-11-09T15:02:08.128Z</when>\r\n" + 
			"<gx:coord>-89.367296 43.088183 248.0</gx:coord>\r\n" + 
			"<when>2013-11-09T15:02:09.890Z</when>\r\n" + 
			"<gx:coord>-89.367241 43.088223 248.0</gx:coord>\r\n" + 
			"<when>2013-11-09T15:02:10.157Z</when>\r\n" + 
			"<gx:coord>-89.367184 43.088273 248.0</gx:coord>\r\n" + 
			"<when>2013-11-09T15:02:11.758Z</when>\r\n" + 
			"<gx:coord>-89.367122 43.088326 248.0</gx:coord>\r\n" + 
			"<when>2013-11-09T15:02:12.738Z</when>\r\n" + 
			"<gx:coord>-89.367077 43.088364 248.0</gx:coord>\r\n" + 
			"<when>2013-11-09T15:02:13.185Z</when>\r\n" + 
			"<gx:coord>-89.367039 43.088394 248.0</gx:coord>\r\n" + 
			"<when>2013-11-09T15:02:14.050Z</when>\r\n" + 
			"<gx:coord>-89.366975 43.088442 248.0</gx:coord>\r\n" + 
			"<when>2013-11-09T15:02:15.047Z</when>\r\n" + 
			"<gx:coord>-89.366901 43.088495 248.0</gx:coord>\r\n" + 
			"<when>2013-11-09T15:02:16.045Z</when>\r\n" + 
			"<gx:coord>-89.366831 43.088545 248.0</gx:coord>\r\n" + 
			"<when>2013-11-09T15:02:17.049Z</when>\r\n" + 
			"<gx:coord>-89.366766 43.088593 247.0</gx:coord>\r\n" + 
			"<when>2013-11-09T15:02:18.046Z</when>\r\n" + 
			"<gx:coord>-89.366704 43.088639 248.0</gx:coord>\r\n" + 
			"<when>2013-11-09T15:02:19.929Z</when>\r\n" + 
			"<gx:coord>-89.366642 43.088684 248.0</gx:coord>\r\n" + 
			"<when>2013-11-09T15:02:20.040Z</when>\r\n" + 
			"<gx:coord>-89.366581 43.088731 248.0</gx:coord>\r\n" + 
			"<when>2013-11-09T15:02:21.043Z</when>\r\n" + 
			"<gx:coord>-89.366518 43.088777 248.0</gx:coord>\r\n" + 
			"<when>2013-11-09T15:02:22.045Z</when>\r\n" + 
			"<gx:coord>-89.366453 43.088821 249.0</gx:coord>\r\n" + 
			"<when>2013-11-09T15:02:23.059Z</when>\r\n" + 
			"<gx:coord>-89.366387 43.088865 249.0</gx:coord>\r\n" + 
			"<when>2013-11-09T15:02:24.163Z</when>\r\n" + 
			"<gx:coord>-89.366318 43.08891 249.0</gx:coord>\r\n" + 
			"<when>2013-11-09T15:02:25.167Z</when>\r\n" + 
			"<gx:coord>-89.366255 43.088953 249.0</gx:coord>\r\n" + 
			"<when>2013-11-09T15:02:27.031Z</when>\r\n" + 
			"<gx:coord>-89.366178 43.089009 249.0</gx:coord>\r\n" + 
			"<when>2013-11-09T15:02:27.067Z</when>\r\n" + 
			"<gx:coord>-89.366156 43.089024 249.0</gx:coord>\r\n" + 
			"<when>2013-11-09T15:02:28.760Z</when>\r\n" + 
			"<gx:coord>-89.366057 43.08909 249.0</gx:coord>\r\n" + 
			"<when>2013-11-09T15:02:29.943Z</when>\r\n" + 
			"<gx:coord>-89.366008 43.08912 250.0</gx:coord>\r\n" + 
			"<when>2013-11-09T15:02:30.279Z</when>\r\n" + 
			"<gx:coord>-89.365917 43.089177 250.0</gx:coord>\r\n" + 
			"<when>2013-11-09T15:02:31.057Z</when>\r\n" + 
			"<gx:coord>-89.365851 43.089222 250.0</gx:coord>\r\n" + 
			"<when>2013-11-09T15:02:32.755Z</when>\r\n" + 
			"<gx:coord>-89.365763 43.089283 250.0</gx:coord>\r\n" + 
			"<when>2013-11-09T15:02:33.242Z</when>\r\n" + 
			"<gx:coord>-89.365723 43.089311 250.0</gx:coord>\r\n" + 
			"<when>2013-11-09T15:02:34.055Z</when>\r\n" + 
			"<gx:coord>-89.365659 43.089355 250.0</gx:coord>\r\n" + 
			"<when>2013-11-09T15:02:35.148Z</when>\r\n" + 
			"<gx:coord>-89.365583 43.089406 249.0</gx:coord>\r\n" + 
			"<when>2013-11-09T15:02:36.058Z</when>\r\n" + 
			"<gx:coord>-89.365517 43.089446 249.0</gx:coord>\r\n" + 
			"<when>2013-11-09T15:02:37.056Z</when>\r\n" + 
			"<gx:coord>-89.365444 43.089488 249.0</gx:coord>\r\n" + 
			"<when>2013-11-09T15:02:38.058Z</when>\r\n" + 
			"<gx:coord>-89.365373 43.089529 249.0</gx:coord>\r\n" + 
			"<when>2013-11-09T15:02:39.999Z</when>\r\n" + 
			"<gx:coord>-89.365307 43.089571 249.0</gx:coord>\r\n" + 
			"<when>2013-11-09T15:02:40.048Z</when>\r\n" + 
			"<gx:coord>-89.365246 43.089617 249.0</gx:coord>\r\n" + 
			"<when>2013-11-09T15:02:41.057Z</when>\r\n" + 
			"<gx:coord>-89.365186 43.089664 249.0</gx:coord>\r\n" + 
			"<when>2013-11-09T15:02:42.058Z</when>\r\n" + 
			"<gx:coord>-89.365125 43.08971 249.0</gx:coord>\r\n" + 
			"<when>2013-11-09T15:02:43.056Z</when>\r\n" + 
			"<gx:coord>-89.365064 43.089758 249.0</gx:coord>\r\n" + 
			"<when>2013-11-09T15:02:44.056Z</when>\r\n" + 
			"<gx:coord>-89.365002 43.089806 249.0</gx:coord>\r\n" + 
			"<when>2013-11-09T15:02:45.196Z</when>\r\n" + 
			"<gx:coord>-89.364936 43.089859 250.0</gx:coord>\r\n" + 
			"<when>2013-11-09T15:02:46.754Z</when>\r\n" + 
			"<gx:coord>-89.364869 43.089918 250.0</gx:coord>\r\n" + 
			"<when>2013-11-09T15:02:47.736Z</when>\r\n" + 
			"<gx:coord>-89.36482 43.089963 250.0</gx:coord>\r\n" + 
			"<when>2013-11-09T15:02:48.246Z</when>\r\n" + 
			"<gx:coord>-89.364773 43.090001 250.0</gx:coord>\r\n" + 
			"<when>2013-11-09T15:02:50.125Z</when>\r\n" + 
			"<gx:coord>-89.364703 43.090056 251.0</gx:coord>\r\n" + 
			"<when>2013-11-09T15:02:50.161Z</when>\r\n" + 
			"<gx:coord>-89.36462 43.090119 250.0</gx:coord>\r\n" + 
			"<when>2013-11-09T15:02:51.837Z</when>\r\n" + 
			"<gx:coord>-89.364536 43.090182 250.0</gx:coord>\r\n" + 
			"<when>2013-11-09T15:02:52.064Z</when>\r\n" + 
			"<gx:coord>-89.364506 43.090205 251.0</gx:coord>\r\n" + 
			"<when>2013-11-09T15:02:53.764Z</when>\r\n" + 
			"<gx:coord>-89.364416 43.090279 251.0</gx:coord>\r\n" + 
			"<when>2013-11-09T15:02:54.062Z</when>\r\n" + 
			"<gx:coord>-89.364378 43.090307 251.0</gx:coord>\r\n" + 
			"<when>2013-11-09T15:02:55.063Z</when>\r\n" + 
			"<gx:coord>-89.364297 43.090367 251.0</gx:coord>\r\n" + 
			"<when>2013-11-09T15:02:56.062Z</when>\r\n" + 
			"<gx:coord>-89.36422 43.090418 251.0</gx:coord>\r\n" + 
			"<when>2013-11-09T15:02:57.064Z</when>\r\n" + 
			"<gx:coord>-89.364146 43.090464 251.0</gx:coord>\r\n" + 
			"<when>2013-11-09T15:02:58.066Z</when>\r\n" + 
			"<gx:coord>-89.364073 43.090506 251.0</gx:coord>\r\n" + 
			"<when>2013-11-09T15:02:59.180Z</when>\r\n" + 
			"<gx:coord>-89.363996 43.090548 251.0</gx:coord>\r\n" + 
			"<when>2013-11-09T15:03:00.192Z</when>\r\n" + 
			"<gx:coord>-89.363926 43.090584 251.0</gx:coord>\r\n" + 
			"<when>2013-11-09T15:03:01.062Z</when>\r\n" + 
			"<gx:coord>-89.363862 43.090621 251.0</gx:coord>\r\n" + 
			"<when>2013-11-09T15:03:02.062Z</when>\r\n" + 
			"<gx:coord>-89.363799 43.090667 251.0</gx:coord>\r\n" + 
			"<when>2013-11-09T15:03:03.064Z</when>\r\n" + 
			"<gx:coord>-89.363752 43.090719 251.0</gx:coord>\r\n" + 
			"<when>2013-11-09T15:03:04.407Z</when>\r\n" + 
			"<gx:coord>-89.363725 43.090773 251.0</gx:coord>\r\n" + 
			"<when>2013-11-09T15:03:05.048Z</when>\r\n" + 
			"<gx:coord>-89.363713 43.090831 251.0</gx:coord>\r\n" + 
			"<when>2013-11-09T15:03:06.049Z</when>\r\n" + 
			"<gx:coord>-89.363707 43.090886 251.0</gx:coord>\r\n" + 
			"<when>2013-11-09T15:03:07.047Z</when>\r\n" + 
			"<gx:coord>-89.363703 43.090937 251.0</gx:coord>\r\n" + 
			"<when>2013-11-09T15:03:11.049Z</when>\r\n" + 
			"<gx:coord>-89.363612 43.090989 251.0</gx:coord>\r\n" + 
			"<when>2013-11-09T15:03:12.651Z</when>\r\n" + 
			"<gx:coord>-89.363547 43.090953 251.0</gx:coord>\r\n" + 
			"<when>2013-11-09T15:03:13.048Z</when>\r\n" + 
			"<gx:coord>-89.363512 43.090924 250.0</gx:coord>\r\n" + 
			"<when>2013-11-09T15:03:14.148Z</when>\r\n" + 
			"<gx:coord>-89.363451 43.090875 250.0</gx:coord>\r\n" + 
			"<when>2013-11-09T15:03:15.048Z</when>\r\n" + 
			"<gx:coord>-89.3634 43.090829 250.0</gx:coord>\r\n" + 
			"<when>2013-11-09T15:03:16.046Z</when>\r\n" + 
			"<gx:coord>-89.363344 43.090781 250.0</gx:coord>\r\n" + 
			"<when>2013-11-09T15:03:17.049Z</when>\r\n" + 
			"<gx:coord>-89.363282 43.090734 249.0</gx:coord>\r\n" + 
			"<when>2013-11-09T15:03:18.048Z</when>\r\n" + 
			"<gx:coord>-89.363214 43.090688 249.0</gx:coord>\r\n" + 
			"<when>2013-11-09T15:03:19.202Z</when>\r\n" + 
			"<gx:coord>-89.363139 43.090644 248.0</gx:coord>\r\n" + 
			"<when>2013-11-09T15:03:20.645Z</when>\r\n" + 
			"<gx:coord>-89.363045 43.090592 248.0</gx:coord>\r\n" + 
			"<when>2013-11-09T15:03:21.823Z</when>\r\n" + 
			"<gx:coord>-89.36297 43.090552 248.0</gx:coord>\r\n" + 
			"<when>2013-11-09T15:03:22.044Z</when>\r\n" + 
			"<gx:coord>-89.362922 43.090531 248.0</gx:coord>\r\n" + 
			"<when>2013-11-09T15:03:23.148Z</when>\r\n" + 
			"<gx:coord>-89.362808 43.090482 248.0</gx:coord>\r\n" + 
			"<when>2013-11-09T15:03:24.204Z</when>\r\n" + 
			"<gx:coord>-89.362701 43.090445 248.0</gx:coord>\r\n" + 
			"<when>2013-11-09T15:03:25.650Z</when>\r\n" + 
			"<gx:coord>-89.362583 43.090404 247.0</gx:coord>\r\n" + 
			"<when>2013-11-09T15:03:27.048Z</when>\r\n" + 
			"<gx:coord>-89.362486 43.090366 247.0</gx:coord>\r\n" + 
			"<when>2013-11-09T15:03:28.116Z</when>\r\n" + 
			"<gx:coord>-89.362403 43.090333 247.0</gx:coord>\r\n" + 
			"<when>2013-11-09T15:03:29.224Z</when>\r\n" + 
			"<gx:coord>-89.362403 43.090333 247.0</gx:coord>\r\n" + 
			"<when>2013-11-09T15:03:32.996Z</when>\r\n" + 
			"<gx:coord>-89.362025 43.090172 247.0</gx:coord>\r\n" + 
			"<when>2013-11-09T15:03:34.011Z</when>\r\n" + 
			"<gx:coord>-89.36197 43.089947 247.0</gx:coord>\r\n" + 
			"<when>2013-11-09T15:03:35.014Z</when>\r\n" + 
			"<gx:coord>-89.361915 43.089875 246.0</gx:coord>\r\n" + 
			"<when>2013-11-09T15:03:36.036Z</when>\r\n" + 
			"<gx:coord>-89.36185 43.089814 246.0</gx:coord>\r\n" + 
			"<when>2013-11-09T15:03:37.036Z</when>\r\n" + 
			"<gx:coord>-89.361795 43.089753 246.0</gx:coord>\r\n" + 
			"<when>2013-11-09T15:03:38.124Z</when>\r\n" + 
			"<gx:coord>-89.361768 43.089692 246.0</gx:coord>\r\n" + 
			"<when>2013-11-09T15:03:39.383Z</when>\r\n" + 
			"<gx:coord>-89.361728 43.089635 246.0</gx:coord>\r\n" + 
			"<when>2013-11-09T15:03:40.643Z</when>\r\n" + 
			"<gx:coord>-89.361676 43.089566 246.0</gx:coord>\r\n" + 
			"<when>2013-11-09T15:03:41.626Z</when>\r\n" + 
			"<gx:coord>-89.361624 43.089518 246.0</gx:coord>\r\n" + 
			"<when>2013-11-09T15:03:42.842Z</when>\r\n" + 
			"<gx:coord>-89.361557 43.089468 247.0</gx:coord>\r\n" + 
			"<when>2013-11-09T15:03:43.039Z</when>\r\n" + 
			"<gx:coord>-89.361518 43.089449 247.0</gx:coord>\r\n" + 
			"<when>2013-11-09T15:03:44.147Z</when>\r\n" + 
			"<gx:coord>-89.361428 43.089396 245.0</gx:coord>\r\n" + 
			"<when>2013-11-09T15:03:45.645Z</when>\r\n" + 
			"<gx:coord>-89.36134 43.089352 245.0</gx:coord>\r\n" + 
			"<when>2013-11-09T15:03:46.931Z</when>\r\n" + 
			"<gx:coord>-89.361283 43.08932 245.0</gx:coord>\r\n" + 
			"<when>2013-11-09T15:03:47.043Z</when>\r\n" + 
			"<gx:coord>-89.36126 43.089303 245.0</gx:coord>\r\n" + 
			"<when>2013-11-09T15:03:48.151Z</when>\r\n" + 
			"<gx:coord>-89.36118 43.089253 245.0</gx:coord>\r\n" + 
			"<when>2013-11-09T15:03:49.414Z</when>\r\n" + 
			"<gx:coord>-89.36111 43.08921 245.0</gx:coord>\r\n" + 
			"<when>2013-11-09T15:03:50.644Z</when>\r\n" + 
			"<gx:coord>-89.361029 43.089166 246.0</gx:coord>\r\n" + 
			"<when>2013-11-09T15:03:51.048Z</when>\r\n" + 
			"<gx:coord>-89.360985 43.089147 247.0</gx:coord>\r\n" + 
			"<when>2013-11-09T15:03:52.046Z</when>\r\n" + 
			"<gx:coord>-89.360901 43.089109 247.0</gx:coord>\r\n" + 
			"<when>2013-11-09T15:03:53.049Z</when>\r\n" + 
			"<gx:coord>-89.360823 43.089069 248.0</gx:coord>\r\n" + 
			"<when>2013-11-09T15:03:54.050Z</when>\r\n" + 
			"<gx:coord>-89.360754 43.089027 249.0</gx:coord>\r\n" + 
			"<when>2013-11-09T15:03:55.046Z</when>\r\n" + 
			"<gx:coord>-89.36069 43.088987 249.0</gx:coord>\r\n" + 
			"<when>2013-11-09T15:03:56.049Z</when>\r\n" + 
			"<gx:coord>-89.360631 43.088949 249.0</gx:coord>\r\n" + 
			"<when>2013-11-09T15:03:57.050Z</when>\r\n" + 
			"<gx:coord>-89.360577 43.088913 249.0</gx:coord>\r\n" + 
			"<when>2013-11-09T15:03:58.046Z</when>\r\n" + 
			"<gx:coord>-89.360532 43.08888 249.0</gx:coord>\r\n" + 
			"<when>2013-11-09T15:04:00.045Z</when>\r\n" + 
			"<gx:coord>-89.360448 43.088824 249.0</gx:coord>\r\n" + 
			"<when>2013-11-09T15:04:01.641Z</when>\r\n" + 
			"<gx:coord>-89.360397 43.088798 249.0</gx:coord>\r\n" + 
			"<when>2013-11-09T15:04:03.047Z</when>\r\n" + 
			"<gx:coord>-89.36032 43.088762 249.0</gx:coord>\r\n" + 
			"<when>2013-11-09T15:04:04.044Z</when>\r\n" + 
			"<gx:coord>-89.360269 43.088738 248.0</gx:coord>\r\n" + 
			"<when>2013-11-09T15:04:05.046Z</when>\r\n" + 
			"<gx:coord>-89.360217 43.088715 248.0</gx:coord>\r\n" + 
			"<when>2013-11-09T15:04:06.048Z</when>\r\n" + 
			"<gx:coord>-89.360164 43.08869 248.0</gx:coord>\r\n" + 
			"<when>2013-11-09T15:04:07.645Z</when>\r\n" + 
			"<gx:coord>-89.360095 43.088657 247.0</gx:coord>\r\n" + 
			"<when>2013-11-09T15:04:08.203Z</when>\r\n" + 
			"<gx:coord>-89.360052 43.088635 247.0</gx:coord>\r\n" + 
			"<when>2013-11-09T15:04:09.478Z</when>\r\n" + 
			"<gx:coord>-89.359987 43.088604 247.0</gx:coord>\r\n" + 
			"<when>2013-11-09T15:04:10.045Z</when>\r\n" + 
			"<gx:coord>-89.359911 43.08857 247.0</gx:coord>\r\n" + 
			"<when>2013-11-09T15:04:11.761Z</when>\r\n" + 
			"<gx:coord>-89.359819 43.08853 247.0</gx:coord>\r\n" + 
			"<when>2013-11-09T15:04:12.737Z</when>\r\n" + 
			"<gx:coord>-89.359758 43.0885 247.0</gx:coord>\r\n" + 
			"<when>2013-11-09T15:04:14.249Z</when>\r\n" + 
			"<gx:coord>-89.359718 43.088477 247.0</gx:coord>\r\n" + 
			"<when>2013-11-09T15:04:14.759Z</when>\r\n" + 
			"<gx:coord>-89.359626 43.088424 247.0</gx:coord>\r\n" + 
			"<when>2013-11-09T15:04:16.055Z</when>\r\n" + 
			"<gx:coord>-89.359538 43.088367 246.0</gx:coord>\r\n" + 
			"<when>2013-11-09T15:04:17.060Z</when>\r\n" + 
			"<gx:coord>-89.359489 43.088335 246.0</gx:coord>\r\n" + 
			"<when>2013-11-09T15:04:19.072Z</when>\r\n" + 
			"<gx:coord>-89.359408 43.088275 246.0</gx:coord>\r\n" + 
			"<when>2013-11-09T15:04:20.180Z</when>\r\n" + 
			"<gx:coord>-89.359366 43.088246 246.0</gx:coord>\r\n" + 
			"<when>2013-11-09T15:04:22.737Z</when>\r\n" + 
			"<gx:coord>-89.359266 43.088205 246.0</gx:coord>\r\n" + 
			"<when>2013-11-09T15:04:23.062Z</when>\r\n" + 
			"<gx:coord>-89.359228 43.088197 246.0</gx:coord>\r\n" + 
			"<when>2013-11-09T15:04:24.063Z</when>\r\n" + 
			"<gx:coord>-89.359157 43.088181 246.0</gx:coord>\r\n" + 
			"<when>2013-11-09T15:04:25.063Z</when>\r\n" + 
			"<gx:coord>-89.359093 43.088166 246.0</gx:coord>\r\n" + 
			"<when>2013-11-09T15:04:29.065Z</when>\r\n" + 
			"<gx:coord>-89.359061 43.088087 246.0</gx:coord>\r\n" + 
			"<when>2013-11-09T15:04:30.063Z</when>\r\n" + 
			"<gx:coord>-89.359093 43.088069 246.0</gx:coord>\r\n" + 
			"<when>2013-11-09T15:04:34.059Z</when>\r\n" + 
			"<gx:coord>-89.359122 43.088059 246.0</gx:coord>\r\n" + 
			"<when>2013-11-09T15:04:35.179Z</when>\r\n" + 
			"<gx:coord>-89.359116 43.08806 246.0</gx:coord>\r\n" + 
			"<when>2013-11-09T15:04:35.261Z</when>\r\n" + 
			"<gx:coord>-89.359116 43.08806 246.0</gx:coord>\r\n" + 
			"<when>2013-11-09T15:04:36.212Z</when>\r\n" + 
			"<gx:coord>-89.359109 43.088061 246.0</gx:coord>\r\n" + 
			"<when>2013-11-09T15:04:42.046Z</when>\r\n" + 
			"<gx:coord>-89.359089 43.088083 245.0</gx:coord>\r\n" + 
			"<ExtendedData>\r\n" + 
			"<SchemaData schemaUrl=\"#schema\">\r\n" + 
			"</SchemaData>\r\n" + 
			"</ExtendedData>\r\n" + 
			"</gx:Track>\r\n" + 
			"</gx:MultiTrack>\r\n" + 
			"</Placemark>\r\n" + 
			"<Placemark>\r\n" + 
			"<name><![CDATA[11/9/2013 8:58AM (End)]]></name>\r\n" + 
			"<description><![CDATA[Created by Google My Tracks on Android.\r\n" + 
			"\r\n" + 
			"Name: 11/9/2013 8:58AM\r\n" + 
			"Activity type: biking\r\n" + 
			"Description: -\r\n" + 
			"Total distance: 2.06 km (1.3 mi)\r\n" + 
			"Total time: 05:44\r\n" + 
			"Moving time: 05:14\r\n" + 
			"Average speed: 21.58 km/h (13.4 mi/h)\r\n" + 
			"Average moving speed: 23.65 km/h (14.7 mi/h)\r\n" + 
			"Max speed: 34.29 km/h (21.3 mi/h)\r\n" + 
			"Average pace: 2:47 min/km (4:28 min/mi)\r\n" + 
			"Average moving pace: 2:32 min/km (4:05 min/mi)\r\n" + 
			"Fastest pace: 1:45 min/km (2:49 min/mi)\r\n" + 
			"Max elevation: 251 m (823 ft)\r\n" + 
			"Min elevation: 237 m (777 ft)\r\n" + 
			"Elevation gain: 16 m (54 ft)\r\n" + 
			"Max grade: 3 %\r\n" + 
			"Min grade: -2 %\r\n" + 
			"Recorded: 11/9/2013 8:58AM\r\n" + 
			"]]></description>\r\n" + 
			"<TimeStamp><when>2013-11-09T15:04:42.046Z</when></TimeStamp>\r\n" + 
			"<styleUrl>#end</styleUrl>\r\n" + 
			"<Point>\r\n" + 
			"<coordinates>-89.359089,43.088083,245.0</coordinates>\r\n" + 
			"</Point>\r\n" + 
			"</Placemark>\r\n" + 
			"</Document>\r\n" + 
			"</kml>\r\n" + 
			"";
	
	public static String WashAve = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>\r\n" + 
			"<kml xmlns=\"http://earth.google.com/kml/2.2\">\r\n" + 
			"<Document>\r\n" + 
			"  <name>Walking directions to E Washington Ave</name>\r\n" + 
			"  <description><![CDATA[]]></description>\r\n" + 
			"  <Style id=\"style3\">\r\n" + 
			"    <IconStyle>\r\n" + 
			"      <Icon>\r\n" + 
			"        <href></href>\r\n" + 
			"      </Icon>\r\n" + 
			"    </IconStyle>\r\n" + 
			"  </Style>\r\n" + 
			"  <Style id=\"style1\">\r\n" + 
			"    <LineStyle>\r\n" + 
			"      <color>73FF0000</color>\r\n" + 
			"      <width>5</width>\r\n" + 
			"    </LineStyle>\r\n" + 
			"  </Style>\r\n" + 
			"  <Style id=\"style2\">\r\n" + 
			"    <IconStyle>\r\n" + 
			"      <Icon>\r\n" + 
			"        <href></href>\r\n" + 
			"      </Icon>\r\n" + 
			"    </IconStyle>\r\n" + 
			"  </Style>\r\n" + 
			"  <Placemark>\r\n" + 
			"    <name>From: E Washington Ave</name>\r\n" + 
			"    <styleUrl>#style3</styleUrl>\r\n" + 
			"    <Point>\r\n" + 
			"      <coordinates>-89.375366,43.080997,0.000000</coordinates>\r\n" + 
			"    </Point>\r\n" + 
			"  </Placemark>\r\n" + 
			"  <Placemark>\r\n" + 
			"    <name>Walking directions to E Washington Ave</name>\r\n" + 
			"    <styleUrl>#style1</styleUrl>\r\n" + 
			"    <ExtendedData>\r\n" + 
			"      <Data name=\"_SnapToRoads\">\r\n" + 
			"        <value>true</value>\r\n" + 
			"      </Data>\r\n" + 
			"    </ExtendedData>\r\n" + 
			"    <LineString>\r\n" + 
			"      <tessellate>1</tessellate>\r\n" + 
			"      <coordinates>\r\n" + 
			"        <gx:coord>-89.375366 43.080997 0.000000</gx:coord>\r\n" + 
			"        <gx:coord>-89.382187 43.076206 0.000000</gx:coord>\r\n" + 
			"        <gx:coord>-89.382286 43.076027 0.000000</gx:coord>\r\n" + 
			"        <gx:coord>-89.382957 43.075546 0.000000</gx:coord>\r\n" + 
			"      </coordinates>\r\n" + 
			"    </LineString>\r\n" + 
			"  </Placemark>\r\n" + 
			"  <Placemark>\r\n" + 
			"    <name>To: E Washington Ave</name>\r\n" + 
			"    <styleUrl>#style2</styleUrl>\r\n" + 
			"    <Point>\r\n" + 
			"      <coordinates>-89.382957,43.075546,0.000000</coordinates>\r\n" + 
			"    </Point>\r\n" + 
			"  </Placemark>\r\n" + 
			"</Document>\r\n" + 
			"</kml>\r\n" + 
			"";
	
	
	
	public static void CreatePath(final String content, String originalName){	
		
		String fileExt = "";
		int dot = originalName.lastIndexOf(".");
		if(dot < 0){
			dot = originalName.length();
		}
		else{
			fileExt = originalName.substring(dot);
		}
		
		String firstPart = originalName.substring(0, dot);
		final String name = firstPart.replaceAll("\\W+", "")+fileExt;
		final ParseObject parseObject = new ParseObject("PathFile");
		parseObject.saveInBackground(new SaveCallback(){
			@Override
			public void done(ParseException e) {
				if(e == null){
					byte[] data = content.getBytes();
			        final ParseFile file = new ParseFile(name, data);
			        file.saveInBackground(new SaveCallback(){

						@Override
						public void done(ParseException e) {
							if(e==null){
								int dot = name.lastIndexOf(".");
								dot = dot < 0 ? name.length() : dot;
								parseObject.put("name", name.substring(0, dot));
								parseObject.put("file", file);
								try {
									parseObject.save();
								} catch (ParseException e1) {
									// TODO Auto-generated catch block
									e1.printStackTrace();
								}
							}
							else{
								e.printStackTrace();
								Log.d("problem saving file", "second lap");
							}
						}});
				}
			}});
	}
	
	public static void CreatePlanetScale(){
		final Scale PlanetScale = new Scale();
		PlanetScale.SetName("Planets");
		PlanetScale.push(new SaveCallback(){
			@Override
			public void done(ParseException arg0) {
				if(arg0 == null){
					AddObj(PlanetScale, "Mercury", 
							"The closest planet to the Sun and the smallest planet in the Solar System, it has no natural satellites.",
							0.01295681063,
							"http://moonlady.com/wp-content/uploads/2013/02/Mercury.jpg");	
					AddObj(PlanetScale, "Neptune",
							"The most distant planet from earth in our solar system and the smallest of the gas giants.",
							1,
							"http://solarsystem.nasa.gov/multimedia/gallery/Neptune_Full.jpg");
					AddObj(PlanetScale, "Uranus",
							"The coldest planet in our solar system with temperatures as low as -224C",
							0.63787375415,
							"http://www.crystalinks.com/uranus.jpg");
					AddObj(PlanetScale, "Saturn",
							"The least dense planet in the solar system. It has 62 moons, including some believed to possibly contain life.",
							0.31561461794,
							"http://nssdc.gsfc.nasa.gov/image/planetary/saturn/saturn_false.jpg");
					AddObj(PlanetScale, "Jupiter",
							"The largest and densest of the inner planets.",
							0.17275747508,
							"http://plus.maths.org/issue36/features/davies/mars.jpg");
					AddObj(PlanetScale, "Mars",
							"The largest and densest of the inner planets.",
							0.04983388704,
							"http://plus.maths.org/issue36/features/davies/mars.jpg");
					AddObj(PlanetScale, "Earth",
							"The largest and densest of the inner planets.",
							0.03322259136,
							"http://www.openthefuture.com/images/sunset.jpg");
					AddObj(PlanetScale, "Venus",
							"This planet is much drier than Earth, and its atmosphere is ninety times as dense.",
							0.02325581395,
							"http://www.windows2universe.org/venus/images/venus_med.jpg");
				}				
			}});
	}
	
	private static void AddObj(final Scale scale, final String name, final String text, 
			final double percent, final String image){		
		final ScaleObject Obj = new ScaleObject();				
		Obj.push(new SaveCallback() {
			  public void done(ParseException e) {
				Obj.SetName(name);
				Obj.SetText(text);
				Obj.SetPercentage(percent);
				Obj.SetImageLocation(image);
				Obj.push();
				scale.AddObject(Obj);
			  }
			});			
	}
	
	public static void CreateDinoScale(){
		final Scale DinoScale = new Scale();
		DinoScale.SetName("Dinos!");
		DinoScale.push();
		
		final ScaleObject veloco = new ScaleObject();
		veloco.SetName("Velociraptor");
		veloco.SetText("Actually stood no taller than 3 feet, the ones from Jurassic Park were based on the Deinonychus");
		veloco.SetPercentage(.077);
		veloco.SetImageLocation("http://www.fimfiction-static.net/images/avatars/11792_256.jpg");
		veloco.parseObject.saveInBackground(new SaveCallback() {
			  public void done(ParseException e) {
				DinoScale.AddObject(veloco);
			  }
			});
		
		final ScaleObject steg = new ScaleObject();
		steg.SetName("Stegosaurus");
		steg.SetText("Had one of the smallest brains among dinasours at roughly the size of a walnut.");
		steg.SetPercentage(.354);
		steg.SetImageLocation("http://shop.yukonkids.com/photos/product/r/rubber-stegosaurus-256px-256px.jpg");
		steg.parseObject.saveInBackground(new SaveCallback() {
			  public void done(ParseException e) {
				DinoScale.AddObject(steg);
			  }
			});
		
		final ScaleObject trici = new ScaleObject();
		trici.SetName("Triceratops");
		trici.SetText("Triceratops means \"3-horned face\" in Greek, but the dinasour had only two horns and a snout.");
		trici.SetPercentage(.385);
		trici.SetImageLocation("http://images2.wikia.nocookie.net/__cb20130214223732/dinosaurs/images/0/04/Triceratops_raul-_martin_net_(1).jpg");
		trici.parseObject.saveInBackground(new SaveCallback() {
			  public void done(ParseException e) {
				DinoScale.AddObject(trici);
			  }
			});		
		
		final ScaleObject pter = new ScaleObject();
		pter.SetName("Pteranodon");
		pter.SetText("Had the longest wingspan of any dinasour at ~27 feet, aproximatly 3.5 times a Pterodactylus'");
		pter.SetPercentage(.415);
		pter.SetImageLocation("http://www.kidsdinos.com/images/dinosaurs/Pteranodon1202331923.jpg");
		pter.parseObject.saveInBackground(new SaveCallback() {
			  public void done(ParseException e) {
				DinoScale.AddObject(pter);
			  }
			});	

		final ScaleObject rex = new ScaleObject();
		rex.SetName("Tyrannosaurus rex");
		rex.SetText("Modern estimates put the force of its bite at over 5000 metric tons, or more than 10 times a gator.");
		rex.SetPercentage(.692);
		rex.SetImageLocation("http://www.officialpsds.com/images/thumbs/T-Rex-psd49647.png");
		rex.parseObject.saveInBackground(new SaveCallback() {
			  public void done(ParseException e) {
				DinoScale.AddObject(rex);
			  }
			});	
	

		final ScaleObject bront = new ScaleObject();
		bront.SetName("Brontosaurus");
		bront.SetText("One of the largest dinasours, aproximatly 65ft long and weghing upwards of 30 tons");
		bront.SetPercentage(1);
		bront.SetImageLocation("http://magpo.blogs.com/davesblog/images/2007/12/03/apato2.jpg");
		bront.parseObject.saveInBackground(new SaveCallback() {
			  public void done(ParseException e) {
				DinoScale.AddObject(bront);
			  }
			});	

	}
}
