package com.itechart.activities;

import java.io.File;
import java.io.IOException;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ArrayAdapter;

import com.itechart.PhotoMap;
import com.itechart.fragments.MapFragment;
import com.itechart.fragments.PhotoGridFragment;
import com.itechart.fragments.PhotoListFragment;
import com.itechart.photomap.R;
import com.itechart.utils.Utils;
import com.itechart.utils.interfaces.AlertDialogListener;

public class MainActivity extends ActionBarActivity implements ActionBar.OnNavigationListener, AlertDialogListener {
	public static final int MAP_PAGE = 0;
	public static final int LIST_PAGE = 1;
	public static final int GRID_PAGE = 2;
	public static final int CAPTURE_IMAGE_ACTIVITY_REQUEST_CODE = 100;

	public static FragmentManager fragmentManager;
	private ActionBar actionBar;
	private final MapFragment mapFragemnt = new MapFragment();
	private final PhotoListFragment photoListFragemnt = new PhotoListFragment();
	private final PhotoGridFragment photoGridFragemnt = new PhotoGridFragment();
	private String photoForSave;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		setContentView(R.layout.main_activity);

		fragmentManager = getSupportFragmentManager();

		setUpActionBar();
	}

	@Override
	protected void onResume() {

		super.onResume();
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
			fragmentManager.beginTransaction().replace(R.id.main_fragment_container, mapFragemnt).commitAllowingStateLoss();

			break;
		case LIST_PAGE:
			fragmentManager.beginTransaction().replace(R.id.main_fragment_container, photoListFragemnt).commitAllowingStateLoss();

			break;
		case GRID_PAGE:
			fragmentManager.beginTransaction().replace(R.id.main_fragment_container, photoGridFragemnt).commitAllowingStateLoss();

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
	        	
	            takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT,
	                    Uri.fromFile(photoFile));
	            startActivityForResult(takePictureIntent, CAPTURE_IMAGE_ACTIVITY_REQUEST_CODE);
	        }
	    }
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		if (requestCode == CAPTURE_IMAGE_ACTIVITY_REQUEST_CODE && resultCode == RESULT_OK) {
			Intent mediaScanIntent = new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE);
		    File f = new File(photoForSave);
		    Uri contentUri = Uri.fromFile(f);
		    mediaScanIntent.setData(contentUri);
		    this.sendBroadcast(mediaScanIntent);
		    
			Utils.showAlertDialogWithEditText(MainActivity.this, getString(R.string.enter_photo_name), MainActivity.this);
		}
	}

	@Override
	protected void onSaveInstanceState(Bundle outState) {
		outState.putInt("1", actionBar.getSelectedNavigationIndex());
		super.onSaveInstanceState(outState);
	}

	@Override
	protected void onRestoreInstanceState(Bundle savedInstanceState) {
		actionBar.setSelectedNavigationItem(savedInstanceState.getInt("1"));
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
		
		//Utils.saveBitmapToFile(photoForSave, fileForSave);
		//photoForSave = null;
	}
}
