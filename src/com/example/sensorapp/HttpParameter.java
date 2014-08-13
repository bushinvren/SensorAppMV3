/**
 * 
 */
package com.example.sensorapp;

import java.util.HashMap;

import com.example.sensorapp.Http.NetActionListener;

/**
 * @author Administrator
 * 
 */
public class HttpParameter {

	public String url;
	public String param1 = null;
	public HashMap<String, String> param2 = new HashMap<String, String>();

	public int what;
	public String tag;

	public int arg1;
	public String arg2;
	public Object arg3;

	public NetActionListener listener = new Http.NetActionListener() {

		@Override
		public void onNetProgressChanged(float progress, HttpParameter parameter) {
			// TODO Auto-generated method stub

		}

		@Override
		public void onNetFinished(String a, HttpParameter parameter) {
			// TODO Auto-generated method stub

		}

		@Override
		public void onNetError(String message, HttpParameter parameter) {
			// TODO Auto-generated method stub

		}
	};
}
