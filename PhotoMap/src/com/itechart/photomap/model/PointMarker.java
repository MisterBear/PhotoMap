package com.itechart.photomap.model;

import com.google.android.gms.maps.model.LatLng;
import com.google.maps.android.clustering.ClusterItem;

public class PointMarker implements ClusterItem {
    private final LatLng mPosition;

    public PointMarker(LatLng position) {
        this.mPosition = position;
    }

	@Override
	public LatLng getPosition() {
		// TODO Auto-generated method stub
		return null;
	}
}
