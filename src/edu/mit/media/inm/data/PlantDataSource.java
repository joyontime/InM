package edu.mit.media.inm.data;

import java.util.ArrayList;
import java.util.List;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

public class PlantDataSource {

	private static String TAG = "PlantDataSource";
	
	// Database fields
	private SQLiteDatabase database;
	private SQLitePlant dbHelper;
	private String[] allColumns = { SQLitePlant.COLUMN_ID,
			SQLitePlant.COLUMN_AUTHOR, SQLitePlant.COLUMN_DATE,
			SQLitePlant.COLUMN_IMAGE, SQLitePlant.COLUMN_DATE,
			SQLitePlant.COLUMN_STORY, SQLitePlant.COLUMN_TITLE };

	public PlantDataSource(Context context) {
		dbHelper = new SQLitePlant(context);
	}

	public void open() throws SQLException {
		database = dbHelper.getWritableDatabase();
	}

	public void close() {
		dbHelper.close();
	}

	public Plant createPlant(String author, long date, String image,
			String share, String plant, String title) {

		// Enter the new Plant into the db
		ContentValues values = new ContentValues();
		values.put(SQLitePlant.COLUMN_AUTHOR, defIfEmpty(author, "Anon."));
		values.put(SQLitePlant.COLUMN_DATE, date);
		values.put(SQLitePlant.COLUMN_IMAGE, defIfEmpty(image, "None"));
		values.put(SQLitePlant.COLUMN_SHARE, defIfEmpty(share, "Everyone"));
		values.put(SQLitePlant.COLUMN_STORY, defIfEmpty(plant, "-"));
		values.put(SQLitePlant.COLUMN_TITLE, defIfEmpty(title, "Untitled."));
		long insertId = database.insert(SQLitePlant.TABLE_STORY, null, values);

		// Get the entered plant back out as a Plant object
		Cursor cursor = database.query(SQLitePlant.TABLE_STORY, allColumns,
				SQLitePlant.COLUMN_ID + " = " + insertId, null, null, null,
				null);
		cursor.moveToFirst();
		Plant newPlant = cursorToPlant(cursor);
		cursor.close();
		return newPlant;
	}
	
	private String defIfEmpty(String in, String def){
		if (in.length() == 0){
			return def;
		} else {
			return in;
		}
	}

	public void deletePlant(Plant plant) {
		long id = plant.id;
		Log.i(TAG, "Plant deleted with id: " + id);
		database.delete(SQLitePlant.TABLE_STORY, SQLitePlant.COLUMN_ID
				+ " = " + id, null);
	}
	
	public Plant getPlant(long id) {
		Log.i(TAG, "Trying to find plant with id: " + id);
		Cursor cursor = database.query(SQLitePlant.TABLE_STORY, allColumns,
				SQLitePlant.COLUMN_ID + " = " + id, null, null, null, null);
		cursor.moveToFirst();
		Plant plant = cursorToPlant(cursor);
		// make sure to close the cursor
		cursor.close();
		return plant;
	}

	public List<Plant> getAllStories() {
		List<Plant> Plants = new ArrayList<Plant>();

		Cursor cursor = database.query(SQLitePlant.TABLE_STORY, allColumns,
				null, null, null, null, null);

		cursor.moveToFirst();
		while (!cursor.isAfterLast()) {
			Plant Plant = cursorToPlant(cursor);
			Plants.add(Plant);
			cursor.moveToNext();
		}
		// make sure to close the cursor
		cursor.close();
		return Plants;
	}
	
	public List<Plant> getUserStories(String username) {
		List<Plant> Plants = new ArrayList<Plant>();

		Cursor cursor = database.query(SQLitePlant.TABLE_STORY, allColumns,
				SQLitePlant.COLUMN_AUTHOR + " = '" + username + "'", null, null, null, null);

		cursor.moveToFirst();
		while (!cursor.isAfterLast()) {
			Plant Plant = cursorToPlant(cursor);
			Plants.add(Plant);
			cursor.moveToNext();
		}
		// make sure to close the cursor
		cursor.close();
		return Plants;
	}

	private Plant cursorToPlant(Cursor cursor) {
		Plant Plant = new Plant();
		Plant.id = cursor.getLong(0);
		Plant.author = cursor.getString(1);
		Plant.date = cursor.getLong(2);
		Plant.image = cursor.getString(3);
		Plant.share = cursor.getString(4);
		Plant.plant = cursor.getString(5);
		Plant.title = cursor.getString(6);
		return Plant;
	}
}
