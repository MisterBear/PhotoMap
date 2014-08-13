package com.itechart.photomap.fragments;

import java.sql.SQLException;
import java.util.ArrayList;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.GridView;

import com.itechart.photomap.Constants;
import com.itechart.photomap.PhotoMap;
import com.itechart.photomap.R;
import com.itechart.photomap.activities.FullScreenView;
import com.itechart.photomap.adapters.PhotoGridAdapter;
import com.itechart.photomap.database.model.Photo;

public class PhotoGridFragment extends Fragment {
	private GridView gvPhoto;
	private PhotoGridAdapter mAdapter;

	@Override
	public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
		View view = inflater.inflate(R.layout.photo_grid_fragment, container, false);

		gvPhoto = (GridView) view.findViewById(R.id.pg_gv_photo);
		ArrayList<Photo> photoList = null;
		try {
			photoList = new ArrayList<Photo>(PhotoMap.getInstance().getPhotoMapDAO().queryAllUploaded());
		} catch (SQLException e) {
			e.printStackTrace();
		}

		if (photoList != null) {
			mAdapter = new PhotoGridAdapter(getActivity(), photoList);
			gvPhoto.setAdapter(mAdapter);
			gvPhoto.setOnItemClickListener(new OnItemClickListener() {

				@Override
				public void onItemClick(AdapterView<?> adapter, View view, int position, long id) {
					Intent fullscreenview = new Intent(getActivity(), FullScreenView.class);

					fullscreenview.putParcelableArrayListExtra(Constants.BUNDLE_KEY_PHOTOS_ARRAY_LIST, mAdapter.getPhotoList());
					fullscreenview.putExtra(Constants.BUNDLE_KEY_SELECTED_PHOTO_INDEX, position);

					startActivity(fullscreenview);
				}
			});
		}

		return view;
	}
}
