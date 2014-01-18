package edu.mit.media.inm.data;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

public class UserSQLite extends SQLiteOpenHelper{
	
	public static final String TABLE_USER = "user";
	public static final String COLUMN_ID = "_id";
	public static final String COLUMN_SERVER_ID = "server_id";
	public static final String COLUMN_ALIAS = "alias";
	public static final String COLUMN_DATE_JOINED = "date_joined";	

	private static final String STORY_DB = "inm.story.db";
	private static final int DATABASE_VERSION = 1;
	
	// Database creation sql statement
	private static final String DATABASE_CREATE = "create table "
			+ TABLE_USER + "(" 
			+ COLUMN_ID + " integer primary key autoincrement, "
			+ COLUMN_SERVER_ID + " text not null, "
			+ COLUMN_ALIAS + " text not null, "
			+ COLUMN_DATE_JOINED + " integer not null)";
	
	public UserSQLite(Context context) {
		super(context, STORY_DB, null, DATABASE_VERSION);
	}

	@Override
	public void onCreate(SQLiteDatabase db) {
		db.execSQL(DATABASE_CREATE);
	}

	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		 Log.w(UserSQLite.class.getName(),
			        "Upgrading database from version " + oldVersion + " to "
			            + newVersion + ", which will destroy all old data");
	    db.execSQL("DROP TABLE IF EXISTS " + TABLE_USER);
	    onCreate(db);
	}
}