/**
 * 
 */
package com.example.sensorapp.ui.adapter;

import java.util.List;

import android.content.Context;
import android.os.Parcelable;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.View;

/**
 * ViewPager适配器
 */
public class MyViewPagerAdapter_old1 extends PagerAdapter {
	public List<View> views;
	Context context;
	int mCount;

	public MyViewPagerAdapter_old1(Context context, List<View> views) {
		this.views = views;
		this.context = context;
		mCount = views.size();
	}

	@Override
	public void destroyItem(View collection, int position, Object arg2) {
		if (position >= views.size()) {
			int newPosition = position % views.size();
			position = newPosition;
			// ((ViewPager) collection).removeView(views.get(position));
		}
		if (position < 0) {
			position = -position;
			// ((ViewPager) collection).removeView(views.get(position));
		}
	}

	@Override
	public void finishUpdate(View arg0) {
	}

	@Override
	public int getCount() {
		return mCount + 1;// 此处+1才能向右连续滚动
	}

	@Override
	public Object instantiateItem(View collection, int position) {
		if (position >= views.size()) {
			int newPosition = position % views.size();

			position = newPosition;
			mCount++;
		}
		if (position < 0) {
			position = -position;
			mCount--;
		}
		try {
			((ViewPager) collection).addView(views.get(position), 0);
		} catch (Exception e) {
		}
		return views.get(position);
	}

	@Override
	public boolean isViewFromObject(View view, Object object) {
		return view == (object);
	}

	@Override
	public void restoreState(Parcelable arg0, ClassLoader arg1) {
	}

	@Override
	public Parcelable saveState() {
		return null;
	}

	@Override
	public void startUpdate(View arg0) {
	}
}