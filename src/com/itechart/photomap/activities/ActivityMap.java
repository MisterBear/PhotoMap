package com.itechart.photomap.activities;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesClient;
import com.google.android.gms.location.LocationClient;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.itechart.photomap.R;

import android.location.Location;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;

public class ActivityMap extends FragmentActivity implements GooglePlayServicesClient.ConnectionCallbacks, GooglePlayServicesClient.OnConnectionFailedListener {
	private GoogleMap mMap;
	private LocationClient mLocationClient;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.map_activity);

		SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);

		if (savedInstanceState == null) {
			mapFragment.setRetainInstance(true);
		} else {
			mMap = mapFragment.getMap();
		}
		setUpMapIfNeeded();

		mLocationClient = new LocationClient(getApplicationContext(), ActivityMap.this, ActivityMap.this);
	}

	@Override
	protected void onResume() {
		super.onResume();
		setUpMapIfNeeded();
	}

	@Override
	protected void onStart() {
		super.onStart();
		
		mLocationClient.connect();
	}

	@Override
	protected void onStop() {
		mLocationClient.disconnect();
		super.onStop();
	}

	private void setUpMapIfNeeded() {
		if (mMap == null) {
			mMap = ((SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map)).getMap();
			if (mMap != null) {
				setUpMap();
			}
		}
	}

	private void setUpMap() {
		mMap.addMarker(new MarkerOptions().position(new LatLng(0, 0)).title("Marker"));
	}
	
	private void scrollMapToCurrentPostion() {
		Location mCurrentLocation = mLocationClient.getLastLocation();

		LatLng coordinate = new LatLng(mCurrentLocation.getLatitude(), mCurrentLocation.getLongitude());
		CameraUpdate yourLocation = CameraUpdateFactory.newLatLngZoom(coordinate, 11);
		mMap.animateCamera(yourLocation);
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
}
