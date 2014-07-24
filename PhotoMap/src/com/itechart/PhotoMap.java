package com.itechart;

import android.app.Application;

public class PhotoMap extends Application {
	private static PhotoMap instance;
	
	public PhotoMap() {
		instance = this;
	}
	
	public static PhotoMap getInstance() {
		return instance;
	}
}
