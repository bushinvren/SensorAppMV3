/**
 * liuzw.
 */
package com.example.sensorapp.ui.adapter;

import java.util.ArrayList;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

/**
 * @author Administrator
 * 
 */
public class MyFragmentPagerAdapter extends FragmentPagerAdapter {
	private ArrayList<Class<? extends Fragment>> fragments;
	public int count = 0;

	/**
	 * @param fm
	 */
	public MyFragmentPagerAdapter(FragmentManager fm) {
		super(fm);
		// TODO Auto-generated constructor stub
	}

	public MyFragmentPagerAdapter(FragmentManager fm, ArrayList<Class<? extends Fragment>> fms) {
		super(fm);
		this.fragments = fms;
		count = fms.size();

	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see android.support.v4.app.FragmentPagerAdapter#getItem(int)
	 */
	@Override
	public Fragment getItem(int arg0) {
		// TODO Auto-generated method stub
		// return fragments == null ? null : fragments.get(arg0);
		try {
			return fragments.get(arg0 % count).newInstance();
		} catch (InstantiationException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IllegalAccessException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return null;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see android.support.v4.view.PagerAdapter#getCount()
	 */
	@Override
	public int getCount() {
		// TODO Auto-generated method stub
		// return fragments == null ? 0 : fragments.size();
		return Integer.MAX_VALUE;
	}

}
