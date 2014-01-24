package edu.mit.media.inm.http;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;

import edu.mit.media.inm.R;

import android.content.Context;
import android.util.Log;

public class GetNotes extends GetThread {
	private static final String TAG = "GetUsers HTTP";

	public GetNotes(int id, Context ctx) {
		super(id, ctx);

		String server = ctx.getResources().getString(R.string.url_server);
		String notes = ctx.getResources().getString(R.string.uri_messages);
		String check = ctx.getResources().getString(R.string.uri_check);

		String pinged_at = "123";
		String plant_ids = "123";
		// ...
		String query;
		query = "";
		try {
			query = String.format("pinged_at=%s&plants=[%s]",
					URLEncoder.encode(pinged_at, charset),
					URLEncoder.encode(plant_ids, charset));
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}

		this.uri = server + "/" + notes + "/" + check + "?" + query;
	}

	@Override
	public void setupParams() {
	}

	@Override
	protected void onPostExecute(String result) {
		Log.d(TAG, result);
	}
}