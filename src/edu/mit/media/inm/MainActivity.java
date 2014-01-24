package edu.mit.media.inm;

import java.io.IOException;
import java.util.UUID;

import android.os.Bundle;
import android.app.ActionBar;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.support.v4.app.FragmentActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;
import edu.mit.media.inm.data.UserDataSource;
import edu.mit.media.inm.http.ThreadedHTTPClient;
import edu.mit.media.inm.plant.PlanterFragment;
import edu.mit.media.inm.prefs.PrefsFragment;
import edu.mit.media.inm.user.FriendFragment;

public class MainActivity extends FragmentActivity {
	private static String TAG = "MainActivity";
	private ActionBar actionBar;
	private FragmentManager fm;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		FragmentManager.enableDebugLogging(true);
		fm = getFragmentManager();
		if (savedInstanceState == null) {
			fm.beginTransaction().add(android.R.id.content, new PlanterFragment())
					.commit();
		}

		// Initilization
		actionBar = getActionBar();

		// actionBar.setHomeButtonEnabled(false);
		actionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_STANDARD);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case R.id.action_refresh:
			Log.d(TAG, "Refreshing views.");
			ThreadedHTTPClient http_client = new ThreadedHTTPClient(this);
			try {
				http_client.updateAll();
				//http_client.pingServer();
			} catch (IOException e) {
				Toast.makeText(this, "Failed to contact server!", Toast.LENGTH_LONG);
				e.printStackTrace();
			}
			
			return true;
		case R.id.action_settings:
			fm.beginTransaction()
					.replace(android.R.id.content, new PrefsFragment())
					.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN)
					.addToBackStack("prefs").commit();
	        actionBar.setDisplayHomeAsUpEnabled(true);

			return true;
		case R.id.action_friends:
			fm.beginTransaction()
			.replace(android.R.id.content, new FriendFragment())
			.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN)
			.addToBackStack("friends").commit();
			actionBar.setDisplayHomeAsUpEnabled(true);
			return true;
		case R.id.action_about:
			String info = "Email joyc@mit.edu if you have any questions or bugs!";
			Toast.makeText(this, info, Toast.LENGTH_LONG);
			return true;
		case android.R.id.home:
			if (fm.getBackStackEntryCount() > 0) {
				if (fm.getBackStackEntryCount() == 1){
					actionBar.setDisplayHomeAsUpEnabled(false);
			        actionBar.setTitle(R.string.app_name);
				}
				fm.popBackStack();
			} else {
				Toast.makeText(this, "Welcome to InMind!", Toast.LENGTH_LONG)
				.show();
			}
		}
		return false;
	}

	@Override
	public void onBackPressed() {
		// check to see if stack is empty
		if (fm.getBackStackEntryCount() > 0) {
			if (fm.getBackStackEntryCount() == 1){
				actionBar.setDisplayHomeAsUpEnabled(false);
		        actionBar.setTitle(R.string.app_name);
			}
			fm.popBackStack();
		} else {
			super.onBackPressed();
		}
	}
}
