package com.millo.abcshare;
import java.net.URI;

import org.java_websocket.client.WebSocketClient;
import org.java_websocket.handshake.ServerHandshake;

import com.millo.abcshare.MilloHelpers.MyLog;

public class MyWebSocket extends WebSocketClient {

	public MyWebSocket(URI serverURI) {
		super(serverURI);
		// TODO Auto-generated constructor stub
	}

	@Override
	public void onOpen(ServerHandshake handshakedata) {
		// TODO Auto-generated method stub
		MyLog.i("MyWebSocket::open");
		this.send("android");
	}

	@Override
	public void onMessage(String message) {
		// TODO Auto-generated method stub
		MyLog.i("MyWebSocket::message: "+message);		
	}

	@Override
	public void onClose(int code, String reason, boolean remote) {
		// TODO Auto-generated method stub
		MyLog.i("MyWebSocket::close");
	}

	@Override
	public void onError(Exception ex) {
		// TODO Auto-generated method stub
		MyLog.i("MyWebSocket::error");
	}
}
