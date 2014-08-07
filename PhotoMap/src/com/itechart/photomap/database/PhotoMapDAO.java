package com.itechart.photomap.database;

import java.sql.SQLException;
import java.util.List;

import com.itechart.photomap.database.model.Photo;
import com.j256.ormlite.dao.BaseDaoImpl;
import com.j256.ormlite.stmt.PreparedQuery;
import com.j256.ormlite.stmt.QueryBuilder;
import com.j256.ormlite.support.ConnectionSource;

public class PhotoMapDAO extends BaseDaoImpl<Photo, Integer>{
	protected PhotoMapDAO(ConnectionSource connectionSource, Class<Photo> dataClass) throws SQLException {
		super(connectionSource, dataClass);
	}
	
	public List<Photo> queryAllUnploaded() throws SQLException {
		QueryBuilder<Photo, Integer> queryBuilder = queryBuilder();
        queryBuilder.where().eq(Photo.IS_UPLOADED_FIELD_NAME, false);
        PreparedQuery<Photo> preparedQuery = queryBuilder.prepare();
        List<Photo> fileCacheRecordsList = query(preparedQuery);
        return fileCacheRecordsList;
	}
	
	public void deleteAllNotUploaded() throws SQLException {
		delete(queryAllUnploaded());
	}
}
