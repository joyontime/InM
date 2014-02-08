package edu.mit.media.inm.handlers;

import java.util.ArrayList;
import java.util.List;

import edu.mit.media.inm.data.CollectionSQLite;
import edu.mit.media.inm.types.Collection;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

public class CollectionDataSource {

	private static String TAG = "CollectionDataSource";
	
	// Database fields
	private SQLiteDatabase database;
	private CollectionSQLite dbHelper;
	private String[] allColumns = { CollectionSQLite.COLUMN_ID,
			CollectionSQLite.COLUMN_SERVER_ID, CollectionSQLite.COLUMN_NAME,
			CollectionSQLite.COLUMN_PLANTS };

	public CollectionDataSource(Context context) {
		dbHelper = new CollectionSQLite(context);
	}

	public void open() throws SQLException {
		database = dbHelper.getWritableDatabase();
	}

	public void close() {
		dbHelper.close();
	}

	public Collection createCollection(String server_id, String alias, String plants) {
		// Enter the new Collection into the db
		ContentValues values = new ContentValues();
		values.put(CollectionSQLite.COLUMN_SERVER_ID, server_id);
		values.put(CollectionSQLite.COLUMN_NAME, alias);
		values.put(CollectionSQLite.COLUMN_PLANTS, plants);
		long insertId = database.insert(CollectionSQLite.TABLE_COLLECTION, null, values);
		
		// Get the entered Collection back out as a Collection object
		Cursor cursor = database.query(CollectionSQLite.TABLE_COLLECTION, allColumns,
				CollectionSQLite.COLUMN_ID + " = " + insertId, null, null, null,
				null);
		cursor.moveToFirst();
		Collection newCollection = cursorToCollection(cursor);
		cursor.close();
		return newCollection;
	}

	public void deleteCollection(Collection Collection) {
		long id = Collection.id;
		Log.i(TAG, "Collection deleted with id: " + id);
		database.delete(CollectionSQLite.TABLE_COLLECTION, CollectionSQLite.COLUMN_ID
				+ " = " + id, null);
	}
	
	public Collection getCollection(long id) {
		Cursor cursor = database.query(CollectionSQLite.TABLE_COLLECTION, allColumns,
				CollectionSQLite.COLUMN_ID + " = " + id, null, null, null, null);
		cursor.moveToFirst();
		Collection Collection = cursorToCollection(cursor);
		// make sure to close the cursor
		cursor.close();
		return Collection;
	}

	public List<Collection> getAllCollections() {
		List<Collection> Collections = new ArrayList<Collection>();

		Cursor cursor = database.query(CollectionSQLite.TABLE_COLLECTION, allColumns,
				null, null, null, null, null);

		cursor.moveToFirst();
		while (!cursor.isAfterLast()) {
			Collection Collection = cursorToCollection(cursor);
			Collections.add(Collection);
			cursor.moveToNext();
		}
		// make sure to close the cursor
		cursor.close();
		return Collections;
	}

	public void deleteAll(){
		database.delete(CollectionSQLite.TABLE_COLLECTION, null, null);
	}

	private Collection cursorToCollection(Cursor cursor) {
		Collection collection = new Collection();
		collection.id = cursor.getLong(0);
		collection.server_id = cursor.getString(1);
		collection.name = cursor.getString(2);
		collection.plants = cursor.getString(3);
		collection.plant_list = collection.plants.split(",");
		return collection;
	}
}