/**
 * 
 */
package com.example.sensorapp.camera;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Date;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Environment;
import android.text.format.DateFormat;

// save pic
public class ImageSaver extends AsyncTask<byte[], String, String> {
	private Context mContext=null;
	
	@Override
	protected String doInBackground(byte[]... params) {
		String fname = "DCIM_" + DateFormat.format("yyyyMMddhhmmss", new Date()).toString() + ".jpg";

		File picture = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DCIM) +"/"+ fname);
		try {
			picture.createNewFile();

			FileOutputStream fos = new FileOutputStream(picture.getPath());
			fos.write(params[0]); // Written to the file
			fos.close();
			//mContext.sendBroadcast(new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE, Uri.parse("file://"+Environment.getExternalStorageDirectory())));
			//mContext.sendBroadcast(new Intent(Intent.ACTION_MEDIA_MOUNTED, Uri.parse("file://"+ Environment.getExternalStorageDirectory())));
			scanFileAsync(mContext, picture.getPath());
		} catch (IOException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		return null;
	}
	
	public ImageSaver(Context context)
	{
		this.mContext=context;
	}
	
	public void scanFileAsync(Context ctx, String filePath) {
        Intent scanIntent = new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE);
        scanIntent.setData(Uri.fromFile(new File(filePath)));
        ctx.sendBroadcast(scanIntent);
 }
}
