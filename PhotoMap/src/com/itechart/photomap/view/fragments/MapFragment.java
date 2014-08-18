package com.itechart.photomap.view.fragments;

import java.io.File;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;

import android.content.Context;
import android.content.Intent;
import android.graphics.BitmapFactory;
import android.location.Location;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.InflateException;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
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
import com.google.maps.android.clustering.Cluster;
import com.google.maps.android.clustering.ClusterManager;
import com.google.maps.android.clustering.ClusterManager.OnClusterClickListener;
import com.google.maps.android.clustering.ClusterManager.OnClusterItemClickListener;
import com.google.maps.android.clustering.view.DefaultClusterRenderer;
import com.itechart.photomap.Constants;
import com.itechart.photomap.PhotoMap;
import com.itechart.photomap.R;
import com.itechart.photomap.database.model.Photo;
import com.itechart.photomap.model.PointMarker;
import com.itechart.photomap.view.activities.FullScreenView;
import com.itechart.photomap.view.activities.MainActivity;

public class MapFragment extends Fragment implements ClusterManager.OnClusterClickListener<PointMarker>, ClusterManager.OnClusterInfoWindowClickListener<PointMarker>, ClusterManager.OnClusterItemClickListener<PointMarker>, ClusterManager.OnClusterItemInfoWindowClickListener<PointMarker> {
	private GoogleMap mMap;
	private static View view;
	private ArrayList<Photo> photosList;
	private ClusterManager<PointMarker> mClusterManager;
	private Cluster<PointMarker> clickedCluster;
	private PointMarker clickedClusterItem;

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
		mClusterManager = new ClusterManager<PointMarker>(getActivity(), getMap());
		mClusterManager.setRenderer(new PhotoMarkerRender());
		getMap().setOnCameraChangeListener(mClusterManager);
		getMap().setOnMarkerClickListener(mClusterManager);
		getMap().setOnInfoWindowClickListener(mClusterManager);
		mClusterManager.setOnClusterClickListener(this);
		mClusterManager.setOnClusterInfoWindowClickListener(this);
		mClusterManager.setOnClusterItemClickListener(this);
		mClusterManager.setOnClusterItemInfoWindowClickListener(this);

		addAllPhotoToMap();

		mClusterManager.cluster();
		mMap.setInfoWindowAdapter(mClusterManager.getMarkerManager());

		mClusterManager.getClusterMarkerCollection().setOnInfoWindowAdapter(new MyCustomAdapterForClusters());
		mClusterManager.getMarkerCollection().setOnInfoWindowAdapter(new MyCustomAdapterForItems());

		mMap.setOnMarkerClickListener(mClusterManager);
		mClusterManager.setOnClusterClickListener(new OnClusterClickListener<PointMarker>() {
			@Override
			public boolean onClusterClick(Cluster<PointMarker> cluster) {
				clickedCluster = cluster;
				return false;
			}
		});

