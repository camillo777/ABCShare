package com.millo.abcshare;

import android.annotation.TargetApi;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.net.nsd.NsdManager;
import android.net.nsd.NsdServiceInfo;
import android.nfc.NdefMessage;
import android.nfc.NdefRecord;
import android.nfc.NfcAdapter;
import android.nfc.NfcEvent;
import android.os.Binder;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.IBinder;
import android.os.Message;
import android.os.Messenger;
import android.os.RemoteException;
import android.os.SystemClock;
import android.support.v4.app.NotificationCompat;
import android.util.Log;
import android.widget.Toast;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Locale;
import java.util.Random;

public class MainService extends Service {

    private static final String TAG = "ABCShare MainService";
    public static final String BROADCAST_ACTION = "com.millo.abcshare";
    Intent intent;
    int counter = 0;

    //	private Boolean play = false;
    //	SoundPool sp;
    //	int sndId_Forest = -1;

    //	final int LOOP_FOREVER = -1;
    //	final int MAX_STREAMS = 10;

    final int NOTIFICATION_ID = 11;

    private static int THREAD_SLEEP = 5000;

    //private AudioTrackSoundPlayer soundPlayer = null;
    private Messenger _messageHandler;
    private Boolean _serviceStop = false;
    private Boolean _serviceRunning = false;

    private Thread mServiceThread = null;

    static MyWebServer sws = null;
    //	HashMap<String, File> _files = null;

    //static HashMap<String, ArrayList<String>> _data = new HashMap<String, ArrayList<String>>();

    public class MainServiceBinder extends Binder {
        MainService getService() {
            Log.d(TAG, "MainServiceBinder::getService");
            return MainService.this;
        }
    }

    @Override
    public IBinder onBind(Intent intent) {
        Log.d(TAG, "MainServiceBinder::onBind");
        return mBinder;
    }

    // This is the object that receives interactions from clients.  See
    // RemoteService for a more complete example.
    private final IBinder mBinder = new MainServiceBinder();

    @Override
    public void onCreate() {
        Log.i(TAG, "onCreate");
        //Toast.makeText(this, "The new Service was Created", Toast.LENGTH_SHORT).show();

        //		 sp = new SoundPool(MAX_STREAMS, AudioManager.STREAM_MUSIC, 0);
        //		 sp.setOnLoadCompleteListener(new OnLoadCompleteListener()
        //		 {
        //		    @Override
        //		    public void onLoadComplete(SoundPool soundPool, int sampleId,int status) {
        //		    	Log.i("SoundPool::onLoadComplete", "status:"+status);
        //		    	//if (status == 0) Play();
        //		    	play = true;
        //		    }
        //		 });
        //
        //		sndId_Forest = sp.load(getApplicationContext(), R.raw.snd_forest, 0);
        //		Log.i("onCreate", "sndId_Forest:"+sndId_Forest);

        Log.d(TAG, "onCreate");
        intent = new Intent(BROADCAST_ACTION);
        //soundPlayer = new AudioTrackSoundPlayer();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Log.i(TAG, "onStartCommand");

//        if(!_serviceRunning) {
//            _serviceRunning = true;
//            Log.d(TAG, "Received start id " + startId + ": " + intent);
//            Bundle extras = intent.getExtras();
//            _messageHandler = (Messenger) extras.get("MESSENGER");
//
//            //serviceLoop(_messageHandler);
//        }

        serviceLoop(_messageHandler);

        return START_NOT_STICKY;
    }

    public void myStopService(){
        Log.d(TAG, "myStopService");
        _serviceStop = true;
    }

