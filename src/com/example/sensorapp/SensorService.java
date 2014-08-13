package com.example.sensorapp;

import java.util.List;

import com.example.sensorapp.ui.SuperFragmentActivity;
import com.example.sensorapp.util.PowerManagerHelper;

import android.app.ActivityManager;
import android.app.KeyguardManager;
import android.app.ActivityManager.RunningAppProcessInfo;
import android.app.ActivityManager.RunningTaskInfo;
import android.app.KeyguardManager.KeyguardLock;
import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Binder;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.os.PowerManager;
import android.os.PowerManager.WakeLock;
import android.provider.Settings;
import android.telephony.PhoneStateListener;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.widget.Filter;

public class SensorService extends Service
{
	private IBinder binder = new SensorService.LocalBinder();
	private boolean isRun = false;

	private ActivityManager activityManager;
	// private String packageName;
	private static SuperFragmentActivity mActivity;

	private PowerManagerHelper powerManagerHelper;

	private SensorManager mSensorManager;
	private Sensor mSensor;
	WakeLock wl;
	//private int mRate = SensorManager.SENSOR_DELAY_NORMAL;
	private int mRate = 5000;
	
	private float value = -999f;

	public static boolean isCloseCover = false;

	// 声明键盘管理器
	KeyguardManager mKeyguardManager = null;
	// 声明键盘锁
	private KeyguardLock mKeyguardLock = null;
	// 声明电源管理器
	private static PowerManager pm;
	private static PowerManager.WakeLock wakeLock;

	private TelephonyManager telM;
	private static CallViewHelper callViewHelper;
	private MyPhoneStatesListener phoneStatesListener;

	private String currentPhoneNum = "";

	ScreenReceiver sr = new ScreenReceiver();

	private BroadcastReceiver myReceiver = new BroadcastReceiver()
	{

		@Override
		public void onReceive(Context context, Intent intent)
		{
			// TODO Auto-generated method stub
			if (intent.getAction().equals("action.hs.closesensor"))
			{
				// closeSensor();
			}

			if (intent.getAction().equals("action.hs.opensensor"))
			{
				// openSensor();
			}
		}
	};

	@Override
	public IBinder onBind(Intent intent)
	{
		// TODO Auto-generated method stub
		return binder;
	}

	@Override
	public void onCreate()
	{
		super.onCreate();
		SharedPreferences preferences = getSharedPreferences("settingInfos",
				Context.MODE_PRIVATE);
		GlobalVar.isOpenSensor = preferences.getBoolean("startService", true);

		// 注册监听
		IntentFilter myFilter = new IntentFilter();
		myFilter.addAction("action.hs.closesensor");
		myFilter.addAction("action.hs.opensensor");
		registerReceiver(myReceiver, myFilter);

		// 监听屏幕
		final IntentFilter filter = new IntentFilter();
		filter.addAction(Intent.ACTION_SCREEN_OFF);
		filter.addAction(Intent.ACTION_SCREEN_ON);
		registerReceiver(sr, filter);

		// 获取监听来电服务
		pm = (PowerManager) getSystemService(Context.POWER_SERVICE);
		// 获取电源管理器对象
		wakeLock = pm.newWakeLock(PowerManager.ACQUIRE_CAUSES_WAKEUP
				| PowerManager.SCREEN_DIM_WAKE_LOCK, "bright");
		// 获取PowerManager.WakeLock对象,后面的参数|表示同时传入两个值,最后的是LogCat里用的Tag
		// 获取系统服务

		telM = (TelephonyManager) getSystemService(Context.TELEPHONY_SERVICE);
		phoneStatesListener = new MyPhoneStatesListener(this, handler);
		telM.listen(phoneStatesListener, PhoneStateListener.LISTEN_CALL_STATE);
		// 获取电源的服务

		mKeyguardManager = (KeyguardManager) getSystemService(Context.KEYGUARD_SERVICE);

		mActivity = ((SensorApp) getApplication()).getInstance();
		mSensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
		mSensor = mSensorManager.getDefaultSensor(Sensor.TYPE_MAGNETIC_FIELD);
		mSensorManager.registerListener(mSensorListener, mSensor, mRate);

		isRun = true;

		activityManager = (ActivityManager) this
				.getSystemService(Context.ACTIVITY_SERVICE);
		System.out.println("启动服务");

		callViewHelper = new CallViewHelper(this);
	};

