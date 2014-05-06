/* (The MIT License)

Copyright (c) 2009-2012 Fedor Vlasov <thest2@gmail.com>

Permission is hereby granted, free of charge, to any person obtaining
a copy of this software and associated documentation files (the
'Software'), to deal in the Software without restriction, including
without limitation the rights to use, copy, modify, merge, publish,
distribute, sublicense, and/or sell copies of the Software, and to
permit persons to whom the Software is furnished to do so, subject to
the following conditions:

The above copyright notice and this permission notice shall be
included in all copies or substantial portions of the Software.

THE SOFTWARE IS PROVIDED 'AS IS', WITHOUT WARRANTY OF ANY KIND,
EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF
MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT.
IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY
CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT,
TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION WITH THE
SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
 */

/*
This file has been modified from it's original source.
 */

package com.wisc.cs407project.ImageLoader;

import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Collections;
import java.util.Map;
import java.util.WeakHashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import org.apache.commons.validator.routines.UrlValidator;

import com.parse.ParseException;
import com.parse.ParseFile;
import com.parse.SaveCallback;
import com.wisc.cs407project.Popup;
import com.wisc.cs407project.R;

import android.os.Handler;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Log;
import android.widget.ImageView;

public class ImageLoader {
    
    MemoryCache memoryCache=new MemoryCache();
    FileCache fileCache;
    private Map<ImageView, String> imageViews=Collections.synchronizedMap(new WeakHashMap<ImageView, String>());
    ExecutorService executorService;
    Handler handler=new Handler();//handler to display images in UI thread

    private static int REQUIRED_WIDTH, REQUIRED_HEIGHT;
    

    public ImageLoader(Context context, int imageWidth, int imageHeight) {
    	this(context);
    	REQUIRED_WIDTH = imageWidth;
    	REQUIRED_HEIGHT = imageHeight;
    }

    public void setSize(int imageWidth, int imageHeight) {
    	REQUIRED_WIDTH = imageWidth;
    	REQUIRED_HEIGHT = imageHeight;
    }
    
    public ImageLoader(Context context){
        fileCache=new FileCache(context);
        executorService=Executors.newFixedThreadPool(5);
    }
    
    public static void SaveParseImage(String url, Bitmap bitmap, final com.wisc.cs407project.ParseObjects.ScaleObject parseObject){
    	final String parseName = url.substring(url.lastIndexOf("/")+1);
    	if(!parseObject.updateImage)
    		return;
    	ByteArrayOutputStream stream = new ByteArrayOutputStream();
    	Bitmap.CompressFormat format = null;
    	if(parseName.contains(".jpg") || parseName.contains(".jpeg")){
    		format = Bitmap.CompressFormat.JPEG;
    	}
    	else if(parseName.contains(".png")){
    		format = Bitmap.CompressFormat.PNG;
    	}
    	else if(parseName.contains(".webp")){
    		format = Bitmap.CompressFormat.WEBP;
    	}
    	else{
    		return;
    	}
        bitmap.compress(format, 100, stream);
        byte[] data = stream.toByteArray();
        final ParseFile file = new ParseFile(parseName, data);
        file.saveInBackground(new SaveCallback(){

			@Override
			public void done(ParseException e) {
				if(e==null){
					parseObject.SetImage(file);
				}
				else{
					Log.d("problem saving file", parseName);
				}
			}});
    }
    
    public void DisplayImage(String url, ImageView imageView, com.wisc.cs407project.ParseObjects.ScaleObject parseObject)
    {
        imageViews.put(imageView, url);
        Bitmap bitmap=memoryCache.get(url);
        if(bitmap!=null) {
        	SaveParseImage(url, bitmap, parseObject);
            imageView.setImageBitmap(bitmap);
        	imageView.setBackgroundResource(R.drawable.gray_image_border);
        } else
        {
            queuePhoto(url, imageView, parseObject);
        }
    }
        
    private void queuePhoto(String url, ImageView imageView, com.wisc.cs407project.ParseObjects.ScaleObject parseObject)
    {
        PhotoToLoad p=new PhotoToLoad(url, imageView, parseObject);
        executorService.submit(new PhotosLoader(p));
    }
    
