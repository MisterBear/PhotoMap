package com.itechart.photomap.view.adapters;

import java.util.ArrayList;

import android.content.Context;
import android.support.v4.view.PagerAdapter;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.itechart.photomap.PhotoMap;
import com.itechart.photomap.R;
import com.itechart.photomap.database.model.Photo;

public class GalleryAdapter extends PagerAdapter {
	private ArrayList<Photo> photoList;
	private LayoutInflater inflater;

	public GalleryAdapter(Context context, ArrayList<Photo> photoList) {
		this.photoList = photoList;
		this.inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
	}
	
	@Override
	public int getItemPosition(Object object) {
	    return POSITION_NONE;
	}

	@Override
	public int getCount() {
		return photoList.size();
	}

	@Override
	public boolean isViewFromObject(View view, Object object) {
		return view == object;
	}

	@Override
	public Object instantiateItem(ViewGroup container, int position) {
		Photo photo = photoList.get(position);

		LinearLayout layout = (LinearLayout) inflater.inflate(R.layout.photo_view, container, false);
		ImageView imageView = (ImageView) layout.findViewById(R.id.pv_iv_photo);
		
		PhotoMap.getInstance().getImageLoader().displayImage("file://" + photo.getFilePath(), imageView);
		
		container.addView(layout);
		return layout;
	}	

	@Override
	public void destroyItem(ViewGroup container, int position, Object object) {
		container.removeView((LinearLayout) object);
	}
}
