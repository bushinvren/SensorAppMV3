package com.example.sensorapp;

import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.SharedPreferences;
import android.os.IBinder;
import android.provider.Settings;
import android.telephony.PhoneStateListener;
import android.telephony.TelephonyManager;
import android.util.Log;

public class OutCallReceiver extends BroadcastReceiver
{

	private static final String ACTION_OUT_CALLING = Intent.ACTION_NEW_OUTGOING_CALL;
	private String Tag = this.getClass().getSimpleName();
	private static String call_number = null;

	@Override
	public void onReceive(Context context, Intent intent)
	{

		SharedPreferences preferences = context.getSharedPreferences(
				"settingInfos", Context.MODE_PRIVATE);
		boolean startServiceFlag = preferences.getBoolean("startService", true);
		if (!startServiceFlag)
		{
			return;
		}

		if (ACTION_OUT_CALLING.equals(intent.getAction()))
		{
			String phoneNum = intent.getStringExtra(Intent.EXTRA_PHONE_NUMBER);
			Log.e("test", phoneNum);
			Intent intent1 = new Intent(context, SensorService.class);
			intent1.putExtra("OUTCALL", true);
			intent1.putExtra("INCOMINGNUMS", phoneNum);
			context.startService(intent1);
			Log.v("OutCallReceiver", "ACTION_OUT_CALLING");
		}

		// 去电
		if (intent.getAction().equals(Intent.ACTION_NEW_OUTGOING_CALL))
		{
//			Settings.System.putString(context.getContentResolver(),
//					"proximity_sensor", "0");
			String phoneNumber = intent
					.getStringExtra(Intent.EXTRA_PHONE_NUMBER);
			Log.d(Tag, String.format("call Ringing : %s", call_number));
			Intent intent2 = new Intent(context, SensorService.class);
			if (phoneNumber == null)
			{
				phoneNumber = "";
			}
			intent2.putExtra("OUTGOINGCALL", true);
			intent2.putExtra("INCOMINGNUMS", phoneNumber);
			context.startService(intent2);
			Log.d(Tag, String.format("call Out : %s", phoneNumber));
			// 来电
		} else
		{
			Log.e("incomingCall", intent.getAction());
			TelephonyManager tm = (TelephonyManager) context
					.getSystemService(Service.TELEPHONY_SERVICE);

			switch (tm.getCallState())
			{
			// 响铃中
			case TelephonyManager.CALL_STATE_RINGING:
				call_number = intent.getStringExtra("incoming_number");

					Log.d(Tag, String.format("call Ringing : %s", call_number));
					Intent intent2 = new Intent(context, SensorService.class);
					if (call_number == null)
					{
						call_number = "";
					}
					intent2.putExtra("INCOMINGCALL", true);
					intent2.putExtra("INCOMINGNUMS", call_number);
					context.startService(intent2);
				break;
			// 已接通
			case TelephonyManager.CALL_STATE_OFFHOOK:
				Log.d(Tag, String.format("call Offhook : %s", call_number));
//				Settings.System.putString(context.getContentResolver(),
//						"proximity_sensor", "0");// 禁止屏幕熄灭
				break;
			// 挂断
			case TelephonyManager.CALL_STATE_IDLE:
				Log.d(Tag, "call Idle");
//				Settings.System.putString(context.getContentResolver(),
//						"proximity_sensor", "1");
				break;
			}
		}

	}

	PhoneStateListener listener = new PhoneStateListener()
	{

		@Override
		public void onCallStateChanged(int state, String incomingNumber)
		{
			// 注意，方法必须写在super方法后面，否则incomingNumber无法获取到值。
			super.onCallStateChanged(state, incomingNumber);
			switch (state)
			{
			case TelephonyManager.CALL_STATE_IDLE:
				System.out.println("挂断");
				break;
			case TelephonyManager.CALL_STATE_OFFHOOK:
				System.out.println("接听");
				break;
			case TelephonyManager.CALL_STATE_RINGING:
				System.out.println("响铃:来电号码" + incomingNumber);
				
				// 输出来电号码
				break;
			}
		}
	};

	private SensorService Myservice;

	private void binderService(Context context)
	{
		Intent intent = new Intent(context, SensorService.class);
		context.bindService(intent, new ServiceConnection()
		{

			@Override
			public void onServiceDisconnected(ComponentName componentName)
			{
				Myservice = null;
				// 这里可以提示用户
			}

			@Override
			public void onServiceConnected(ComponentName name, IBinder service)
			{
				// TODO Auto-generated method stub
				Myservice = ((SensorService.LocalBinder) service).getService();
				Log.i("StartReceiver.ACTION_OUT_CALLING", "service start");
				Myservice.showViewCallWithAnsweringMode(true);
			}

		}, Context.BIND_AUTO_CREATE);
	}

}
