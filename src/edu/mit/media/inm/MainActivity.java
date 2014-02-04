package edu.mit.media.inm;

import java.util.Calendar;

import com.google.analytics.tracking.android.EasyTracker;
import com.google.analytics.tracking.android.MapBuilder;

import android.app.ActionBar;
import android.app.AlertDialog;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.text.Editable;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Toast;
import edu.mit.media.inm.data.NoteDataSource;
import edu.mit.media.inm.data.PlantDataSource;
import edu.mit.media.inm.data.UserDataSource;
import edu.mit.media.inm.http.GetIV;
import edu.mit.media.inm.plant.PlantFragment;
import edu.mit.media.inm.plant.PlanterFragment;
import edu.mit.media.inm.prefs.PreferenceHandler;
import edu.mit.media.inm.prefs.PrefsFragment;
import edu.mit.media.inm.user.FriendFragment;
import edu.mit.media.inm.util.NotifyService;

public class MainActivity extends FragmentActivity {
	private static String TAG = "MainActivity";
	private ActionBar actionBar;
	private FragmentManager fm;
	private PreferenceHandler ph;
	
	private Intent notifyService;
	
	private EasyTracker tracker;
	
	private long start_time;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		FragmentManager.enableDebugLogging(true);
		fm = getFragmentManager();
		if (savedInstanceState == null) {
			fm.beginTransaction()
			.add(android.R.id.content, new PlanterFragment(), "planter")
			.commit();
		}

		actionBar = getActionBar();

		actionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_STANDARD);
		
		ph = new PreferenceHandler(this);
		notifyService = new Intent(this, NotifyService.class);
		if (ph.prompt() && !ph.password().equals("None")){
			startService(notifyService);
		} else {
			stopService(notifyService);
		}
		Calendar cal = Calendar.getInstance();
		Long minute = Long.valueOf(60 * cal.get(Calendar.HOUR_OF_DAY)
				+ cal.get(Calendar.MINUTE));
        
		tracker = EasyTracker.getInstance(this);
		tracker.send(MapBuilder
			      .createEvent("ui_action",
			                   "access_main",
			                   ph.server_id(),
			                   minute)
			      .build());
		start_time = System.currentTimeMillis();

		pingServer();
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case R.id.action_refresh:
			pingServer();
			return true;
		case R.id.action_login:
			loginDialog();
			return true;
		case R.id.action_archived:
            fm.beginTransaction()
			.replace(android.R.id.content, PlanterFragment.newInstance(true), "archived")
			.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN)
			.addToBackStack("archived").commit();
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
			String info = "Email joyc@mit.edu if you have any questions or bugs to report!";
			Toast.makeText(this, info, Toast.LENGTH_LONG).show();
			return true;
		case R.id.action_settings:
			fm.beginTransaction()
					.replace(android.R.id.content, new PrefsFragment())
					.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN)
					.addToBackStack("prefs").commit();
	        actionBar.setDisplayHomeAsUpEnabled(true);
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
	
	public void loginDialog(){
		LayoutInflater inflater = this.getLayoutInflater();
		final View login_view = inflater.inflate(R.layout.dialog_signin, null);
		
		AlertDialog.Builder login_dialog = new AlertDialog.Builder(this)
	    .setTitle(R.string.action_login)
	    .setView(login_view);
	    
	    login_dialog.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
	        public void onClick(DialogInterface dialog, int whichButton) {
	        	clearAllDb(
	        			((EditText)login_view.findViewById(R.id.login_username)).getText().toString(),
	        			((EditText)login_view.findViewById(R.id.login_password)).getText().toString());
	        	pingServer();
	        }
	    }).setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
	        public void onClick(DialogInterface dialog, int whichButton) {
	            // Do nothing.
	        }
	    }).show();
	}
	
	private void clearAllDb(String username, String password){
		ph.set_server_id("None");
		ph.set_IV(PreferenceHandler.default_IV);
		ph.set_last_pinged(0);
    	ph.setPassword(password);
    	ph.setUsername(username);
		UserDataSource userdata = new UserDataSource(this);
		userdata.open();
		userdata.deleteAll();
		userdata.close();
		PlantDataSource plantdata = new PlantDataSource(this);
		plantdata.open();
		plantdata.deleteAll();
		plantdata.close();
		NoteDataSource notedata = new NoteDataSource(this);
		notedata.open();
		notedata.deleteAll();
		notedata.close();
	}
	
	private void pingServer(){
		Log.d(TAG, "Starting update.");
		final GetIV iv_thread = new GetIV(0, this);
		iv_thread.execute();
	}
	
	public void refresh(){
		PlanterFragment planter_frag = (PlanterFragment) getFragmentManager()
				.findFragmentByTag("planter");
		if (planter_frag !=null){
			planter_frag.refresh();
		}
		
		PlanterFragment archived_frag = (PlanterFragment) getFragmentManager()
				.findFragmentByTag("archived");
		if (archived_frag != null){
			archived_frag.refresh();
		}

		PlantFragment plant_frag = (PlantFragment) getFragmentManager()
				.findFragmentByTag("plant");
		if (plant_frag != null){
			plant_frag.refresh();
		}
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
	
	@Override
	  public void onStart() {
	    super.onStart();
	    tracker.activityStart(this);  // Add this method.
		this.refresh();
	  }

	@Override
	public void onResume(){
		super.onResume();
	}
	
	@Override
	  public void onStop() {
	    super.onStop();
	    tracker.activityStop(this);  // Add this method.
	    tracker.send(MapBuilder
	    	      .createTiming("engagement",
	                      System.currentTimeMillis()-this.start_time, 
	                      "main",
	                      ph.server_id())
	        .build()
	    );
	  }
}
