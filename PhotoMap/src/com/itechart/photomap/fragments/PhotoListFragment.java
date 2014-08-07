package com.itechart.photomap.fragments;

import java.sql.SQLException;
import java.util.ArrayList;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

import com.itechart.photomap.PhotoMap;
import com.itechart.photomap.R;
import com.itechart.photomap.adapters.PhotoListAdapter;
import com.itechart.photomap.database.model.Photo;

public class PhotoListFragment extends Fragment {
	private ListView lvPhoto;
	private PhotoListAdapter mAdapter;
	
	@Override
	public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
		View view = inflater.inflate(R.layout.photo_list_fragment, container, false);
		
		lvPhoto = (ListView) view.findViewById(R.id.pl_lv_photo);
		ArrayList<Photo> photoList = null;
		try {
			photoList = new ArrayList<Photo>(PhotoMap.getInstance().getPhotoMapDAO().queryForAll());
		} catch (SQLException e) {
			e.printStackTrace();
		}
		
		if (photoList !=  null) {
			mAdapter = new PhotoListAdapter(getActivity(), photoList);
			lvPhoto.setAdapter(mAdapter);
		}
		
		return view;
	}

}
