/**
 * 
 */
package com.example.sensorapp.ui;

import java.io.IOException;

import android.content.Context;
import android.hardware.Camera;
import android.hardware.Camera.AutoFocusCallback;
import android.hardware.Camera.PictureCallback;
import android.hardware.Camera.ShutterCallback;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.SurfaceHolder;
import android.view.SurfaceHolder.Callback;
import android.view.SurfaceView;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.GridLayout;
import android.widget.ImageView;
import android.widget.Toast;

import com.example.sensorapp.R;
import com.example.sensorapp.camera.CameraManager;
import com.example.sensorapp.camera.ImageSaver;
import com.example.sensorapp.util.Util;

/**
 * @author Administrator
 * 
 */
public class CaptureViewFragment extends Fragment implements Callback {
	private View view;
	private Context context;
	private SurfaceHolder surfaceHolder;

	private ImageView takePicture;
	private ImageView imageViewShutdown;

	private final static int AUTO_FOCUS_INTERVEL = 3000;

	private Boolean takePictureBtnClicked = false;

	MediaPlayer shootMP;
	// private Timer autoFocusTimer;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup root, Bundle bundle) {
		view = inflater.inflate(R.layout.camera_preview_layout, null);
		context = getActivity();
		init();
		return view;
	}

	protected void init() {

		CameraManager.init(getActivity().getApplication());
		// initCamera(surfaceHolder);
		takePicture = (ImageView) view.findViewById(R.id.imageViewTakephoto);
		takePicture.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				takePictureBtnClicked = true;
				handler.removeCallbacks(autoFocusTask);
				if (hasSurface) {
					CameraManager.get().setAutoFocusCallback(autoFocusCallback);
					
				}
				Util.showToast(context, "正在聚焦拍照中，请稍候。", Toast.LENGTH_SHORT);
			}
		});

		imageViewShutdown = (ImageView) view.findViewById(R.id.imageViewShutdown);
		imageViewShutdown.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				// 关闭此窗口。
				((SuperFragmentActivity) getActivity()).removeFragment(CaptureViewFragment.this);
			}
		});
	}

	@Override
	public void onResume() {
		super.onResume();

		SurfaceView surfaceView = (SurfaceView) view.findViewById(R.id.preview_view);
		SurfaceHolder surfaceHolder = surfaceView.getHolder();

		if (hasSurface) {
			initCamera(surfaceHolder);
		} else {
			surfaceHolder.addCallback(this);
			surfaceHolder.setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS);
		}
		handler.postDelayed(autoFocusTask, AUTO_FOCUS_INTERVEL);
	}

	@Override
	public void onPause() {
		super.onPause();
		CameraManager.get().stopPreview();
		CameraManager.get().closeDriver();
		handler.removeCallbacks(autoFocusTask);
	}

	@Override
	public void onDestroy() {
		// inactivityTimer.shutdown();
		super.onDestroy();
		CameraManager.get().stopPreview();
		CameraManager.get().closeDriver();
		handler.removeCallbacks(autoFocusTask);
	}

	private void initCamera(SurfaceHolder surfaceHolder) {
		try {
			CameraManager.get().openDriver(surfaceHolder);
			CameraManager.get().startPreview();
			// CameraManager.get().setAutoFocusCallback(autoFocusCallback);

		} catch (IOException ioe) {
			return;
		} catch (RuntimeException e) {
			return;
		}
	}

	@Override
	public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {

	}

	@Override
	public void surfaceCreated(SurfaceHolder holder) {
		if (!hasSurface) {
			hasSurface = true;
			initCamera(holder);
		}
	}

	@Override
	public void surfaceDestroyed(SurfaceHolder holder) {
		// TODO Auto-generated method stub
		hasSurface = false;
		// camera.release(); // Release camera resources
		// camera = null;
	}

	private boolean hasSurface;

	private AutoFocusCallback autoFocusCallback = new AutoFocusCallback() {

		@Override
		public void onAutoFocus(boolean success, Camera camera) {
			// TODO Auto-generated method stub
			// Log.e("autoFocus success", success + "");
			if (takePictureBtnClicked && success) {
				// handler.removeCallbacks(autoFocusTask);
				camera.takePicture(myShutterCallback, null, null, jpegSaveCallback);
			}
		}
	};
	
	ShutterCallback myShutterCallback = new ShutterCallback()   
	//快门按下的回调，在这里我们可以设置类似播放“咔嚓”声之类的操作。默认的就是咔嚓。  
	{  

	    public void onShutter() {  
	        // TODO Auto-generated method stub  
	        Log.i("tag", "myShutterCallback:onShutter...");  
	        shootSound();
	    }  
	}; 
	
	/**
	 *   播放系统拍照声音
	 */
	public void shootSound()
	{
//	    AudioManager meng = (AudioManager) context.getSystemService(Context.AUDIO_SERVICE);
//	    int volume = meng.getStreamVolume( AudioManager.STREAM_NOTIFICATION);
//	    
//	    if (volume != 0)
//	    {
//	        if (shootMP == null)
//	            shootMP = MediaPlayer.create(context, Uri.parse("file:///system/media/audio/ui/camera_click.ogg"));
//	        if (shootMP != null)
//	            shootMP.start();
//	    }
	}

	private Handler handler = new Handler();

	private Runnable autoFocusTask = new Runnable() {
		@Override
		public void run() {
			if (hasSurface) {
				CameraManager.get().setAutoFocusCallback(autoFocusCallback);
			}
			handler.postDelayed(this, AUTO_FOCUS_INTERVEL);
			// postDelayed(this,1000)
		}
	};

	private PictureCallback jpegSaveCallback = new PictureCallback() {

		@Override
		public void onPictureTaken(byte[] data, Camera camera) {
			// TODO Auto-generated method stub
			// CameraManager.get().startPreview();
			{
				// save bitmap
				new ImageSaver(getActivity()).execute(data);

				Util.showToast(context, "照片保存成功。", Toast.LENGTH_SHORT);
			}

			takePictureBtnClicked = false;
			camera.startPreview();
			handler.postDelayed(autoFocusTask, 100);

		}

	};
}
