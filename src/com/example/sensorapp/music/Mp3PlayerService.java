/**
 * 
 */
package com.example.sensorapp.music;

import java.util.ArrayList;
import java.util.List;

import com.example.sensorapp.EnterActivity;
import com.example.sensorapp.R;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.media.AudioManager;
import android.media.AudioManager.OnAudioFocusChangeListener;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Binder;
import android.provider.BaseColumns;
import android.provider.MediaStore;
import android.provider.MediaStore.Audio.AudioColumns;
import android.provider.MediaStore.MediaColumns;
import android.util.Log;

/**
 * @author Administrator
 * 
 */
public class Mp3PlayerService extends Service implements Mp3Player {

	private static final String TAG = "Mp3PlayerService";

	private MediaPlayer player;

	private AudioManager mAm;

	private MyOnAudioFocusChangeListener mListener;

	private List<Mp3Info> mp3List = null;
	private int currentItemIndex = 0;

	private PlayStatusChangeListener listener;

	Notification mNotification;
	NotificationManager mNotificationManager;

	@Override
	public void onCreate() {
		mAm = (AudioManager) getApplicationContext().getSystemService(
				Context.AUDIO_SERVICE);
		mListener = new MyOnAudioFocusChangeListener();
		Thread thread = new Thread() {
			public void run() {
				mp3List = getMp3Infos(Mp3PlayerService.this);
			}
		};
		thread.start();
		initNotification();

	}

	private void initNotification() {
		PendingIntent pendingIntent = PendingIntent
				.getActivity(getApplicationContext(), 0, new Intent(
						getApplicationContext(), EnterActivity.class),
						PendingIntent.FLAG_UPDATE_CURRENT);
		mNotification = new Notification();
		mNotification.tickerText = "音乐正在播放中";

		mNotification.icon = R.drawable.ic_launcher;// TODO 更换服务前置的图标
		mNotification.flags = Notification.FLAG_ONGOING_EVENT;
		mNotification.icon = R.drawable.ic_launcher;
		mNotificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
		mNotification.setLatestEventInfo(getApplicationContext(), "", "",
				pendingIntent);
	}

	@Override
	public IBinder onBind(Intent intent) {
		return binder;
	}

	@Override
	public void onStart(Intent intent, int startid) {
		// Toast.makeText(this, "My Service Start", Toast.LENGTH_LONG).show();
		Log.i(TAG, "onStart");

	}

	@Override
	public void onDestroy() {
		// Toast.makeText(this, "My Service Stoped", Toast.LENGTH_LONG).show();
		Log.i(TAG, "onDestroy");
		if (player != null) {
			player.stop();
			player.release();
			player = null;

		}
		mAm.abandonAudioFocus(mListener);
	}

	private class MyOnAudioFocusChangeListener implements
			OnAudioFocusChangeListener {
		@Override
		public void onAudioFocusChange(int focusChange) {
			Log.i(TAG, "focusChange=" + focusChange);
		}
	}

	public void startPlay() {
		if (mp3List != null && mp3List.size() > 0) {
			play(Uri.parse(mp3List.get(currentItemIndex).getUri()));
			if (listener != null) {
				listener.onPlayStatusChange(player,
						mp3List.get(currentItemIndex));
			}
		}
	}

	@Override
	public void play(Uri uri) {
		// TODO Auto-generated method stub
		//

		if (player != null) {
			player.stop();
			player.release();
			player = null;
		}

		player = MediaPlayer.create(this, uri); // 在res目录下新建raw目录，复制一个test.mp3文件到此目录下。

		player.setLooping(false);

		// Request audio focus for playback
		int result = mAm.requestAudioFocus(mListener,
				AudioManager.STREAM_MUSIC, AudioManager.AUDIOFOCUS_GAIN);

		if (result == AudioManager.AUDIOFOCUS_REQUEST_GRANTED) {
			Log.i(TAG, "requestAudioFocus successfully.");

			// Start playback.
			if (player != null) {
				player.start();

				player.setOnCompletionListener(onCompletionListener);
			}
		} else {
			Log.e(TAG, "requestAudioFocus failed.");
		}
	}

	private MediaPlayer.OnCompletionListener onCompletionListener = new MediaPlayer.OnCompletionListener() {

		@Override
		public void onCompletion(MediaPlayer mp) {
			// TODO Auto-generated method stub
			next();
		}
	};

