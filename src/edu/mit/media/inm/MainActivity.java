package edu.mit.media.inm;

import android.os.Bundle;
import android.app.ActionBar;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.support.v4.app.FragmentActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;
import edu.mit.media.inm.http.GetIV;
import edu.mit.media.inm.http.GetNotes;
import edu.mit.media.inm.http.GetPlants;
import edu.mit.media.inm.http.GetThread;
import edu.mit.media.inm.http.GetUsers;
import edu.mit.media.inm.plant.PlanterFragment;
import edu.mit.media.inm.prefs.PreferenceHandler;
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
			int THREAD_COUNT = 4;
			Log.d(TAG, "Starting update.");

			// create a thread for each URI
			GetThread[] threads = new GetThread[THREAD_COUNT];

			threads[0] = new GetPlants(0, this);
			threads[1] = new GetUsers(1, this);
			threads[2] = new GetNotes(2, this);
			threads[3] = new GetIV(3, this);

			// start the threads
			for (int j = 0; j < THREAD_COUNT; j++) {
				Log.d(TAG, "Executing " + j);
				threads[j].execute();
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
