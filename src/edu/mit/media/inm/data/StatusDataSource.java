package edu.mit.media.inm.data;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

public class StatusDataSource {

	private static String TAG = "StatusDataSource";

	// Database fields
	private SQLiteDatabase database;
	private SQLiteStatus dbHelper;
	private String[] allColumns = { SQLiteStatus.COLUMN_ID,
			SQLiteStatus.COLUMN_AVAIL, SQLiteStatus.COLUMN_BRIEFING,
			SQLiteStatus.COLUMN_DATE, SQLiteStatus.COLUMN_MOOD,
			SQLiteStatus.COLUMN_NAME };

	public StatusDataSource(Context context) {
		dbHelper = new SQLiteStatus(context);
	}

	public void open() throws SQLException {
		database = dbHelper.getWritableDatabase();
	}

	public void close() {
		dbHelper.close();
	}

	public Status createStatus(int avail, String briefing, long date, int mood,
			String name) {

		// Enter the new Status into the db
		ContentValues values = new ContentValues();
		values.put(SQLiteStatus.COLUMN_AVAIL, avail);
		values.put(SQLiteStatus.COLUMN_BRIEFING, defIfEmpty(briefing, "-"));
		values.put(SQLiteStatus.COLUMN_DATE, date);
		values.put(SQLiteStatus.COLUMN_MOOD, mood);
		values.put(SQLiteStatus.COLUMN_NAME, defIfEmpty(name, "Anon."));
		long insertId = database
				.insert(SQLiteStatus.TABLE_STATUS, null, values);

		// Get the entered Status back out as a Status object
		Cursor cursor = database.query(SQLiteStatus.TABLE_STATUS, allColumns,
				SQLiteStatus.COLUMN_ID + " = " + insertId, null, null, null,
				null);
		cursor.moveToFirst();
		Status newStatus = cursorToStatus(cursor);
		cursor.close();
		return newStatus;
	}

	private String defIfEmpty(String in, String def) {
		if (in.length() == 0) {
			return def;
		} else {
			return in;
		}
	}

	public void deleteStatus(Status Status) {
		long id = Status.id;
		Log.i(TAG, "Status deleted with id: " + id);
		database.delete(SQLiteStatus.TABLE_STATUS, SQLiteStatus.COLUMN_ID
				+ " = " + id, null);
	}

	public List<Status> getAllStatuses() {
		Map<String, Status> nameToStatus = new HashMap<String, Status>();

		Cursor cursor = database.query(SQLiteStatus.TABLE_STATUS, allColumns,
				null, null, null, null, null);

		cursor.moveToFirst();
		while (!cursor.isAfterLast()) {
			Status status = cursorToStatus(cursor);
			if (nameToStatus.containsKey(status.name)){
				if (status.date > nameToStatus.get(status.name).date){
					nameToStatus.put(status.name, status);
				}
			} else {
				nameToStatus.put(status.name, status);
			}
			cursor.moveToNext();
		}
		// make sure to close the cursor
		cursor.close();
		return new ArrayList<Status>(nameToStatus.values());
	}

	public List<Status> getUserStatuses(String username) {
		List<Status> Statuses = new ArrayList<Status>();

		Cursor cursor = database.query(SQLiteStatus.TABLE_STATUS, allColumns,
				SQLiteStatus.COLUMN_NAME + " = '" + username + "'", null, null,
				null, SQLiteStatus.COLUMN_DATE);

		cursor.moveToFirst();
		while (!cursor.isAfterLast()) {
			Status Status = cursorToStatus(cursor);
			Statuses.add(Status);
			cursor.moveToNext();
		}
		// make sure to close the cursor
		cursor.close();
		return Statuses;
	}

	private Status cursorToStatus(Cursor cursor) {
		Status Status = new Status();
		Status.id = cursor.getLong(0);
		Status.avail = cursor.getInt(1);
		Status.briefing = cursor.getString(2);
		Status.date = cursor.getLong(3);
		Status.mood = cursor.getInt(4);
		Status.name = cursor.getString(5);
		return Status;
	}
}