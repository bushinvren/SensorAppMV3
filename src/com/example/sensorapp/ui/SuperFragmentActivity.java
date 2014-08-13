/**
 * 
 */
package com.example.sensorapp.ui;

import android.content.Context;
import android.content.SharedPreferences;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentTransaction;
import android.util.Log;

import com.example.sensorapp.R;

/**
 * @author Administrator
 * 
 */
public class SuperFragmentActivity extends FragmentActivity {

	private Fragment currentFragment = null;

	private boolean _isActive = false;

	public boolean isActive() {
		Log.i("EnterActivity", "isActivity:" + _isActive);
		return _isActive;
	}

	public void onStart() {
		super.onStart();
		_isActive = true;
		Log.i("EnterActivity", "startActivity:isActivity:" + _isActive);
	}

	public void onStop() {
		super.onStop();
		_isActive = false;
		Log.i("EnterActivity", "stopActivity:isActivity:" + _isActive);
	}

	public void changeFragment(Fragment f) {

		FragmentTransaction t1 = this.getSupportFragmentManager()
				.beginTransaction();
		if (f == null) {
			if (currentFragment != null) {
				t1.remove(currentFragment);
				currentFragment = null;
			}
		} else {
			currentFragment = f;

			if (t1.isEmpty()) {
				t1.add(R.id.fragment_container, f);
			} else {
				t1.replace(R.id.fragment_container, f);
			}
		}

		t1.commit();
	}

	public void addFragment(Fragment fragment, String flag) {
		FragmentTransaction t1 = this.getSupportFragmentManager()
				.beginTransaction();
		if (fragment != null) {
			t1.add(R.id.fragment_container, fragment, flag);
			currentFragment = fragment;
		}
		t1.commit();
	}

	public void removeFragment(Fragment fragment) {
		FragmentTransaction t1 = this.getSupportFragmentManager()
				.beginTransaction();
		if (fragment != null) {
			t1.remove(fragment);

		}
		t1.commit();
	}

	public SharedPreferences getSharedPreferences() {
		SharedPreferences preferences = this.getSharedPreferences("sensorapp",
				Context.MODE_PRIVATE);
		return preferences;
	}
}
