package com.example.sensorapp;

import java.util.HashMap;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.telephony.TelephonyManager;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.example.sensorapp.Http.NetActionListener;
import com.example.sensorapp.util.Util;

public class RegisterActivity extends Activity {
	private Button btn_register;
	private EditText et_sNum;

	// liuzw.20140122.
	private final static int MSG_CORRECT = 1;
	private final static int MSG_ERROR = 2;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.register);

		et_sNum = (EditText) findViewById(R.id.sNum);
		btn_register = (Button) findViewById(R.id.confirm);

		btn_register.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				if (et_sNum.getText().toString().equals("")) {

					Util.showToast(RegisterActivity.this, "请输入正确的序列号",
							Toast.LENGTH_SHORT);
				} else {
					TelephonyManager tm = (TelephonyManager) getSystemService(Context.TELEPHONY_SERVICE);
					String deviceNum = tm.getDeviceId();
					String sNum = et_sNum.getText().toString().toUpperCase()
							.trim();
					registeFromNet(sNum, deviceNum);
				}

			}
		});
	}

	private void registeFromNet(String sNum, String deviceNum) {
		HttpParameter parameter = new HttpParameter();
		parameter.url = "http://115.28.42.231:8080/idserver/checkSNUM";
		parameter.param2 = new HashMap<String, String>();
		parameter.param2.put("sNum", sNum);
		parameter.param2.put("deviceNum", deviceNum);
		parameter.listener = listener;

		// parameter.url = "http://112.124.15.190/everycontrol_test/" +
		// "getPublicMessage";
		// parameter.param2 = new HashMap<String, String>();
		// int smid= 0;
		// parameter.param2.put("id", String.valueOf(0));
		// parameter.param2.put("os", "android");
		// parameter.param2.put("clientId", "00");
		// parameter.listener = listener;

		new Http(parameter).get();
	}

	private final NetActionListener listener = new NetActionListener() {

		@Override
		public void onNetProgressChanged(float progress, HttpParameter parameter) {
			// TODO Auto-generated method stub

		}

		@Override
		public void onNetFinished(String a, HttpParameter parameter) {

			if (a.equals("true")) {
				SharedPreferences preferences = getApplicationContext()
						.getSharedPreferences("registerInfo",
								Context.MODE_PRIVATE);
				Editor editor = preferences.edit();
				editor.putBoolean("register", true);
				editor.commit();

				Message message = new Message();
				message.what = MSG_CORRECT;
				handler.sendMessage(message);

				return;
			} else {
				Message message = new Message();
				message.what = MSG_ERROR;
				handler.sendMessage(message);
			}
		}

		@Override
		public void onNetError(String message, HttpParameter parameter) {
			// TODO Auto-generated method stub
			Message message1 = new Message();
			message1.what = MSG_ERROR;
			handler.sendMessage(message1);
		}
	};

	Handler handler = new Handler() {

		@Override
		public void handleMessage(Message msg) {
			switch (msg.what) {
			case MSG_ERROR:

				Util.showToast(RegisterActivity.this,
						"注册失败，请检查序列号是否正确或网络是否正常！", Toast.LENGTH_SHORT);
				break;

			case MSG_CORRECT:
				Intent intent = new Intent();
				intent.setClass(RegisterActivity.this, EnterActivity.class);
				startActivity(intent);
				RegisterActivity.this.finish();

				break;
			}
			super.handleMessage(msg);
		}
	};

	@Override
	public void onDestroy() {
		super.onDestroy();
	}
}
