package com.millo.abcshare;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.util.HashMap;

import com.millo.abcshare.MilloHelpers.MyLog;

import android.content.Context;
import android.net.Uri;
import android.os.Environment;
import android.util.Log;

public class Helpers {
	
	static Boolean quiet = false;
	
	static String GetRootDir(Context context){
		return Environment.getExternalStorageDirectory().toString()+"/"+
				context.getResources().getString(R.string.app_name);
	}
	static String GetWWWDir(Context context){
		return GetRootDir(context)+"//www";
	}
	//	static void shareURI(Context context, Uri uri)
	//	{
	//		if(!Environment.MEDIA_MOUNTED.equals(Environment.getExternalStorageState())){
	//			Log.i("[w] MyLog", "MyLog::write External SD card not mounted");
	//			return;
	//		}
	//		
	//		File dir = new File (GetWWWDir(context));	    
	//		dir.mkdirs();
	//
	//		File file = new File(GetPointFilePath());
	//		MyLog.i("Writing to: "+file.getAbsolutePath());
	//		MilloHelpers.writeToFile(file.getAbsolutePath(), RenderLocationForOutput(l));
	//		
	//		
	//		
	//		String suri = uri.toString();
	//		String path = getFilePath(uri);
	//		
	//		MyLog.i("Writing: "+suri);
	//		MyLog.i("Writing to: "+path);
	//		//MilloHelpers.writeToFile(suri, path);
	//	}
	static String getFilePath(Uri uri){
		return null;

	}

	// ***************** Handle multiple files at the same time
//	static String getMapPath(Context context){
//		String path = context.getDir(context.getPackageName(), Context.MODE_PRIVATE)+"/sharemap.txt";
//		//String path = Environment.getExternalStorageDirectory().getAbsolutePath()+"/sharemap.txt"; 
//		if (!Helpers.quiet) MyLog.i("path: "+path);
//		return path;
//	}
//	static HashMap<String, File> loadMap(Context context){
//		HashMap<String, File> map = new HashMap<String, File>();
//		File file = new File(getMapPath(context));
//		FileInputStream f;
//		try {
//			f = new FileInputStream(file);		
//			ObjectInputStream s = new ObjectInputStream(f);
//			map = (HashMap<String, File>) s.readObject();
//			s.close();
//		} catch (Exception e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		}
//		
//		if(!quiet){
//			for (String key : map.keySet())
//			{
//				MyLog.i("key:"+key+" val:"+map.get(key));
//			}
//		}
//		
//		return map;
//	}
//	static void saveMap(Context context, HashMap<String, File> map){
//		File file = new File(getMapPath(context));
//		FileOutputStream f;
//		try {
//			f = new FileOutputStream(file);
//			ObjectOutputStream s = new ObjectOutputStream(f);
//			s.writeObject(map);
//			s.close();
//		} catch (Exception e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		}
//	}

}