	@Override
	public int onStartCommand(Intent intent, int flags, int startId)
	{
		try
		{
			// TODO Auto-generated method stub
			if (intent.getBooleanExtra("OUTCALL", false))// 去电
			{
				// showViewCallWithAnsweringMode(true);
				if (isCloseCover)
					callViewHelper.show();
				// callViewHelper.setIncomingNumber((String) msg.obj);
			}

			if (intent.getBooleanExtra("INCOMINGCALL", false))// 来电
			{
				showCallView(intent.getStringExtra("INCOMINGNUMS"));
				currentPhoneNum = intent.getStringExtra("INCOMINGNUMS");
			}
			if (intent.getBooleanExtra("OUTGOINGCALL", false))// 去电
			{
				callViewHelper.setIncomingNumber(intent
						.getStringExtra("INCOMINGNUMS"));
				
//				PowerManager pm = (PowerManager) getSystemService(Context.POWER_SERVICE);
//			    wl = pm.newWakeLock(PowerManager.PARTIAL_WAKE_LOCK, "My Tag");
//			    wl.acquire();
//			    wl.release();
//
//			    new Handler().postDelayed(new Runnable(){    
//			        public void run() {    
//			        //execute the task   
//			        	wl.acquire();
//			        }    
//			     }, 3000); 
			}
		} catch (Exception ex)
		{
			Log.e("serviceError", ex.toString());
		}

		return START_STICKY;
		// return super.onStartCommand(intent, flags, startId);
	}

	public void openSensor()
	{
		isRun = true;
		mSensorManager.registerListener(mSensorListener, mSensor, mRate);
	}

	public void closeSensor()
	{
		mSensorManager.unregisterListener(mSensorListener, mSensor);
		isRun = false;
	}

	public void showViewCallWithAnsweringMode(boolean answerMode)
	{
		if (isCloseCover)
		{
			callViewHelper.setAnsweringMode(answerMode);
		}
	}

	public void showCallView(String incomingNums)
	{
		try
		{
			Thread.sleep(2000);
		} catch (InterruptedException e)
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		if (isCloseCover)
		{
			callViewHelper.show();
			callViewHelper.setIncomingNumber(incomingNums);
			// powerManagerHelper.wakeUp();
		}
	}

	public void closeViewCall()
	{
		callViewHelper.close();
	}

	private final SensorEventListener mSensorListener = new SensorEventListener()
	{

		@Override
		public void onSensorChanged(SensorEvent event)
		{
			// 判断是否注册
			if (GlobalVar.checkId)
			{
				SharedPreferences preferences2 = getApplicationContext()
						.getSharedPreferences("registerInfo",
								Context.MODE_PRIVATE);
				boolean reg = preferences2.getBoolean("register", false);
				if (!reg)
				{
					return;
				}
			}

			// 判断是否开启
			if (!GlobalVar.isOpenSensor)
			{
				return;
			}

			// 屏蔽腾讯软件
			ActivityManager activityManager = (ActivityManager) getSystemService(Context.ACTIVITY_SERVICE);
			List<RunningTaskInfo> tasksInfo = activityManager
					.getRunningTasks(1);
			String currentPackName = "";
			if (tasksInfo.size() > 0)
			{

				currentPackName = tasksInfo.get(0).topActivity.getPackageName();
				Log.e("current", currentPackName);
			}
			if (currentPackName.contains("com.tencent"))
			{
				return;
			}

			// TODO Auto-generated method stub
			if (event.sensor.getType() != Sensor.TYPE_MAGNETIC_FIELD)
				return;
			value = event.values[SensorManager.DATA_X];
			float valuey = event.values[SensorManager.DATA_Y];
			float valuez = event.values[SensorManager.DATA_Z];
			
			
			Log.d("valuex", String.valueOf(value));
			Log.d("valuey", String.valueOf(valuey));
			Log.d("valuez", String.valueOf(valuez));
			Log.d("valued", String.valueOf(value+valuez+valuey));
			if (Math.abs(valuey) > 400 && isCloseCover)// 远离
			{
				isCloseCover = false;
				// 之前的方法太暴力，不管当前在运行什么都会被切换到后台去。
				// 此方式只结束当前的程序，而service在结束自己后又会自动启动，所以可行。
				if (isAppOnForeground())
				{
					// android.os.Process.killProcess(android.os.Process.myPid());

					// Intent MyIntent = new Intent(Intent.ACTION_MAIN);
					// MyIntent.addCategory(Intent.CATEGORY_HOME);
					// MyIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
					// startActivity(MyIntent);

					wakeLock.acquire(2000);
					sendBroadcast(new Intent("action.hs.finish"));

				}

				callViewHelper.close();

			} else if(Math.abs(valuey) < 400 && !isCloseCover)
			// 关闭
			{
				try
				{
					
					isCloseCover = true;
					{

						Log.e("sensor", "close!!!!!!");
						Intent intent = new Intent();
						intent.setClassName("com.example.sensorapp",
								"com.example.sensorapp.EnterActivity");
						intent.putExtra("startFlag", "1");
						intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
						startActivity(intent);
					}
					if (phoneStatesListener.getCurrentState() == TelephonyManager.CALL_STATE_RINGING)
					{
						Message msg = handler
								.obtainMessage(MyPhoneStatesListener.MSG_CALL_RING);
						msg.obj = phoneStatesListener.getIncomingNumber();
						msg.sendToTarget();
					}
					if (phoneStatesListener.getCurrentState() == TelephonyManager.CALL_STATE_OFFHOOK)
					{
						handler.sendEmptyMessage(MyPhoneStatesListener.MSG_CALL_HOOK);
					}
				} catch (Exception e)
				{
					// TODO Auto-generated catch block
					Log.e("onSensorChange",e.toString());
				}

			}

		}

		// }

		@Override
		public void onAccuracyChanged(Sensor sensor, int accuracy)
		{
			// TODO Auto-generated method stub
			Log.e("!!!!", "onAccuracyChanged");
		}
	};

