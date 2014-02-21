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

import android.util.Log;
import edu.mit.media.inm.MainActivity;
import edu.mit.media.inm.R;
import edu.mit.media.inm.handlers.PlantDataSource;
import edu.mit.media.inm.types.Plant;

public class GetPlants extends GetThread {
	private static final String TAG = "GetPlants HTTP";

	public GetPlants(int id, MainActivity ctx) {
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
	protected void onPostExecute(Boolean result) {
		Log.d(TAG, "GET plants finished");
		if (result){
			GetNotes check_message = new GetNotes(this.id + 1, ctx);
			check_message.execute();
		}
	}

	@Override
	protected void handleResults(String result) {
		HashSet<String> server_ids = new HashSet<String>();
		for (Plant u : ctx.plant_ds.getAllPlants()) {
			server_ids.add(u.server_id);
		}

		JSONParser js = new JSONParser();
		DateTimeFormatter joda_ISO_parser = ISODateTimeFormat
				.dateTimeParser();
		try {
			JSONArray plants_json = (JSONArray) js.parse(result);

			for (int i = 0; i < plants_json.size(); i++) {
				JSONObject plant = (JSONObject) plants_json.get(i);
				String created_at = (String) plant.get("created_at");				
				String plant_id = (String) plant.get("server_id");
				if (!server_ids.contains(plant_id)) {
					JSONArray shared_users_json = (JSONArray) plant.get("shared_with");
					StringBuilder shared_with = new StringBuilder();
					for (int j =0; j< shared_users_json.size(); j++){
						String user_id = (String) shared_users_json.get(j);
						if (!user_id.equalsIgnoreCase(ph.server_id())){
							shared_with.append(user_id);
	        				shared_with.append(',');
						}
	        		}
					boolean shiny = true;
					
					long modified_at = joda_ISO_parser.parseDateTime(
							(String) plant.get("modified_at"))
							.getMillis();
					if (ph.now() > modified_at){
						shiny = false;
					}
					ctx.plant_ds.createPlant(
							(String) plant.get("owner"),
							(Boolean) plant.get("archived"),
							joda_ISO_parser.parseDateTime(created_at)
									.getMillis(),
							(String) plant.get("passphrase"),
							Integer.parseInt((String) plant.get("color")),
							(String) plant.get("salt"),
							plant_id,
							shared_with.toString(),
							Integer.parseInt((String) plant.get("status")),
							(String) plant.get("title"),
							(String) plant.get("type"),
							shiny);
				} else {
					Plant old_plant = ctx.plant_ds.getPlantByServerID(plant_id);
					String status = (String) plant.get("status");
					Boolean archived = (Boolean) plant.get("archived");
					if ((old_plant.archived ^ archived)
							|| (old_plant.status != Integer.valueOf(status))) {
						ctx.plant_ds.updatePlant(plant_id,
								Integer.valueOf(status), archived);
						ctx.plant_ds.setPlantShiny(plant_id, true);
					}
					server_ids.remove(plant_id);
				}
			}
		} catch (ParseException e) {
			e.printStackTrace();
		}
	}
}