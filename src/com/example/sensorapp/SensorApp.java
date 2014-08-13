package com.example.sensorapp;

import android.app.Application;

import com.example.sensorapp.ui.SuperFragmentActivity;

public class SensorApp extends Application {
	private SuperFragmentActivity mActivity;

	public void setInstance(SuperFragmentActivity instance) {
		mActivity = instance;
	}

	public SuperFragmentActivity getInstance() {
		return mActivity;
	}

}
