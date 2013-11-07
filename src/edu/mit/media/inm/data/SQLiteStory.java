package edu.mit.media.inm.data;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteDatabase.CursorFactory;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

public class SQLiteStory extends SQLiteOpenHelper{
	
	public static final String TABLE_STORY = "story";
	public static final String COLUMN_ID = "_id";
	public static final String COLUMN_AUTHOR = "author";
	public static final String COLUMN_DATE = "date";
	public static final String COLUMN_IMAGE = "image";
	public static final String COLUMN_SHARE = "share";
	public static final String COLUMN_STORY = "story";
	public static final String COLUMN_TITLE = "title";
	

	private static final String STORY_DB = "inm.story.db";
	private static final int DATABASE_VERSION = 1;
	
	// Database creation sql statement
	private static final String DATABASE_CREATE = "create table "
			+ TABLE_STORY + "(" 
			+ COLUMN_ID + " integer primary key autoincrement, "
			+ COLUMN_AUTHOR + " text not null, "
			+ COLUMN_DATE + " integer not null, "
			+ COLUMN_IMAGE + " text not null, "
			+ COLUMN_SHARE + " text not null, "
			+ COLUMN_STORY + " text not null, "
			+ COLUMN_TITLE + " text not null)";
	
	public SQLiteStory(Context context) {
		super(context, STORY_DB, null, DATABASE_VERSION);
	}

	@Override
	public void onCreate(SQLiteDatabase db) {
		db.execSQL(DATABASE_CREATE);
	}

	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		 Log.w(SQLiteStory.class.getName(),
			        "Upgrading database from version " + oldVersion + " to "
			            + newVersion + ", which will destroy all old data");
	    db.execSQL("DROP TABLE IF EXISTS " + TABLE_STORY);
	    onCreate(db);
	}
}