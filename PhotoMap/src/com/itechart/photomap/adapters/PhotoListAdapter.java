package com.itechart.photomap.adapters;

import java.text.DateFormat;
import java.util.ArrayList;
import java.util.Date;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.itechart.photomap.PhotoMap;
import com.itechart.photomap.R;
import com.itechart.photomap.database.model.Photo;

public class PhotoListAdapter extends BaseAdapter {
	ArrayList<Photo> photoList;
	private LayoutInflater inflater;
	
	public PhotoListAdapter(Context context, ArrayList<Photo> photoList)
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
		if (convertView == null)
		{
			convertView = inflater.inflate(R.layout.row_photo_info, parent, false);
			PhotoPlaceholder placeholder = new PhotoPlaceholder(convertView);
			convertView.setTag(placeholder);
		}
		
		Photo photo = getItem(position);
		
		PhotoPlaceholder placeholder = (PhotoPlaceholder) convertView.getTag();
		
		PhotoMap.getInstance().getImageLoader().displayImage("file://"+photo.getFilePath(), placeholder.ivPhoto);
		placeholder.tvName.setText(photo.getPhotoName());
		
		DateFormat dateFormat = android.text.format.DateFormat.getDateFormat(PhotoMap.getInstance().getApplicationContext());
		placeholder.tvCreateDate.setText(String.format(PhotoMap.getInstance().getString(R.string.create_date), dateFormat.format(new Date(photo.getCreateDate()))));
		
		return convertView;
	}
	
	private class PhotoPlaceholder
	{
		public ImageView ivPhoto;
		public TextView tvName;
		public TextView tvCreateDate;
		
		public PhotoPlaceholder(View view) {
			ivPhoto = (ImageView) view.findViewById(R.id.rp_iv_photo);
			tvName = (TextView) view.findViewById(R.id.rp_tv_photo_name);
			tvCreateDate = (TextView) view.findViewById(R.id.rp_tv_creare_date);
		}
	}

}
