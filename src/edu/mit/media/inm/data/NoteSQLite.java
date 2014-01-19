package edu.mit.media.inm.data;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

public class NoteSQLite extends SQLiteOpenHelper{
	
	public static final String TABLE_NOTE = "story";
	public static final String COLUMN_ID = "_id";
	public static final String COLUMN_AUTHOR = "author";
	public static final String COLUMN_DATE = "date";
	public static final String COLUMN_TEXT = "text";
	public static final String COLUMN_PLANT = "plant_id";

	private static final String STORY_DB = "inm.story.db";
	private static final int DATABASE_VERSION = 1;
	
	// Database creation sql statement
	private static final String DATABASE_CREATE = "create table "
			+ TABLE_NOTE + "(" 
			+ COLUMN_ID + " integer primary key autoincrement, "
			+ COLUMN_AUTHOR + " text not null, "
			+ COLUMN_DATE + " integer not null, "
			+ COLUMN_TEXT + " text not null, "
			+ COLUMN_PLANT + " text not null)";
	
	public NoteSQLite(Context context) {
		super(context, STORY_DB, null, DATABASE_VERSION);
	}

	@Override
	public void onCreate(SQLiteDatabase db) {
		db.execSQL(DATABASE_CREATE);
	}

	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		 Log.w(NoteSQLite.class.getName(),
			        "Upgrading database from version " + oldVersion + " to "
			            + newVersion + ", which will destroy all old data");
	    db.execSQL("DROP TABLE IF EXISTS " + TABLE_NOTE);
	    onCreate(db);
	}
}