	@Override
	public void play() {
		// TODO Auto-generated method stub
		if (player != null) {
			player.start();
		}
	}

	@Override
	public void pause() {
		// TODO Auto-generated method stub
		if (player != null && player.isPlaying()) {
			player.pause();

			if (listener != null) {
				listener.onPlayStatusChange(player,
						mp3List.get(currentItemIndex));
			}
		}
	}

	@Override
	public void stop() {
		// TODO Auto-generated method stub
		if (player != null) {
			player.stop();

			if (listener != null) {
				listener.onPlayStatusChange(player,
						mp3List.get(currentItemIndex));
			}

		}
	}

	// liuzw .简单的循环播放。后期可以加入随机，单曲等模式。
	@Override
	public void prev() {
		// TODO Auto-generated method stub
		if (mp3List.size() > 0) {
			if (--currentItemIndex < 0) {
				currentItemIndex += mp3List.size();
			}
			play(Uri.parse(mp3List.get(currentItemIndex).getUri()));

			if (listener != null) {
				listener.onPlayStatusChange(player,
						mp3List.get(currentItemIndex));
			}
		}
	}

	@Override
	public void next() {
		// TODO Auto-generated method stub
		if (mp3List.size() > 0) {
			if (++currentItemIndex >= mp3List.size()) {
				currentItemIndex -= mp3List.size();
			}
			play(Uri.parse(mp3List.get(currentItemIndex).getUri()));
			if (listener != null) {
				listener.onPlayStatusChange(player,
						mp3List.get(currentItemIndex));
			}
		}
	}

	@Override
	public Object currentMp3Info() {
		// TODO Auto-generated method stub
		if (currentItemIndex >= 0 && currentItemIndex < mp3List.size()) {
			return mp3List.get(currentItemIndex);
		}
		return null;
	}

	@Override
	public boolean mp3Playing() {
		// TODO Auto-generated method stub
		if (player != null)
			return player.isPlaying();
		return false;
	}

	@Override
	public int onStartCommand(Intent intent, int flags, int startId) {
		Log.i(TAG, "onStartCommand");
		return START_STICKY_COMPATIBILITY;
	}

	private IBinder binder = new IBinder();

	// 定义内容类继承Binder
	public class IBinder extends Binder {
		// 返回本地服务
		public Mp3PlayerService getService() {
			return Mp3PlayerService.this;
		}
	}

	// liuzw. 为了不依赖其他文件，可以到处播放。必要代码写下面。

	private List<Mp3Info> getMp3Infos(Context context) {
		List<Mp3Info> mp3Infos = new ArrayList<Mp3Info>();
		do {
			Cursor cursor = context.getContentResolver().query(
					MediaStore.Audio.Media.EXTERNAL_CONTENT_URI, null, null,
					null, MediaStore.Audio.Media.DEFAULT_SORT_ORDER);

			if (cursor == null)// 有时候数据库可能还没建起来,如新系统.
				break;

			for (int i = 0; i < cursor.getCount(); i++) {
				cursor.moveToNext();
				Mp3Info mp3Info = new Mp3Info();
				long id = cursor
						.getLong(cursor.getColumnIndex(BaseColumns._ID)); // 音乐id
				String title = cursor.getString((cursor
						.getColumnIndex(MediaColumns.TITLE))); // 音乐标题
				String artist = cursor.getString(cursor
						.getColumnIndex(AudioColumns.ARTIST)); // 艺术家
				String album = cursor.getString(cursor
						.getColumnIndex(AudioColumns.ALBUM)); // 专辑
				long albumId = cursor.getInt(cursor
						.getColumnIndex(AudioColumns.ALBUM_ID));
				long duration = cursor.getLong(cursor
						.getColumnIndex(AudioColumns.DURATION)); // 时长
				long size = cursor.getLong(cursor
						.getColumnIndex(MediaColumns.SIZE)); // 文件大小
				String url = cursor.getString(cursor
						.getColumnIndex(MediaColumns.DATA)); // 文件路径
				int isMusic = cursor.getInt(cursor
						.getColumnIndex(AudioColumns.IS_MUSIC)); // 是否为音乐
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
		} while (false);

		return mp3Infos;
	}

	public void setOnPlayStatusChangeListener(PlayStatusChangeListener l) {
		this.listener = l;
	}

	public interface PlayStatusChangeListener {
		public void onPlayStatusChange(MediaPlayer player, Mp3Info currentInfo);
	}
}
