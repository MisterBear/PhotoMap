package com.itechart.database;

import java.sql.SQLException;

import com.itechart.database.model.Photo;
import com.j256.ormlite.dao.BaseDaoImpl;
import com.j256.ormlite.support.ConnectionSource;

public class PhotoMapDAO extends BaseDaoImpl<Photo, Integer>{
	protected PhotoMapDAO(ConnectionSource connectionSource, Class<Photo> dataClass) throws SQLException {
		super(connectionSource, dataClass);
	}
	
	
}