/**
 * 
 */
package com.example.sensorapp.ui;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.SharedPreferences;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.IBinder;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.example.sensorapp.R;
import com.example.sensorapp.music.Mp3Info;
import com.example.sensorapp.music.Mp3PlayerService;
import com.example.sensorapp.music.Mp3PlayerService.PlayStatusChangeListener;

/**
 * @author Administrator
 * 
 */
public class MediaPageFragment extends Object implements View.OnClickListener {

	private View view;
	// private Context context;

	private ImageView imageViewcameraicon;
	private ImageView imageViewmessageicon;
	private ImageView imageViewmusicicon;
	private ImageView imageViewprevious;
	private ImageView imageViewplay;
	private ImageView imageViewnext;
	private TextView textViewCurrentMusicName;
	SuperFragmentActivity activity;
	Intent intent;

	private LinearLayout settingBgLayout;
	
	// private int rBacks[];
	// 实现无限循环滚动过程需要不停的new fragment. 因此界面上的一此状态要共享。
	private static Boolean playMusic = false;
	private static String currentMusicName = "";

	private View.OnClickListener externalClickListener;

	public void setOnClickListener(View.OnClickListener listener) {
		this.externalClickListener = listener;
	}

	private Mp3PlayerService mp3PlayerService;

	public MediaPageFragment(Context context) {
		activity = (SuperFragmentActivity) context;
	}

	public View onCreateView(LayoutInflater inflater, ViewGroup root,
			Bundle bundle) {
		view = inflater.inflate(R.layout.mediapage, null);
		// activity = (SuperFragmentActivity) getActivity();
		init();
		return view;
	}

	
	
	private void init() {
		imageViewcameraicon = (ImageView) view
				.findViewById(R.id.imageViewcameraicon);
		imageViewmessageicon = (ImageView) view
				.findViewById(R.id.imageViewmessageicon);
		imageViewmusicicon = (ImageView) view
				.findViewById(R.id.imageViewmusicicon);
		imageViewprevious = (ImageView) view
				.findViewById(R.id.imageViewprevious);
		imageViewplay = (ImageView) view.findViewById(R.id.imageViewplay);
		imageViewnext = (ImageView) view.findViewById(R.id.imageViewnext);

		textViewCurrentMusicName = (TextView) view
				.findViewById(R.id.textViewCurrentMusicName);

		imageViewcameraicon.setOnClickListener(this);
		imageViewmessageicon.setOnClickListener(this);
		imageViewmusicicon.setOnClickListener(this);
		imageViewprevious.setOnClickListener(this);
		imageViewplay.setOnClickListener(this);
		imageViewnext.setOnClickListener(this);

		binderService();
	}

	public void unbindService()
	{
		activity.unbindService(sc);
	}
	
	private ServiceConnection sc=new ServiceConnection()
	{
		
		@Override
		public void onServiceDisconnected(ComponentName arg0)
		{
			// TODO Auto-generated method stub
			mp3PlayerService = null;
		}
		
		@Override
		public void onServiceConnected(ComponentName name, IBinder service)
		{
			// TODO Auto-generated method stub
			mp3PlayerService = ((Mp3PlayerService.IBinder) service)
					.getService();
			mp3PlayerService
					.setOnPlayStatusChangeListener(playStatusChangeListener);
			if (mp3PlayerService.mp3Playing()) {
				imageViewplay.setBackgroundResource(R.drawable.pause);
				Mp3Info currentInfo = (Mp3Info) mp3PlayerService
						.currentMp3Info();
				if (currentInfo != null) {

					textViewCurrentMusicName.setText(currentInfo
							.getArtist() + "-" + currentInfo.getTitle());
					// Log.e("music info:", currentInfo.getArtist() + "," +
					// currentInfo.getTitle() + "," +
					// currentInfo.getAlbum());
				}
			} else {
				imageViewplay.setBackgroundResource(R.drawable.play);
			}
		}
	};
	
	private void binderService() {
	    intent = new Intent(activity, Mp3PlayerService.class);
		activity.bindService(intent, sc, Context.BIND_AUTO_CREATE);
	}

	private PlayStatusChangeListener playStatusChangeListener = new PlayStatusChangeListener() {

		@Override
		public void onPlayStatusChange(MediaPlayer player, Mp3Info currentInfo) {
			// TODO Auto-generated method stub
			if (player.isPlaying()) {
				imageViewplay.setBackgroundResource(R.drawable.pause);
			} else {
				imageViewplay.setBackgroundResource(R.drawable.play);
			}
			if (currentInfo != null) {

				textViewCurrentMusicName.setText(currentInfo.getArtist() + "-"
						+ currentInfo.getTitle());
				Log.e("music info:", currentInfo.getArtist() + ","
						+ currentInfo.getTitle() + "," + currentInfo.getAlbum());
			}
		}
	};

	private View.OnClickListener settingPageOnClickListener = new View.OnClickListener() {

		@Override
		public void onClick(View arg0) {
			// TODO Auto-generated method stub
			int tag = (Integer) arg0.getTag();
			SharedPreferences preferences = activity.getSharedPreferences();
			preferences.edit().putInt("bgSelectedIndex", tag).commit();
			// imageViewmusicicon.setBackgroundResource(rBgs[tag]);
			settingBgLayout.setVisibility(View.GONE);
			// imageViewmusicicon.setImageResource(rBgs[tag]);
			if (externalClickListener != null) {
				externalClickListener.onClick(arg0);
			}
			// view.invalidate();
		}
	};

	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		switch (v.getId()) {
		case R.id.imageViewcameraicon: {
			CaptureViewFragment captureViewFragment = new CaptureViewFragment();
			activity.addFragment(captureViewFragment,
					CaptureViewFragment.class.getSimpleName());
		}
			break;
		case R.id.imageViewmessageicon: {

			if (externalClickListener != null) {// /涉及多层次界面调用,开放接口,自行处理.
				externalClickListener.onClick(v);
			}
			break;
		}
		case R.id.imageViewmusicicon: {
//			if (settingBgLayout.getVisibility() != View.VISIBLE) {
//				settingBgLayout.setVisibility(View.VISIBLE);
//			} else {
//				settingBgLayout.setVisibility(View.GONE);
//			}
			// view.invalidate();
			break;
		}
		case R.id.imageViewnext: {
			if (mp3PlayerService != null) {
				mp3PlayerService.next();
			}
			break;
		}
		case R.id.imageViewplay: {
			if (mp3PlayerService != null && mp3PlayerService.mp3Playing()) {
				mp3PlayerService.pause();
			} else {
				mp3PlayerService.startPlay();
			}
			break;
		}
		case R.id.imageViewprevious: {
			if (mp3PlayerService != null && mp3PlayerService != null) {
				mp3PlayerService.prev();
			}
			break;
		}

		default:
			break;
		}

	}

}
