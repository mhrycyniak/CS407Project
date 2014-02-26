package com.wisc.cs407project;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;

public class DirUtils {
	public static void storeDir(String location, String toStore) {
		if(!toStore.endsWith("/")) {
			toStore += "/";
		} 

		try {
			BufferedReader br = new BufferedReader(new FileReader(location));
			String line;
			while( (line = br.readLine()) != null) {
				if(line.trim().equals(toStore.trim())) {
					return;
				}
			}	
			File f = new File(location);
			if(!f.exists()) {
				f.createNewFile();
			} else {
				toStore = "\n" + toStore;
			}
			
			FileWriter out = new FileWriter(f, true);
			out.write(toStore);
			out.close();
			br.close();
		} catch (Exception e) {
			
		}
	}
}
