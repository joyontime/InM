package edu.mit.media.inm.data;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

public class PlantSQLite extends SQLiteOpenHelper{
	
	public static final String TABLE_PLANT = "plant";
	public static final String COLUMN_ID = "_id";
	public static final String COLUMN_AUTHOR = "author";
	public static final String COLUMN_ARCHIVED = "archived";
	public static final String COLUMN_DATE = "date";
	public static final String COLUMN_PASSPHRASE = "passphrase";
	public static final String COLUMN_POT = "pot";
	public static final String COLUMN_SALT = "salt";
	public static final String COLUMN_SERVER_ID = "server_id";
	public static final String COLUMN_SHARED_WITH = "share";
	public static final String COLUMN_STATUS = "status";
	public static final String COLUMN_UPDATED = "updated";
	public static final String COLUMN_TITLE = "title";

	private static final String STORY_DB = "inm.plant.db";
	private static final int DATABASE_VERSION = 1;
	
	// Database creation sql statement
	private static String integer = " integer not null, ";
	private static String text = " text not null, ";
	
	private static final String DATABASE_CREATE = "create table "
			+ TABLE_PLANT + "(" 
			+ COLUMN_ID + " integer primary key autoincrement, "
			+ COLUMN_AUTHOR + text
			+ COLUMN_ARCHIVED + integer
			+ COLUMN_DATE + integer
			+ COLUMN_PASSPHRASE + text
			+ COLUMN_POT + text
			+ COLUMN_SALT + text
			+ COLUMN_SERVER_ID + text
			+ COLUMN_SHARED_WITH + text
			+ COLUMN_STATUS + integer
			+ COLUMN_TITLE + text
			+ COLUMN_UPDATED + " integer not null)";
	
	public PlantSQLite(Context context) {
		super(context, STORY_DB, null, DATABASE_VERSION);
	}

	@Override
	public void onCreate(SQLiteDatabase db) {
		db.execSQL(DATABASE_CREATE);
	}

	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		 Log.w(PlantSQLite.class.getName(),
			        "Upgrading database from version " + oldVersion + " to "
			            + newVersion + ", which will destroy all old data");
	    db.execSQL("DROP TABLE IF EXISTS " + TABLE_PLANT);
	    onCreate(db);
	}
}
