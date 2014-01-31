package edu.mit.media.inm.data;

import java.util.ArrayList;
import java.util.List;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;
import edu.mit.media.inm.note.Note;

public class NoteDataSource {

	private static String TAG = "NoteDataSource";
	
	// Database fields
	private SQLiteDatabase database;
	private NoteSQLite dbHelper;
	private String[] allColumns = { NoteSQLite.COLUMN_ID,
			NoteSQLite.COLUMN_AUTHOR, NoteSQLite.COLUMN_DATE,
			NoteSQLite.COLUMN_TEXT, NoteSQLite.COLUMN_PLANT,
			NoteSQLite.COLUMN_SERVER_ID};

	public NoteDataSource(Context context) {
		dbHelper = new NoteSQLite(context);
	}

	public void open() throws SQLException {
		database = dbHelper.getWritableDatabase();
	}

	public void close() {
		dbHelper.close();
	}

	public Note createNote(String author, long date, String text, String plant, String server_id) {

		// Enter the new Note into the db
		ContentValues values = new ContentValues();
		values.put(NoteSQLite.COLUMN_AUTHOR, author);
		values.put(NoteSQLite.COLUMN_DATE, date);
		values.put(NoteSQLite.COLUMN_TEXT, text);
		values.put(NoteSQLite.COLUMN_PLANT, plant);
		values.put(NoteSQLite.COLUMN_SERVER_ID, server_id);
		long insertId = database.insert(NoteSQLite.TABLE_NOTE, null, values);

		// Get the entered Note back out as a Note object
		Cursor cursor = database.query(NoteSQLite.TABLE_NOTE, allColumns,
				NoteSQLite.COLUMN_ID + " = " + insertId, null, null, null,
				null);
		cursor.moveToFirst();
		Note newNote = cursorToNote(cursor);
		cursor.close();
		return newNote;
	}

	public void deleteNote(Note Note) {
		long id = Note.id;
		Log.i(TAG, "Note deleted with id: " + id);
		database.delete(NoteSQLite.TABLE_NOTE, NoteSQLite.COLUMN_ID
				+ " = " + id, null);
	}

	public Note getNote(long id) {
		Log.i(TAG, "Trying to find Note with id: " + id);
		Cursor cursor = database.query(NoteSQLite.TABLE_NOTE, allColumns,
				NoteSQLite.COLUMN_ID + " = " + id, null, null, null, null);
		cursor.moveToFirst();
		Note Note = cursorToNote(cursor);
		// make sure to close the cursor
		cursor.close();
		return Note;
	}

	public List<Note> getAllNotes() {
		List<Note> Notes = new ArrayList<Note>();

		Cursor cursor = database.query(NoteSQLite.TABLE_NOTE, allColumns,
				null, null, null, null, null);

		cursor.moveToFirst();
		while (!cursor.isAfterLast()) {
			Note Note = cursorToNote(cursor);
			Notes.add(Note);
			cursor.moveToNext();
		}
		// make sure to close the cursor
		cursor.close();
		return Notes;
	}

	public List<Note> getPlantNotes(String plant) {
		List<Note> Notes = new ArrayList<Note>();

		Cursor cursor = database.query(NoteSQLite.TABLE_NOTE, allColumns,
				NoteSQLite.COLUMN_PLANT + " = ?", new String[]{plant},
				null, null, null);

		cursor.moveToFirst();
		while (!cursor.isAfterLast()) {
			Note Note = cursorToNote(cursor);
			Notes.add(Note);
			cursor.moveToNext();
		}
		// make sure to close the cursor
		cursor.close();
		return Notes;
	}

	private Note cursorToNote(Cursor cursor) {
		Note Note = new Note();
		Note.id = cursor.getLong(0);
		Note.author = cursor.getString(1);
		Note.date = cursor.getLong(2);
		Note.text = cursor.getString(3);
		Note.plant = cursor.getString(4);
		Note.server_id = cursor.getString(5);
		return Note;
	}
}