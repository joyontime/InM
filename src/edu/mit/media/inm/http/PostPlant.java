package edu.mit.media.inm.http;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.joda.time.format.DateTimeFormatter;
import org.joda.time.format.ISODateTimeFormat;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import android.content.Context;
import android.util.Log;
import edu.mit.media.inm.MainActivity;
import edu.mit.media.inm.R;
import edu.mit.media.inm.handlers.PlantDataSource;
import edu.mit.media.inm.types.Plant;

public class PostPlant extends PostThread{
	private String username;
	private int pot_color;
	private String passphrase;
	private String salt;
	private String shared_with;
	private String title;
	private String type;
	
	private MainActivity main;

	public PostPlant(int id, MainActivity ctx) {
		super(id, ctx);

		String server = ctx.getResources().getString(R.string.url_server);
		String plants = ctx.getResources().getString(R.string.uri_plants);
		this.uri = server + "/" + plants;
		
		//Log.d(TAG, "URI to ping: " + this.uri);
	}

	List<NameValuePair> urlParameters = new ArrayList<NameValuePair>();
	
	public void setupParams(String username,int pot_color,
			String shared_with, String share_local, String title, String type) {
		this.username = username;
		this.pot_color = pot_color;
		this.passphrase = randomString();
		this.salt = randomString();
		this.shared_with = share_local;
		this.title = title;
		this.type = type;
		
		params.add(new BasicNameValuePair("color", String.valueOf(this.pot_color)));
		params.add(new BasicNameValuePair("passphrase", this.passphrase));
		params.add(new BasicNameValuePair("salt", this.salt));
		params.add(new BasicNameValuePair("shared_with", shared_with));
		params.add(new BasicNameValuePair("title", this.title));
		params.add(new BasicNameValuePair("type", this.type));
	}

	@Override
	protected void onPostExecute(String result) {
		try{
			Log.d(TAG, result);
			DateTimeFormatter joda_ISO_parser = ISODateTimeFormat
					.dateTimeParser();
			JSONParser js = new JSONParser();
			JSONObject plant_data = (JSONObject) js.parse(result);
			String iso_string = (String) plant_data.get("created_at");
			long created_at = joda_ISO_parser.parseDateTime(iso_string)
					.getMillis();
			String server_id = (String) plant_data.get("server_id");

			int status = 0;
			if (this.type.equals("bird")){
				status = 2;
			}
			// Save the plant locally
			Plant s = ctx.plant_ds.createPlant(this.username, false, created_at,
					this.passphrase, this.pot_color, this.salt, server_id,
					this.shared_with, status, this.title, this.type, true);
			
			Log.d(TAG, "Published " + s);                                        

			main.refresh();
		} catch (ParseException e) {
			e.printStackTrace();
		}
	}
	
	private String randomString() {
		Random r = new Random();
		StringBuffer sb = new StringBuffer();
		while (sb.length() < 32) {
			sb.append(Integer.toHexString(r.nextInt()));
		}

		String id = sb.toString().substring(0, 32);
		return id;
	}
	
}