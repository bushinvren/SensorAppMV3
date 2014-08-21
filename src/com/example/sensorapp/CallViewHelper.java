package com.example.sensorapp;

import java.lang.reflect.Method;
import java.text.SimpleDateFormat;
import java.util.Locale;

import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.PixelFormat;
import android.graphics.Typeface;
import android.graphics.drawable.AnimationDrawable;
import android.net.Uri;
import android.os.RemoteException;
import android.provider.ContactsContract;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.TextView;

import com.android.internal.telephony.ITelephony;

public class CallViewHelper implements View.OnClickListener {
	// private String incomingNumber;
	// private long callTime;

	private Context context = null;
	private View view;

	private TextView incomingNumberTextView;
	// private LockImageView answerCallView;
	// private LockImageView blockCallView;
	private TextView callTimeTextView;
	private View hangupView;
	private View callCheckLayout;
	private View answeringLayout;
	private View closeBtn;
	private ImageView hangCallImageView;

	public ITelephony iTelephony;
	// private int selectedIndex = 0;
	private WindowManager windowManager = null;
	WindowManager.LayoutParams param;

	private boolean visible = false;
	private float startY = 0;

	public boolean getVisible() {
		return visible;
	}

	public CallViewHelper(Context context) {
		this.context = context;
		LayoutInflater inflater = (LayoutInflater) context
				.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		view = inflater.inflate(R.layout.incomingcall_layout, null);
		init();
	}

	private void init() {

		windowManager = (WindowManager) context.getApplicationContext()
				.getSystemService(Context.WINDOW_SERVICE);
		param = new WindowManager.LayoutParams(
				WindowManager.LayoutParams.MATCH_PARENT,
				WindowManager.LayoutParams.MATCH_PARENT,
				WindowManager.LayoutParams.TYPE_SYSTEM_ERROR,
				WindowManager.LayoutParams.FLAG_FULLSCREEN
						| WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON,
				PixelFormat.RGBA_8888);

		Typeface fontFace = Typeface.createFromAsset(context.getAssets(),
				"DFPixelFont.ttf");
		incomingNumberTextView = (TextView) view
				.findViewById(R.id.incomingCallInfo);
		incomingNumberTextView.setTypeface(fontFace);
		hangCallImageView = (ImageView) view.findViewById(R.id.hangingCall);
		hangCallImageView.setBackgroundResource(R.drawable.incoming_anim);
		AnimationDrawable anim = (AnimationDrawable) hangCallImageView
				.getBackground();
		anim.start();

		callTimeTextView = (TextView) view.findViewById(R.id.callTime);
		callTimeTextView.setTypeface(fontFace);
		hangupView = view.findViewById(R.id.hangupCall);
		callCheckLayout = view.findViewById(R.id.callCheckLayout);
		answeringLayout = view.findViewById(R.id.answeringLayout);
		closeBtn = view.findViewById(R.id.closeBtn);

		TelephonyManager telephonyManager = (TelephonyManager) context
				.getSystemService(Context.TELEPHONY_SERVICE);
		try {
			Method getITelephonyMethod = telephonyManager.getClass()
					.getDeclaredMethod("getITelephony");
			getITelephonyMethod.setAccessible(true);
			iTelephony = (ITelephony) getITelephonyMethod
					.invoke(telephonyManager);
		} catch (Exception e) {
			e.printStackTrace();
		}

		// answerCallView.setOnLockListener(this);
		// blockCallView.setOnLockListener(this);
		hangCallImageView.setOnClickListener(this);
		hangupView.setOnClickListener(this);
		closeBtn.setOnClickListener(this);

		hangCallImageView.setOnTouchListener(new OnTouchListener() {

			@Override
			public boolean onTouch(View v, MotionEvent event) {
				int y = (int) event.getRawY();

				switch (event.getAction()) {
				case MotionEvent.ACTION_DOWN:
					startY = y;
					break;
				case MotionEvent.ACTION_MOVE:
					break;
				case MotionEvent.ACTION_UP:
					if (y - startY > 20) {
						endCall();
					} else if (y - startY < -20) {
						answerCall();
					}
					break;
				}
				return true;// 处理了触摸消息，消息不再传递
			}
		});
		
		hangupView.setOnTouchListener(new OnTouchListener() {

			@Override
			public boolean onTouch(View v, MotionEvent event) {
				int y = (int) event.getRawY();

				switch (event.getAction()) {
				case MotionEvent.ACTION_DOWN:
					startY = y;
					break;
				case MotionEvent.ACTION_MOVE:
					break;
				case MotionEvent.ACTION_UP:
					if (y - startY > 20) {
						endCall();
					}
					break;
				}
				return true;// 处理了触摸消息，消息不再传递
			}
		});
		
	}

