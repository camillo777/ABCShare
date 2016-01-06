package com.millo.abcshare;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;

//import com.google.android.gms.ads.AdRequest;
//import com.google.android.gms.ads.AdView;
import com.google.zxing.BarcodeFormat;
import com.google.zxing.WriterException;
import com.millo.abcshare.MilloHelpers.MyLog;

import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.app.Activity;
import android.app.Fragment;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.graphics.Bitmap;
import android.graphics.Point;
import android.net.Uri;
import android.net.nsd.NsdManager;
import android.net.nsd.NsdManager.RegistrationListener;
import android.net.nsd.NsdServiceInfo;
import android.nfc.NdefMessage;
import android.nfc.NdefRecord;
import android.nfc.NfcAdapter;
import android.nfc.NfcAdapter.OnNdefPushCompleteCallback;
import android.nfc.NfcEvent;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.os.Messenger;
import android.os.PowerManager;
import android.support.v4.app.FragmentActivity;
import android.text.method.LinkMovementMethod;
import android.util.Log;
import android.view.Display;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

public class SimpleShareActivity extends FragmentActivity implements
        FragmentHome.OnFragmentHomeInteractionListener,
        FragmentSettings.OnFragmentSettingsInteractionListener
{
	public static final String TAG = "SimpleShareActivity";

//	private static PowerManager.WakeLock wl;
//	private static Boolean wlAcquired = false;

    private Intent intent;

	//	Handler mHandler = null;

	public static Handler messageHandler = new MessageHandler();
	Messenger _messenger; 

//	static TextView tvWebServerStarted;
//	static TextView tvWebServerConnected;
//	static TextView tvURI;
//	AdView mAdView;
//	static ImageView ivShare;
//	static ImageView ivWebServerStarted;
//	static ImageView ivWebServerConnected;
	static ImageView ivNfc;
	static LinearLayout helpView;
	static LinearLayout shareView;
	static Button btnFeedback;
	static TextView tvCredits;
	static TextView tvTitle;

	static String _key = "";
	static ArrayList<String> _val = new ArrayList<String>();
	static HashMap<String, ArrayList<String>> _data = new HashMap<String, ArrayList<String>>();

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		//Log.i("a", "a");
		Log.i(TAG, "onCreate START");

        //Remove title bar
        this.requestWindowFeature(Window.FEATURE_NO_TITLE);

//		PowerManager pm = (PowerManager) getSystemService(Context.POWER_SERVICE);
//		wl = pm.newWakeLock(PowerManager.PARTIAL_WAKE_LOCK, "com.millo.abcshare");
		
		_data.clear();
		_val.clear();

		// Check whether we're recreating a previously destroyed instance
		if (savedInstanceState != null) {
			Log.i(TAG,"Restoring from savedInstanceState");
			// Restore value of members from saved state
			
			_key = savedInstanceState.getString("KEY");
			
			String s = savedInstanceState.getString("VAL");
			_val = parseArrayFromString(s);
			if(_val == null){
				_val.add(s);
			}
						
			_data.put(_key, _val);
			Log.i(TAG,"key: "+_key);
			Log.i(TAG,"val: " + _val);
		}

		setContentView(R.layout.activity_share2);
		getWindow().addFlags(android.view.WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);

		Thread.setDefaultUncaughtExceptionHandler(
				new MyUncaughtException(SimpleShareActivity.this));

		// Check that the activity is using the layout version with
		// the fragment_container FrameLayout
		if (findViewById(R.id.fragment_container) != null) {

			// However, if we're being restored from a previous state,
			// then we don't need to do anything and should return or else
			// we could end up with overlapping fragments.
			if (savedInstanceState != null) {
				return;
			}

			SetFragment_Home();
		}

//		mAdView = (AdView) findViewById(R.id.adView);
//		//mAdView.setAdListener(new MyAdListener());
//		// Create an ad request. Check logcat output for the hashed device ID to
//		// get test ads on a physical device.
//		AdRequest adRequest = new AdRequest.Builder()
//		.addTestDevice(AdRequest.DEVICE_ID_EMULATOR)
//		//		    .addTestDevice("INSERT_YOUR_HASHED_DEVICE_ID_HERE")
//		.tagForChildDirectedTreatment(true)
//		.build();
//		// Start loading the ad in the background.
//		mAdView.loadAd(adRequest);

//		tvWebServerStarted = (TextView) findViewById(R.id.tvWebServerStarted);
//		tvWebServerConnected = (TextView) findViewById(R.id.tvWebServerConnected);


//		tvURI = (TextView) findViewById(R.id.tvURI);
//
//		ivWebServerStarted = (ImageView) findViewById(R.id.ivWebServerStarted);
//		ivWebServerConnected = (ImageView) findViewById(R.id.ivWebServerConnected);
//		ivNfc = (ImageView) findViewById(R.id.ivNfc);
//
//		ivShare = (ImageView) findViewById(R.id.ivShare);
//		ivShare.setOnClickListener(new OnClickListener() {
//			@Override
//			public void onClick(View v) {
//				Intent sendIntent = new Intent();
//				sendIntent.setAction(Intent.ACTION_SEND);
//				sendIntent.putExtra(Intent.EXTRA_TEXT, tvURI.getText());
//				sendIntent.setType("text/plain");
//				//sendIntent.addFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
//				sendIntent.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
//				startActivity(sendIntent);
//			}
//		});
//
//		helpView = (LinearLayout) findViewById(R.id.help);
//		helpView.setVisibility(View.GONE);
////		shareView = (LinearLayout) findViewById(R.id.share);
////		shareView.setVisibility(View.GONE);
//
//		btnFeedback = (Button) findViewById(R.id.btnFeedback);
//		btnFeedback.setOnClickListener(new OnClickListener() {
//			@Override
//			public void onClick(View v) {
//				new MyAppReporter(SimpleShareActivity.this).send();
//			}
//		});
//
//		tvTitle = (TextView) findViewById(R.id.tvTitle);
//		PackageManager pm = getApplicationContext().getPackageManager();
//		PackageInfo pi;
//		try {
//			pi = pm.getPackageInfo(getApplicationContext().getPackageName(), 0);
//			tvTitle.setText(""+pi.packageName+" "+
//					getApplicationContext().getResources().getString(R.string.version)+" "+pi.versionName);
//		} catch (NameNotFoundException e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		}
//
//		tvCredits = (TextView) findViewById(R.id.tvCredits);
//		//tvCredits.setText(Html.fromHtml(source));
//		tvCredits.setMovementMethod(LinkMovementMethod.getInstance());

		_messenger = new Messenger(messageHandler);
        MessageHandler.mActivity = this;

        ProcessIntent(getIntent());

        intent = new Intent(this, MainService.class);
        startService(intent);

		//		_files = new HashMap<String, File>();

		//		mHandler = new Handler(Looper.getMainLooper()) {
		//			@Override
		//			public void handleMessage(Message inputMessage) {
		//				// Gets the task from the incoming Message object.
		//				WebServerTask photoTask = (WebServerTask) inputMessage.obj;
		//				// Gets the ImageView for this task
		//				switch (inputMessage.what) {
		//				// The decoding is done
		//				case Settings.TASK_COMPLETE:
		//					/*
		//					 * Moves the Bitmap from the task
		//					 * to the View
		//					 */
		//					//localView.setImageBitmap(photoTask.getImage());
		//					photoTask.tv.setText(photoTask.s);
		//					break;
		//				case Settings.WEBSERVER_STARTED:
		//					NotifyTaskbar("title", "text");
		//					break;
		//				default:
		//					/*
		//					 * Pass along other messages from the UI
		//					 */
		//					super.handleMessage(inputMessage);
		//				}
		//			}
		//		};

		//Intent intent = getIntent();
		//Toast.makeText(getApplicationContext(), intent.getAction(), Toast.LENGTH_LONG).show();
		Log.i(TAG,"onCreate STOP");
	}

    private BroadcastReceiver broadcastReceiver = new BroadcastReceiver() {
        public static final String TAG = "BroadcastReceiver";

        @Override
        public void onReceive(Context context, Intent intent) {
            Log.d(TAG,"onReceive");

            int type = intent.getIntExtra("type", 0);
            String s = intent.getStringExtra("value");
            FragmentHome f = (FragmentHome)getFragmentManager().findFragmentByTag(FragmentHome.TAG);

            Log.d(TAG,"type:"+type+" value:"+s);
            if (s==null) return;

            switch(type){
                case Settings.TYPE_CONNECTED:
                    f.GUI_SetConnected(s.contentEquals("1")?true:false);
                    break;
                case Settings.TYPE_STARTED:
                    f.GUI_SetStarted(s.contentEquals("1")?true:false);
                    break;
                case Settings.TYPE_LINK:
                    f.GUI_SetURI(s);
                    f.GUI_SetShare(s);
                    f.GUI_SetURI_QRCode(s, getWindowManager().getDefaultDisplay());

                    if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.JELLY_BEAN) {
                        NsdService(Settings.WEBSERVER_PORT);
                        Nfc(s);
                    }

                    break;
                case Settings.TYPE_ACTIVE:
                    break;
                case Settings.TYPE_SERVING:
                    break;
                case Settings.TYPE_DATA:
                    f.GUI_SetData(s);
                    break;
            }
        }
    };

    @SuppressLint("NewApi")
	void ProcessIntent(Intent intent) {

//		
//		Uri data = intent.getData();
//		if (data!=null){
//			// content://media/external/images/media/13488
//			//			data.get
//			//Uri uri = Uri.fromFile(file);
//			ContentResolver cR = getContentResolver();
//			String s = cR.getType(data);
//			Log.i(TAG,"getType: "+s);
//
//			ProcessIntentData(intent);
//		}
//		else{      
//			Log.i(TAG,"intent.getData() is null");
//
//			ProcessIntentData(intent);
//		}	

        //		String filePath = null;
        //		if (data!=null){
        //			//tvURI.setText(data.toString());
        //			Log.i(TAG,"data: "+data.toString());
        //			filePath = MilloHelpers.getFilePathByUri(getApplicationContext(),data);
        //			Log.i(TAG,"filePath: "+filePath);
        //			tvFile.setText(filePath);
        //		}

        //		SessionIdentifierGenerator sig = new SessionIdentifierGenerator();
        //		String token = sig.nextSessionId();
        //		Log.i(TAG,"token: "+token);

        //		_files = Helpers.loadMap(getBaseContext());
        //		_files.put(token, new File(filePath));
        //		Helpers.saveMap(getBaseContext(),_files);

        //		_files.put(token, new File(filePath));

        ProcessIntentData(intent);
        if (_data.size() <= 0) {
            Log.i(TAG, "-------------------> NO DATA!!!!");
//			helpView.setVisibility(View.VISIBLE);
//			shareView.setVisibility(View.GONE);
//			return;
        } else {

//		try {
////			MyWebSocket mws = new MyWebSocket(new URI("ws://js-camillorh.rhcloud.com:8000"));
//			MyWebSocket mws = new MyWebSocket(new URI("ws://w-47862.onmodulus.net"));
//			mws.connect();
////			mws.send("android");
//		} catch (URISyntaxException e1) {
//			// TODO Auto-generated catch block
//			e1.printStackTrace();
//		}

        Log.i(TAG, "-------------------> YES DATA!!!!");
//		helpView.setVisibility(View.GONE);
//		shareView.setVisibility(View.VISIBLE);
    }


//		//String url = "http://"+MilloHelpers.getLocalIpAddress()+":8080/"+token;
//		String url = "http://"+MilloHelpers.getLocalIpAddress()+":8080";
//		TextView tvURI = (TextView)findViewById(R.id.tvURI);
//		if (tvURI!=null) tvURI.setText(url);
//
//		// ImageView to display the QR code in.  This should be defined in
//		// your Activity's XML layout file
//		ImageView imageView = (ImageView) findViewById(R.id.ivBarcode);
//
//		if (imageView!=null) {
//			//String qrData = "Data I want to encode in QR code";
//			int qrCodeDimention = 500;
//
//			//		Display display = getWindowManager().getDefaultDisplay();
//			//		Point size = new Point();
//
//			int width = 0;
//			int height = 0;
//			Display display = getWindowManager().getDefaultDisplay();
//			Point size = new Point();
//			if (android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB_MR2){
//				display.getSize(size);
//				width = size.x;
//				height = size.y;
//			}
//			else{
//				width = display.getWidth();  // deprecated
//				height = display.getHeight();  // deprecated
//			}
//
//			qrCodeDimention = width;
//
//			QRCodeEncoder qrCodeEncoder = new QRCodeEncoder(url, null,
//					Contents.Type.TEXT, BarcodeFormat.QR_CODE.toString(), qrCodeDimention);
//
//			try {
//				Bitmap bitmap = qrCodeEncoder.encodeAsBitmap();
//				imageView.setImageBitmap(bitmap);
//			} catch (WriterException e) {
//				e.printStackTrace();
//			}
//		}
//
//		if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.JELLY_BEAN) {
//			NsdService(Settings.WEBSERVER_PORT);
//			Nfc(url);
//		}
	}

	//	// Handle status messages from tasks
	//	public void handleState(WebServerTask photoTask, int state) {
	//		Log.i(TAG,"SimpleShareActivity::handleState START");
	//		switch (state) {
	//		// The task finished downloading and decoding the image
	//		case Settings.TASK_COMPLETE:
	//			Log.i(TAG,"TASK_COMPLETE recv");
	//			/*
	//			 * Creates a message for the Handler
	//			 * with the state and the task object
	//			 */
	//			Message completeMessage =
	//			mHandler.obtainMessage(state, photoTask);
	//			completeMessage.sendToTarget();
	//			break;
	//		}
	//		Log.i(TAG,"SimpleShareActivity::handleState START");
	//	}

	//	void NotifyTaskbar(String title, String text, Boolean on){
	//		Log.i(TAG,"SimpleShareActivity::NotifyTaskbar START");
	//		NotificationManager mNotificationManager =
	//				(NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
	//
	//		NotificationCompat.Builder mBuilder =
	//				new NotificationCompat.Builder(getApplicationContext())
	//		.setSmallIcon(on?R.drawable.ic_launcher:R.drawable.ic_launcher)
	//		.setContentTitle(title)
	//		.setContentText(text);
	//		// Creates an explicit intent for an Activity in your app
	//		Intent resultIntent = new Intent(getApplicationContext(), SimpleShareActivity.class);
	//
	//		// The stack builder object will contain an artificial back stack for the
	//		// started Activity.
	//		// This ensures that navigating backward from the Activity leads out of
	//		// your application to the Home screen.
	//		TaskStackBuilder stackBuilder = TaskStackBuilder.create(getApplicationContext());
	//		// Adds the back stack for the Intent (but not the Intent itself)
	//		stackBuilder.addParentStack(SimpleShareActivity.class);
	//		// Adds the Intent that starts the Activity to the top of the stack
	//		stackBuilder.addNextIntent(resultIntent);
	//		PendingIntent resultPendingIntent =
	//				stackBuilder.getPendingIntent(
	//						0,
	//						PendingIntent.FLAG_UPDATE_CURRENT
	//						);
	//		mBuilder.setContentIntent(resultPendingIntent);
	//		// mId allows you to update the notification later on.
	//		mNotificationManager.notify(10, mBuilder.build());	
	//		Log.i(TAG,"SimpleShareActivity::NotifyTaskbar START");
	//	}

	//	//	@SuppressLint("NewApi")
	//	void NotifyTaskbar(String title, String text){
	//		NotificationManager mNotificationManager =
	//				(NotificationManager) getApplicationContext().getSystemService(Context.NOTIFICATION_SERVICE);
	//
	//		NotificationCompat.Builder mBuilder =
	//				new NotificationCompat.Builder(getBaseContext())
	//		.setSmallIcon(R.drawable.ic_launcher)
	//		.setContentTitle(title)
	//		.setContentText(text)
	//		.setOngoing(true);
	//
	//		Intent resultIntent = new Intent(getApplicationContext(), SimpleShareActivity.class);
	//		//		if(Build.VERSION.SDK_INT >= 16 ){
	//		//			// Creates an explicit intent for an Activity in your app
	//		//
	//		//			// The stack builder object will contain an artificial back stack for the
	//		//			// started Activity.
	//		//			// This ensures that navigating backward from the Activity leads out of
	//		//			// your application to the Home screen.
	//		//			TaskStackBuilder stackBuilder = TaskStackBuilder.create(getApplicationContext());
	//		//			// Adds the back stack for the Intent (but not the Intent itself)
	//		//			stackBuilder.addParentStack(SimpleShareActivity.class);
	//		//			// Adds the Intent that starts the Activity to the top of the stack
	//		//			stackBuilder.addNextIntent(resultIntent);
	//		//			PendingIntent resultPendingIntent =
	//		//					stackBuilder.getPendingIntent(
	//		//							0,
	//		//							PendingIntent.FLAG_UPDATE_CURRENT
	//		//							);
	//		//			mBuilder.setContentIntent(resultPendingIntent);
	//		//		}
	//		//		else{
	//		PendingIntent contentIntent = PendingIntent.getActivity(this, 0, resultIntent, 0);
	//		mBuilder.setContentIntent(contentIntent);
	//		//		}
	//		mNotificationManager.notify(Settings.NOTIFICATION_ID, mBuilder.build());		
	//	}
	//	void cancelNotification(){
	//		NotificationManager mNotificationManager =
	//				(NotificationManager) getApplicationContext().getSystemService(Context.NOTIFICATION_SERVICE);
	//		mNotificationManager.cancel(Settings.NOTIFICATION_ID);
	//	}


	public void ProcessIntentData(Intent intent){

		Uri uriData = intent.getData();
		if (uriData!=null){
			Log.i(TAG,"uriData: "+uriData);
			// uriData: file:///storage/emulated/0/Download/026_PDP_AC_130315.pdf
			// content://media/external/images/media/13488	
			ContentResolver cR = getContentResolver();
			String s = cR.getType(uriData);
			Log.i(TAG,"getType: "+s);
			
			//String file1 = MilloHelpers.UriResolver.getPath(getApplicationContext(), uriData);
			//if(Settings.DEBUG)Log.i(TAG,"MyWebServer::respond filePath: "+file1);
			
			_key = Settings.INTENTEXTRA_STREAM;
			_val.add(uriData.toString());
			
			_data.clear();
			_data.put(_key, _val);
			
			return;
		}

		//Toast.makeText(getApplicationContext(), intent.getExtras().toString(), Toast.LENGTH_LONG).show();
		Bundle b = intent.getExtras();
		if (b==null) {
			Log.i(TAG,"intent.getExtras() is null");
			return;
		}
		else{

			// [Extras]
			// key:android.intent.extra.STREAM 
			// val:[content://media/external/images/media/4055, content://media/external/images/media/4054, content://media/external/images/media/4053]

			if (Settings.DEBUG)
			for (String key : b.keySet())
			{
				// [Extras]
				// key:android.intent.extra.STREAM
				// val:content://media/external/images/media/13485

				Log.i(TAG,"[Extras] key:"+key+" val:"+b.get(key));
				//Toast.makeText(getApplicationContext(), key + " = \"" + b.get(key) + "\"", Toast.LENGTH_LONG).show();
			}

			// *********** COPIED TXT
			// key:android.intent.extra.TEXT val:if filling up quickly, bu...

			//				String key = "";
			//				String val = "";

			if (b.get(Settings.INTENTEXTRA_STREAM)!=null){
				_key = Settings.INTENTEXTRA_STREAM;
				String stream = b.get(Settings.INTENTEXTRA_STREAM).toString();
				_val = parseArrayFromString(stream);
				if(_val == null){
					_val = new ArrayList<String>();
					_val.add(stream);
				}
				//					_data.put(_key, _val);
			}
			else if (b.get(Settings.INTENTEXTRA_TEXT)!=null){
				_key = Settings.INTENTEXTRA_TEXT;
				_val.add(b.get(Settings.INTENTEXTRA_TEXT).toString());
				//					_data.put(_key, _val);
			}
			else {
				MyLog.e("unknown/cannot parse data");
				return;
			}

			_data.clear();
			_data.put(_key, _val);

			Log.i(TAG,"_key:"+_key);
			Log.i(TAG,"_val:"+_val);

			//			TextView tvKey = (TextView)findViewById(R.id.tvKey);
			//			if (tvKey!=null) tvKey.setText(_key);
			//			TextView tvVal = (TextView)findViewById(R.id.tvVal);
			//			if (tvVal!=null) tvVal.setText(_val);

			//			data = Uri.parse(b.get("android.intent.extra.STREAM").toString());
		}
	}

	public static ArrayList<String> parseArrayFromString(String a){
		ArrayList<String> al = null;
		if (a.startsWith("[")){
			al = new ArrayList<String>();
			// array
			a = a.replace("[", "");
			a = a.replace("]", "");
			String[] sa = a.split(",");
			for(String s:sa){
				al.add(s.trim());
			}
			return al;
		}
		return al;
	}

    @Override
    public void onFragmentHomeInteraction_OpenFragmentSettings() {

    }

