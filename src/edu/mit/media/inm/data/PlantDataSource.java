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
			SQLitePlant.COLUMN_PASSPHRASE, SQLitePlant.COLUMN_SALT,
			SQLitePlant.COLUMN_SERVER_ID, SQLitePlant.COLUMN_SHARED_WITH,
			SQLitePlant.COLUMN_STATUS,SQLitePlant.COLUMN_TITLE };

	public PlantDataSource(Context context) {
		dbHelper = new SQLitePlant(context);
	}

	public void open() throws SQLException {
		database = dbHelper.getWritableDatabase();
	}

	public void close() {
		dbHelper.close();
	}

	public Plant createPlant(String author,
			long date, String pass, String salt, String server_id,
			String share, int status, String title) {

		ContentValues values = new ContentValues();
		values.put(SQLitePlant.COLUMN_AUTHOR, author);
		values.put(SQLitePlant.COLUMN_DATE, date);
		values.put(SQLitePlant.COLUMN_PASSPHRASE, pass);
		values.put(SQLitePlant.COLUMN_SALT, salt);
		values.put(SQLitePlant.COLUMN_SERVER_ID, server_id);
		values.put(SQLitePlant.COLUMN_SHARED_WITH, share);
		values.put(SQLitePlant.COLUMN_STATUS, status);
		values.put(SQLitePlant.COLUMN_TITLE, title);
		long insertId = database.insert(SQLitePlant.TABLE_PLANT, null, values);

		// Get the entered plant back out as a Plant object
		Cursor cursor = database.query(SQLitePlant.TABLE_PLANT, allColumns,
				SQLitePlant.COLUMN_ID + " = " + insertId, null, null, null,
				null);
		cursor.moveToFirst();
		Plant newPlant = cursorToPlant(cursor);
		cursor.close();
		return newPlant;
	}

	public void deletePlant(Plant plant) {
		long id = plant.id;
		Log.i(TAG, "Plant deleted with id: " + id);
		database.delete(SQLitePlant.TABLE_PLANT, SQLitePlant.COLUMN_ID
				+ " = " + id, null);
	}
	
	public Plant getPlant(long id) {
		Log.i(TAG, "Trying to find plant with id: " + id);
		Cursor cursor = database.query(SQLitePlant.TABLE_PLANT, allColumns,
				SQLitePlant.COLUMN_ID + " = " + id, null, null, null, null);
		cursor.moveToFirst();
		Plant plant = cursorToPlant(cursor);
		cursor.close();
		return plant;
	}

	public List<Plant> getAllStories() {
		List<Plant> Plants = new ArrayList<Plant>();

		Cursor cursor = database.query(SQLitePlant.TABLE_PLANT, allColumns,
				null, null, null, null, null);

		cursor.moveToFirst();
		while (!cursor.isAfterLast()) {
			Plant Plant = cursorToPlant(cursor);
			Plants.add(Plant);
			cursor.moveToNext();
		}
		cursor.close();
		return Plants;
	}
	
	public List<Plant> getUserStories(String username) {
		List<Plant> Plants = new ArrayList<Plant>();

		Cursor cursor = database.query(SQLitePlant.TABLE_PLANT, allColumns,
				SQLitePlant.COLUMN_AUTHOR + " = '" + username + "'", null, null, null, null);

		cursor.moveToFirst();
		while (!cursor.isAfterLast()) {
			Plant Plant = cursorToPlant(cursor);
			Plants.add(Plant);
			cursor.moveToNext();
		}
		cursor.close();
		return Plants;
	}

	private Plant cursorToPlant(Cursor cursor) {
		Plant Plant = new Plant();
		Plant.id = cursor.getLong(0);
		Plant.author = cursor.getString(1);
		Plant.date = cursor.getLong(2);
		Plant.passphrase = cursor.getString(3);
		Plant.salt = cursor.getString(4);
		Plant.server_id = cursor.getString(5);
		Plant.shared_with = cursor.getString(6);
		Plant.status = cursor.getInt(7);
		Plant.title = cursor.getString(8);
		return Plant;
	}
}
