package com.example.sensorapp;

import java.lang.reflect.Field;
import java.lang.reflect.Method;

import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.view.KeyEvent;

@Deprecated
public class PhoneUtil {
	public static String TAG = PhoneUtil.class.getSimpleName();

	static public com.android.internal.telephony.ITelephony getITelephony(TelephonyManager telMgr) throws Exception {
		Method getITelephonyMethod = telMgr.getClass().getDeclaredMethod("getITelephony");
		getITelephonyMethod.setAccessible(true);// 私有化函数也能使用
		return (com.android.internal.telephony.ITelephony) getITelephonyMethod.invoke(telMgr);
	}

	static public void printAllInform(Class clsShow) {
		try {
			// 取得所有方法
			Method[] hideMethod = clsShow.getDeclaredMethods();
			int i = 0;
			for (; i < hideMethod.length; i++) {
				Log.e("method name", hideMethod[i].getName());
			}
			// 取得所有常量
			Field[] allFields = clsShow.getFields();
			for (i = 0; i < allFields.length; i++) {
				Log.e("Field name", allFields[i].getName());
			}
		} catch (SecurityException e) {
			// throw new RuntimeException(e.getMessage());
			e.printStackTrace();
		} catch (IllegalArgumentException e) {
			// throw new RuntimeException(e.getMessage());
			e.printStackTrace();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	// /**
	// * 挂断电话
	// *
	// * @param context
	// */
	// public static void endCall(Context context)
	// {
	// try
	// {
	// Log.v("endCall", "endCall");
	// Object telephonyObject = getTelephonyObject(context);
	// if (null != telephonyObject)
	// {
	// Class telephonyClass = telephonyObject.getClass();
	//
	// Method endCallMethod = telephonyClass.getMethod("endCall");
	// endCallMethod.setAccessible(true);
	//
	// endCallMethod.invoke(telephonyObject);
	// }
	// } catch (SecurityException e)
	// {
	// e.printStackTrace();
	// } catch (NoSuchMethodException e)
	// {
	// e.printStackTrace();
	// } catch (IllegalArgumentException e)
	// {
	// e.printStackTrace();
	// } catch (IllegalAccessException e)
	// {
	// e.printStackTrace();
	// } catch (InvocationTargetException e)
	// {
	// e.printStackTrace();
	// }
	//
	// }

	// private static Object getTelephonyObject(Context context)
	// {
	// Object telephonyObject = null;
	// try
	// {
	// // 初始化iTelephony
	// TelephonyManager telephonyManager = (TelephonyManager) context
	// .getSystemService(Context.TELEPHONY_SERVICE);
	// // Will be used to invoke hidden methods with reflection
	// // Get the current object implementing ITelephony interface
	// Class telManager = telephonyManager.getClass();
	// Method getITelephony = telManager
	// .getDeclaredMethod("getITelephony");
	// getITelephony.setAccessible(true);
	// telephonyObject = getITelephony.invoke(telephonyManager);
	// } catch (SecurityException e)
	// {
	// e.printStackTrace();
	// } catch (NoSuchMethodException e)
	// {
	// e.printStackTrace();
	// } catch (IllegalArgumentException e)
	// {
	// e.printStackTrace();
	// } catch (IllegalAccessException e)
	// {
	// e.printStackTrace();
	// } catch (InvocationTargetException e)
	// {
	// e.printStackTrace();
	// }
	// return telephonyObject;
	// }
	//
	// /**
	// * 通过反射调用的方法，接听电话，该方法只在android 2.3之前的系统上有效。
	// *
	// * @param context
	// */
	// private static void answerRingingCallWithReflect(Context context)
	// {
	// try
	// {
	// Object telephonyObject = getTelephonyObject(context);
	// if (null != telephonyObject)
	// {
	// Class telephonyClass = telephonyObject.getClass();
	// Method endCallMethod = telephonyClass
	// .getMethod("answerRingingCall");
	// endCallMethod.setAccessible(true);
	//
	// endCallMethod.invoke(telephonyObject);
	// // ITelephony iTelephony = (ITelephony) telephonyObject;
	// // iTelephony.answerRingingCall();
	// }
	// } catch (SecurityException e)
	// {
	// e.printStackTrace();
	// } catch (IllegalArgumentException e)
	// {
	// e.printStackTrace();
	// } catch (IllegalAccessException e)
	// {
	// e.printStackTrace();
	// } catch (InvocationTargetException e)
	// {
	// e.printStackTrace();
	// } catch (NoSuchMethodException e)
	// {
	// e.printStackTrace();
	// }
	//
	// }

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

	// /**
	// * 打电话
	// *
	// * @param context
	// * @param phoneNumber
	// */
	// public static void callPhone(Context context, String phoneNumber)
	// {
	// if (!TextUtils.isEmpty(phoneNumber))
	// {
	// try
	// {
	// Intent callIntent = new Intent(Intent.ACTION_CALL,
	// Uri.parse("tel:" + phoneNumber));
	// context.startActivity(callIntent);
	// } catch (Exception e)
	// {
	// e.printStackTrace();
	// }
	// }
	// }
	//
	// /**
	// * 拨电话
	// *
	// * @param context
	// * @param phoneNumber
	// */
	// public static void dialPhone(Context context, String phoneNumber)
	// {
	// if (!TextUtils.isEmpty(phoneNumber))
	// {
	// try
	// {
	// Intent callIntent = new Intent(Intent.ACTION_DIAL,
	// Uri.parse("tel:" + phoneNumber));
	// context.startActivity(callIntent);
	// } catch (Exception e)
	// {
	// e.printStackTrace();
	// }
	// }
	// }
}