//    @Override
//    public void onFragmentHomeInteraction_BindService() {
////        doBindService();
//    }
//
//    @Override
//    public void onFragmentHomeInteraction_UnbindService() {
////        doUnbindService();
//    }

    @Override
    public void onFragmentSettingsInteraction(Uri uri) {

    }

    public static class MessageHandler extends Handler {
        public static Activity mActivity;

        @Override
		public void handleMessage(Message message) {
			if (message.getData().containsKey("state")){
                int state = message.getData().getInt("state");
				Log.i(TAG, "MessageHandler::handleMessage state received "+state);
                FragmentHome f = (FragmentHome)mActivity.getFragmentManager().findFragmentByTag(FragmentHome.TAG);

				switch(state){
				case Settings.WEBSERVER_STARTED:
//					if (tvWebServerStarted!=null) tvWebServerStarted.setText("web server started");
//					if (ivWebServerStarted!=null) {
//						ivWebServerStarted.setImageURI(null);
//						ivWebServerStarted.setImageResource(R.drawable.led_gre);
//					}

                    if (f!=null){
                        f.GUI_SetStarted(true);
                    }
					break;
				case Settings.WEBSERVER_STOPPED:
//					if (tvWebServerStarted!=null) tvWebServerStarted.setText("web server stopped");
//					if (ivWebServerStarted!=null) {
//						ivWebServerStarted.setImageURI(null);
//						ivWebServerStarted.setImageResource(R.drawable.led_red);
//					}

                    if (f!=null){
                        f.GUI_SetStarted(false);
                    }
                    break;
				case Settings.WEBSERVER_CONNECTED:
//					if (tvWebServerConnected!=null) tvWebServerConnected.setText("new connection");
//					if (ivWebServerConnected!=null) {
//						ivWebServerConnected.setImageURI(null);
//						ivWebServerConnected.setImageResource(R.drawable.led_gre);
//					}
                    if (f!=null){
                        f.GUI_SetConnected(true);
                    }
					break;
				case Settings.WEBSERVER_DISCONNECTED:
//					if (tvWebServerConnected!=null) tvWebServerConnected.setText("new disconnection");
//					if (ivWebServerConnected!=null) {
//						ivWebServerConnected.setImageURI(null);
//						ivWebServerConnected.setImageResource(R.drawable.led_red);
//					}
                    if (f!=null){
                        f.GUI_SetConnected(false);
                    }
					break;
				}
			}
		}
	}

	/* (non-Javadoc)
	 * @see android.app.Activity#onResume()
	 */
	@Override
	protected void onResume() {
		Log.i(TAG,"onResume START");
//		if (!wlAcquired) acquireLock();

		// TODO Auto-generated method stub
		super.onResume();
        registerReceiver(broadcastReceiver, new IntentFilter(MainService.BROADCAST_ACTION));

//		if (mAdView != null) {
//			mAdView.resume();
//		}

		//		Bundle b = getIntent().getExtras();
		//		if (b!=null){
		//			for (String key : b.keySet())
		//			{
		//				Log.i(TAG,"[Extras] key:"+key+" val:"+b.get(key));
		//				//Toast.makeText(getApplicationContext(), key + " = \"" + b.get(key) + "\"", Toast.LENGTH_LONG).show();
		//			}
		//		}
		
		//ProcessIntent(getIntent());
		
		//getIntent().getData();

//		doBindService();
		
		Log.i(TAG,"onResume STOP");
	}

	/* (non-Javadoc)
	 * @see android.app.Activity#onPause()
	 */
	@Override
	protected void onPause() {
		Log.i(TAG,"onPause START");
//		if (wlAcquired) releaseLock();

		// TODO Auto-generated method stub
//		if (mAdView != null) {
//			mAdView.pause();
//		}
		super.onPause();
        unregisterReceiver(broadcastReceiver);
        Log.i(TAG,"onPause STOP");
	}

