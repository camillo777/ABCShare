package com.millo.abcshare;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.math.BigInteger;
import java.net.Inet4Address;
import java.net.InetAddress;
import java.net.MalformedURLException;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.security.SecureRandom;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Enumeration;
import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.ContentUris;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.provider.DocumentsContract;
import android.provider.MediaStore;
import android.text.Html;
import android.text.format.Formatter;
import android.util.Log;
import android.widget.ImageView;
import android.widget.Toast;

public class MilloHelpers {

	public final static boolean DEBUG = false;

	public static void ShowNetworkSettings(final Context ctx) {
		new AlertDialog.Builder(ctx)
		.setTitle("Nessuna connessione ad internet")
		.setMessage("Vuoi aprire le impostazioni di rete?")
		.setPositiveButton("SI", new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface dialog, int which) { 
				// continue
				ctx.getApplicationContext().startActivity(new Intent(
						android.provider.Settings.ACTION_DATA_ROAMING_SETTINGS));
			}
		})
		.setNegativeButton("NO", new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface dialog, int which) { 
				// do nothing
			}
		})
		.show();
	}

	// <uses-permission android:name="android.permission.ACCESS_NETWORK_STATE" />
	public static boolean hasActiveInternetConnection(Context ctx) {
		ConnectivityManager cm =
				(ConnectivityManager) ctx.getSystemService(Context.CONNECTIVITY_SERVICE);
		NetworkInfo netInfo = cm.getActiveNetworkInfo();
		if (netInfo != null && netInfo.isConnectedOrConnecting()) {
			return true;
		}
		return false;
	}

	//		private static boolean isNetworkAvailable(Activity a) {
	//		    ConnectivityManager connectivityManager 
	//        		= (ConnectivityManager) a.getSystemService(Context.CONNECTIVITY_SERVICE);
	//		    NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
	//		    return activeNetworkInfo != null;
	//		}	
	//		public static boolean hasActiveInternetConnection(Activity a) {
	//		    if (isNetworkAvailable(a)) {
	//		        try {
	//		            HttpURLConnection urlc = (HttpURLConnection) (new URL("http://www.rockit.it").openConnection());
	//		            urlc.setRequestProperty("User-Agent", "Test");
	//		            urlc.setRequestProperty("Connection", "close");
	//		            urlc.setConnectTimeout(1500); 
	//		            urlc.connect();
	//		            return (urlc.getResponseCode() == 200);
	//		        } catch (IOException e) {
	//		            return false;
	//		        }
	//		    }
	//		    return false;
	//		}		

	public static void ShowLocationSettings(final Context ctx) {
		new AlertDialog.Builder(ctx)
		.setTitle("Nessun servizio di localizzazione")
		.setMessage("Vuoi aprire le impostazioni di localizzazione?")
		.setPositiveButton("SI", new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface dialog, int which) { 
				// continue
				ctx.startActivity(new Intent(
						android.provider.Settings.ACTION_LOCATION_SOURCE_SETTINGS));
			}
		})
		.setNegativeButton("NO", new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface dialog, int which) { 
				// do nothing
			}
		})
		.show();
	}

	public static void Popup(Context c, String text)
	{
		Toast.makeText(c, text, Toast.LENGTH_SHORT).show();
	}

	public static void DialogOK(Context ctx, String text){
		AlertDialog.Builder builder = new AlertDialog.Builder(ctx);
		builder.setMessage(text)
		.setCancelable(false)
		.setPositiveButton("OK", new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface dialog, int id) {
				dialog.cancel();
			}
		});
		AlertDialog alert = builder.create();
		alert.show();
	}
	
	static String httpUrl(String urlStr) {
		String shttp = "";
		try {
			//shttp = URLEncoder.encode(s.trim(), "UTF-8");
			
			//String urlStr = "http://abc.dev.domain.com/0007AC/ads/800x480 15sec h.264.mp4";
			URL url = new URL(urlStr);
			URI uri = new URI(url.getProtocol(), url.getUserInfo(), url.getHost(), url.getPort(), url.getPath(), url.getQuery(), url.getRef());
			url = uri.toURL();
			shttp = url.toString();
		} catch (MalformedURLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (URISyntaxException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return shttp;
	}
	static String httpDecode(String s){
		s = s.replace("\r\n", "<br/>");
		return Html.fromHtml(s).toString();
	}
	static String stripTabs(String s){
		return s.replace("\t", "");
	}
	static String stripMultipleCRLF(String s){
//		String snew = "";
//		while(!snew.equals(s)){
//			snew = s.replace("\r\n\r\n", "\r\n");
//		}
		return s.replace("\r\n\r\n", "\r\n");
	}

	static class MyLog{
//		static MyHandler h = null;
		static File f = null;
		static OutputStream out = null;
		
		private static String appName = "milloapp";
		
		static void setAppName(String s){
			appName=s;
		}

		static String gettime(){
			return FormatDateTimePrecise(new Date());
		}
		static String format(String s){
			return gettime() + " " + s;
		}
		static String format_w(String s){
			return format(" *** " + s + " *** ");
		}
		static void i(String s){
			Log.i("[i] MyLog", s);

			String news = format(s);
			if (DEBUG) {
				//h.NotifyStatus(s);
				write(news);
			}
			//MilloHelpers.Popup(c, s);
		}
		static void w(String s){
			Log.i("[w] MyLog", s);

			String news = format_w(s);
			if (DEBUG) {
				//h.NotifyStatus(s);
				write(news);
			}
			//MilloHelpers.Popup(c, s);
		}
		static void e(String s){
			Log.i("[e] MyLog", s);

			String news = format(s);
			if (DEBUG) {
				//h.NotifyStatus(s);
				write(news);
			}
			//MilloHelpers.Popup(c, s);
		}
		static void append(String s) throws IOException{
			if (f!= null){
				BufferedWriter buf = new BufferedWriter(new FileWriter(f, true)); 
				buf.append(s);
				buf.newLine();
				buf.close();
			}
		}
		static void write(String s){
			try
			{
				if(!Environment.MEDIA_MOUNTED.equals(Environment.getExternalStorageState())){
					Log.i("[w] MyLog", "MyLog::write External SD card not mounted");
					return;
				}
				if (f==null){
					Log.i("[w] MyLog", "MyLog::write f is null");					
					f = new File(Environment.getExternalStorageDirectory(), appName+".txt");

					// if file doesnt exists, then create it
					if (!f.exists()) {
						f.createNewFile();
					}
					else
					{
						f.delete();
					}

					append("-- START OF LOG --");
					append("Logging to: "+f.getAbsolutePath());
				}
				append(s);
			}
			catch (IOException e)
			{
				e.printStackTrace();
			}
		}
		static String getLogPath(){
			if (f==null) return null;
			return f.getAbsolutePath();
		}
	}

	///////////////////////////////////////////////////////////////
	// DATE	
	private static String[] NomeMese = {
		"GENNAIO",
		"FEBBRAIO",
		"MARZO",
		"APRILE",
		"MAGGIO",
		"GIUGNO",
		"LUGLIO",
		"AGOSTO",
		"SETTEMBRE",
		"OTTOBRE",
		"NOVEMBRE",
		"DICEMBRE"
	};
	private static String[] NomeMeseShort = {
		"GEN",
		"FEB",
		"MAR",
		"APR",
		"MAG",
		"GIU",
		"LUG",
		"AGO",
		"SET",
		"OTT",
		"NOV",
		"DIC"
	};
	private static String[] NomeGiornoShort = {
		"DOM","LUN","MAR","MER","GIO","VEN","SAB"
	};
	private static String[] NomeGiorno = {
		"DOMENICA","LUNEDI","MARTEDI","MERCOLEDI","GIOVEDI","VENERDI","SABATO"
	};

	public static Date addDays(Date date, int days)
	{
		Calendar cal = Calendar.getInstance();
		cal.setTime(date);
		cal.add(Calendar.DATE, days); //minus number would decrement the days
		return cal.getTime();
	}

	public static Integer GetMonthNumber(Date d) {
		Calendar cal = Calendar.getInstance();
		cal.setTime(d);
		return cal.get(Calendar.MONTH);
	}
	public static String GetMonthName(Date d) {
		return NomeMese[GetMonthNumber(d)];
	}
	public static String GetMonthNameShort(Date d) {
		return NomeMeseShort[GetMonthNumber(d)];
	}
	public static Integer GetDayOfMonth(Date d) {
		Calendar cal = Calendar.getInstance();
		cal.setTime(d);
		return cal.get(Calendar.DAY_OF_MONTH);
	}
	public static Integer GetDayOfWeekNumber(Date d) {
		Calendar cal = Calendar.getInstance();
		cal.setTime(d);
		return cal.get(Calendar.DAY_OF_WEEK);
	}
	public static String GetDayOfWeekName(Date d) {
		return NomeGiorno[GetDayOfWeekNumber(d)-1];
	}
	public static String GetDayOfWeekNameShort(Date d) {
		return NomeGiornoShort[GetDayOfWeekNumber(d)-1];
	}

	public static String FormatDate(Date d){
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd", java.util.Locale.getDefault());
		return sdf.format(d);
	}
	public static String FormatDateTimePrecise(Date d){
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSSZ", java.util.Locale.getDefault());
		return sdf.format(d);
	}

	static Date ConvertStringToDate(String dateString){
		SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd", java.util.Locale.getDefault());
		Date convertedDate = new Date();
		try {
			convertedDate = dateFormat.parse(dateString);
		} catch (ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return convertedDate;
	}

	///////////////////////////////////////////////////////////////
	// DIALOGS	
	public static void ShowDialogOk(final Context ctx) {
		new AlertDialog.Builder(ctx)
		.setTitle("Nessuna connessione ad internet")
		.setMessage("Questa applicazione ha bisogno di una connessione ad internet per funzionare; controllare la connettivit? di rete")
		.setPositiveButton("OK", new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface dialog, int which) { 
				// continue
			}
		})
		.show();
	}

	///////////////////////////////////////////////////////////////
	// INTENTS	
	public static Intent newEmailIntent(
			Context context, String address, String subject, String body, String cc) {
		Intent intent = new Intent(Intent.ACTION_SEND);
		intent.putExtra(Intent.EXTRA_EMAIL, new String[] { address });
		intent.putExtra(Intent.EXTRA_TEXT, body);
		intent.putExtra(Intent.EXTRA_SUBJECT, subject);
		intent.putExtra(Intent.EXTRA_CC, cc);
		intent.setType("message/rfc822");
		return intent;			
	}
	public static Intent newSmsIntent(Context context, Uri smsUri) {
		Intent sendIntent = new Intent(Intent.ACTION_SEND, smsUri);
		sendIntent.putExtra("sms_body", "ciao ciao"); 
		sendIntent.setType("vnd.android-dir/mms-sms");
		return sendIntent;			
	}
	public static Intent newCallIntent(Context context, String telUri){
		// tel:022123456
		/*
java.lang.SecurityException: Permission Denial: starting Intent 
{ act=android.intent.action.CALL dat=tel:xxxxxxxxxxxx cmp=com.android.phone/.OutgoingCallBroadcaster } 
from ProcessRecord{40d24378 8651:com.example.rockitconcerti/u0a10039} (pid=8651, uid=10039) 
requires android.permission.CALL_PHONE
		 */    	
		MyLog.i("newCallIntent: "+telUri);
		Intent callIntent = new Intent(Intent.ACTION_CALL);
		try{
			callIntent.setData(Uri.parse(telUri));
		}
		catch(Exception ex){
			MyLog.e(ex.getMessage());
		}
		return callIntent;
	}
	public static Intent newCalendarIntent(
			Context context, 
			Date startDate, 
			String title, 
			String location, 
			String note) {
		Calendar cal = Calendar.getInstance();     
		cal.setTime(startDate);

		Intent intent = new Intent(Intent.ACTION_EDIT);
		intent.setType("vnd.android.cursor.item/event");
		intent.putExtra("beginTime", cal.getTimeInMillis());
		intent.putExtra("endTime", cal.getTimeInMillis()+60*60*1000);
		intent.putExtra("allDay", true);
		//intent.putExtra("rrule", "FREQ=WEEKLY;COUNT="+numWeeks);
		//intent.putExtra("rrule", "FREQ=YEARLY");
		intent.putExtra("title", title);
		intent.putExtra("eventLocation", location);
		intent.putExtra("description", note);    	
		//intent.putExtra(Events.ACCESS_LEVEL, Events.ACCESS_PRIVATE);
		//intent.putExtra(Events.AVAILABILITY, Events.AVAILABILITY_BUSY);
		return intent;
	}
	public static Intent newMapIntent_LatLong(Context context, double lat, double lng) {
		String uri = String.format("geo:%f,%f", lat, lng);
		Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(uri));
		return intent;
	}
	public static Intent newMapIntent_Address(Context context, String address) {
		String uri = String.format("geo:0,0?q=%s", address);
		Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(uri));
		return intent;
	}

	public static void FreeImageView(ImageView iv){
		MyLog.i("MilloHelpers::FreeImageView START");
		if (iv!=null&&iv.getDrawable()!=null){	

			Bitmap bitmap = ((android.graphics.drawable.BitmapDrawable)iv.getDrawable()).getBitmap();
			if (bitmap!=null && !bitmap.isRecycled()){
				bitmap.recycle();
				bitmap = null;
			}
		}
		else{
			MyLog.i("MilloHelpers::FreeImageView iv nullo");			
		}
		MyLog.i("MilloHelpers::FreeImageView STOP");
	}
	
	
	public static void writeToFile(String path, String data) {
	    try {
	        OutputStreamWriter outputStreamWriter = new OutputStreamWriter(new FileOutputStream(path));
	        outputStreamWriter.write(data);
	        outputStreamWriter.close();
	    }
	    catch (IOException e) {
	        Log.e("Exception", "File write failed: " + e.toString());
	    } 
	}


	public static String readFromFile(String path) {

	    String ret = "";

	    try {
	        InputStream inputStream = new FileInputStream(path);

	        if ( inputStream != null ) {
	            InputStreamReader inputStreamReader = new InputStreamReader(inputStream);
	            BufferedReader bufferedReader = new BufferedReader(inputStreamReader);
	            String receiveString = "";
	            StringBuilder stringBuilder = new StringBuilder();

	            while ( (receiveString = bufferedReader.readLine()) != null ) {
	                stringBuilder.append(receiveString);
	            }

	            inputStream.close();
	            ret = stringBuilder.toString();
	        }
	    }
	    catch (FileNotFoundException e) {
	        Log.e("login activity", "File not found: " + e.toString());
	    } catch (IOException e) {
	        Log.e("login activity", "Can not read file: " + e.toString());
	    }

	    return ret;
	}
	
