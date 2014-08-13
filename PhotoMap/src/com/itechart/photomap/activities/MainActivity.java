package com.itechart.photomap.activities;

import java.io.File;
import java.io.IOException;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.location.Location;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ArrayAdapter;

import com.dropbox.client2.DropboxAPI;
import com.dropbox.client2.android.AndroidAuthSession;
import com.dropbox.client2.session.AccessTokenPair;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesClient;
import com.google.android.gms.location.LocationClient;
import com.itechart.photomap.Constants;
import com.itechart.photomap.PhotoMap;
import com.itechart.photomap.R;
import com.itechart.photomap.database.model.Photo;
import com.itechart.photomap.fragments.MapFragment;
import com.itechart.photomap.fragments.PhotoGridFragment;
import com.itechart.photomap.fragments.PhotoListFragment;
import com.itechart.photomap.services.UploadService;
import com.itechart.photomap.utils.Utils;
import com.itechart.photomap.utils.interfaces.AlertDialogApplyListener;
import com.itechart.photomap.utils.interfaces.AlertDialogWithEditTextListener;

public class MainActivity extends ActionBarActivity implements ActionBar.OnNavigationListener, AlertDialogWithEditTextListener, GooglePlayServicesClient.ConnectionCallbacks, GooglePlayServicesClient.OnConnectionFailedListener, AlertDialogApplyListener {
	public static final int MAP_PAGE = 0;
	public static final int LIST_PAGE = 1;
	public static final int GRID_PAGE = 2;
	public static final int CAPTURE_IMAGE_ACTIVITY_REQUEST_CODE = 100;

	DropboxAPI<AndroidAuthSession> mApi;

	private BroadcastReceiver uploadReceiver;
	private BroadcastReceiver connectionChangeReceiver;

	public static FragmentManager fragmentManager;
	private ActionBar actionBar;
	private LocationClient mLocationClient;
	private final MapFragment mapFragement = new MapFragment();
	private final PhotoListFragment photoListFragement = new PhotoListFragment();
	private final PhotoGridFragment photoGridFragement = new PhotoGridFragment();
	private String photoForSave;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		setContentView(R.layout.main_activity);

		mLocationClient = new LocationClient(PhotoMap.getInstance(), MainActivity.this, MainActivity.this);

		connectionChangeReceiver = new BroadcastReceiver() {
			public void onReceive(Context context, Intent intent) {
				if (mapFragement != null) {
					if (mapFragement.isVisible()) {
						List<Photo> photos;
						try {
							photos = PhotoMap.getInstance().getPhotoMapDAO().queryAllUnuploaded();
							if (photos.size() > 0) {
								Utils.showDilogApplyDialog(MainActivity.this, getString(R.string.upload_items_message), getString(android.R.string.yes), getString(android.R.string.no), MainActivity.this);
							}
						} catch (SQLException e) {
							e.printStackTrace();
						}
					}
				}
			}
		};

		uploadReceiver = new BroadcastReceiver() {
			public void onReceive(Context context, Intent intent) {
				if (mapFragement != null) {
					if (mapFragement.isVisible()) {
						mapFragement.updateAllMarkers();
					}
				}
			}
		};

		fragmentManager = getSupportFragmentManager();
		mApi = PhotoMap.getInstance().getmApi();

