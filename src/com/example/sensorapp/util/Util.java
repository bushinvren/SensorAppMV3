/**
 * 
 */
package com.example.sensorapp.util;

import android.content.Context;
import android.view.Gravity;
import android.widget.Toast;

/**
 * @author Administrator
 * 
 */
public class Util {

	public static void showToast(Context context, CharSequence text, int duration) {
		Toast toast = Toast.makeText(context, text, duration);
		toast.setGravity(Gravity.TOP | Gravity.CENTER_HORIZONTAL, 0, 100);
		toast.show();
	}

}
