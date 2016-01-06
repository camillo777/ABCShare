package com.millo.abcshare;

public class Settings {
	static final int TASK_COMPLETE = 0;
	static final int WEBSERVER_STARTED = 1;
	static final int WEBSERVER_STOPPED = 2;
	static final int WEBSERVER_CONNECTED = 3;
	static final int WEBSERVER_DISCONNECTED = 4;

	static final int TYPE_CONNECTED =     1;
	static final int TYPE_STARTED =       2;
	static final int TYPE_LINK =          3;
	static final int TYPE_ACTIVE =        4;
	static final int TYPE_SERVING =       5;
	static final int TYPE_DATA =          6;

//	public static enum enumStateMessage {
//		WEBSERVER_STARTED,
//		WEBSERVER_STOPPED,
//		WEBSERVER_CONNECTED,
//		WEBSERVER_DISCONNECTED,
//		SERVICE_STOPPED,
//		SERVICE_STARTED
//	}
	
	static final int DECODE_STATE_COMPLETED = 10;
	static final int WEBSERVER_STATE_COMPLETED = 11;

	
	static final int NOTIFICATION_ID = 1001;
	
	
	static final String INTENTEXTRA_STREAM = "android.intent.extra.STREAM";
	static final String INTENTEXTRA_TEXT = "android.intent.extra.TEXT";
	
	static final String NOTIFICATION_TITLE = "share app";
	static final String NOTIFICATION_TEXT = "web server active";

	static final Boolean DEBUG = false;
	
	static final int WEBSERVER_PORT = 8080;
}
