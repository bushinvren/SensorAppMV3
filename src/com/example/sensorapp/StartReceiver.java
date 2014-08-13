package com.example.sensorapp;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.util.Log;

public class StartReceiver extends BroadcastReceiver
{
	private static final String ACTION_BOOT_COMPLETED = Intent.ACTION_BOOT_COMPLETED;

	@Override
	public void onReceive(Context context, Intent intent)
	{

		if (ACTION_BOOT_COMPLETED.equals(intent.getAction()))
		{
			// TODO Auto-generated method stub
			SharedPreferences preferences = context.getSharedPreferences(
					"settingInfo", Context.MODE_PRIVATE);
			boolean autoStartServiceFlag = preferences.getBoolean(
					"autoStartService", true);

			Log.e("autoStartService", String.valueOf(autoStartServiceFlag));
			if (!autoStartServiceFlag)
				return;
			else
			{
				Intent myIntent = new Intent(context, SensorService.class);
				myIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
				context.startService(myIntent);
				Log.v("StartReceiver", "收到广播");
			}

		}
	}
}
