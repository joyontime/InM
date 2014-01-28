package edu.mit.media.inm.http;

import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import edu.mit.media.inm.R;
import edu.mit.media.inm.prefs.PreferenceHandler;
import android.content.Context;

public class GetIV extends GetThread {
	protected static final String TAG = "GetIV HTTP";

	public GetIV(int id, Context ctx) {
		super(id, ctx);
		String server = ctx.getResources().getString(R.string.url_server);
		String users = ctx.getResources().getString(R.string.uri_users);
		String IV = ctx.getResources().getString(R.string.uri_IV);
		this.uri = server + "/" + users + "/" + IV;
	}

	@Override
	protected void onPostExecute(String result) {
		PreferenceHandler ph = new PreferenceHandler(ctx);

		JSONParser js = new JSONParser();
		try {
			JSONObject iv = (JSONObject) js.parse(result);
			ph.set_IV((String)iv.get("IV"));
			ph.set_server_id((String)iv.get("server_id"));
			
		} catch (ParseException e) {
			e.printStackTrace();
		}
	}
}
