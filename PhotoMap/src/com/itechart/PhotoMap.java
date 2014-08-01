package com.itechart;

import java.io.File;
import java.util.ArrayList;

import android.app.Application;

import com.itechart.utils.Utils;

public class PhotoMap extends Application {
	private static PhotoMap instance;
	private ArrayList<String> directoriesWithMedia;

	public PhotoMap() {
		instance = this;
	}

	@Override
	public void onCreate() {
		checkDirectoriesWithMedia();

		super.onCreate();
	}

	public static PhotoMap getInstance() {
		return instance;
	}

	private void checkDirectoriesWithMedia() {
		directoriesWithMedia = new ArrayList<String>();
		String[] temp = Utils.getStorageDirectories();
		for (int index = 0; index < temp.length; index++) {
			if (new File(temp[index] + "/DCIM").exists()) {
				directoriesWithMedia.add(temp[index] + "/DCIM/");
			}
		}
	}

	public ArrayList<String> getDirectoriesWithMedia() {
		return directoriesWithMedia;
	}
}
