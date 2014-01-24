package edu.mit.media.inm.http;

import org.apache.http.params.HttpParams;

import android.content.Context;
import android.util.Log;

public class GetUsers extends GetThread {
	private static final String TAG = "GetUsers HTTP";

	public GetUsers(String uri, int id, Context ctx) {
		super(uri, id, ctx);
	}

	@Override
	public void setupParams() {
		HttpParams params = null;
		httpget.setParams(params);
	}

	@Override
	protected void onPostExecute(String result) {
		Log.d(TAG, "boops");
		Log.d(TAG, result);
	}
}