    public void serviceLoop(Messenger messageHandler){

        Log.i(TAG, "serviceLoop");
        // For time consuming an long tasks you can launch a new thread here...
        //Toast.makeText(this, "Service Started", Toast.LENGTH_SHORT).show();

        _messageHandler = messageHandler;

//        sendState(Settings.WEBSERVER_STARTED);
//        sendCommand("start");
        sendGUI(intent, Settings.TYPE_STARTED, "1");

        //NotifyTaskbar("title", "text");

        if (mServiceThread!=null){
            Log.i(TAG, "Thread already active");
            return;
        }
        mServiceThread = new Thread()
        {
            @Override
            public void run() {
                Log.i(TAG, "Thread::run");

                if (sws==null){
                    String url = "http://"+MilloHelpers.getLocalIpAddress()+":8080";
                    Log.i(TAG, url);
                    sws = new MyWebServer(
                            getBaseContext(),
                            //new WebServerTask(this),
                            _messageHandler,
                            intent,
                            //					"127.0.0.1",
                            //					MilloHelpers.getLocalIpAddress(),
                            null,
                            Settings.WEBSERVER_PORT,
                            new HashMap<String, ArrayList<String>>(),
                            //_data,
                            //					_files,
                            Environment.getExternalStorageDirectory(),
                            Settings.DEBUG);
                    try {
                        Log.i(TAG,"Starting web server");
                        sws.start();
                        sendGUI(intent, Settings.TYPE_LINK, url);
                    } catch (IOException e) {
                        // TODO Auto-generated catch block
                        e.printStackTrace();
                    }
                }
                else{
                    Log.i(TAG,"web server already active");
                    if (!sws.isAlive()) {
                        Log.i(TAG,"web server is NOT alive!!!");
                    }
                    //				else
                    //					NotifyTaskbar(Settings.NOTIFICATION_TITLE, Settings.NOTIFICATION_TEXT);
                }


                try {
//                    android.os.Process.setThreadPriority(android.os.Process.THREAD_PRIORITY_URGENT_AUDIO);
//                    Log.i(TAG, "mServiceThread::run");
//
//                    long prevtime = SystemClock.uptimeMillis();
//                    float deltatime = 0;
//                    long temp = 0;
//
                    while(!_serviceStop) {

                        sendGUI(intent, Settings.TYPE_DATA, "" + counter++);

                        NotifyTaskbar("ABCShare WebServer alive");
                        Log.i(TAG, "ABCShare WebServer alive");
                        sleep(THREAD_SLEEP);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
                Log.i(TAG, "stopping service...");
                cancelNotification();
                stopSelf();
                //if (_playService!=null) _playService.sendState("MAIN Thread stopped");
            }
        };

        mServiceThread.setPriority(Thread.MAX_PRIORITY);
        mServiceThread.start();
    }

    @Override
    public void onDestroy() {
        Log.d(TAG, "onDestroy");
        //    	if (sp != null) sp.release();

//        _serviceStop = true;
//		soundPlayer.stopAll();

        if (sws!=null&&sws.isAlive()){
            Log.i(TAG,"Stopping web server...");
            sws.stop();
            sws = null;
        }
        else{
            Log.i(TAG,"Web server is null");
        }

        myStopService();

        sendGUI(intent, Settings.TYPE_STARTED, "0");

//        sendState(Settings.WEBSERVER_STOPPED);
//        sendCommand("stop");
        cancelNotification();

        Toast.makeText(this, "Service Destroyed", Toast.LENGTH_SHORT).show();
    }

    //	void Play(){
    //		int streamId_Forest = sp.play(sndId_Forest, 1, 1, 0, LOOP_FOREVER, 1);
    //		//sp.setLoop(streamId_Forest, -1);
    //    	Log.i("Play", "streamId_Forest:"+streamId_Forest);
    //
    ////		 MediaPlayer mPlayer = MediaPlayer.create(getBaseContext(), R.raw.windows_8_notify);
    ////		 // in 2nd param u have to pass your desire ringtone
    ////		 //mPlayer.prepare();
    ////		 mPlayer.start();
    //
    //	}


    public void sendGUI(Intent i, int command, String value){
        i.putExtra("type", command);
        i.putExtra("value", value);
        sendBroadcast(i);
    }

//    public void sendState(int state) {
//        try {
//            if (_messageHandler!=null)
//            _messageHandler.send(prepareMessageInt("state", state));
//
//        } catch (RemoteException e) {
//            e.printStackTrace();
//        }
//    }
//    public void sendPlayState(String playstate) {
//        try {
//            if (_messageHandler!=null)
//            _messageHandler.send(prepareMessageString("playstate", playstate));
//        } catch (RemoteException e) {
//            e.printStackTrace();
//        }
//    }
//    public void sendCommand(String command) {
//        try {
//            if (_messageHandler!=null)
//            _messageHandler.send(prepareMessageString("command", command));
//        } catch (RemoteException e) {
//            e.printStackTrace();
//        }
//    }
//    public void sendTime(long deltatime) {
//        try {
//            if (_messageHandler!=null)
//            _messageHandler.send(prepareMessageLong("time", deltatime));
//        } catch (RemoteException e) {
//            e.printStackTrace();
//        }
//    }
    public static Message prepareMessageString(String key, String val){
        Message message = Message.obtain();
        Bundle b = new Bundle();
        b.putString(key, val);
        message.setData(b);
        return message;
    }
    public static Message prepareMessageLong(String key, long val){
        Message message = Message.obtain();
        Bundle b = new Bundle();
        b.putLong(key, val);
        message.setData(b);
        return message;
    }
    public static Message prepareMessageInt(String key, int val){
        Message message = Message.obtain();
        Bundle b = new Bundle();
        b.putInt(key, val);
        message.setData(b);
        return message;
    }

    //	@SuppressLint("NewApi")
    //void NotifyTaskbar(String title, String text){
    void NotifyTaskbar(String text){
        String title = getString(R.string.app_name);
        NotificationManager mNotificationManager =
                (NotificationManager) getApplicationContext().getSystemService(Context.NOTIFICATION_SERVICE);

        NotificationCompat.Builder mBuilder =
                new NotificationCompat.Builder(getBaseContext())
                        .setSmallIcon(R.drawable.ic_launcher)
                        .setContentTitle(title)
                        .setContentText(text)
                        .setOngoing(false);

        Intent resultIntent = new Intent(getApplicationContext(), SimpleShareActivity.class);
//		if(Build.VERSION.SDK_INT >= 16 ){
//			// Creates an explicit intent for an Activity in your app
//
//			// The stack builder object will contain an artificial back stack for the
//			// started Activity.
//			// This ensures that navigating backward from the Activity leads out of
//			// your application to the Home screen.
//			TaskStackBuilder stackBuilder = TaskStackBuilder.create(getApplicationContext());
//			// Adds the back stack for the Intent (but not the Intent itself)
//			stackBuilder.addParentStack(MainActivity.class);
//			// Adds the Intent that starts the Activity to the top of the stack
//			stackBuilder.addNextIntent(resultIntent);
//			PendingIntent resultPendingIntent =
//					stackBuilder.getPendingIntent(
//							0,
//							PendingIntent.FLAG_UPDATE_CURRENT
//							);
//			mBuilder.setContentIntent(resultPendingIntent);
//		}
//		else{
        PendingIntent contentIntent = PendingIntent.getActivity(this, 0, resultIntent, 0);
        mBuilder.setContentIntent(contentIntent);
//		}
        mNotificationManager.notify(NOTIFICATION_ID, mBuilder.build());
    }
    void cancelNotification(){
        NotificationManager mNotificationManager =
                (NotificationManager) getApplicationContext().getSystemService(Context.NOTIFICATION_SERVICE);
        mNotificationManager.cancel(NOTIFICATION_ID);
    }



//    public static Thread performOnBackgroundThread(final MyRunnable runnable) {
//        final Thread t = new Thread() {
//            @Override
//            public void run() {
//                try {
//                    System.out.println("performOnBackgroundThread START");
//                    runnable.run(); // blocking!!!
//                    System.out.println("performOnBackgroundThread END");
//                } finally {
//
//                }
//            }
//        };
//        t.start();
//        return t;
//    }


}