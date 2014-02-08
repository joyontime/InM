package edu.mit.media.inm.http;

import java.util.HashSet;

import org.joda.time.format.DateTimeFormatter;
import org.joda.time.format.ISODateTimeFormat;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import edu.mit.media.inm.MainActivity;
import edu.mit.media.inm.R;
import edu.mit.media.inm.handlers.UserDataSource;
import edu.mit.media.inm.types.User;

public class GetUsers extends GetThread {

	public GetUsers(int id, MainActivity ctx) {
		super(id, ctx);

		String server = ctx.getResources().getString(R.string.url_server);
		String users = ctx.getResources().getString(R.string.uri_users);
		String check = ctx.getResources().getString(R.string.uri_check);
		this.uri = server + "/" + users + "/" + check;
	}

	@Override
	protected void onPostExecute(String result) {
		UserDataSource datasource = new UserDataSource(ctx);
		datasource.open();

		HashSet<String> server_ids = new HashSet<String>();
		for (User u : datasource.getAllUsers()) {
			server_ids.add(u.server_id);
		}

		JSONParser js = new JSONParser();
		DateTimeFormatter joda_ISO_parser = ISODateTimeFormat
				.dateTimeParser();
		try {
			JSONArray users_json = (JSONArray) js.parse(result);
			for (int i = 0; i < users_json.size(); i++) {
				JSONObject user = (JSONObject) users_json.get(i);
				String iso_date = (String) user.get("date_joined");
				
				
				String user_id = (String) user.get("server_id");
				if (!server_ids.contains(user_id)) {
					datasource.createUser(
							user_id,
							(String) user.get("alias"),
							joda_ISO_parser.parseDateTime(iso_date)
									.getMillis());
				} else {
					server_ids.remove(user_id);
				}
			}
			
			if (server_ids.size() > 0){
				for (String id: server_ids){
					for (User u : datasource.getAllUsers()) {
						if (id.equalsIgnoreCase(u.server_id)){
							datasource.deleteUser(u);
						}
					}
				}
			}
		} catch (ParseException e) {
			e.printStackTrace();
		}
		
		GetPlants plant_thread = new GetPlants(this.id +1, ctx);
		plant_thread.execute();
		
		datasource.close();
	}
}
