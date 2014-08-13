/**
 * 
 */
package com.example.sensorapp;

import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.Charset;
import java.util.Map;

import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;

import android.util.Log;

/**
 * @author Administrator
 * 
 */
public class Http {
	/**
	 * 
	 * @author Administrator
	 * 
	 */
	public interface NetActionListener {

		public void onNetError(String message, HttpParameter parameter);

		public void onNetFinished(String a, HttpParameter parameter);

		public void onNetProgressChanged(float progress, HttpParameter parameter);
	}

	private final static String TAG_STRING = "HTTP_PROCESS";
	private final HttpParameter parameter;
	private final String urlString;

	private static byte[] streamToByte(InputStream stream) throws Exception {
		ByteArrayOutputStream outSteam = new ByteArrayOutputStream();
		byte[] buffer = new byte[1024];
		int len = -1;
		while ((len = stream.read(buffer)) != -1) {
			outSteam.write(buffer, 0, len);
		}
		outSteam.close();
		stream.close();
		return outSteam.toByteArray();
	}

	/**
	 * 
	 * @param params
	 */

	public Http(HttpParameter params) {
		parameter = params;
		assert (params.url != null);

		String url = params.url;
		if (!url.endsWith("?"))
			url += "?";

		if (params.param2.isEmpty()) {
			if (params.param1 != null && params.param1.length() > 0) {
				url += params.param1;
			}
			urlString = url;
		} else {
			String url0 = "";
			for (Map.Entry<String, String> entry : params.param2.entrySet()) {
				url0 += "&" + entry.getKey() + "=" + entry.getValue();
			}
			urlString = url + url0.substring(1);
		}

		Log.i(TAG_STRING, urlString);
	}

	public boolean downloadToFile() {
		// if (!SystemStates.getInstance().getNet_connect_state())
		// return false;
		Thread thread = new Thread() {
			@Override
			public void run() {

				try {

					HttpGet httpGet = new HttpGet(urlString);

					HttpClient httpClient = new DefaultHttpClient();

					HttpResponse httpResp = httpClient.execute(httpGet);
					if (!httpResp.getEntity().isStreaming()) {
						throw new Exception("Http Download File is not Stream.");
					}

					if (httpResp.getStatusLine().getStatusCode() == HttpURLConnection.HTTP_OK) {
						File file = new File(parameter.arg2);

						if (!file.exists()) {
							File dirFile = file.getParentFile();// new
																// File(filePath.substring(0,
																// filePath.lastIndexOf('/')));
							if (!dirFile.exists()) {
								dirFile.mkdirs();
							}
							file.createNewFile();
						}

						InputStream inputStream = httpResp.getEntity()
								.getContent();
						FileOutputStream outStream = new FileOutputStream(file);
						int cursor = -1;
						long length = httpResp.getEntity().getContentLength();
						float cur = 0f;
						byte[] buffer = new byte[1024];

						// listener.onNetProgressChanged(0, parameter);
						parameter.listener.onNetProgressChanged(0, parameter);

						while ((cursor = inputStream.read(buffer)) != -1) {
							outStream.write(buffer, 0, cursor);
							cur += cursor;
							parameter.listener.onNetProgressChanged(cur * 100
									/ length, parameter);
						}
						inputStream.close();
						outStream.close();

						parameter.listener.onNetFinished(httpResp
								.getStatusLine().getReasonPhrase(), parameter);

					} else {
						parameter.listener.onNetError(httpResp.getStatusLine()
								.getReasonPhrase(), parameter);
					}
				} catch (Exception e) {
					parameter.listener.onNetError(e.getMessage(), parameter);
				}
			}
		};
		thread.start();
		return true;
	}

	public boolean get() {
		// if (!SystemStates.getInstance().getNet_connect_state())
		// return false;
		Thread thread = new Thread() {
			@Override
			public void run() {

				try {
					HttpGet httpGet = new HttpGet(urlString);

					HttpClient httpClient = new DefaultHttpClient();

					HttpResponse httpResp = httpClient.execute(httpGet);

					if (httpResp.getStatusLine().getStatusCode() == HttpURLConnection.HTTP_OK) {

						String result = EntityUtils.toString(
								httpResp.getEntity(), "UTF-8");
						parameter.listener.onNetFinished(result, parameter);

					} else {
						parameter.listener.onNetError(httpResp.getStatusLine()
								.getReasonPhrase(), parameter);
					}
				} catch (Exception e) {
					parameter.listener.onNetError(e.getMessage(), parameter);
				}
			}
		};
		thread.start();
		return true;
	}

	public boolean post() {
		// if (!SystemStates.getInstance().getNet_connect_state())
		// return false;
		Thread thread = new Thread() {
			@Override
			public void run() {
				try {
					String params = urlString
							.substring(urlString.indexOf('?') + 1);

					URL url = new URL(urlString);

					HttpURLConnection urlConn = (HttpURLConnection) url
							.openConnection();

					urlConn.setConnectTimeout(5000);

					urlConn.setDoOutput(true);

					urlConn.setUseCaches(false);

					urlConn.setRequestMethod("POST");
					urlConn.setInstanceFollowRedirects(true);

					urlConn.setRequestProperty("Content-Type",
							"application/x-www-form-urlencode");

					urlConn.connect();

					if (params != null && params.length() > 0) {
						DataOutputStream dos = new DataOutputStream(
								urlConn.getOutputStream());
						dos.write(params.getBytes(Charset.defaultCharset()));
						// dos.write(params.getBytes());
						dos.flush();
						dos.close();
					}

					if (urlConn.getResponseCode() == HttpURLConnection.HTTP_OK) {

						byte[] data = streamToByte(urlConn.getInputStream());
						String result = new String(data);
						parameter.listener.onNetFinished(result, parameter);

					} else {
						parameter.listener.onNetError(
								urlConn.getResponseMessage(), parameter);

					}
				} catch (Exception e) {
					parameter.listener.onNetError(e.getMessage(), parameter);

				}
			}
		};
		thread.start();
		return true;
	}

	public void uploadWithFile(String fileName) {

	}
}
