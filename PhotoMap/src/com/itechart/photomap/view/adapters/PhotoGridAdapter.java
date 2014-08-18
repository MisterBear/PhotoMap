package com.itechart.photomap.view.adapters;

import java.util.ArrayList;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;

import com.itechart.photomap.PhotoMap;
import com.itechart.photomap.R;
import com.itechart.photomap.database.model.Photo;
import com.itechart.photomap.services.UploadService;

public class PhotoGridAdapter extends BaseAdapter {
	private ArrayList<Photo> photoList;
	private LayoutInflater inflater;
	
	public PhotoGridAdapter(Context context, ArrayList<Photo> photoList)
	{
		this.photoList = photoList;
		this.inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
	}

	@Override
	public int getCount() {
		return photoList.size();
	}

	@Override
	public Photo getItem(int index) {
		return photoList.get(index);
	}

	@Override
	public long getItemId(int index) {
		return index;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		if (convertView == null) {
			convertView = inflater.inflate(R.layout.item_photo, parent, false);
			PhotoPlaceholder placeholder = new PhotoPlaceholder(convertView);
			convertView.setTag(placeholder);
		}
		
		Photo photo = getItem(position);
		
		PhotoPlaceholder placeholder = (PhotoPlaceholder) convertView.getTag();
		
		int index = photo.getFilePath().lastIndexOf("/");
		
		PhotoMap.getInstance().getDropboxThumbnailLoader().DisplayImage(UploadService.photosFolderPath + photo.getFilePath().substring(index), placeholder.ivPhoto);
				
		return convertView;
	}
	
	private class PhotoPlaceholder
	{
		public ImageView ivPhoto;
		
		public PhotoPlaceholder(View view) {
			ivPhoto = (ImageView) view.findViewById(R.id.ip_iv_photo);
		}
	}

	public ArrayList<Photo> getPhotoList() {
		return photoList;
	}

	public void setPhotoList(ArrayList<Photo> photoList) {
		this.photoList = photoList;
	}
}