		mClusterManager.setOnClusterItemClickListener(new OnClusterItemClickListener<PointMarker>() {
			@Override
			public boolean onClusterItemClick(PointMarker item) {
				clickedClusterItem = item;
				return false;
			}
		});
	}

	public void addAllPhotoToMap() {
		try {
			photosList = new ArrayList<Photo>(PhotoMap.getInstance().getPhotoMapDAO().queryForAll());

			for (Photo photo : photosList) {
				addPhotoToMap(photo);
			}		
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

	public void addPhotoToMap(Photo photo) throws SQLException {

		File photoFile = new File(photo.getFilePath());

		if (photoFile.exists()) {
			PointMarker point = new PointMarker(new LatLng(photo.getLatitude(), photo.getLongitude()));
			point.setPhoto(photo);
			mClusterManager.addItem(point);
		} else {
			PhotoMap.getInstance().getPhotoMapDAO().delete(photo);
		}
	}

	public void updateAllMarkers() {
		mMap.clear();
		mClusterManager.clearItems();

		addAllPhotoToMap();
		
		clusterMarkers();
	}
	
	public void clusterMarkers() {
		mClusterManager.cluster();
	}

	public void scrollMapToCurrentPostion(Location mCurrentLocation) {
		if (mCurrentLocation != null && mMap != null) {
			LatLng coordinate = new LatLng(mCurrentLocation.getLatitude(), mCurrentLocation.getLongitude());
			CameraUpdate yourLocation = CameraUpdateFactory.newLatLngZoom(coordinate, 11);
			mMap.animateCamera(yourLocation);
		}
	}

	class MyCustomAdapterForClusters implements InfoWindowAdapter {
		@Override
		public View getInfoContents(Marker marker) {
			LayoutInflater inflater = (LayoutInflater) getActivity().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
			RelativeLayout layout = (RelativeLayout) inflater.inflate(R.layout.info_window_claster, null);

			return layout;
		}

		@Override
		public View getInfoWindow(Marker marker) {
			return null;
		}
	}

	class MyCustomAdapterForItems implements InfoWindowAdapter {
		@Override
		public View getInfoContents(Marker marker) {
			if (clickedClusterItem != null) {
				// for (Pho item : clickedCluster.getItems()) {
				// Extract data from each item in the cluster as needed
				// }
			}
			View v = getActivity().getLayoutInflater().inflate(R.layout.photo_marker, null);

			Photo photo = clickedClusterItem.getPhoto();

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

	public class PhotoMarkerInfoWindowAdapter implements InfoWindowAdapter {

		@Override
		public View getInfoContents(Marker marker) {
			View v = getActivity().getLayoutInflater().inflate(R.layout.photo_marker, null);
			int index = 0;
			Iterator<Marker> iterator = mClusterManager.getMarkerCollection().getMarkers().iterator();
			Marker newMarker = null;
			while (iterator.hasNext() && newMarker != marker) {
				index++;
				newMarker = iterator.next();
			}
			Photo photo = photosList.get(index);

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

	@Override
	public void onClusterItemInfoWindowClick(PointMarker item) {
		if (clickedClusterItem != null) {
			ArrayList<Photo> photos = new ArrayList<Photo>();

			photos.add(clickedClusterItem.getPhoto());

			startFullscreenActivity(photos, clickedClusterItem.getPhoto());
		}
	}

	@Override
	public boolean onClusterItemClick(PointMarker item) {
		return false;
	}

	@Override
	public void onClusterInfoWindowClick(Cluster<PointMarker> cluster) {
		if (clickedCluster != null) {
			ArrayList<Photo> photos = new ArrayList<Photo>();
			for (PointMarker marker : clickedCluster.getItems()) {
				photos.add(marker.getPhoto());
			}

			startFullscreenActivity(photos, photos.get(0));
		}

	}

	private void startFullscreenActivity(ArrayList<Photo> photos, Photo selectedPhoto) {
		Intent fullscreenview = new Intent(getActivity(), FullScreenView.class);

		fullscreenview.putParcelableArrayListExtra(Constants.BUNDLE_KEY_PHOTOS_ARRAY_LIST, photos);
		fullscreenview.putExtra(Constants.BUNDLE_KEY_SELECTED_PHOTO_INDEX, selectedPhoto);

		startActivity(fullscreenview);
	}

	@Override
	public boolean onClusterClick(Cluster<PointMarker> cluster) {
		return false;
	}

	private class PhotoMarkerRender extends DefaultClusterRenderer<PointMarker> {
		public PhotoMarkerRender() {
			super(getActivity().getApplicationContext(), getMap(), mClusterManager);
		}

		@Override
		protected void onBeforeClusterItemRendered(PointMarker item, MarkerOptions markerOptions) {
			super.onBeforeClusterItemRendered(item, markerOptions);
			markerOptions.icon(BitmapDescriptorFactory.defaultMarker(item.getPhoto().getIsUploaded() ? BitmapDescriptorFactory.HUE_GREEN : BitmapDescriptorFactory.HUE_RED));
		}

		@Override
		protected void onBeforeClusterRendered(Cluster<PointMarker> cluster, MarkerOptions markerOptions) {
		
			super.onBeforeClusterRendered(cluster, markerOptions);
		}

		@Override
		protected boolean shouldRenderAsCluster(Cluster cluster) {
			// Always render clusters.
			return cluster.getSize() > 1;
		}
	}

	protected GoogleMap getMap() {
		setUpMapIfNeeded();
		return mMap;
	}
}
