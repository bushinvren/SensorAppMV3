/**
 * 
 */
package com.example.sensorapp.util;

import java.lang.reflect.Method;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.telephony.PhoneNumberUtils;
import android.telephony.TelephonyManager;
import android.view.KeyEvent;

/**
 * @author Administrator
 * 
 */
public class PhoneUtil {

	private Context context;

	public PhoneUtil(Context context) {
		this.context = context;
	}

	public void makCall(String number) {
		if (checkNumberInvalidate(number)) {
			Intent intent = new Intent();
			intent.setAction(Intent.ACTION_CALL);
			intent.setData(Uri.parse("tel:" + number));
			context.startActivity(intent);
		} else {

		}
	}

	static public com.android.internal.telephony.ITelephony getITelephony(TelephonyManager telMgr) throws Exception {
		Method getITelephonyMethod = telMgr.getClass().getDeclaredMethod("getITelephony");
		getITelephonyMethod.setAccessible(true);// 私有化函数也能使用
		return (com.android.internal.telephony.ITelephony) getITelephonyMethod.invoke(telMgr);
	}

	/**
	 * 伪造一个有线耳机插入，并按接听键的广播，让系统开始接听电话。
	 * 
	 * @param context
	 */
	private static synchronized void answerRingingCallWithBroadcast(Context context) {
		// 插耳机
		Intent localIntent1 = new Intent(Intent.ACTION_HEADSET_PLUG);
		localIntent1.addFlags(Intent.FLAG_ACTIVITY_NO_HISTORY);
		localIntent1.putExtra("state", 1);
		localIntent1.putExtra("microphone", 1);
		localIntent1.putExtra("name", "Headset");
		context.sendOrderedBroadcast(localIntent1, "android.permission.CALL_PRIVILEGED");
		// 按下耳机按钮
		Intent localIntent2 = new Intent(Intent.ACTION_MEDIA_BUTTON);
		KeyEvent localKeyEvent1 = new KeyEvent(KeyEvent.ACTION_DOWN, KeyEvent.KEYCODE_HEADSETHOOK);
		localIntent2.putExtra("android.intent.extra.KEY_EVENT", localKeyEvent1);
		context.sendOrderedBroadcast(localIntent2, "android.permission.CALL_PRIVILEGED");
		// 放开耳机按钮
		Intent localIntent3 = new Intent(Intent.ACTION_MEDIA_BUTTON);
		KeyEvent localKeyEvent2 = new KeyEvent(KeyEvent.ACTION_UP, KeyEvent.KEYCODE_HEADSETHOOK);
		localIntent3.putExtra("android.intent.extra.KEY_EVENT", localKeyEvent2);
		context.sendOrderedBroadcast(localIntent3, "android.permission.CALL_PRIVILEGED");
		// 拔出耳机
		Intent localIntent4 = new Intent(Intent.ACTION_HEADSET_PLUG);
		localIntent4.addFlags(Intent.FLAG_ACTIVITY_NO_HISTORY);
		localIntent4.putExtra("state", 0);
		localIntent4.putExtra("microphone", 1);
		localIntent4.putExtra("name", "Headset");
		context.sendOrderedBroadcast(localIntent4, "android.permission.CALL_PRIVILEGED");

	}

	/**
	 * 接听电话
	 * 
	 * @param context
	 */
	public static void answerRingingCall(Context context) {
		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.GINGERBREAD) { // 2.3或2.3以上系统
			answerRingingCallWithBroadcast(context);
		} else {
			// answerRingingCallWithReflect(context);
		}
	}

	private boolean checkNumberInvalidate(String number) {
		return !(number == null || number.equals("")) && PhoneNumberUtils.isGlobalPhoneNumber(number);
	}

}
