package com.itechart.photomap.receivers;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import com.itechart.photomap.Constants;
import com.itechart.photomap.PhotoMap;

public class ConnectionChangeListener extends BroadcastReceiver {

	public void onReceive(Context context, Intent intent) {
		if (PhotoMap.getInstance().isConnectionAvailable()) {
			 context.sendBroadcast(new Intent(Constants.BROADCAST_ACTION_CONNECTION_STATE_CHANGE));
		}
	}
}
