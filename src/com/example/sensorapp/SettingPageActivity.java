/**
 * 
 */
package com.example.sensorapp;

import com.example.sensorapp.music.Mp3Info;
import com.example.sensorapp.music.Mp3PlayerService;
import com.example.sensorapp.util.Util;

import android.R.integer;
import android.app.Activity;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.os.Bundle;
import android.os.IBinder;
import android.support.v4.widget.SimpleCursorAdapter.ViewBinder;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.Toast;
import android.widget.CompoundButton.OnCheckedChangeListener;

/**
 * @author Administrator
 * 
 */
public class SettingPageActivity extends Activity implements
		View.OnClickListener, OnCheckedChangeListener
{
	private View serviceFrameLayout;
	private View startServiceFrameLayout;
	private View setBackgroundFrameLayout;
	private CheckBox serviceStartCheckBox;
	private CheckBox openServiceCheckBox;

	public void onCreate(Bundle saveInstanceBundle)
	{
		super.onCreate(saveInstanceBundle);
		setContentView(R.layout.settingpage);
		init();
	}

	public void init()
	{
		serviceFrameLayout = findViewById(R.id.serviceFrameLayout);
		startServiceFrameLayout = findViewById(R.id.startServiceFrameLayout);
		setBackgroundFrameLayout = findViewById(R.id.setBackgroundFrameLayout);
		serviceStartCheckBox = (CheckBox) findViewById(R.id.serviceStartCheckBox);
		openServiceCheckBox = (CheckBox) findViewById(R.id.openServiceCheckBox);
		serviceFrameLayout.setOnClickListener(this);
		startServiceFrameLayout.setOnClickListener(this);
		setBackgroundFrameLayout.setOnClickListener(this);
		serviceStartCheckBox.setOnCheckedChangeListener(this);
		openServiceCheckBox.setOnCheckedChangeListener(this);

		SharedPreferences preferences = this.getSharedPreferences(
				"settingInfos", Context.MODE_PRIVATE);
		boolean autoStartServiceFlag = preferences.getBoolean(
				"autoStartService", false);

		boolean startServiceFlag = preferences.getBoolean("startService", true);
		serviceStartCheckBox.setChecked(autoStartServiceFlag);
		openServiceCheckBox.setChecked(startServiceFlag);

	}

	public boolean onKeyDown(int keyCode, KeyEvent event)
	{
		if (keyCode == KeyEvent.KEYCODE_BACK
				&& event.getAction() == KeyEvent.ACTION_DOWN)
		{
			//finish();
		}
		return super.onKeyDown(keyCode, event);
	}

	@Override
	public void onCheckedChanged(CompoundButton buttonView, boolean isChecked)
	{
		// TODO Auto-generated method stub
		switch (buttonView.getId())
		{
		case R.id.serviceStartCheckBox:
		{
			SharedPreferences preferences = this.getSharedPreferences(
					"settingInfos", Context.MODE_PRIVATE);
			Editor editor = preferences.edit();

			editor.putBoolean("autoStartService", isChecked);
			editor.commit();
		}
			break;
		case R.id.openServiceCheckBox:
		{
			SharedPreferences preferences = this.getSharedPreferences(
					"settingInfos", Context.MODE_PRIVATE);
			Editor editor = preferences.edit();

			editor.putBoolean("startService", isChecked);
			editor.commit();
			GlobalVar.isOpenSensor=isChecked;
			
			if (isChecked)
			{

				binderService();
				if (sensorService != null)
				{
					//sensorService.openSensor();
//					Intent intent=new Intent();
//					intent.setAction("action.hs.opensensor");
//					sendBroadcast(intent);
					Util.showToast(this, "服务已经启动,合上皮套即可享受更多有趣功能.",
							Toast.LENGTH_SHORT);
				}
			} else
			{
				binderService();
				if (sensorService != null)
				{
					//sensorService.closeSensor();
//					Intent intent=new Intent();
//					intent.setAction("action.hs.closesensor");
//					sendBroadcast(intent);
//					Intent intent1 = new Intent(this, SensorService.class);
//					this.stopService(intent1);
					Util.showToast(this, "服务暂时关闭,您将不能享受众多有趣内容.",
							Toast.LENGTH_SHORT);
				} else
				{
					Util.showToast(this, "服务关闭失败.请稍候再试.", Toast.LENGTH_SHORT);
				}
			}
			break;
		}

		default:
			break;
		}

	}

	@Override
	public void onClick(View v)
	{
		// TODO Auto-generated method stub
		switch (v.getId())
		{
		case R.id.serviceFrameLayout:
		{
			serviceStartCheckBox.setChecked(!serviceStartCheckBox.isChecked());
		}
			break;
		case R.id.startServiceFrameLayout:
		{
			openServiceCheckBox.setChecked(!openServiceCheckBox.isChecked());
			break;
		}
		case R.id.setBackgroundFrameLayout:
		{
			Intent intent = new Intent(this, SettingWallpaperActivity.class);
			intent.setAction(Intent.ACTION_VIEW);
			startActivity(intent);
			break;
		}

		default:
			break;
		}
	}

	private static SensorService sensorService;
	ServiceConnection sc = new ServiceConnection()
	{

		@Override
		public void onServiceDisconnected(ComponentName componentName)
		{
			sensorService = null;
			// 这里可以提示用户
		}

		@Override
		public void onServiceConnected(ComponentName name, IBinder service)
		{
			// TODO Auto-generated method stub
			if(sensorService==null)
			{
				sensorService = ((SensorService.LocalBinder) service).getService();
				SharedPreferences preferences = getSharedPreferences(
						"settingInfos", Context.MODE_PRIVATE);
				boolean startServiceFlag = preferences.getBoolean("startService", true);
				if(startServiceFlag)
				{
					//sensorService.openSensor();
//					Intent intent=new Intent();
//					intent.setAction("action.hs.opensensor");
//					sendBroadcast(intent);
				}
			}
		}
	};

	private void binderService()
	{
		Intent intent = new Intent(this, SensorService.class);
		this.bindService(intent, sc, Context.BIND_AUTO_CREATE);
	}

	@Override
	public void onDestroy()
	{
		super.onDestroy();
		try
		{
			this.unbindService(sc);
		}
		catch(Exception ex)
		{
			Log.e("error", ex.toString());
		}
	}
}
