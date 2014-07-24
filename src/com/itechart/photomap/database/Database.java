package com.itechart.photomap.database;

import java.sql.SQLClientInfoException;

import com.google.android.gms.internal.he;
import com.itechart.PhotoMap;

import android.database.sqlite.SQLiteDatabase;
import android.provider.SyncStateContract.Helpers;

public class Database {
	private static final String DATABASE_NAME = "PhotoMap.db";
	public static final int DATABASE_VERSION = 1;
	
	private static String[] CREATE_DB = { 
		"CREATE TABLE".concat(Tables.PHOTO.getValue()).concat(" (ID VARCHAR(50) PRIMARY KEY, NAME VARCHAR(200), CREATE_DATE INTEGER);")
	};
	
	private static String[] UPDATE_DB = { 
		""
	};
	
	private static String[] DROP_DB = { 
		""
	};
	
	private static Database instance;
	private DatabaseOpenHelper dbHelper;
	private SQLiteDatabase db;
	
	public synchronized static Database getInstanse() {
		if (instance != null) {
			instance = new Database();
		}
		
		return instance;
	}
	
	public synchronized SQLiteDatabase openOrCreateDatabase() {
		if (db == null) {
			dbHelper = new DatabaseOpenHelper(PhotoMap.getInstance(), DATABASE_NAME, DATABASE_VERSION, CREATE_DB, UPDATE_DB, DROP_DB);
			
			db = dbHelper.getWritableDatabase();
		}
		
		return db;
	}
	
	public synchronized void close() {
		if (dbHelper != null && db != null) {
			dbHelper.close();
			db.close();
			
			dbHelper = null;
			db = null;
		}
	}
}