		setUpActionBar();
	}

	@Override
	protected void onResume() {
		super.onResume();
		IntentFilter intFilt = new IntentFilter(Constants.BROADCAST_ACTION_FINISH_UPLOAD);
		registerReceiver(uploadReceiver, intFilt);
		intFilt = new IntentFilter(Constants.BROADCAST_ACTION_CONNECTION_STATE_CHANGE);
		registerReceiver(connectionChangeReceiver, intFilt);

		AndroidAuthSession session = mApi.getSession();

		// The next part must be inserted in the onResume() method of the
		// activity from which session.startAuthentication() was called, so
		// that Dropbox authentication completes properly.
		if (session.authenticationSuccessful()) {
			try {
				// Mandatory call to complete the auth
				session.finishAuthentication();

				// Store it locally in our app for later use
				storeAuth(session);
			} catch (IllegalStateException e) {
			}
		}
	}

	@Override
	protected void onPause() {
		unregisterReceiver(uploadReceiver);
		unregisterReceiver(connectionChangeReceiver);

		super.onPause();
	}

	@Override
	public void onStart() {
		super.onStart();

		mLocationClient.connect();
	}

	@Override
	public void onStop() {
		mLocationClient.disconnect();

		super.onStop();
	}

	private void setUpActionBar() {
		actionBar = getSupportActionBar();
		actionBar.setDisplayShowTitleEnabled(false);
		actionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_LIST);

		final String[] dropdownValues = getResources().getStringArray(R.array.navigation_menu);

		ArrayAdapter<String> adapter = new ArrayAdapter<String>(actionBar.getThemedContext(), android.R.layout.simple_spinner_item, android.R.id.text1, dropdownValues);

		actionBar.setListNavigationCallbacks(adapter, MainActivity.this);
	}

	@Override
	public boolean onNavigationItemSelected(int position, long id) {
		switch (position) {
		case MAP_PAGE:
			fragmentManager.beginTransaction().replace(R.id.main_fragment_container, mapFragement).commitAllowingStateLoss();

			break;
		case LIST_PAGE:
			fragmentManager.beginTransaction().replace(R.id.main_fragment_container, photoListFragement).commitAllowingStateLoss();

			break;
		case GRID_PAGE:
			fragmentManager.beginTransaction().replace(R.id.main_fragment_container, photoGridFragement).commitAllowingStateLoss();

			break;
		default:
			break;
		}

		supportInvalidateOptionsMenu();

		return false;
	}

	@Override
	protected void onDestroy() {
		fragmentManager.beginTransaction().remove(MainActivity.fragmentManager.findFragmentById(R.id.map)).commitAllowingStateLoss();

		super.onDestroy();
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.main_activity_menu, menu);

		menu.setGroupVisible(R.id.ma_map_group_gr, actionBar.getSelectedNavigationIndex() == MAP_PAGE);

		return super.onCreateOptionsMenu(menu);
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case R.id.ma_create_photo_it:
			takePicture();

			break;

		case R.id.ma_link_dropbox_account:
			mApi.getSession().startOAuth2Authentication(MainActivity.this);

			break;

		default:
			break;
		}

		return super.onOptionsItemSelected(item);
	}

	private void takePicture() {
		Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
		// Ensure that there's a camera activity to handle the intent
		if (takePictureIntent.resolveActivity(getPackageManager()) != null) {
			// Create the File where the photo should go
			File photoFile = null;
			try {
				photoFile = Utils.createImageFile();
			} catch (IOException ex) {
			}
			// Continue only if the File was successfully created
			if (photoFile != null) {
				photoForSave = photoFile.getAbsolutePath();

				takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(photoFile));
				startActivityForResult(takePictureIntent, CAPTURE_IMAGE_ACTIVITY_REQUEST_CODE);
			}
		}
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		if (requestCode == CAPTURE_IMAGE_ACTIVITY_REQUEST_CODE && resultCode == RESULT_OK) {
			Utils.showAlertDialogWithEditText(MainActivity.this, getString(R.string.enter_photo_name), MainActivity.this);
		}
	}

	@Override
	protected void onSaveInstanceState(Bundle outState) {
		outState.putInt(Constants.BUNDLE_KEY_SELECTED_PHOTO_INDEX, actionBar.getSelectedNavigationIndex());
		super.onSaveInstanceState(outState);
	}

	@Override
	protected void onRestoreInstanceState(Bundle savedInstanceState) {
		actionBar.setSelectedNavigationItem(savedInstanceState.getInt(Constants.BUNDLE_KEY_SELECTED_PHOTO_INDEX));
		super.onRestoreInstanceState(savedInstanceState);
	}

	@Override
	public void dialogClosedWithString(String resultString) {
		File fileForSave = new File(PhotoMap.getInstance().getDirectoriesWithMedia().get(0) + resultString + ".jpg");
		int sameNameCount = 1;
		do {
			if (fileForSave.exists()) {
				fileForSave = new File(PhotoMap.getInstance().getDirectoriesWithMedia().get(0) + resultString + String.valueOf(sameNameCount) + ".jpg");
				sameNameCount++;
			}
		} while (fileForSave.exists());

		File tempImage = new File(photoForSave);
		tempImage.renameTo(fileForSave);

		Photo photo = new Photo();

		photo.setPhotoName(resultString);
		photo.setCreateDate(new Date().getTime());
		photo.setFilePath(fileForSave.getAbsolutePath());

		try {
			PhotoMap.getInstance().getPhotoMapDAO().create(photo);

			if (mapFragement.isVisible()) {
				mapFragement.addPhotoToMap(photo);
			}

		} catch (SQLException e) {
			Utils.handleException(MainActivity.class.getName(), e);
		} catch (IOException e) {
			Utils.handleException(MainActivity.class.getName(), e);
		}
		
		startService(new Intent(MainActivity.this, UploadService.class));
	}

	@Override
	public void onConnectionFailed(ConnectionResult result) {
	}

	@Override
	public void onConnected(Bundle connectionHint) {
		if (mapFragement.isVisible()) {
			mapFragement.scrollMapToCurrentPostion(mLocationClient.getLastLocation());
		}
	}

	@Override
	public void onDisconnected() {
	}

	public Location getLocation() {
		if (mLocationClient.isConnected()) {
			return mLocationClient.getLastLocation();
		}

		return null;
	}

	private void storeAuth(AndroidAuthSession session) {
		// Store the OAuth 2 access token, if there is one.
		String oauth2AccessToken = session.getOAuth2AccessToken();
		if (oauth2AccessToken != null) {
			SharedPreferences prefs = getSharedPreferences(Constants.ACCOUNT_PREFS_NAME, 0);
			Editor edit = prefs.edit();
			edit.putString(Constants.ACCESS_KEY_NAME, "oauth2:");
			edit.putString(Constants.ACCESS_SECRET_NAME, oauth2AccessToken);
			edit.commit();
			return;
		}
		// Store the OAuth 1 access token, if there is one. This is only
		// necessary if
		// you're still using OAuth 1.
		AccessTokenPair oauth1AccessToken = session.getAccessTokenPair();
		if (oauth1AccessToken != null) {
			SharedPreferences prefs = getSharedPreferences(Constants.ACCOUNT_PREFS_NAME, 0);
			Editor edit = prefs.edit();
			edit.putString(Constants.ACCESS_KEY_NAME, oauth1AccessToken.key);
			edit.putString(Constants.ACCESS_SECRET_NAME, oauth1AccessToken.secret);
			edit.commit();
			return;
		}
	}

	@Override
	public void dialogCloseWithPositive() {
		startService(new Intent(MainActivity.this, UploadService.class));
	}

	@Override
	public void dialogCloseWithNegative() {
		try {
			ArrayList<Photo> photos = new ArrayList<Photo>(PhotoMap.getInstance().getPhotoMapDAO().queryAllUnuploaded());

			for (Photo photo : photos) {
				File file = new File(photo.getFilePath());

				if (file.exists()) {
					file.delete();
				}
			}

			PhotoMap.getInstance().getPhotoMapDAO().delete(photos);
			if (mapFragement.isVisible()) {
				mapFragement.updateAllMarkers();
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}
}
