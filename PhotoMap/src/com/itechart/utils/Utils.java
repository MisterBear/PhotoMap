package com.itechart.utils;

import com.itechart.Constants;

import android.util.Log;

public class Utils {
	public static void handleException(String message, Throwable t) {
		String exception = "";

		if (t != null) {
			exception = Log.getStackTraceString(t);
		}

		if (Constants.DEBUG_MODE) {
			Log.d("PhtoMap", message + " handledException: " + exception);
		}
	}
}
