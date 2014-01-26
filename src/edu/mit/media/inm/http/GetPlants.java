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
import edu.mit.media.inm.data.PlantDataSource;
import edu.mit.media.inm.plant.Plant;

import android.content.Context;

public class GetPlants extends GetThread {
	private static final String TAG = "GetPlants HTTP";

	public GetPlants(int id, Context ctx) {
		super(id, ctx);
		
		String query;
		query = "";
		try {
			query = String.format("pinged_at=%s",
					URLEncoder.encode(String.valueOf(ph.last_pinged()), charset));
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}

		String server = ctx.getResources().getString(R.string.url_server);
		String plants = ctx.getResources().getString(R.string.uri_plants);
		String check = ctx.getResources().getString(R.string.uri_check);
		this.uri = server + "/" + plants + "/" + check + "?" + query;
	}

	@Override
	protected void onPostExecute(String result) {

		PlantDataSource datasource = new PlantDataSource(ctx);
		datasource.open();

		HashSet<String> server_ids = new HashSet<String>();
		for (Plant u : datasource.getAllPlants()) {
			server_ids.add(u.server_id);
		}

		JSONParser js = new JSONParser();
		DateTimeFormatter joda_ISO_parser = ISODateTimeFormat
				.dateTimeParser();
		try {
			JSONArray Plants_json = (JSONArray) js.parse(result);
			for (int i = 0; i < Plants_json.size(); i++) {
				JSONObject plant = (JSONObject) Plants_json.get(i);
				String iso_date = (String) plant.get("created_at");
				
				String plant_id = (String) plant.get("server_id");
				if (!server_ids.contains(plant_id)) {
					JSONArray shared_users_json = (JSONArray) plant.get("shared_with");
					
					StringBuilder shared_with = new StringBuilder();
					for (int j =0; i< shared_users_json.size(); i++){
						String user_id = (String) shared_users_json.get(j);
						shared_with.append(user_id);
        				shared_with.append(',');
	        		}
					
					
					datasource.createPlant(
							(String) plant.get("owner"),
							joda_ISO_parser.parseDateTime(iso_date)
									.getMillis(),
							(String) plant.get("passphrase"),
							Integer.parseInt((String) plant.get("color")),
							(String) plant.get("salt"),
							plant_id,
							shared_with.toString(),
							Integer.parseInt((String) plant.get("status")),
							(String) plant.get("title"));
				} else {
					server_ids.remove(plant_id);
				}
			}
			
			if (server_ids.size() > 0){
				for (String id: server_ids){
					for (Plant p : datasource.getAllPlants()) {
						if (id.equalsIgnoreCase(p.server_id)){
							datasource.deletePlant(p);
						}
					}
				}
			}
		} catch (ParseException e) {
			e.printStackTrace();
		}
		
		datasource.close();
	}
}