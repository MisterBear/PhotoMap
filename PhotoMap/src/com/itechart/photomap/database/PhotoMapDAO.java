package com.itechart.photomap.database;

import com.google.android.gms.internal.in;

public class PhotoMapDAO {
	private static PhotoMapDAO instance;
	
	public synchronized static PhotoMapDAO getInstance() {
		if (instance == null) {
			instance = new PhotoMapDAO();
		}
		
		return instance;
	}
	
	

}
