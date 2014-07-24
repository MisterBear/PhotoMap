package com.itechart.activities;

import android.content.Intent;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ArrayAdapter;

import com.itechart.fragments.MapFragment;
import com.itechart.fragments.PhotoGridFragment;
import com.itechart.fragments.PhotoListFragment;
import com.itechart.photomap.R;

public class MainActivity extends ActionBarActivity implements ActionBar.OnNavigationListener {
	private static final int MAP_PAGE = 0;
	private static final int LIST_PAGE = 1;
	private static final int GRID_PAGE = 2;
	private static final int CAPTURE_IMAGE_ACTIVITY_REQUEST_CODE = 100;
	
	public static FragmentManager fragmentManager;
	private ActionBar actionBar;
	private final MapFragment mapFragemnt = new MapFragment();
	private final PhotoListFragment photoListFragemnt = new PhotoListFragment();
	private final PhotoGridFragment photoGridFragemnt = new PhotoGridFragment();

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		setContentView(R.layout.main_activity);

		fragmentManager = getSupportFragmentManager();
		
		setUpActionBar();		
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
			fragmentManager.beginTransaction().replace(R.id.main_fragment_container, mapFragemnt).commit();			
			
			break;
		case LIST_PAGE:
			fragmentManager.beginTransaction().replace(R.id.main_fragment_container, photoListFragemnt).commit();			
			
			break;
		case GRID_PAGE:
			fragmentManager.beginTransaction().replace(R.id.main_fragment_container, photoGridFragemnt).commit();			
			
			break;
		default:
			break;
		}
		
		supportInvalidateOptionsMenu();

		return false;
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
			Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);

		   // fileUri = getOutputMediaFileUri(MEDIA_TYPE_IMAGE); // create a file to save the image
		   // intent.putExtra(MediaStore.EXTRA_OUTPUT, fileUri); // set the image file name

		    // start the image capture Intent
		    startActivityForResult(intent, CAPTURE_IMAGE_ACTIVITY_REQUEST_CODE);
			
			break;

		default:
			break;
		}
		
		return super.onOptionsItemSelected(item);
	}
}
