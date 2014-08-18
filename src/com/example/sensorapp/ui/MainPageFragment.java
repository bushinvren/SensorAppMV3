/**
 * 
 */
package com.example.sensorapp.ui;

import java.util.Calendar;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.database.Cursor;
import android.graphics.Typeface;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.provider.CallLog.Calls;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.sensorapp.R;

/**
 * @author Administrator
 * 
 */
public class MainPageFragment extends Object {

	private View view;
	private Activity context;

	private TextView simpledigitalclock;

	private ImageView unansweredImage;
	private ImageView unreadSMSImage;
	private ImageView batteryImage;

	static int newSmsCount = 0;
	static int newMmsCount = 0;
	static int newCallCount = 0;
	private String timeString;

	private boolean isBatteryLow;

	final private Handler handler = new Handler() {
		public void dispatchMessage(Message msg) {
			switch (msg.what) {
			case 0: {
				simpledigitalclock.setText(timeString);
				setPhoneInfoView();
			}
			default:
				break;
			}
		}
	};

	private LockImageView.OnUnLockListener starterClickListener;

	public MainPageFragment(Activity context) {
		this.context = context;
		isBatteryLow = false;
	}

	public View onCreateView(LayoutInflater inflater, ViewGroup root,
			Bundle bundle) {
		view = inflater.inflate(R.layout.mainframe, null);
		// context = getActivity();
		init();

		return view;
	}

	private void init() {
		Typeface fontFace = Typeface.createFromAsset(context.getAssets(),
				"DFPixelFont.ttf");

		simpledigitalclock = (TextView) view
				.findViewById(R.id.simpledigitalclock);
		simpledigitalclock.setTypeface(fontFace);

		unansweredImage = (ImageView) view
				.findViewById(R.id.imageViewmissedincomingicon);
		unreadSMSImage = (ImageView) view
				.findViewById(R.id.imageViewnewmessageicon);
		batteryImage = (ImageView) view
				.findViewById(R.id.imageViewbatterylowicon);

		Thread thread = new Thread() {
			public void run() {
				while (true) {
					getSysTime();
					findNewSmsCount();
					findNewMmsCount();
					findNewCallCount();

					// Message msgMessage= handler.obtainMessage(0);
					handler.sendEmptyMessage(0);
					try {
						sleep(30000);
					} catch (InterruptedException e) {
						e.printStackTrace();
					}
				}
			}
		};
		thread.start();

		IntentFilter intentFilter = new IntentFilter(
				Intent.ACTION_BATTERY_CHANGED);
		context.registerReceiver(new BatteryReceiver(), intentFilter);
	}

	public void lock() {
		// pageStart.setVisibility(View.VISIBLE);
	}

	public void unlock() {
		// pageStart.setVisibility(View.INVISIBLE);
	}

	public void setOnUnlockListener(LockImageView.OnUnLockListener listener) {
		this.starterClickListener = listener;
	}

	private void getSysTime() {
		Calendar mCalendar = Calendar.getInstance();

		int hour = mCalendar.get(Calendar.HOUR_OF_DAY);
		int minute = mCalendar.get(Calendar.MINUTE);

		timeString = (hour < 10 ? "0" + hour : hour) + ":"
				+ (minute < 10 ? "0" + minute : minute);

	}

	private void setPhoneInfoView() {
		int smsNoReadCount = newSmsCount + newMmsCount;

		if (newCallCount > 0) {
			unansweredImage.setVisibility(View.VISIBLE);
			unreadSMSImage.setVisibility(View.INVISIBLE);
			batteryImage.setVisibility(View.INVISIBLE);
		} else if (smsNoReadCount > 0) {
			unansweredImage.setVisibility(View.INVISIBLE);
			unreadSMSImage.setVisibility(View.VISIBLE);
			batteryImage.setVisibility(View.INVISIBLE);
		} else if (isBatteryLow) {
			unansweredImage.setVisibility(View.INVISIBLE);
			unreadSMSImage.setVisibility(View.INVISIBLE);
			batteryImage.setVisibility(View.VISIBLE);
		} else {
			unansweredImage.setVisibility(View.INVISIBLE);
			unreadSMSImage.setVisibility(View.INVISIBLE);
			batteryImage.setVisibility(View.INVISIBLE);
		}
	}

	private void findNewSmsCount() {
		Cursor csr = null;
		try {
			csr = context
					.getApplicationContext()
					.getContentResolver()
					.query(Uri.parse("content://sms"), new String[] { "read" },
							"type = 1 and read = 0", null, null);
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			newSmsCount = csr.getCount(); // 未读短信数目
			csr.close();
		}

	}

	private void findNewMmsCount() {
		Cursor csr = null;
		try {
			csr = context
					.getApplicationContext()
					.getContentResolver()
					.query(Uri.parse("content://mms/inbox"),
							new String[] { "read" }, "read = 0", null, null);
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			newMmsCount = csr.getCount();// 未读彩信数目
			csr.close();
		}

	}

	private void findNewCallCount() {
		Cursor csr = null;
		int missedCallCount = 0;
		try {
			csr = context.getContentResolver().query(Calls.CONTENT_URI,
					new String[] { Calls.NUMBER, Calls.TYPE, Calls.NEW }, null,
					null, Calls.DEFAULT_SORT_ORDER);
			if (null != csr) {
				while (csr.moveToNext()) {
					int type = csr.getInt(csr.getColumnIndex(Calls.TYPE));
					switch (type) {
					case Calls.MISSED_TYPE:
						if (csr.getInt(csr.getColumnIndex(Calls.NEW)) == 1) {
							missedCallCount++;
						}
						break;
					case Calls.INCOMING_TYPE:
						break;
					case Calls.OUTGOING_TYPE:
						break;
					}
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			newCallCount = missedCallCount;// 未读电话数目
			csr.close();
		}

	}

	private class BatteryReceiver extends BroadcastReceiver {

		@Override
		public void onReceive(Context context, Intent intent) {
			context.unregisterReceiver(this);
			if (Intent.ACTION_BATTERY_CHANGED.equals(intent.getAction())) {

				int level = intent.getIntExtra("level", 0);
				int scale = intent.getIntExtra("scale", 100);

				if (level * 100 / scale < 20) {
					isBatteryLow = true;
				} else {
					isBatteryLow = false;
				}
			}
		}

	}

}
