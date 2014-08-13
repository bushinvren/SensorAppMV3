package com.example.sensorapp.util;

import android.content.Context;
import android.os.Handler;
import android.os.Message;
import android.os.PowerManager;
import android.os.PowerManager.WakeLock;

public class PowerManagerHelper {
	private Context context;
	private PowerManager powerManager;
	private WakeLock mWakeLock;

	public PowerManagerHelper(Context context) {
		// TODO Auto-generated constructor stub
		this.context = context;
		powerManager = (PowerManager) context
				.getSystemService(Context.POWER_SERVICE);
		mWakeLock = powerManager.newWakeLock(
				PowerManager.SCREEN_BRIGHT_WAKE_LOCK
						| PowerManager.ON_AFTER_RELEASE, "test");
	}

	// 通过延时.增加有效命中率.
	public void wakeUp() {
		// handler.sendEmptyMessageDelayed(0, 1000);

		_wakeUp();
	}

	private void _wakeUp() {

		{
			mWakeLock.acquire();
			// 唤醒屏幕
		}
	}

	public void destroy() {
		if (mWakeLock != null) {
			mWakeLock.release();
			mWakeLock = null;
		}

	}
}
