package com.itechart.photomap.database;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteDatabase.CursorFactory;
import android.database.sqlite.SQLiteOpenHelper;

public class DatabaseOpenHelper extends SQLiteOpenHelper {
	
	private String[] create_db;
	private String[] update_db;
	private String[] drop_db;
		
	public DatabaseOpenHelper(Context context, String name, int version, String[] create_db, String[] update_db, String[] drop_db) {
		super(context, name, null, version);
		
		this.create_db = create_db;
		this.update_db = update_db;
		this.drop_db = drop_db;
	}	

	@Override
	public void onCreate(SQLiteDatabase database) {
		if (create_db != null) {
			for (int index = 0; index < create_db.length; index++) {
				database.execSQL(create_db[index]);
			}
		}
	}

	@Override
	public void onUpgrade(SQLiteDatabase database, int oldVersion, int newVersion) {
		if (create_db != null) {
			for (int index = 0; index < update_db.length; index++) {
				database.execSQL(update_db[index]);
			}
		}
		
		database.setVersion(Database.DATABASE_VERSION);
	}
}
