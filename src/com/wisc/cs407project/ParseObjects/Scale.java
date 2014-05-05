package com.wisc.cs407project.ParseObjects;

import java.util.ArrayList;

import android.util.Log;

import com.parse.ParseObject;

public class Scale extends ScaleParseObject{
	private String Objects = "scaleObjects";
	private String Name = "name";
	
	public Scale()
	{
		super();
	}
	
	public Scale(ParseObject object)
	{
		pullData(object);
	}
	
	public Scale(String objectId)
	{
		super(objectId);
	}
	
	public ArrayList<ScaleObject> GetObjects(){
		ArrayList<String> objectIds = convertJSONStringArrayToArrayList(parseObject
				.getJSONArray(this.Objects));
		ArrayList<ScaleObject> objects = new ArrayList<ScaleObject>();
		for(String id : objectIds){
			objects.add(new ScaleObject(id));
		}
		return objects;
	}
	
	public ScaleObject GetObject(int i){
		try{
			return new ScaleObject(GetObjectsIds().get(i));
		}catch(Exception e){
			return null;
		}
	}
	
	public String GetObjectId(){
		return parseObject.getObjectId();
	}
	
	public void AddObject(ScaleObject object){
		ArrayList<ScaleObject> objects = GetObjects();
		objects.add(object);
		SetObjects(objects);
		this.push();
	}
	
	public void RemoveObject(ScaleObject object){
		ArrayList<ScaleObject> objects = GetObjects();
		objects.remove(object.parseObject.getObjectId());
		SetObjects(objects);
		this.push();
	}
	
	public ArrayList<String> GetObjectsIds(){
		return convertJSONStringArrayToArrayList(
				parseObject.getJSONArray(this.Objects));
	}
	
	public void SetObjects(ArrayList<ScaleObject> objects){
		ArrayList<String> objectIds = new ArrayList<String>();
		for(ScaleObject object : objects){
			objectIds.add(object.Id());
		}
		parseObject.put(this.Objects, objectIds);
	}
	
	public void SetName(String name){
		parseObject.put(this.Name, name);
	}
	public String GetName(){
		return parseObject.getString(this.Name);
	}
}
