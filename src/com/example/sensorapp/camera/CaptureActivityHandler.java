/**
 * 
 */
package com.example.sensorapp.camera;

import android.content.Context;
import android.os.Handler;
import android.os.Message;

/**
 * @author Administrator
 * 
 */
public class CaptureActivityHandler extends Handler {

	/*
	 * Copyright (C) 2008 ZXing authors
	 * 
	 * Licensed under the Apache License, Version 2.0 (the "License"); you may
	 * not use this file except in compliance with the License. You may obtain a
	 * copy of the License at
	 * 
	 * http://www.apache.org/licenses/LICENSE-2.0
	 * 
	 * Unless required by applicable law or agreed to in writing, software
	 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
	 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
	 * License for the specific language governing permissions and limitations
	 * under the License.
	 */

	private static final String TAG = CaptureActivityHandler.class.getSimpleName();

	private final Context activity;
	// private final DecodeThread decodeThread;
	private State state;

	private enum State {
		PREVIEW, SUCCESS, DONE
	}

	public CaptureActivityHandler(Context activity) {
		this.activity = activity;

		state = State.SUCCESS;

		// Start ourselves capturing previews and decoding.
		CameraManager.get().startPreview();
		restartPreviewAndDecode();
	}

	@Override
	public void handleMessage(Message message) {
		switch (message.what) {

		}
	}

	private void restartPreviewAndDecode() {
		if (state == State.SUCCESS) {
			state = State.PREVIEW;
			// CameraManager.get().requestPreviewFrame(decodeThread.getHandler(),
			// R.id.decode);
			// CameraManager.get().requestAutoFocus(this, R.id.auto_focus);
			// activity.drawViewfinder();
		}
	}

}
