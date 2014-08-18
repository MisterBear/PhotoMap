package com.itechart.photomap.view.activities;

import java.util.ArrayList;

import android.content.res.Configuration;
import android.os.Bundle;
import android.support.v4.view.ViewPager;
import android.support.v7.app.ActionBarActivity;

import com.itechart.photomap.Constants;
import com.itechart.photomap.R;
import com.itechart.photomap.database.model.Photo;
import com.itechart.photomap.view.adapters.GalleryAdapter;

public class FullScreenView extends ActionBarActivity {
	private GalleryAdapter mAdapter;
	private ViewPager mPager;
	private ArrayList<Photo> photoList;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		setContentView(R.layout.fullscreen_view);
		
		getSupportActionBar().hide();
		
		Bundle extra = getIntent().getExtras();
		int selectedItem = 0;
		
		if (extra != null) {
			photoList = extra.getParcelableArrayList(Constants.BUNDLE_KEY_PHOTOS_ARRAY_LIST);
			selectedItem = extra.getInt(Constants.BUNDLE_KEY_SELECTED_PHOTO_INDEX);
		}
		
		mPager = (ViewPager) findViewById(R.id.fs_vp_pager);
						
		if (photoList !=  null) {
			mAdapter = new GalleryAdapter(FullScreenView.this, photoList);
			mPager.setAdapter(mAdapter);
			mPager.setCurrentItem(selectedItem);
		}
	}
	
	@Override
	public void onConfigurationChanged(Configuration newConfig) {
		//int displayHeight = Utils.getDisplayHeight();
		//int displayWidth = Utils.getDisplayWidth();
		
		//mPager.getLayoutParams().width = displayWidth;
		//mPager.getLayoutParams().height = displayHeight;
		mPager.requestLayout();
		mPager.invalidate();		

		super.onConfigurationChanged(newConfig);
	}
}
