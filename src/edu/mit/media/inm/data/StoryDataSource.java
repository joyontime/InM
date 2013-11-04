package edu.mit.media.inm.data;

import java.util.ArrayList;
import java.util.List;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

public class StoryDataSource {

	private static String TAG = "StoryDataSource";
	
	// Database fields
	private SQLiteDatabase database;
	private SQLiteStory dbHelper;
	private String[] allColumns = { SQLiteStory.COLUMN_ID,
			SQLiteStory.COLUMN_AUTHOR, SQLiteStory.COLUMN_DATE,
			SQLiteStory.COLUMN_IMAGE, SQLiteStory.COLUMN_STORY,
			SQLiteStory.COLUMN_TITLE };

	public StoryDataSource(Context context) {
		dbHelper = new SQLiteStory(context);
	}

	public void open() throws SQLException {
		database = dbHelper.getWritableDatabase();
	}

	public void close() {
		dbHelper.close();
	}

	public Story createStory(String author, long date, String image,
			String story, String title) {

		// Enter the new Story into the db
		ContentValues values = new ContentValues();
		values.put(SQLiteStory.COLUMN_AUTHOR, author);
		values.put(SQLiteStory.COLUMN_DATE, date);
		values.put(SQLiteStory.COLUMN_IMAGE, image);
		values.put(SQLiteStory.COLUMN_STORY, story);
		values.put(SQLiteStory.COLUMN_TITLE, title);
		long insertId = database.insert(SQLiteStory.TABLE_STORY, null, values);

		// Get the entered story back out as a Story object
		Cursor cursor = database.query(SQLiteStory.TABLE_STORY, allColumns,
				SQLiteStory.COLUMN_ID + " = " + insertId, null, null, null,
				null);
		cursor.moveToFirst();
		Story newStory = cursorToStory(cursor);
		cursor.close();
		return newStory;
	}

	public void deleteStory(Story story) {
		long id = story.id;
		Log.i(TAG, "Story deleted with id: " + id);
		database.delete(SQLiteStory.TABLE_STORY, SQLiteStory.COLUMN_ID
				+ " = " + id, null);
	}

	public List<Story> getAllStories() {
		List<Story> Storys = new ArrayList<Story>();

		Cursor cursor = database.query(SQLiteStory.TABLE_STORY, allColumns,
				null, null, null, null, null);

		cursor.moveToFirst();
		while (!cursor.isAfterLast()) {
			Story Story = cursorToStory(cursor);
			Storys.add(Story);
			cursor.moveToNext();
		}
		// make sure to close the cursor
		cursor.close();
		return Storys;
	}

	private Story cursorToStory(Cursor cursor) {
		Story Story = new Story();
		Story.id = cursor.getLong(0);
		Story.author = cursor.getString(1);
		Story.date = cursor.getLong(2);
		Story.image = cursor.getString(3);
		Story.story = cursor.getString(4);
		Story.title = cursor.getString(5);
		return Story;
	}
}