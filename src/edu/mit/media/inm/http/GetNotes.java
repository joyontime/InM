package edu.mit.media.inm.http;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.HashSet;

import org.joda.time.format.DateTimeFormatter;
import org.joda.time.format.ISODateTimeFormat;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import edu.mit.media.inm.R;
import edu.mit.media.inm.data.NoteDataSource;
import edu.mit.media.inm.data.PlantDataSource;
import edu.mit.media.inm.data.UserDataSource;
import edu.mit.media.inm.note.Note;
import edu.mit.media.inm.plant.Plant;

import android.content.Context;
import android.util.Log;

public class GetNotes extends GetThread {
	private static final String TAG = "GetNotes HTTP";

	public GetNotes(int id, Context ctx) {
		super(id, ctx);

		String server = ctx.getResources().getString(R.string.url_server);
		String notes = ctx.getResources().getString(R.string.uri_messages);
		String check = ctx.getResources().getString(R.string.uri_check);

		// Collect the plant ids of plants loaded on this device.		
		StringBuilder query = new StringBuilder();
		try {
			query.append("pinged_at=");
			query.append(URLEncoder.encode(String.valueOf(ph.last_pinged()), charset));
			
			PlantDataSource datasource = new PlantDataSource(ctx);
			datasource.open();
			if (datasource.getAllPlants().size() < 1){
				this.cancel(true);
			}
			
			for (Plant p: datasource.getAllPlants()){
				query.append("&");
				query.append("plants=");
				query.append(URLEncoder.encode(p.server_id, charset));
			}
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}

		this.uri = server + "/" + notes + "/" + check + "?" + query.toString();
		
		Log.d(TAG, "URI to ping: " + this.uri);
	}
	@Override
	protected void onPostExecute(String result) {
		NoteDataSource datasource = new NoteDataSource(ctx);
		datasource.open();
		UserDataSource userdata = new UserDataSource(ctx);
		userdata.open();
		PlantDataSource plantdata = new PlantDataSource(ctx);
		plantdata.open();

		HashSet<String> server_ids = new HashSet<String>();
		for (Note n : datasource.getAllNotes()) {
			server_ids.add(n.server_id);
		}

		JSONParser js = new JSONParser();
		DateTimeFormatter joda_ISO_parser = ISODateTimeFormat
				.dateTimeParser();
		try {
			JSONArray notes_json = (JSONArray) js.parse(result);
			for (int i = 0; i < notes_json.size(); i++) {
				JSONObject note = (JSONObject) notes_json.get(i);
				String iso_date = (String) note.get("created_at");
				
				String note_id = (String) note.get("server_id");
				if (!server_ids.contains(note_id)) {
					datasource.createNote(
							userdata.getUserAlias((String) note.get("user_id")),
							joda_ISO_parser.parseDateTime(iso_date)
									.getMillis(),
							(String) note.get("text"),
							(String) note.get("plant_id"),
							note_id);
					plantdata.setPlantShiny((String) note.get("plant_id"), true);
				} else {
					server_ids.remove(note_id);
				}
			}
		} catch (ParseException e) {
			e.printStackTrace();
		}
		datasource.close();
		userdata.close();
		plantdata.close();
	}
}