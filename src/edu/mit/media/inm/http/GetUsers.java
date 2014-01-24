package edu.mit.media.inm.http;

import org.apache.http.params.HttpParams;

import edu.mit.media.inm.R;

import android.content.Context;
import android.util.Log;

public class GetUsers extends GetThread {
	private static final String TAG = "GetUsers HTTP";

	public GetUsers(int id, Context ctx) {
		super(id, ctx);

		String server = ctx.getResources().getString(R.string.url_server);
		String users = ctx.getResources().getString(R.string.uri_users);
		String check = ctx.getResources().getString(R.string.uri_check);
		this.uri = server + "/" + users + "/" + check;
	}

	@Override
	public void setupParams() {

	}

	@Override
	protected void onPostExecute(String result) {
		Log.d(TAG, result);
	}
}