//	public static String getFileNameByUri(Context context, Uri uri)
//	{
//	    String fileName="unknown";//default fileName
//	    Uri filePathUri = uri;
//	    if (uri.getScheme().toString().compareTo("content")==0)
//	    {      
//	        Cursor cursor = context.getContentResolver().query(uri, null, null, null, null);
//	        if (cursor.moveToFirst())
//	        {
//	            int column_index = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
//	            //Instead of "MediaStore.Images.Media.DATA" can be used "_data"
//	            filePathUri = Uri.parse(cursor.getString(column_index));
//	            fileName = filePathUri.getLastPathSegment().toString();
//	        }
//	    }
//	    else if (uri.getScheme().compareTo("file")==0)
//	    {
//	        fileName = filePathUri.getLastPathSegment().toString();
//	    }
//	    else
//	    {
//	        fileName = fileName+"_"+filePathUri.getLastPathSegment();
//	    }
//	    return fileName;
//	}
//	public static String getFilePathByUri(Context context, Uri uri)
//	{
//	    String filePath="unknown";//default fileName
//	    Uri filePathUri = uri;
//	    if (uri.getScheme().toString().compareTo("content")==0)
//	    {      
//	        Cursor cursor = context.getContentResolver().query(uri, null, null, null, null);
//	        if (cursor.moveToFirst())
//	        {
//	            int column_index = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
//	            //Instead of "MediaStore.Images.Media.DATA" can be used "_data"
//	            filePathUri = Uri.parse(cursor.getString(column_index));
//	            filePath = filePathUri.toString();
//	        }
//	    }
//	    else if (uri.getScheme().compareTo("file")==0)
//	    {
//	    	filePath = filePathUri.getPath();
//	    }
//	    else
//	    {
//	    	filePath = filePathUri.getPath();
//	    }
//	    return filePath;
//	}
	
	public static String getLocalIpAddress() {
	    try {
	        for (Enumeration<NetworkInterface> en = NetworkInterface.getNetworkInterfaces(); en.hasMoreElements();) {
	            NetworkInterface intf = en.nextElement();
	            for (Enumeration<InetAddress> enumIpAddr = intf.getInetAddresses(); enumIpAddr.hasMoreElements();) {
	                InetAddress inetAddress = enumIpAddr.nextElement();
                	//MyLog.i(inetAddress.getHostAddress());
                	//MyLog.i("inetAddress.isSiteLocalAddress():"+inetAddress.isSiteLocalAddress());
                	//MyLog.i("inetAddress.isLinkLocalAddress():"+inetAddress.isLinkLocalAddress());

	      //          if (!inetAddress.isLoopbackAddress()) {
	                if (!inetAddress.isLoopbackAddress() && 
	                		!inetAddress.isLinkLocalAddress()) // && 
//	                		inetAddress.isSiteLocalAddress())
	                {
	                	MyLog.i(inetAddress.getHostAddress());
	                    return inetAddress.getHostAddress();
	                }
	            }
	        }
	    } catch (SocketException ex) {
	        MyLog.e(ex.toString());
	    }
	    return null;
	}
	
