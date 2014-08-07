package com.itechart.photomap;

import java.io.File;
import java.util.ArrayList;

import android.app.Application;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

import com.dropbox.client2.DropboxAPI;
import com.dropbox.client2.android.AndroidAuthSession;
import com.dropbox.client2.session.AccessTokenPair;
import com.dropbox.client2.session.AppKeyPair;
import com.itechart.photomap.database.DatabaseHelper;
import com.itechart.photomap.database.PhotoMapDAO;
import com.itechart.photomap.utils.Utils;

public class PhotoMap extends Application {
	private static PhotoMap instance;
	private ArrayList<String> directoriesWithMedia;
	private DatabaseHelper databaseHelper;
	private DropboxAPI<AndroidAuthSession> mApi;

	public PhotoMap() {
		instance = this;
	}

	@Override
	public void onCreate() {
		checkDirectoriesWithMedia();
		
		databaseHelper = new DatabaseHelper(PhotoMap.this);
		
		AndroidAuthSession session = buildSession();
		mApi = new DropboxAPI<AndroidAuthSession>(session);

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
	
	public PhotoMapDAO getPhotoMapDAO() {
		return databaseHelper.getPhotoMapDAO();
	}
	

	private AndroidAuthSession buildSession() {
		AppKeyPair appKeyPair = new AppKeyPair(getString(R.string.app_key_dropbox), getString(R.string.app_secret_dropbox));

		AndroidAuthSession session = new AndroidAuthSession(appKeyPair);
		loadAuth(session);
		return session;
	}
	
	private void loadAuth(AndroidAuthSession session) {
		SharedPreferences prefs = getSharedPreferences(Constants.ACCOUNT_PREFS_NAME, 0);
		String key = prefs.getString(Constants.ACCESS_KEY_NAME, null);
		String secret = prefs.getString(Constants.ACCESS_SECRET_NAME, null);
		if (key == null || secret == null || key.length() == 0 || secret.length() == 0)
			return;

		if (key.equals("oauth2:")) {
			// If the key is set to "oauth2:", then we can assume the token is
			// for OAuth 2.
			session.setOAuth2AccessToken(secret);
		} else {
			// Still support using old OAuth 1 tokens.
			session.setAccessTokenPair(new AccessTokenPair(key, secret));
		}
	}

	public DropboxAPI<AndroidAuthSession> getmApi() {
		return mApi;
	}

	public void setmApi(DropboxAPI<AndroidAuthSession> mApi) {
		this.mApi = mApi;
	}
	
	public boolean isConnectionAvailable() {
        ConnectivityManager cm = (ConnectivityManager) getSystemService(CONNECTIVITY_SERVICE);
        NetworkInfo _3g = cm.getNetworkInfo(ConnectivityManager.TYPE_MOBILE);
        NetworkInfo wifi = cm.getNetworkInfo(ConnectivityManager.TYPE_WIFI);

        if ((_3g == null || !_3g.isConnected()) && (wifi == null || !wifi.isConnected())) {
            return false;
        }

        return true;
    }
}