//	public static void acquireLock(){
//		wl.acquire();
//		wlAcquired = true;
//	}
//	public static void releaseLock(){
//		wl.release();
//		wlAcquired = false;
//	}

	/* (non-Javadoc)
	 * @see android.app.Activity#onDestroy()
	 */
	@Override
	protected void onDestroy() {
		Log.i(TAG,"onDestroy START");

//		if (mAdView != null) {
//			mAdView.destroy();
//		}



		//KillEverything();
//        doUnbindService();
		super.onDestroy();
		Log.i(TAG,"onDestroy STOP");
	}
	
	@Override
	public void onSaveInstanceState(Bundle savedInstanceState) {
		Log.i(TAG,"onSaveInstanceState START");

		// Always call the superclass so it can save the view hierarchy state
		super.onSaveInstanceState(savedInstanceState);

		// Save the user's current game state
		savedInstanceState.putString("KEY", _key);
		savedInstanceState.putString("VAL", _val.toString());

		Log.i(TAG,"key: "+savedInstanceState.getString("KEY"));
		Log.i(TAG,"val: " + savedInstanceState.getString("VAL"));

		Log.i(TAG,"onSaveInstanceState STOP");
	}

	@Override
	public void onRestoreInstanceState(Bundle savedInstanceState) {
		Log.i(TAG,"SimpleShareActivity::onRestoreInstanceState START");

		// Always call the superclass so it can restore the view hierarchy
		super.onRestoreInstanceState(savedInstanceState);

		//	    // Restore state members from saved instance
		//	    _data.clear();
		//	    _key = savedInstanceState.getString("KEY");
		//	    _val = savedInstanceState.getString("VAL");
		//	    _data.put(_key, _val);

		Log.i(TAG,"SimpleShareActivity::onRestoreInstanceState STOP");
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		MenuInflater inflater = getMenuInflater();
		inflater.inflate(R.menu.main_menu, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// Handle item selection
		switch (item.getItemId()) {
		case R.id.help:
			Log.i(TAG,"onOptionsItemSelected help");
			return true;
		default:
			return super.onOptionsItemSelected(item);
		}
	}

	public static long _backPressedMsecs = 0;

	@Override
	public void onBackPressed() {
		Log.d(TAG, "onBackPressed Called");

		if (GetCurrentFragment() instanceof FragmentHome) {
			Log.d(TAG, "I am on the home fragment");

			long nowTime = android.os.SystemClock.uptimeMillis();
			long elapsedTime = nowTime - _backPressedMsecs;

			if (elapsedTime<4000) {
//				KillEverything();
                stopService();
				this.finish();
			}
			else{
				_backPressedMsecs = nowTime;
				Toast.makeText(getApplicationContext(), R.string.press_back_again_to_exit, Toast.LENGTH_SHORT).show();
			}
		}
		else{
			Log.d(TAG, "I am NOT the home fragment");
			super.onBackPressed();
		}
	}

	//////////// FRAGMENT
	public void SetFragment(Fragment f, String tag){
		Log.d(TAG, "SetFragment START");
		android.app.FragmentTransaction ft = getFragmentManager().beginTransaction();
		ft.replace(R.id.fragment_container, f, tag);
		ft.setTransition(android.app.FragmentTransaction.TRANSIT_FRAGMENT_OPEN);
		ft.addToBackStack(null);
		ft.commit();

		Log.d(TAG, "SetFragment getBackStackEntryCount=" + getFragmentManager().getBackStackEntryCount());
		Log.d(TAG, "SetFragment STOP");
	}
	public void SetFragment_Home(){
		Log.d(TAG, "SetFragment_Home START");

		Fragment f = getFragmentManager().findFragmentByTag(FragmentHome.TAG);
		if (f==null){
			Log.d(TAG, "Creating new FragmentHome");
			f = new FragmentHome();
		}
		SetFragment(f, FragmentHome.TAG);

		Log.d(TAG, "SetFragment_Home STOP");
	}
//	public void SetFragment_Select(){
//		Log.d(TAG, "SetFragment_Select START");
//
//		Fragment f = getFragmentManager().findFragmentByTag(FragmentSelect.TAG);
//		if (f==null){
//			Log.d(TAG, "Creating new FragmentSelect");
//			f = new FragmentSelect();
//		}
//		SetFragment(f, FragmentSelect.TAG);
//
//		Log.d(TAG, "SetFragment_Select STOP");
//	}
	public void SetFragment_Settings(){
		Log.d(TAG, "SetFragment_Settings START");

		Fragment f = getFragmentManager().findFragmentByTag(FragmentSettings.TAG);
		if (f==null){
			Log.d(TAG, "Creating new FragmentSettings");
			f = new FragmentSettings();
		}
		SetFragment(f, FragmentSettings.TAG);

		Log.d(TAG, "SetFragment_Settings STOP");
	}

	Fragment GetCurrentFragment(){
		return getFragmentManager().findFragmentById(R.id.fragment_container);
	}

	///////////////////// Services

//	private boolean mIsBound = false;
//	private MainService mBoundService;
//	void doBindService() {
//        Log.d(TAG, "doBindService");
//		// Establish a connection with the service.  We use an explicit
//		// class name because we want a specific service implementation that
//		// we know will be running in our own process (and thus won't be
//		// supporting component replacement by other applications).
//		bindService(
//				new Intent(getApplicationContext(), MainService.class),
//				mConnection,
//				Context.BIND_AUTO_CREATE
//		);
//		mIsBound = true;
//	}
//
//	void doUnbindService() {
//        Log.d(TAG, "doUnbindService");
//		if (mIsBound) {
//			// Detach our existing connection.
//			unbindService(mConnection);
//			mIsBound = false;
//		}
//	}
//
//	private ServiceConnection mConnection = new ServiceConnection() {
//		public void onServiceConnected(ComponentName className, IBinder service) {
//			// This is called when the connection with the service has been
//			// established, giving us the service object we can use to
//			// interact with the service.  Because we have bound to a explicit
//			// service that we know is running in our own process, we can
//			// cast its IBinder to a concrete class and directly access it.
//			mBoundService = ((MainService.MainServiceBinder)service).getService();
//			//mBoundService.setAppName(R.string.app_name);
//			//mBoundService.setLocale(getResources().getConfiguration().locale);
//			mBoundService.serviceLoop(new Messenger(messageHandler));
//			//mBoundService.setPreset(PlayService.MyPreset.Horror);
//
//			// Tell the user about this for our demo.
//			//Toast.makeText(getApplicationContext(), "Service connected",Toast.LENGTH_SHORT).show();
//			//StartStop(false);
//            Log.d(TAG, "onServiceConnected");
//		}
//
//		public void onServiceDisconnected(ComponentName className) {
//			// This is called when the connection with the service has been
//			// unexpectedly disconnected -- that is, its process crashed.
//			// Because it is running in our same process, we should never
//			// see this happen.
//			mBoundService = null;
//			//Toast.makeText(getApplicationContext(), "Service disconnected",Toast.LENGTH_SHORT).show();
//			//StartStop(true);
//            Log.d(TAG, "onServiceDisconnected");
//		}
//	};
//
//	void KillEverything(){
//        Log.d(TAG, "KillEverything");
//		if (mBoundService!=null)
//			mBoundService.myStopService();
//		stopService();
//		doUnbindService();
//	}
//
	// Stop the  service
	public void stopService() {
        Log.d(TAG, "stopService");
		stopService(new Intent(this, MainService.class));
		//stopService(serv);
	}


    NfcAdapter nfc = null;

    @TargetApi(Build.VERSION_CODES.JELLY_BEAN)
    void Nfc(String url){
        Log.i(TAG,"SimpleShareActivity::Nfc START");
//		ImageView ivNfc = (ImageView)findViewById(R.id.ivNfc);
//        if (ivNfc!=null) {
////			ivNfc.setVisibility(View.GONE);
//            ivNfc.setImageResource(R.drawable.led_red);
//        }

        nfc = NfcAdapter.getDefaultAdapter(this);// (only need to do this once)
        if (nfc != null) { // in case there is no NFC
            // create an NDEF message containing the current URL:
            NdefRecord rec = NdefRecord.createUri(url); // url: current URL (String or Uri)
            NdefMessage ndef = new NdefMessage(rec);
            // make it available via Android Beam:
            if (nfc.isNdefPushEnabled()){
                nfc.setOnNdefPushCompleteCallback(new  NfcAdapter.OnNdefPushCompleteCallback() {

                    @Override
                    public void onNdefPushComplete(NfcEvent event) {
                        Log.i(TAG,"NFC delivered");
                    }
                }, this);
                nfc.setNdefPushMessage(ndef, this);
//                if (ivNfc!=null) {
//                    //ivNfc.setVisibility(View.VISIBLE);
//                    ivNfc.setImageResource(R.drawable.led_gre);
//                }
            }
        }
        Log.i(TAG,"SimpleShareActivity::Nfc END");
    }

    NsdManager.RegistrationListener mRegistrationListener;
    NsdManager mNsdManager;
    @TargetApi(Build.VERSION_CODES.JELLY_BEAN)
    // NSD
    public void NsdService(int port) {
        Log.i(TAG,"SimpleShareActivity::NsdService START " + port);

        mRegistrationListener = new NsdManager.RegistrationListener() {

            String mServiceName = "";

            @Override
            public void onServiceRegistered(NsdServiceInfo NsdServiceInfo) {
                // Save the service name.  Android may have changed it in order to
                // resolve a conflict, so update the name you initially requested
                // with the name Android actually used.
                mServiceName = NsdServiceInfo.getServiceName();
                Log.i(TAG,"onServiceRegistered " + mServiceName);
            }

            @Override
            public void onRegistrationFailed(NsdServiceInfo serviceInfo, int errorCode) {
                // Registration failed!  Put debugging code here to determine why.
                Log.i(TAG,"onRegistrationFailed");
            }

            @Override
            public void onServiceUnregistered(NsdServiceInfo arg0) {
                // Service has been unregistered.  This only happens when you call
                // NsdManager.unregisterService() and pass in this listener.
                Log.i(TAG,"onServiceUnregistered");
            }

            @Override
            public void onUnregistrationFailed(NsdServiceInfo serviceInfo, int errorCode) {
                // Unregistration failed.  Put debugging code here to determine why.
                Log.i(TAG,"onUnregistrationFailed");
            }
        };

        NsdServiceInfo serviceInfo  = new NsdServiceInfo();
        serviceInfo.setServiceName("ABCShare");
        serviceInfo.setServiceType("_http._tcp.");
        serviceInfo.setPort(port);

        mNsdManager = (NsdManager)getSystemService(Context.NSD_SERVICE);

        mNsdManager.registerService(
                serviceInfo, NsdManager.PROTOCOL_DNS_SD, mRegistrationListener);

        Log.i(TAG,"SimpleShareActivity::NsdService END");
    }

}