//	public static String getLocalIpAddress() {
//	    try {
//	        for (Enumeration<NetworkInterface> en = NetworkInterface.getNetworkInterfaces(); en.hasMoreElements();) {
//	            NetworkInterface intf = en.nextElement();
//	            for (Enumeration<InetAddress> enumIpAddr = intf.getInetAddresses(); enumIpAddr.hasMoreElements();) {
//	                InetAddress inetAddress = enumIpAddr.nextElement();
//	                if (!inetAddress.isLoopbackAddress()) {
//	                    String ip = Formatter.formatIpAddress(inetAddress.hashCode());
//	                    MyLog.i("***** IP="+ ip);
//	                    return ip;
//	                }
//	            }
//	        }
//	    } catch (SocketException ex) {
//	    	MyLog.e(ex.toString());
//	    }
//	    return null;
//	}
	
	static class UriResolver{
		/**
		 * Get a file path from a Uri. This will get the the path for Storage Access
		 * Framework Documents, as well as the _data field for the MediaStore and
		 * other file-based ContentProviders.
		 *
		 * @param context The context.
		 * @param uri The Uri to query.
		 * @author paulburke
		 */
		@SuppressLint("NewApi")
		public static String getPath(final Context context, final Uri uri) {

		    final boolean isKitKat = Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT;

		    // DocumentProvider
		    if (isKitKat && DocumentsContract.isDocumentUri(context, uri)) {
		        // ExternalStorageProvider
		        if (isExternalStorageDocument(uri)) {
		            final String docId = DocumentsContract.getDocumentId(uri);
		            final String[] split = docId.split(":");
		            final String type = split[0];

		            if ("primary".equalsIgnoreCase(type)) {
		                return Environment.getExternalStorageDirectory() + "/" + split[1];
		            }

		            // TODO handle non-primary volumes
		        }
		        // DownloadsProvider
		        else if (isDownloadsDocument(uri)) {

		            final String id = DocumentsContract.getDocumentId(uri);
		            final Uri contentUri = ContentUris.withAppendedId(
		                    Uri.parse("content://downloads/public_downloads"), Long.valueOf(id));

		            return getDataColumn(context, contentUri, null, null);
		        }
		        // MediaProvider
		        else if (isMediaDocument(uri)) {
		            final String docId = DocumentsContract.getDocumentId(uri);
		            final String[] split = docId.split(":");
		            final String type = split[0];

		            Uri contentUri = null;
		            if ("image".equals(type)) {
		                contentUri = MediaStore.Images.Media.EXTERNAL_CONTENT_URI;
		            } else if ("video".equals(type)) {
		                contentUri = MediaStore.Video.Media.EXTERNAL_CONTENT_URI;
		            } else if ("audio".equals(type)) {
		                contentUri = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI;
		            }

		            final String selection = "_id=?";
		            final String[] selectionArgs = new String[] {
		                    split[1]
		            };

		            return getDataColumn(context, contentUri, selection, selectionArgs);
		        }
		    }
		    // MediaStore (and general)
		    else if ("content".equalsIgnoreCase(uri.getScheme())) {
		        return getDataColumn(context, uri, null, null);
		    }
		    // File
		    else if ("file".equalsIgnoreCase(uri.getScheme())) {
		        return uri.getPath();
		    }

		    return null;
		}

		/**
		 * Get the value of the data column for this Uri. This is useful for
		 * MediaStore Uris, and other file-based ContentProviders.
		 *
		 * @param context The context.
		 * @param uri The Uri to query.
		 * @param selection (Optional) Filter used in the query.
		 * @param selectionArgs (Optional) Selection arguments used in the query.
		 * @return The value of the _data column, which is typically a file path.
		 */
		public static String getDataColumn(Context context, Uri uri, String selection,
		        String[] selectionArgs) {

		    Cursor cursor = null;
		    final String column = "_data";
		    final String[] projection = {
		            column
		    };

		    try {
		        cursor = context.getContentResolver().query(uri, projection, selection, selectionArgs,
		                null);
		        if (cursor != null && cursor.moveToFirst()) {
		            final int column_index = cursor.getColumnIndexOrThrow(column);
		            return cursor.getString(column_index);
		        }
		    } finally {
		        if (cursor != null)
		            cursor.close();
		    }
		    return null;
		}


		/**
		 * @param uri The Uri to check.
		 * @return Whether the Uri authority is ExternalStorageProvider.
		 */
		public static boolean isExternalStorageDocument(Uri uri) {
		    return "com.android.externalstorage.documents".equals(uri.getAuthority());
		}

		/**
		 * @param uri The Uri to check.
		 * @return Whether the Uri authority is DownloadsProvider.
		 */
		public static boolean isDownloadsDocument(Uri uri) {
		    return "com.android.providers.downloads.documents".equals(uri.getAuthority());
		}

		/**
		 * @param uri The Uri to check.
		 * @return Whether the Uri authority is MediaProvider.
		 */
		public static boolean isMediaDocument(Uri uri) {
		    return "com.android.providers.media.documents".equals(uri.getAuthority());
		}
	}
}
