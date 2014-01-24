package edu.mit.media.inm.http;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;

import edu.mit.media.inm.R;

import android.content.Context;
import android.util.Log;

public class GetPlants extends GetThread {
	private static final String TAG = "GetUsers HTTP";

	public GetPlants(int id, Context ctx) {
		super(id, ctx);

		String pinged_at = "123";
		// ...
		String query;
		query = "";
		try {
			query = String.format("pinged_at=%s",
					URLEncoder.encode(pinged_at, charset));
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}

		String server = ctx.getResources().getString(R.string.url_server);
		String plants = ctx.getResources().getString(R.string.uri_plants);
		String check = ctx.getResources().getString(R.string.uri_check);
		this.uri = server + "/" + plants + "/" + check + "?" + query;
	}

	@Override
	public void setupParams() {
	}

	@Override
	protected void onPostExecute(String result) {
		Log.d(TAG, result);
	}
}