    private Bitmap getBitmap(String url) 
    {
        File f=fileCache.getFile(url);
        Bitmap b = decodeFile(f);
        
        //from Local Directory
        BufferedReader in = null;
		UrlValidator validator = new UrlValidator();
		if(new File(url).exists()) {
			b = decodeFile(new File(url));
			if(b!=null)
				return b;
		}
        
        //from web
        try {
            //check SD cache first
            if(b!=null)
                return b;
            
            Bitmap bitmap=null;
            URL imageUrl = new URL(url);
            HttpURLConnection conn = (HttpURLConnection)imageUrl.openConnection();
            conn.setConnectTimeout(30000);
            conn.setReadTimeout(30000);
            conn.setInstanceFollowRedirects(true);
            InputStream is=conn.getInputStream();
            OutputStream os = new FileOutputStream(f);
            Utils.CopyStream(is, os);
            os.close();
            conn.disconnect();
            bitmap = decodeFile(f);
            return bitmap;
        } catch (Throwable ex){
           ex.printStackTrace();
           if(ex instanceof OutOfMemoryError)
               memoryCache.clear();
           return null;
        }
    }

    //decodes image and scales it to reduce memory consumption
    public static Bitmap decodeFile(File f){
        try {
            //decode image size
            BitmapFactory.Options o = new BitmapFactory.Options();
            o.inJustDecodeBounds = true;
            FileInputStream stream1=new FileInputStream(f);
            BitmapFactory.decodeStream(stream1,null,o);
            stream1.close();
            
            //Find the correct scale value. It should be the power of 2.
            int width_tmp=o.outWidth, height_tmp=o.outHeight;
            int scale=1;
            while(true){
                if(width_tmp/2<REQUIRED_WIDTH && height_tmp/2<REQUIRED_HEIGHT)
                    break;
                width_tmp/=2;
                height_tmp/=2;
                scale*=2;
            }
            
            //decode with inSampleSize
            BitmapFactory.Options o2 = new BitmapFactory.Options();
            o2.inSampleSize=scale;
            FileInputStream stream2=new FileInputStream(f);
            Bitmap bitmap=BitmapFactory.decodeStream(stream2, null, o2);
            stream2.close();
            return bitmap;
        } catch (FileNotFoundException e) {
        } 
        catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }
    
    //Task for the queue
    private class PhotoToLoad
    {
        public String url;
        public ImageView imageView;
        public com.wisc.cs407project.ParseObjects.ScaleObject parseObject;
        public PhotoToLoad(String u, ImageView i, com.wisc.cs407project.ParseObjects.ScaleObject p){
            url=u; 
            imageView=i;
            parseObject = p;
        }
    }
    
    class PhotosLoader implements Runnable {
        PhotoToLoad photoToLoad;
        PhotosLoader(PhotoToLoad photoToLoad){
            this.photoToLoad=photoToLoad;
        }
        
        @Override
        public void run() {
            try{
            	//TODO add back
                //if(imageViewReused(photoToLoad))
                //    return;
                Bitmap bmp=getBitmap(photoToLoad.url);
                SaveParseImage(photoToLoad.url, bmp, photoToLoad.parseObject);
                memoryCache.put(photoToLoad.url, bmp);
                if(imageViewReused(photoToLoad))
                    return;
                BitmapDisplayer bd=new BitmapDisplayer(bmp, photoToLoad);
                handler.post(bd);
            }catch(Throwable th){
                th.printStackTrace();
            }
        }
    }
    
    boolean imageViewReused(PhotoToLoad photoToLoad){
        String tag=imageViews.get(photoToLoad.imageView);
        if(tag==null || !tag.equals(photoToLoad.url))
            return true;
        return false;
    }
    
    //Used to display bitmap in the UI thread
    class BitmapDisplayer implements Runnable
    {
        Bitmap bitmap;
        PhotoToLoad photoToLoad;
        public BitmapDisplayer(Bitmap b, PhotoToLoad p){bitmap=b;photoToLoad=p;}
        public void run()
        {
        	
            if(imageViewReused(photoToLoad)) {
            	photoToLoad.imageView.setBackgroundResource(R.drawable.gray_image_border);
                return;
            }
            if(bitmap!=null) {
                photoToLoad.imageView.setImageBitmap(bitmap);
            	photoToLoad.imageView.setBackgroundResource(R.drawable.gray_image_border);
            }
            else
                photoToLoad.imageView.setBackground(null);
        }
    }

    public void clearCache() {
        memoryCache.clear();
        fileCache.clear();
    }

}
