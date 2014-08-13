/**
 * 
 */
package com.example.sensorapp.util;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Build;
import android.provider.BaseColumns;
import android.provider.ContactsContract;
import android.provider.MediaStore;
import android.provider.MediaStore.Audio.AudioColumns;
import android.provider.MediaStore.MediaColumns;
import android.telephony.PhoneNumberUtils;
import android.telephony.TelephonyManager;
import android.view.KeyEvent;

import com.example.sensorapp.music.Mp3Info;

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

	public static List<Mp3Info> getMp3Infos(Context context) {
		Cursor cursor = context.getContentResolver().query(MediaStore.Audio.Media.EXTERNAL_CONTENT_URI, null, null, null, MediaStore.Audio.Media.DEFAULT_SORT_ORDER);

		List<Mp3Info> mp3Infos = new ArrayList<Mp3Info>();
		if (cursor == null)return mp3Infos;
		for (int i = 0; i < cursor.getCount(); i++) {
			cursor.moveToNext();
			Mp3Info mp3Info = new Mp3Info();
			long id = cursor.getLong(cursor.getColumnIndex(BaseColumns._ID)); // 音乐id
			String title = cursor.getString((cursor.getColumnIndex(MediaColumns.TITLE))); // 音乐标题
			String artist = cursor.getString(cursor.getColumnIndex(AudioColumns.ARTIST)); // 艺术家
			String album = cursor.getString(cursor.getColumnIndex(AudioColumns.ALBUM)); // 专辑
			long albumId = cursor.getInt(cursor.getColumnIndex(AudioColumns.ALBUM_ID));
			long duration = cursor.getLong(cursor.getColumnIndex(AudioColumns.DURATION)); // 时长
			long size = cursor.getLong(cursor.getColumnIndex(MediaColumns.SIZE)); // 文件大小
			String url = cursor.getString(cursor.getColumnIndex(MediaColumns.DATA)); // 文件路径
			int isMusic = cursor.getInt(cursor.getColumnIndex(AudioColumns.IS_MUSIC)); // 是否为音乐
			if (isMusic != 0) { // 只把音乐添加到集合当中
				mp3Info.setId(id);
				mp3Info.setTitle(title);
				mp3Info.setArtist(artist);
				mp3Info.setAlbum(album);
				mp3Info.setAlbumId(albumId);
				mp3Info.setDuration(duration);
				mp3Info.setSize(size);
				mp3Info.setUri(url);
				mp3Infos.add(mp3Info);
			}
		}
		cursor.close();
		return mp3Infos;
	}

}
