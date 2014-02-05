package edu.mit.media.inm;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashSet;
import java.util.Set;

import com.google.analytics.tracking.android.EasyTracker;
import com.google.analytics.tracking.android.MapBuilder;

import android.app.ActionBar;
import android.app.AlertDialog;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;
import edu.mit.media.inm.data.NoteDataSource;
import edu.mit.media.inm.data.PlantDataSource;
import edu.mit.media.inm.data.UserDataSource;
import edu.mit.media.inm.http.GetIV;
import edu.mit.media.inm.plant.PlantFragment;
import edu.mit.media.inm.plant.PlanterFragment;
import edu.mit.media.inm.plant.PotFragment;
import edu.mit.media.inm.prefs.PreferenceHandler;
import edu.mit.media.inm.prefs.PrefsFragment;
import edu.mit.media.inm.user.User;
import edu.mit.media.inm.util.NotifyService;

public class MainActivity extends FragmentActivity implements ActionBar.OnNavigationListener {
	private static String TAG = "MainActivity";
	private ActionBar actionBar;
	private FragmentManager fm;
	private PreferenceHandler ph;
	private Intent notifyService;
	
    private ArrayList<String> navSpinner;
    private MainNavigationAdapter adapter;
	
	private EasyTracker tracker;
	private long start_time;
	
	public String user_id;

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
		
		ph = new PreferenceHandler(this);
		this.user_id = ph.server_id();
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

        // Spinner title navigation
		actionBar = getActionBar();		
        navSpinner = new ArrayList<String>();
        navSpinner.add("All Items");
        navSpinner.add("My collection");   
        navSpinner.add("Shared with me");   
        adapter = new MainNavigationAdapter(this, navSpinner);
        actionBar.setListNavigationCallbacks(adapter, this);

		pingServer();
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		Log.d(TAG, "Main Create Menu");
		menu.clear();
		getMenuInflater().inflate(R.menu.main, menu);
		// This is checking for log in status!
		if (!ph.IV().equals(PreferenceHandler.default_IV)){
			menu.removeItem(R.id.action_login);
		} else {
			menu.removeItem(R.id.action_new);
			menu.removeItem(R.id.action_archived);
			menu.removeItem(R.id.action_settings);
			menu.removeItem(R.id.action_logout);
		}
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case R.id.action_new:
			fm.beginTransaction()
			.replace(android.R.id.content, new PotFragment())
			.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN)
			.addToBackStack("pot").commit();
			this.turnOnActionBarNav(false);
	        return true;
		case R.id.action_refresh:
			pingServer();
			return true;
		case R.id.action_logout:
			clearAllDb();
			this.turnOnActionBarNav(false);
			refresh();
			return true;
		case R.id.action_login:
			loginDialog();
			return true;
		case R.id.action_archived:
            fm.beginTransaction()
			.replace(android.R.id.content, PlanterFragment.newInstance(true), "archived")
			.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN)
			.addToBackStack("archived").commit();
			this.turnOnActionBarNav(false);
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
			this.turnOnActionBarNav(false);
			return true;
		case android.R.id.home:
			if (fm.getBackStackEntryCount() > 0) {
				if (fm.getBackStackEntryCount() == 1){
					refresh();
					this.turnOnActionBarNav(true);
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
	        	ph.setPassword(((EditText)login_view.findViewById(R.id.login_password)).getText().toString());
	        	ph.setUsername(((EditText)login_view.findViewById(R.id.login_username)).getText().toString());
	        	pingServer();
	        }
	    }).setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
	        public void onClick(DialogInterface dialog, int whichButton) {
	            // Do nothing.
	        }
	    }).show();
	}
	
	private void clearAllDb(){
		ph.set_server_id("None");
		ph.set_IV(PreferenceHandler.default_IV);
		ph.set_last_pinged(0);
    	ph.setPassword("");
    	ph.setUsername("");
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
		// This is checking for log in status!
		Log.d(TAG, "Main Refresh");

		PlanterFragment planter_frag = (PlanterFragment) getFragmentManager()
				.findFragmentByTag("planter");
		if (planter_frag !=null){
			planter_frag.refresh(null);
		}
		
		PlanterFragment archived_frag = (PlanterFragment) getFragmentManager()
				.findFragmentByTag("archived");
		if (archived_frag != null){
			archived_frag.refresh(null);
		}

		PlantFragment plant_frag = (PlantFragment) getFragmentManager()
				.findFragmentByTag("plant");
		if (plant_frag != null){
			plant_frag.refresh();
		}
		invalidateOptionsMenu();
	}
	
	public void turnOnActionBarNav(boolean turnOn){
		if (!ph.IV().equals(PreferenceHandler.default_IV)){
			if (turnOn){
				actionBar.setDisplayHomeAsUpEnabled(false);
		        actionBar.setDisplayShowTitleEnabled(false);
		        actionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_LIST);
			} else {
		        actionBar.setDisplayHomeAsUpEnabled(true);
				actionBar.setDisplayShowTitleEnabled(true);
				actionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_STANDARD);
			}
		} else {
			actionBar.setDisplayShowTitleEnabled(true);
			actionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_STANDARD);
			actionBar.setTitle("InMind");
		}
	}

	@Override
	public void onBackPressed() {
		// check to see if stack is empty
		if (fm.getBackStackEntryCount() > 0) {
			if (fm.getBackStackEntryCount() == 1){
				this.turnOnActionBarNav(true);
				refresh();
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
	  }

	@Override
	public void onResume(){
		super.onResume();
		refresh();
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

	@Override
	public boolean onNavigationItemSelected(int itemPosition, long itemId) {
		Log.d(TAG, "Spinner Item :" + itemPosition + " " + itemId);
		Set<String> users = new HashSet<String>();
		PlanterFragment planter_frag = (PlanterFragment) getFragmentManager()
				.findFragmentByTag("planter");

		if (planter_frag != null) {
			switch (itemPosition) {
			case 0:
				planter_frag.refresh(null);
				return true;
			case 1:
				users.add(ph.server_id());
				planter_frag.refresh(users);
				return true;
			case 2:
				UserDataSource userdata = new UserDataSource(this);
				userdata.open();
				for (User u : userdata.getAllUsers()){
					if (!u.server_id.equals(ph.server_id())){
						users.add(u.server_id);
					}
				}
				planter_frag.refresh(users);
				return true;
			}
		}
		return false;
	}
}
