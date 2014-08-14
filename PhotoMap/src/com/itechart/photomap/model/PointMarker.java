package com.itechart.photomap.model;

import com.google.android.gms.maps.model.LatLng;
import com.google.maps.android.clustering.ClusterItem;
import com.itechart.photomap.database.model.Photo;

public class PointMarker implements ClusterItem {
    private final LatLng mPosition;
    private Photo photo;

    public PointMarker(LatLng position) {
        this.mPosition = position;
    }

	@Override
	public LatLng getPosition() {
		return mPosition;
	}

	public Photo getPhoto() {
		return photo;
	}

	public void setPhoto(Photo photo) {
		this.photo = photo;
	}
}
