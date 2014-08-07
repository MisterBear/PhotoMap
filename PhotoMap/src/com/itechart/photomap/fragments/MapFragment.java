package com.itechart.photomap.fragments;

import java.io.File;
import java.io.IOException;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;

import android.graphics.BitmapFactory;
import android.location.Location;
import android.media.ExifInterface;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.InflateException;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.GoogleMap.InfoWindowAdapter;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.itechart.photomap.PhotoMap;
import com.itechart.photomap.R;
import com.itechart.photomap.activities.MainActivity;
import com.itechart.photomap.database.model.Photo;
import com.itechart.photomap.model.Point;
import com.itechart.photomap.utils.Utils;

public class MapFragment extends Fragment {
	private GoogleMap mMap;
	private HashMap<Marker, Photo> mMarkersHashMap;
	private static final int REQUEST_IMAGE_CAPTURE = 100;
	private static View view;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		if (view != null) {
			ViewGroup parent = (ViewGroup) view.getParent();
			if (parent != null)
				parent.removeView(view);
		}
		try {
			view = inflater.inflate(R.layout.map_fragment, container, false);
		} catch (InflateException e) {
		}
		mMarkersHashMap = new HashMap<Marker, Photo>();

		setUpMapIfNeeded();

		return view;
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
	}

	@Override
	public void onResume() {
		super.onResume();

		setUpMapIfNeeded();
	}

	@Override
	public void onDestroy() {
		if (mMap != null) {
			mMap = null;
		}

		super.onDestroy();
	}

	@Override
	public void onViewCreated(View view, Bundle savedInstanceState) {
		setUpMapIfNeeded();
	}

	@Override
	public void onPause() {

		super.onPause();
	}

	public void setUpMapIfNeeded() {
		if (mMap == null) {
			if (getActivity() != null) {
				mMap = ((SupportMapFragment) getActivity().getSupportFragmentManager().findFragmentById(R.id.map)).getMap();
				if (mMap != null) {
					setUpMap();
					scrollMapToCurrentPostion(((MainActivity) getActivity()).getLocation());
				}
			}
		}
	}

	private void setUpMap() {
		addAllPhotoToMap();

		mMap.setInfoWindowAdapter(new PhotoMarkerInfoWindowAdapter());

		mMap.setOnMarkerClickListener(new GoogleMap.OnMarkerClickListener() {
			@Override
			public boolean onMarkerClick(com.google.android.gms.maps.model.Marker marker) {
				marker.showInfoWindow();
				return true;
			}
		});
	}

	public void addAllPhotoToMap() {
		try {
			ArrayList<Photo> photoList = new ArrayList<Photo>(PhotoMap.getInstance().getPhotoMapDAO().queryForAll());

			for (Photo photo : photoList) {
				addPhotoToMap(photo);
			}

		} catch (SQLException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public void addPhotoToMap(Photo photo) throws IOException, SQLException {

		File photoFile = new File(photo.getFilePath());

		if (photoFile.exists()) {
			ExifInterface exif = new ExifInterface(photoFile.getAbsolutePath());

			String latitude = exif.getAttribute(ExifInterface.TAG_GPS_LATITUDE);
			String longitude = exif.getAttribute(ExifInterface.TAG_GPS_LONGITUDE);
			String latitudeRef = exif.getAttribute(ExifInterface.TAG_GPS_LATITUDE_REF);
			String longitudeRef = exif.getAttribute(ExifInterface.TAG_GPS_LONGITUDE_REF);

			Point point = Utils.parseGeoTag(latitude, longitude, latitudeRef, longitudeRef);

			Marker marker = mMap.addMarker(new MarkerOptions().position(new LatLng(point.getLatitude(), point.getLongitude())).icon(BitmapDescriptorFactory.defaultMarker(photo.getIsUploaded() ? BitmapDescriptorFactory.HUE_GREEN : BitmapDescriptorFactory.HUE_RED)).title(photo.getPhotoName()));

			mMarkersHashMap.put(marker, photo);
		} else {
			PhotoMap.getInstance().getPhotoMapDAO().delete(photo);
		}
	}

	public void updateAllMarkers() {
		mMap.clear();

		addAllPhotoToMap();
	}

	public void scrollMapToCurrentPostion(Location mCurrentLocation) {
		if (mCurrentLocation != null && mMap != null) {
			LatLng coordinate = new LatLng(mCurrentLocation.getLatitude(), mCurrentLocation.getLongitude());
			CameraUpdate yourLocation = CameraUpdateFactory.newLatLngZoom(coordinate, 11);
			mMap.animateCamera(yourLocation);
		}
	}

	public class PhotoMarkerInfoWindowAdapter implements InfoWindowAdapter {

		@Override
		public View getInfoContents(Marker marker) {
			View v = getActivity().getLayoutInflater().inflate(R.layout.photo_marker, null);

			Photo photo = mMarkersHashMap.get(marker);

			ImageView ivPhoto = (ImageView) v.findViewById(R.id.pm_photo_iv);

			TextView photo_name = (TextView) v.findViewById(R.id.pm_photo_name_tv);
			TextView photo_create_date = (TextView) v.findViewById(R.id.pm_create_date_tv);

			ivPhoto.setImageBitmap(BitmapFactory.decodeFile(photo.getFilePath()));

			photo_name.setText(photo.getPhotoName());
			java.text.DateFormat dateFormat = android.text.format.DateFormat.getDateFormat(getActivity().getApplicationContext());

			photo_create_date.setText(String.format(getString(R.string.create_date), dateFormat.format(new Date(photo.getCreateDate()))));

			return v;
		}

		@Override
		public View getInfoWindow(Marker marker) {
			return null;
		}
	}
}
