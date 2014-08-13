package com.example.sensorapp;

import android.R.integer;
import android.content.Context;
import android.os.Handler;
import android.os.Message;
import android.provider.Settings;
import android.telephony.PhoneStateListener;
import android.telephony.TelephonyManager;

public class MyPhoneStatesListener extends PhoneStateListener {
	private Context context;

	private int callTime = 0;
	private boolean isCalling = false;
	private Handler handler;

	private int currentState = TelephonyManager.CALL_STATE_IDLE;
	private String incomingNumber = "";

	public String getIncomingNumber() {
		return incomingNumber;
	}

	public int getCallTime() {
		return callTime;
	}

	public int getCurrentState() {
		return currentState;
	}

	public MyPhoneStatesListener(Context context, Handler handler) {
		this.context = context;
		this.handler = handler;
	}

	@Override
	public void onCallStateChanged(int state, String incomingNumber) {
		// TODO Auto-generated method stub
		super.onCallStateChanged(state, incomingNumber);
		currentState = state;
		do {
			if (state == TelephonyManager.CALL_STATE_IDLE) {
				isCalling = false;
				this.incomingNumber = "";

				handler.sendEmptyMessage(MSG_CALL_END);

				Settings.System.putString(context.getContentResolver(),
						"proximity_sensor", "1");
			}
			if (SensorService.isCloseCover == false) {
				break;
			}
			if (state == TelephonyManager.CALL_STATE_RINGING) {
				Message msg = handler.obtainMessage(MSG_CALL_RING);
				msg.obj = incomingNumber;
				handler.sendMessageDelayed(msg, 3000);

				this.incomingNumber = incomingNumber;

			} else if (state == TelephonyManager.CALL_STATE_OFFHOOK) {
				Settings.System.putString(context.getContentResolver(),
						"proximity_sensor", "0");// 禁止屏幕熄灭
				
				isCalling = true;
				Message msg = handler.obtainMessage(MSG_CALL_HOOK);
				msg.obj = incomingNumber;
				handler.sendMessage(msg);
				Thread t = new Thread(timeCounter);
				t.start();
			}
		} while (false);
	}

	final private Runnable timeCounter = new Runnable() {

		@Override
		public void run() {
			// TODO Auto-generated method stub
			callTime = 0;
			do {

				try {
					Thread.sleep(1000);
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				callTime++;
				// handler.sendEmptyMessage(1);
				Message msg = handler.obtainMessage(MSG_UPDATE_CALL_TIME);
				msg.arg1 = callTime;
				msg.sendToTarget();

			} while (isCalling);
		}
	};

	final static public int MSG_UPDATE_CALL_TIME = 1;
	final static public int MSG_CALL_RING = 2;
	final static public int MSG_CALL_END = 3;
	final static public int MSG_CALL_HOOK = 4;
}
