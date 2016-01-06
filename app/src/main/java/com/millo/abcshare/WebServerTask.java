package com.millo.abcshare;

import android.widget.TextView;

public class WebServerTask {
	    SimpleShareActivity mActivity = null;
	    public TextView tv = null;
	    public String s = "";
	    
	    WebServerTask(SimpleShareActivity activity){
		    mActivity = activity;
//		    tv = (TextView) mActivity.findViewById(R.id.tvConnected);
	    }
	    
	    public void handleDecodeState(int state) {
	        int outState = 0;
	        // Converts the decode state to the overall state.
	        s = "disconnected";
	        switch(state) {
//	            case MyWebServer.DECODE_STATE_COMPLETED:
//	                outState = Settings.TASK_COMPLETE;
//	                s = "connected";
//	                break;
//	            case MyWebServer.WEBSERVER_STATE_COMPLETED:
//	                outState = Settings.WEBSERVER_STARTED;
//	                break;
	        }
//	        // Calls the generalized state method
	        handleState(outState);
	    }
	    // Passes the state to PhotoManager
	    void handleState(int state) {
	        /*
	         * Passes a handle to this task and the
	         * current state to the class that created
	         * the thread pools
	         */
//	        mActivity.handleState(this, state);
	    }
	}
