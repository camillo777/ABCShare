package com.millo.abcshare;

import java.io.File;
import java.util.Date;
import java.util.Locale;

import com.millo.abcshare.MilloHelpers.MyLog;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Environment;
import android.os.Looper;
import android.os.StatFs;

public class MyAppReporter {

	private Context mContext;
	private static Context mContext1;
	
	private static String SET_EMAIL = "camillodev@gmail.com";

	public MyAppReporter(Context ctx)
	{
		mContext = ctx;
		mContext1 = ctx;
		//Log.i(MyUncaughtException.class.getName(), "MyUncaughtException START");
	}

	private StatFs getStatFs()
	{
		File path = Environment.getDataDirectory();
		return new StatFs(path.getPath());
	}

	private long getAvailableInternalMemorySize(StatFs stat)
	{
		long blockSize = stat.getBlockSize();
		long availableBlocks = stat.getAvailableBlocks();
		return availableBlocks * blockSize;
	}

	private long getTotalInternalMemorySize(StatFs stat)
	{
		long blockSize = stat.getBlockSize();
		long totalBlocks = stat.getBlockCount();
		return totalBlocks * blockSize;
	}

	private void addInformation(StringBuilder message)
	{
		message.append("Locale: ").append(Locale.getDefault()).append('\n');
		try
		{
			PackageManager pm = mContext.getPackageManager();
			PackageInfo pi;
			pi = pm.getPackageInfo(mContext.getPackageName(), 0);
			message.append("Version: ").append(pi.versionName).append('\n');
			message.append("Package: ").append(pi.packageName).append('\n');
		}
		catch ( Exception e )
		{
			MyLog.e("MyAppReporter" + " Error" + e);
			message.append("Could not get Version information for ").append(mContext.getPackageName());
		}
		message.append("Phone Model: ").append(android.os.Build.MODEL).append('\n');
		message.append("Android Version: ").append(android.os.Build.VERSION.RELEASE).append('\n');
		message.append("Board: ").append(android.os.Build.BOARD).append('\n');
		message.append("Brand: ").append(android.os.Build.BRAND).append('\n');
		message.append("Device: ").append(android.os.Build.DEVICE).append('\n');
		message.append("Host: ").append(android.os.Build.HOST).append('\n');
		message.append("ID: ").append(android.os.Build.ID).append('\n');
		message.append("Model: ").append(android.os.Build.MODEL).append('\n');
		message.append("Product: ").append(android.os.Build.PRODUCT).append('\n');
		message.append("Type: ").append(android.os.Build.TYPE).append('\n');
		StatFs stat = getStatFs();
		message.append("Total Internal memory: ").append(getTotalInternalMemorySize(stat)).append('\n');
		message.append("Available Internal memory: ").append(getAvailableInternalMemorySize(stat)).append('\n');
	}

	public void send()
	{
		try{
			StringBuilder report = new StringBuilder();
			Date curDate = new Date();
			report.append('\n').append('\n');
			report.append("**** Start of Report ***").append('\n').append('\n');
			report.append("Report collected on : ").append(curDate.toString()).append('\n').append('\n');
			report.append("Informations :").append('\n');
			addInformation(report);
			report.append('\n').append('\n');
			report.append("**** End of Report ***");

			MyLog.i(MyUncaughtException.class.getName()+" Report:" + report);

			sendMail(report);
		}
		catch(Exception e){
			MyLog.e("MyAppReporter::send Cannot send report");	    		
		}
	}

	/**
	 * This method for call alert dialog when application crashed!
	 */
	private void sendMail(final StringBuilder errorContent)
	{
		final AlertDialog.Builder builder = new AlertDialog.Builder(mContext);
		new Thread()
		{
			public void send(Boolean withLogs){
				Intent sendIntent = new Intent(Intent.ACTION_SEND);
				String subject = "Application Log: "+MilloHelpers.FormatDateTimePrecise(new Date());
				StringBuilder body = new StringBuilder(""); //Application: "+mContext.getApplicationInfo().packageName);
				//body.append('\n').append('\n');
				body.append(errorContent).append('\n').append('\n');
				body.append("\n**********************************\n");
				body.append(mContext.getResources().getString(R.string.feedback_body));
				body.append("\n**********************************\n");
				body.append('\n').append('\n');
				// sendIntent.setType("text/plain");
				sendIntent.setType("message/rfc822");
				sendIntent.putExtra(Intent.EXTRA_EMAIL, new String[] { SET_EMAIL });
				sendIntent.putExtra(Intent.EXTRA_TEXT, body.toString());
				sendIntent.putExtra(Intent.EXTRA_SUBJECT, subject);

				if (withLogs) {
					if (MyLog.getLogPath()!=null){
						Uri uri = Uri.parse("file://" + MyLog.getLogPath());
						sendIntent.putExtra(Intent.EXTRA_STREAM, uri);
					}
				}

				sendIntent.setType("message/rfc822");
				mContext1.startActivity(sendIntent);				
			}
			
			@Override
			public void run()
			{
				Looper.prepare();
				builder.setTitle(mContext.getResources().getString(R.string.feedback_inviareport));
				builder.create();
				builder.setNegativeButton(
						mContext.getResources().getString(R.string.no), new DialogInterface.OnClickListener()
				{
					@Override
					public void onClick(DialogInterface dialog, int which)
					{
						send(false);
						System.exit(0);
					}
				});
				builder.setPositiveButton(
						mContext.getResources().getString(R.string.si), new DialogInterface.OnClickListener()
				{
					@Override
					public void onClick(DialogInterface dialog, int which)
					{
						send(true);
						System.exit(0);
					}
				});
				builder.setMessage(mContext.getResources().getString(R.string.feedback_message));
				builder.show();
				Looper.loop();
			}
		}.start();
	}
}
