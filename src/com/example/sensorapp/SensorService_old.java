package com.example.sensorapp;

import java.util.List;

import android.app.Activity;
import android.app.ActivityManager;
import android.app.ActivityManager.RunningAppProcessInfo;
import android.app.ActivityManager.RunningTaskInfo;
import android.app.KeyguardManager;
import android.app.KeyguardManager.KeyguardLock;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Binder;
import android.os.IBinder;
import android.os.PowerManager;
import android.telephony.PhoneStateListener;
import android.telephony.TelephonyManager;
import android.util.Log;

public class SensorService_old extends android.app.Service {
	private IBinder binder = new SensorService_old.LocalBinder();
	private boolean isRun = false;

	private ActivityManager activityManager;
	private String packageName;
	private static Activity mActivity;

	private SensorManager mSensorManager;
	private Sensor mSensor;

	private int mRate = SensorManager.SENSOR_DELAY_NORMAL;

	// private int mCycle = 100; // milliseconds
	// private int mEventCycle = 100; // milliseconds
	// private float mAccuracy = 0;
	//
	// private long lastUpdate = -1;
	// private long lastEvent = -1;

	private float value = -999f;

	public static boolean isCloseCover = false;

	// 声明键盘管理器
	KeyguardManager mKeyguardManager = null;
	// 声明键盘锁
	private KeyguardLock mKeyguardLock = null;
	// 声明电源管理器
	private PowerManager pm;
	private PowerManager.WakeLock wakeLock;

	private TelephonyManager telM;

	@Override
	public IBinder onBind(Intent intent) {
		// TODO Auto-generated method stub
		return binder;
	}

	@Override
	public void onCreate() {
		super.onCreate();

		// 获取监听来电服务
		// cv = new CallView(this);
		telM = (TelephonyManager) getSystemService(Context.TELEPHONY_SERVICE);

		telM.listen(new MyPhoneStatesListener(this, null),
				PhoneStateListener.LISTEN_CALL_STATE);
		// 获取电源的服务
		pm = (PowerManager) getSystemService(Context.POWER_SERVICE);
		// 获取系统服务
		mKeyguardManager = (KeyguardManager) getSystemService(Context.KEYGUARD_SERVICE);

		mActivity = ((SensorApp) getApplication()).getInstance();
		mSensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
		mSensor = mSensorManager.getDefaultSensor(Sensor.TYPE_PROXIMITY);

		mSensorManager.registerListener(mSensorListener, mSensor, mRate);
		isRun = true;

		activityManager = (ActivityManager) this
				.getSystemService(Context.ACTIVITY_SERVICE);
		packageName = this.getPackageName();
		System.out.println("启动服务");
	};

	@Override
	public int onStartCommand(Intent intent, int flags, int startId) {
		// TODO Auto-generated method stub
		return START_STICKY;
		// return super.onStartCommand(intent, flags, startId);
	}

	public void openSensor() {
		mSensorManager.registerListener(mSensorListener, mSensor, mRate);
	}

	public void closeSensor() {
		mSensorManager.unregisterListener(mSensorListener);
	}

	private final SensorEventListener mSensorListener = new SensorEventListener() {

		@Override
		public void onSensorChanged(SensorEvent event) {

			// 过滤微信
			ActivityManager activityManager = (ActivityManager) getSystemService(Context.ACTIVITY_SERVICE);
			List<RunningTaskInfo> tasksInfo = activityManager
					.getRunningTasks(1);
			String currentPackName = "";
			if (tasksInfo.size() > 0) {

				currentPackName = tasksInfo.get(0).topActivity.getPackageName();
				Log.e("current", currentPackName);
			}
			// if (currentPackName.contains("com.tencent")) {
			// return;
			// }

			// TODO Auto-generated method stub
			if (event.sensor.getType() != Sensor.TYPE_PROXIMITY)
				return;

			value = event.values[SensorManager.DATA_X];
			Log.v("value", String.valueOf(value));

			if (value > 0)// 远离
			{
				isCloseCover = false;
				if (telM.getCallState() != TelephonyManager.CALL_STATE_RINGING
						&& telM.getCallState() != TelephonyManager.CALL_STATE_OFFHOOK) {
					Log.i("Log : ", "------>mKeyguardLock");
					// 初始化键盘锁，可以锁定或解开键盘锁
					mKeyguardLock = mKeyguardManager.newKeyguardLock("");
					// 禁用显示键盘锁定
					mKeyguardLock.disableKeyguard();

					Intent MyIntent = new Intent(Intent.ACTION_MAIN);
					MyIntent.addCategory(Intent.CATEGORY_HOME);
					MyIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
					MyIntent.removeExtra("startFlag");
					startActivity(MyIntent);
				}

				// 来电时候
				// if (telM.getCallState() ==
				// TelephonyManager.CALL_STATE_RINGING) {
				// cv.hideCallView();
				// } else if (telM.getCallState() ==
				// TelephonyManager.CALL_STATE_IDLE) {
				// cv.hideCallView();
				// } else if (telM.getCallState() ==
				// TelephonyManager.CALL_STATE_OFFHOOK) {
				// cv.hideCallView();
				// }
			} else// 关闭
			{

				isCloseCover = true;
				if (!isAppOnForeground()
						&& telM.getCallState() != TelephonyManager.CALL_STATE_RINGING
						&& telM.getCallState() != TelephonyManager.CALL_STATE_OFFHOOK) {
					Intent intent = new Intent();
					intent.setClassName("com.example.sensorapp",
							"com.example.sensorapp.EnterActivity");
					intent.putExtra("startFlag", "1");

					intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
					startActivity(intent);
				}

			}

		}

		// }

		@Override
		public void onAccuracyChanged(Sensor sensor, int accuracy) {
			// TODO Auto-generated method stub
			Log.e("!!!!", "onAccuracyChanged");
		}
	};

	@Override
	public void onDestroy() {
		// TODO Auto-generated method stub
		// liuzw.
		SharedPreferences preferences = this.getSharedPreferences(
				"settingInfo", Context.MODE_PRIVATE);
		boolean startServiceFlag = preferences.getBoolean("startService", true);

		if (startServiceFlag == false) {
			return;
		}
		Intent localIntent = new Intent();
		localIntent.setClass(this, SensorService_old.class); // 销毁时重新启动Service
		this.startService(localIntent);

	}

	public class LocalBinder extends Binder {
		// 返回本地服务
		public SensorService_old getService() {
			return SensorService_old.this;
		}
	}

	/**
	 * 程序是否在前台运行
	 * 
	 * @return
	 */
	public boolean isAppOnForeground() {
		// Returns a list of application processes that are running on the
		// device
		List<RunningAppProcessInfo> appProcesses = activityManager
				.getRunningAppProcesses();
		if (appProcesses == null)
			return false;

		for (RunningAppProcessInfo appProcess : appProcesses) {
			// The name of the process that this object is associated with.
			if (appProcess.processName.equals(packageName)
					&& appProcess.importance == RunningAppProcessInfo.IMPORTANCE_FOREGROUND) {
				return true;
			}
		}

		return false;
	}

}
