/**
 * 
 */
package com.example.sensorapp.music;

import android.net.Uri;

/**
 * @author Administrator
 * 
 */
public interface Mp3Player {
	public void play(Uri uri);

	public void play();

	public void pause();

	public void stop();

	public void prev();

	public void next();

	public Object currentMp3Info();

	public boolean mp3Playing();
}
