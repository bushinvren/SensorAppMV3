package com.example.sensorapp;

import java.lang.reflect.Method;
import java.text.SimpleDateFormat;

import android.app.Activity;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.graphics.PixelFormat;
import android.net.Uri;
import android.os.RemoteException;
import android.provider.ContactsContract;
import android.provider.Settings;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.TextView;

import com.android.internal.telephony.ITelephony;
import com.example.sensorapp.ui.LockImageView;

public class CallViewHelper implements LockImageView.OnUnLockListener,
		View.OnClickListener
{
	private String incomingNumber;
	// private long callTime;

	private Context context = null;
	private View view;

	private ImageView bgImageView;
	private TextView incomingNumberTextView;
	private LockImageView answerCallView;
	private LockImageView blockCallView;
	private TextView callTimeTextView;
	private View hangupView;
	private View callCheckLayout;
	private View answeringLayout;
	private View closeBtn;

	public ITelephony iTelephony;
	private int selectedIndex = 0;
	private WindowManager windowManager = null;
	WindowManager.LayoutParams param;

	private boolean visible = false;

	public boolean getVisible()
	{
		return visible;
	}

	private final static int[] rBacks =
	{ R.drawable.back00, R.drawable.back01, R.drawable.back02,
			R.drawable.back03, R.drawable.back04, R.drawable.back05,
			R.drawable.back06, R.drawable.back07, R.drawable.back08,
			R.drawable.back09 };

	public CallViewHelper(Context context)
	{
		this.context = context;
		LayoutInflater inflater = (LayoutInflater) context
				.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		view = inflater.inflate(R.layout.incomingcall_layout, null);
		init();
	}

	private void init()
	{

		windowManager = (WindowManager) context.getApplicationContext()
				.getSystemService(Context.WINDOW_SERVICE);
		param = new WindowManager.LayoutParams(
				WindowManager.LayoutParams.MATCH_PARENT,
				WindowManager.LayoutParams.MATCH_PARENT,
				WindowManager.LayoutParams.TYPE_SYSTEM_ERROR,
				WindowManager.LayoutParams.FLAG_FORCE_NOT_FULLSCREEN
						| WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON,
				PixelFormat.RGBA_8888);

		bgImageView = (ImageView) view.findViewById(R.id.bg);
		incomingNumberTextView = (TextView) view
				.findViewById(R.id.incomingCallInfo);
		answerCallView = (LockImageView) view.findViewById(R.id.answerCall);
		blockCallView = (LockImageView) view.findViewById(R.id.blockCall);
		callTimeTextView = (TextView) view.findViewById(R.id.callTime);
		hangupView = view.findViewById(R.id.hangupCall);
		callCheckLayout = view.findViewById(R.id.callCheckLayout);
		answeringLayout = view.findViewById(R.id.answeringLayout);
		closeBtn = view.findViewById(R.id.closeBtn);

		SharedPreferences preferences = context.getSharedPreferences(
				"settingInfos", Context.MODE_PRIVATE);
		selectedIndex = preferences.getInt("bgSelectedIndex", selectedIndex);

		bgImageView.setImageResource(rBacks[selectedIndex]);

		TelephonyManager telephonyManager = (TelephonyManager) context
				.getSystemService(Context.TELEPHONY_SERVICE);
		try
		{
			Method getITelephonyMethod = telephonyManager.getClass()
					.getDeclaredMethod("getITelephony");
			getITelephonyMethod.setAccessible(true);
			iTelephony = (ITelephony) getITelephonyMethod
					.invoke(telephonyManager);
		} catch (Exception e)
		{
			e.printStackTrace();
		}

		answerCallView.setOnLockListener(this);
		blockCallView.setOnLockListener(this);
		hangupView.setOnClickListener(this);
		closeBtn.setOnClickListener(this);
	}

	public void setBgImg()
	{
		SharedPreferences preferences = context.getSharedPreferences(
				"settingInfos", Context.MODE_PRIVATE);
		selectedIndex = preferences.getInt("bgSelectedIndex", selectedIndex);
		bgImageView.setImageResource(rBacks[selectedIndex]);
	}

	public void show()
	{
		setBgImg();
		if (visible == false)
		{
			windowManager.addView(view, param);
			visible = true;
			setAnsweringMode(false);
		}
	}

	public void setAnsweringMode(boolean t)
	{
		show();
		if (t)
		{
			callCheckLayout.setVisibility(View.INVISIBLE);
			answeringLayout.setVisibility(View.VISIBLE);

		} else
		{
			callCheckLayout.setVisibility(View.VISIBLE);
			answeringLayout.setVisibility(View.INVISIBLE);
			callTimeTextView.setText("");
			incomingNumberTextView.setText("");
		}
	}

	public void setIncomingNumber(String numberString)
	{
		if (visible)
		{
			try{
			incomingNumberTextView.setText(getContactNameFromPhoneBook(context,
					numberString));
			Log.e("test", getContactNameFromPhoneBook(context, numberString));
			}
			catch(Exception ex)
			{
				Log.e("incomingNumberEx",ex.toString());
			}
		}
	}

	public void close()
	{
		try
		{
			if (visible)
			{
				setAnsweringMode(false);
				windowManager.removeView(view);
				visible = false;
			}

		} catch (Exception e)
		{
			// TODO: handle exception
		}

	}

	private void endCall()
	{
		try
		{
			callCheckLayout.setVisibility(View.VISIBLE);
			answeringLayout.setVisibility(View.INVISIBLE);
			// liuzw. 2014 0122. iTelephony 可能为空。 异常后界面无法消除。
			if (iTelephony != null)
			{
				iTelephony.endCall();
			}

		} catch (RemoteException e)
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		} finally
		{
			close();
		}
	}

	private void answerCall()
	{
		try
		{

			callCheckLayout.setVisibility(View.INVISIBLE);
			answeringLayout.setVisibility(View.VISIBLE);

			// liuzw. 20140122. iTelephony == null.
			if (iTelephony != null)
			{
				iTelephony.answerRingingCall();
			}
		} catch (Exception e)
		{
			// TODO Auto-generated catch block
			Log.e("call_answer", e.toString());
			try
			{
				// 插耳机
				Intent localIntent1 = new Intent(Intent.ACTION_HEADSET_PLUG);
				localIntent1.addFlags(Intent.FLAG_ACTIVITY_NO_HISTORY);
				localIntent1.putExtra("state", 1);
				localIntent1.putExtra("microphone", 1);
				localIntent1.putExtra("name", "Headset");
				context.sendOrderedBroadcast(localIntent1,
						"android.permission.CALL_PRIVILEGED");

				// 按下耳机按钮
				Intent localIntent2 = new Intent(Intent.ACTION_MEDIA_BUTTON);
				KeyEvent localKeyEvent1 = new KeyEvent(KeyEvent.ACTION_DOWN,
						KeyEvent.KEYCODE_HEADSETHOOK);
				localIntent2.putExtra("android.intent.extra.KEY_EVENT",
						localKeyEvent1);
				context.sendOrderedBroadcast(localIntent2,
						"android.permission.CALL_PRIVILEGED");

				// 放开耳机按钮
				Intent localIntent3 = new Intent(Intent.ACTION_MEDIA_BUTTON);
				KeyEvent localKeyEvent2 = new KeyEvent(KeyEvent.ACTION_UP,
						KeyEvent.KEYCODE_HEADSETHOOK);
				localIntent3.putExtra("android.intent.extra.KEY_EVENT",
						localKeyEvent2);
				context.sendOrderedBroadcast(localIntent3,
						"android.permission.CALL_PRIVILEGED");

				// 拔出耳机
				Intent localIntent4 = new Intent(Intent.ACTION_HEADSET_PLUG);
				localIntent4.addFlags(Intent.FLAG_ACTIVITY_NO_HISTORY);
				localIntent4.putExtra("state", 0);
				localIntent4.putExtra("microphone", 1);
				localIntent4.putExtra("name", "Headset");
				context.sendOrderedBroadcast(localIntent4,
						"android.permission.CALL_PRIVILEGED");

			} catch (Exception e2)
			{
				Log.e("call_answer", e2.toString());
				Intent meidaButtonIntent = new Intent(
						Intent.ACTION_MEDIA_BUTTON);
				KeyEvent keyEvent = new KeyEvent(KeyEvent.ACTION_UP,
						KeyEvent.KEYCODE_HEADSETHOOK);
				meidaButtonIntent.putExtra(Intent.EXTRA_KEY_EVENT, keyEvent);
				context.sendOrderedBroadcast(meidaButtonIntent, "android.permission.CALL_PRIVILEGED");
			}
		}
	}

	public void updateCallTime(long time)
	{
		if (visible)
		{
			callTimeTextView.setText(getFormatTime(time));
		}
	}

	public static String getContactNameFromPhoneBook(Context context,
			String phoneNum)
	{
		String contactName = phoneNum;
		// ContentResolver cr = context.getContentResolver();
		// Cursor pCur = cr.query(
		// ContactsContract.CommonDataKinds.Phone.CONTENT_URI, null,
		// ContactsContract.CommonDataKinds.Phone.NUMBER + " = ?",
		// new String[] { phoneNum }, null);

		String[] projection =
		{ ContactsContract.PhoneLookup.DISPLAY_NAME,
				ContactsContract.PhoneLookup.NUMBER };
		Uri uri = Uri.withAppendedPath(
				ContactsContract.PhoneLookup.CONTENT_FILTER_URI,
				Uri.encode(phoneNum));
		Cursor cursor = context.getContentResolver().query(uri, projection,
				null, null, null);

		if (cursor == null)
			return phoneNum;
		if (cursor.moveToFirst())
		{
			contactName = cursor
					.getString(cursor
							.getColumnIndex(ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME));
		}
		cursor.close();
		return contactName;
	}

	public static String getFormatTime(long ms)
	{
		SimpleDateFormat formatter = new SimpleDateFormat("mm:ss");
		return formatter.format(ms * 1000);
	}

	// 用来模拟滑动接电话或挂电话。
	@Override
	public void onUnlocked(View v)
	{
		// TODO Auto-generated method stub
		switch (v.getId())
		{
		case R.id.answerCall:
		{
			answerCall();

		}
			break;
		case R.id.blockCall:
		{
			endCall();
		}
			break;
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
		case R.id.hangupCall:
		{
			endCall();
		}
			break;
		case R.id.closeBtn:
		{
			close();
		}
		default:
			break;
		}
	}
}
