package com.example.sensorapp;

import android.app.KeyguardManager;
import android.app.KeyguardManager.KeyguardLock;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

public class ScreenReceiver extends BroadcastReceiver
{
	String SCREEN_ON = "android.intent.action.SCREEN_ON";  
	String SCREEN_OFF = "android.intent.action.SCREEN_OFF";  
	SensorService mSensorService;

	@Override
	public void onReceive(Context context, Intent arg1)
	{
		KeyguardManager mManager = (KeyguardManager)context.getSystemService("keyguard"); 
		KeyguardLock mKeyguardLock = mManager.newKeyguardLock("Lock");
		// TODO Auto-generated method stub
		// 屏幕唤醒  
	    if(SCREEN_ON.equals(arg1.getAction())){  
	        Log.e("screen", SCREEN_ON);
	      //让键盘锁失效 
			mKeyguardLock.disableKeyguard();
//			Intent intent1 = new Intent(context, SensorService.class);
//			context.startService(intent1);
	    }  
	    // 屏幕休眠  
	    else if(SCREEN_OFF.equals(arg1.getAction())){  
	        Log.e("screen", SCREEN_OFF); 
	      //让键盘锁失效 
			mKeyguardLock.reenableKeyguard(); 
//			Intent intent1 = new Intent(context, SensorService.class);
//			context.stopService(intent1);
	    }  
	}

}