	public void show() {
		if (visible == false) {
			windowManager.addView(view, param);
			visible = true;
			setAnsweringMode(false);
		}
	}

	public void setAnsweringMode(boolean t) {
		show();
		if (t) {
			callCheckLayout.setVisibility(View.INVISIBLE);
			answeringLayout.setVisibility(View.VISIBLE);

		} else {
			callCheckLayout.setVisibility(View.VISIBLE);
			answeringLayout.setVisibility(View.INVISIBLE);
			callTimeTextView.setText("");
			incomingNumberTextView.setText("");
		}
	}

	public void setIncomingNumber(String numberString) {
		if (visible) {
			try {
				incomingNumberTextView.setText(getContactNameFromPhoneBook(
						context, numberString));
				Log.e("test",
						getContactNameFromPhoneBook(context, numberString));
			} catch (Exception ex) {
				Log.e("incomingNumberEx", ex.toString());
			}
		}
	}

	public void close() {
		try {
			if (visible) {
				setAnsweringMode(false);
				windowManager.removeView(view);
				visible = false;
			}

		} catch (Exception e) {
		}

	}

	private void endCall() {
		try {
			callCheckLayout.setVisibility(View.VISIBLE);
			answeringLayout.setVisibility(View.INVISIBLE);
			// liuzw. 2014 0122. iTelephony 可能为空。 异常后界面无法消除。
			if (iTelephony != null) {
				iTelephony.endCall();
			}

		} catch (RemoteException e) {
			e.printStackTrace();
		} finally {
			close();
		}
	}

	private void answerCall() {
		try {

			incomingNumberTextView.setText("");
			callCheckLayout.setVisibility(View.INVISIBLE);
			answeringLayout.setVisibility(View.VISIBLE);

			// liuzw. 20140122. iTelephony == null.
			if (iTelephony != null) {
				iTelephony.answerRingingCall();
			}
		} catch (Exception e) {
			Log.e("call_answer", e.toString());
			try {
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

			} catch (Exception e2) {
				Log.e("call_answer", e2.toString());
				Intent meidaButtonIntent = new Intent(
						Intent.ACTION_MEDIA_BUTTON);
				KeyEvent keyEvent = new KeyEvent(KeyEvent.ACTION_UP,
						KeyEvent.KEYCODE_HEADSETHOOK);
				meidaButtonIntent.putExtra(Intent.EXTRA_KEY_EVENT, keyEvent);
				context.sendOrderedBroadcast(meidaButtonIntent,
						"android.permission.CALL_PRIVILEGED");
			}
		}
	}

	public void updateCallTime(long time) {
		if (visible) {
			callTimeTextView.setText(getFormatTime(time));
		}
	}

	public static String getContactNameFromPhoneBook(Context context,
			String phoneNum) {
		String contactName = phoneNum;
		// ContentResolver cr = context.getContentResolver();
		// Cursor pCur = cr.query(
		// ContactsContract.CommonDataKinds.Phone.CONTENT_URI, null,
		// ContactsContract.CommonDataKinds.Phone.NUMBER + " = ?",
		// new String[] { phoneNum }, null);

		String[] projection = { ContactsContract.PhoneLookup.DISPLAY_NAME,
				ContactsContract.PhoneLookup.NUMBER };
		Uri uri = Uri.withAppendedPath(
				ContactsContract.PhoneLookup.CONTENT_FILTER_URI,
				Uri.encode(phoneNum));
		Cursor cursor = context.getContentResolver().query(uri, projection,
				null, null, null);

		if (cursor == null)
			return phoneNum;
		if (cursor.moveToFirst()) {
			contactName = cursor
					.getString(cursor
							.getColumnIndex(ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME));
		}
		cursor.close();
		return contactName;
	}

	public static String getFormatTime(long ms) {
		SimpleDateFormat formatter = new SimpleDateFormat("mm:ss",
				Locale.getDefault());
		return formatter.format(ms * 1000);
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.hangupCall: {
			// endCall();
		}
			break;
		case R.id.closeBtn: {
			close();
			break;
		}
		case R.id.hangingCall: {
			// endCall();
			break;
		}
		default:
			break;
		}
	}
}
