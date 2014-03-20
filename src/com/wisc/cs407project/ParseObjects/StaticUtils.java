package com.wisc.cs407project.ParseObjects;

import com.parse.ParseException;
import com.parse.SaveCallback;

import android.util.Log;


public class StaticUtils {
	
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
