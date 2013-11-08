package edu.mit.media.inm.data;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteDatabase.CursorFactory;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

public class SQLiteStatus extends SQLiteOpenHelper{
	
	public static final String TABLE_STATUS = "status";
	public static final String COLUMN_ID = "_id";
	public static final String COLUMN_AVAIL = "avail";
	public static final String COLUMN_BRIEFING = "briefing";
	public static final String COLUMN_DATE = "date";
	public static final String COLUMN_MOOD = "mood";
	public static final String COLUMN_NAME = "name";
	
	private static final String STATUS_DB = "inm.status.db";
	private static final int DATABASE_VERSION = 1;
	
	// Database creation sql statement
	private static final String DATABASE_CREATE = "create table "
			+ TABLE_STATUS + "(" 
			+ COLUMN_ID + " integer primary key autoincrement, "
			+ COLUMN_AVAIL + " integer not null, "
			+ COLUMN_BRIEFING + " text not null, "
			+ COLUMN_DATE + " integer not null, "
			+ COLUMN_MOOD + " int not null, "
			+ COLUMN_NAME + " text not null)";
	
	public SQLiteStatus(Context context) {
		super(context, STATUS_DB, null, DATABASE_VERSION);
	}

	@Override
	public void onCreate(SQLiteDatabase db) {
		db.execSQL(DATABASE_CREATE);
	}

	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		 Log.w(SQLiteStatus.class.getName(),
			        "Upgrading database from version " + oldVersion + " to "
			            + newVersion + ", which will destroy all old data");
	    db.execSQL("DROP TABLE IF EXISTS " + TABLE_STATUS);
	    onCreate(db);
	}
}