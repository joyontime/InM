package edu.mit.media.inm.handlers;

import java.util.ArrayList;
import java.util.List;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.CursorIndexOutOfBoundsException;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;
import edu.mit.media.inm.data.UserSQLite;
import edu.mit.media.inm.types.User;

public class UserDataSource {

	private static String TAG = "UserDataSource";
	
	// Database fields
	private SQLiteDatabase database;
	private UserSQLite dbHelper;
	private String[] allColumns = { UserSQLite.COLUMN_ID,
			UserSQLite.COLUMN_SERVER_ID, UserSQLite.COLUMN_ALIAS,
			UserSQLite.COLUMN_DATE_JOINED };

	public UserDataSource(Context context) {
		dbHelper = new UserSQLite(context);
	}

	public void open() throws SQLException {
		database = dbHelper.getWritableDatabase();
	}

	public void close() {
		dbHelper.close();
	}

	public User createUser(String server_id, String alias, long date_joined) {
		// Enter the new User into the db
		ContentValues values = new ContentValues();
		values.put(UserSQLite.COLUMN_SERVER_ID, server_id);
		values.put(UserSQLite.COLUMN_ALIAS, alias);
		values.put(UserSQLite.COLUMN_DATE_JOINED, date_joined);
		long insertId = database.insert(UserSQLite.TABLE_USER, null, values);

		// Get the entered User back out as a User object
		Cursor cursor = database.query(UserSQLite.TABLE_USER, allColumns,
				UserSQLite.COLUMN_ID + " = " + insertId, null, null, null,
				null);
		cursor.moveToFirst();
		User newUser = cursorToUser(cursor);
		cursor.close();
		return newUser;
	}

	public void deleteUser(User User) {
		long id = User.id;
		Log.i(TAG, "User deleted with id: " + id);
		database.delete(UserSQLite.TABLE_USER, UserSQLite.COLUMN_ID
				+ " = " + id, null);
	}
	
	public User getUser(long id) {
		Cursor cursor = database.query(UserSQLite.TABLE_USER, allColumns,
				UserSQLite.COLUMN_ID + " = " + id, null, null, null, null);
		cursor.moveToFirst();
		User User = cursorToUser(cursor);
		// make sure to close the cursor
		cursor.close();
		return User;
	}
	
	public String getUserAlias(String server_id) {
		if (server_id.equalsIgnoreCase("everyone")) return "Everyone";
		try {
			Cursor cursor = database.query(UserSQLite.TABLE_USER, allColumns,
					UserSQLite.COLUMN_SERVER_ID + " = \"" + server_id + "\"",
					null, null, null, null);
			cursor.moveToFirst();
			User user = cursorToUser(cursor);
			// make sure to close the cursor
			cursor.close();
			return user.alias;
		} catch (CursorIndexOutOfBoundsException e) {
			return "Unknown";
		}
	}

	public List<User> getAllUsers() {
		List<User> Users = new ArrayList<User>();

		Cursor cursor = database.query(UserSQLite.TABLE_USER, allColumns,
				null, null, null, null, null);

		cursor.moveToFirst();
		while (!cursor.isAfterLast()) {
			User User = cursorToUser(cursor);
			Users.add(User);
			cursor.moveToNext();
		}
		// make sure to close the cursor
		cursor.close();
		return Users;
	}

	public void deleteAll(){
		database.delete(UserSQLite.TABLE_USER, null, null);
	}

	private User cursorToUser(Cursor cursor) {
		User User = new User();
		User.id = cursor.getLong(0);
		User.server_id = cursor.getString(1);
		User.alias = cursor.getString(2);
		User.date_joined = cursor.getLong(3);
		return User;
	}
}