package com.itechart.photomap.database;

import java.sql.SQLException;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;

import com.itechart.photomap.database.model.Photo;
import com.itechart.utils.Utils;
import com.j256.ormlite.android.apptools.OrmLiteSqliteOpenHelper;
import com.j256.ormlite.support.ConnectionSource;
import com.j256.ormlite.table.TableUtils;

public class DatabaseHelper extends OrmLiteSqliteOpenHelper {
	private static final String DATABASE_NAME = "photo_map.db";
	public static final int DATABASE_VERSION = 1;
	private PhotoMapDAO photoMapDAO = null;
		
	public DatabaseHelper(Context context) {
		super(context, DATABASE_NAME, null, DATABASE_VERSION);
	}	

	@Override
	public void onCreate(SQLiteDatabase database, ConnectionSource connectionSource) {
		try {
			TableUtils.createTable(connectionSource, Photo.class);
		} catch (SQLException e) {
			Utils.handleException(DATABASE_NAME + " OnCreate", e);
			throw new RuntimeException();
		}
	}

	@Override
	public void onUpgrade(SQLiteDatabase database, ConnectionSource connectionSource, int oldVersion, int newVersion) {
		try {
			TableUtils.dropTable(connectionSource, Photo.class, true);
		} catch (SQLException e) {
			Utils.handleException(DATABASE_NAME + " OnUpgrade", e);
			throw new RuntimeException();
		}
	}
	
	public synchronized PhotoMapDAO getPhotoMapDAO() throws SQLException {
		if (photoMapDAO == null) {
			photoMapDAO = new PhotoMapDAO(getConnectionSource(), Photo.class);
		}
		
		return photoMapDAO;
	}
	
	@Override
	public void close() {
		photoMapDAO = null;
		
		super.close();
	}
}