	@Override
	public void onDestroy()
	{
		// TODO Auto-generated method stub
		// liuzw.
		super.onDestroy();
		unregisterReceiver(myReceiver);
		unregisterReceiver(sr);
		SharedPreferences preferences = this.getSharedPreferences(
				"settingInfo", Context.MODE_PRIVATE);
		boolean startServiceFlag = preferences.getBoolean("startService", true);

		if (startServiceFlag == false)
		{
			return;
		}

		Intent localIntent = new Intent();
		localIntent.setClass(this, SensorService.class); // 销毁时重新启动Service
		this.startService(localIntent);

		// closeSensor();
		// powerManagerHelper.destroy();

	}

	public class LocalBinder extends Binder
	{
		// 返回本地服务
		public SensorService getService()
		{
			return SensorService.this;
		}
	}

	final Handler handler = new Handler()
	{

		@Override
		public void handleMessage(Message msg)
		{
			switch (msg.what)
			{
			case MyPhoneStatesListener.MSG_UPDATE_CALL_TIME:
			{
				callViewHelper.updateCallTime(msg.arg1);
				// callViewHelper.setIncomingNumber(String.valueOf(msg.arg2));
				break;
			}
			case MyPhoneStatesListener.MSG_CALL_RING:
			{
				if (isCloseCover)
				{
					callViewHelper.show();
					String num = (String) msg.obj;
					if (!num.equals(""))
						callViewHelper.setIncomingNumber((String) msg.obj);
					else
						callViewHelper.setIncomingNumber(currentPhoneNum);
					// powerManagerHelper.wakeUp();
				}
			}
				break;
			case MyPhoneStatesListener.MSG_CALL_END:
			{
				callViewHelper.setIncomingNumber("");
				callViewHelper.close();
				break;
			}
			case MyPhoneStatesListener.MSG_CALL_HOOK:
			{
				callViewHelper.setAnsweringMode(true);
			}
				break;
			}
			super.handleMessage(msg);
		}

	};

	/**
	 * 程序是否在前台运行
	 * 
	 * @return
	 */
	public boolean isAppOnForeground()
	{
		// Returns a list of application processes that are running on the
		// device
		List<RunningAppProcessInfo> appProcesses = activityManager
				.getRunningAppProcesses();
		if (appProcesses == null)
			return false;
		String packageName = getPackageName();
		for (RunningAppProcessInfo appProcess : appProcesses)
		{
			// The name of the process that this object is associated with.
			if (appProcess.processName.equals(packageName)
					&& appProcess.importance == RunningAppProcessInfo.IMPORTANCE_FOREGROUND)
			{
				return true;
			}
		}

		return false;
	}
}
