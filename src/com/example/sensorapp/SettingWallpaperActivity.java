/**
 * 
 */
package com.example.sensorapp;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v4.widget.SimpleCursorAdapter.ViewBinder;
import android.view.View;
import android.view.ViewGroup.LayoutParams;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ImageView.ScaleType;
import android.widget.LinearLayout;

/**
 * @author Administrator
 * 
 */
public class SettingWallpaperActivity extends Activity implements
		View.OnClickListener {
	private LinearLayout linearscrollview;
	private View buttonSetting;
	private ImageView backgroundPreview;

	private int selectedIndex = 0;

	private final static int[] rBgs = { R.drawable.bg00, R.drawable.bg01,
			R.drawable.bg02, R.drawable.bg03, R.drawable.bg04, R.drawable.bg05, R.drawable.bg06, R.drawable.bg07, R.drawable.bg08, R.drawable.bg09};

	private final static int[] rBacks = { R.drawable.back00, R.drawable.back01,
		R.drawable.back02, R.drawable.back03, R.drawable.back04, R.drawable.back05, R.drawable.back06, R.drawable.back07, R.drawable.back08, R.drawable.back09 };

	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.settingwillpaper);
		init();
	}

	private void init() {
		linearscrollview = (LinearLayout) findViewById(R.id.linearscrollview);
		buttonSetting = findViewById(R.id.buttonSetting);
		backgroundPreview = (ImageView) findViewById(R.id.backgroundPreview);

		buttonSetting.setOnClickListener(this);

		LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(100,
				100);

		for (int i = 0; i < rBgs.length; i++) {
			ImageView imageView = new ImageView(this);
			imageView.setScaleType(ScaleType.FIT_XY);
			imageView.setImageResource(rBgs[i]);
			// imageView.setTag(i);
			imageView.setId(i);

			imageView.setOnClickListener(this);
			linearscrollview.addView(imageView, params);
		}
		SharedPreferences preferences = this.getSharedPreferences(
				"settingInfos", Context.MODE_PRIVATE);
		selectedIndex = preferences.getInt("bgSelectedIndex", selectedIndex);
		backgroundPreview.setBackgroundResource(rBacks[selectedIndex]);
	}

	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		switch (v.getId()) {
		case R.id.buttonSetting: {
			SharedPreferences preferences = this.getSharedPreferences(
					"settingInfos", Context.MODE_PRIVATE);
			preferences.edit().putInt("bgSelectedIndex", selectedIndex)
					.commit();
			// imageViewmusicicon.setBackgroundResource(rBgs[tag]);
			finish();
		}
			break;

		default: {
			selectedIndex = v.getId();
			backgroundPreview.setBackgroundResource(rBacks[selectedIndex]);
		}
			break;
		}
	}
}
