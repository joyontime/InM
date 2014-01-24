package edu.mit.media.inm.http;

import java.util.List;
import java.util.UUID;

import edu.mit.media.inm.R;
import edu.mit.media.inm.data.UserDataSource;
import edu.mit.media.inm.user.User;
import edu.mit.media.inm.util.JSONUtil;

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
		UserDataSource datasource = new UserDataSource(ctx);
		datasource.open();
		
		List<User> users = datasource.getAllUsers();
		
		for (int i = 0; i < 10; i++){
			datasource.createUser(
					UUID.randomUUID().toString(),
					"User " + i,
					System.currentTimeMillis());
		}

		datasource.close();

		JSONUtil users_json = new JSONUtil(result);
		
		Log.d(TAG, users_json.toString());
	}
}
