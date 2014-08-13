/**
 * 
 */
package com.example.sensorapp.ui.adapter;

import java.util.List;

import android.content.Context;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.View;

/**
 * @author Administrator 造成循环滑动的假像。 其实只有有限
 */
public class MyViewPagerAdapter extends PagerAdapter {

	private List<View> views;
	private int count = 0;
	private Context context;

	/**
	 * 
	 */
	public MyViewPagerAdapter(Context context, List<View> views) {
		// TODO Auto-generated constructor stub
		this.views = views;
		this.count = views.size();
		this.context = context;
	}

	@Override
	public int getItemPosition(Object object) {
		// TODO Auto-generated method stub
		return super.getItemPosition(object);
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see android.support.v4.view.PagerAdapter#getCount()
	 */
	@Override
	public int getCount() {
		// TODO Auto-generated method stub
		return Integer.MAX_VALUE;
	}

	@Override
	public void destroyItem(View arg0, int arg1, Object arg2) {
		// TODO Auto-generated method stub
		// ((ViewPager) arg0).removeView(views.get(arg1 % count));
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * android.support.v4.view.PagerAdapter#isViewFromObject(android.view.View,
	 * java.lang.Object)
	 */
	@Override
	public boolean isViewFromObject(View arg0, Object arg1) {
		// TODO Auto-generated method stub
		return arg0 == arg1;
	}

	@Override
	public Object instantiateItem(View arg0, int arg1) {
		// TODO Auto-generated method stub
		try {
			((ViewPager) arg0).addView(views.get(arg1 % count), 0);
		} catch (Exception e) {
			// TODO: handle exception
		}
		return views.get(arg1 % count);
	}

}
