package com.itechart.fragments;

import java.util.Date;
import java.util.HashMap;

import android.location.Location;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.InflateException;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesClient;
import com.google.android.gms.location.LocationClient;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.GoogleMap.InfoWindowAdapter;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.itechart.PhotoMap;
import com.itechart.database.model.Photo;
import com.itechart.photomap.R;

public class MapFragment extends Fragment implements GooglePlayServicesClient.ConnectionCallbacks, GooglePlayServicesClient.OnConnectionFailedListener {
	private GoogleMap mMap;
	private LocationClient mLocationClient;
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
			/* map is already there, just return view as it is */
		}
		mMarkersHashMap = new HashMap<Marker, Photo>();

		setUpMapIfNeeded();

		mLocationClient = new LocationClient(PhotoMap.getInstance(), MapFragment.this, MapFragment.this);

		return view;
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
	}

	@Override
	public void onResume() {
		super.onResume();

		setUpMapIfNeeded();
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
				}
			}
		}
	}

	private void setUpMap() {
		mMap.setInfoWindowAdapter(new PhotoMarkerInfoWindowAdapter());
		Marker marker = mMap.addMarker(new MarkerOptions().position(new LatLng(0, 0)).title("Marker"));
		mMarkersHashMap.put(marker, new Photo("name", "name", new Date().getTime()));

		mMap.setOnMarkerClickListener(new GoogleMap.OnMarkerClickListener() {
			@Override
			public boolean onMarkerClick(com.google.android.gms.maps.model.Marker marker) {
				marker.showInfoWindow();
				return true;
			}
		});
	}

	private void scrollMapToCurrentPostion() {
		Location mCurrentLocation = mLocationClient.getLastLocation();
		if (mCurrentLocation != null && mMap != null) {

			LatLng coordinate = new LatLng(mCurrentLocation.getLatitude(), mCurrentLocation.getLongitude());
			CameraUpdate yourLocation = CameraUpdateFactory.newLatLngZoom(coordinate, 11);
			mMap.animateCamera(yourLocation);
		}
	}

	@Override
	public void onConnectionFailed(ConnectionResult result) {
	}

	@Override
	public void onConnected(Bundle connectionHint) {
		scrollMapToCurrentPostion();
	}

	@Override
	public void onDisconnected() {
	}

	public class PhotoMarkerInfoWindowAdapter implements InfoWindowAdapter {

		@Override
		public View getInfoContents(Marker marker) {
			View v = getActivity().getLayoutInflater().inflate(R.layout.photo_marker, null);

			Photo photoMarker = mMarkersHashMap.get(marker);

			ImageView photo = (ImageView) v.findViewById(R.id.pm_photo_iv);

			TextView photo_name = (TextView) v.findViewById(R.id.pm_photo_name_tv);
			TextView photo_create_date = (TextView) v.findViewById(R.id.pm_create_date_tv);

			photo.setImageDrawable(getResources().getDrawable(R.drawable.transistor));

			photo_name.setText(photoMarker.getPhotoName());
			photo_create_date.setText(new Date(photoMarker.getCreateDate()).toString());

			return v;
		}

		@Override
		public View getInfoWindow(Marker marker) {
			return null;
		}
	}
}
