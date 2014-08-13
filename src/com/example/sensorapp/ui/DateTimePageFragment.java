/**
 * 
 */
package com.example.sensorapp.ui;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.sensorapp.R;

/**
 * @author Administrator
 * 
 */
public class DateTimePageFragment extends Object {

	private View view;
	private SuperFragmentActivity activity;

	public DateTimePageFragment(SuperFragmentActivity activity) {
		this.activity = activity;
	}

	public View onCreateView(LayoutInflater inflater, ViewGroup root, Bundle bundle) {
		view = inflater.inflate(R.layout.mainframe, null);
		init();
		return view;
	}

	private void init() {

	}
}
