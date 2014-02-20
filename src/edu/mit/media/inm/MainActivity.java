package edu.mit.media.inm;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashSet;
import java.util.List;
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
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;
import edu.mit.media.inm.fragments.CollectionFragment;
import edu.mit.media.inm.fragments.NoteFragment;
import edu.mit.media.inm.fragments.PlantFragment;
import edu.mit.media.inm.fragments.PlanterFragment;
import edu.mit.media.inm.fragments.PotFragment;
import edu.mit.media.inm.fragments.PrefsFragment;
import edu.mit.media.inm.handlers.CollectionDataSource;
import edu.mit.media.inm.handlers.PreferenceHandler;
import edu.mit.media.inm.handlers.UserDataSource;
import edu.mit.media.inm.types.Collection;
import edu.mit.media.inm.types.User;
import edu.mit.media.inm.util.LoginUtil;
import edu.mit.media.inm.util.NotifyService;

public class MainActivity extends FragmentActivity implements ActionBar.OnNavigationListener {
	private static String TAG = "MainActivity";
	public ActionBar actionBar;
	private FragmentManager fm;
	private PreferenceHandler ph;
	private Intent notifyService;
	private LoginUtil login_util;
	
    private ArrayList<String> navSpinner;
    private List<Collection> collections;
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
		
		setUpNavigation();
        
		tracker = EasyTracker.getInstance(this);
		tracker.send(MapBuilder
			      .createEvent(
			    		  ph.server_id(),
			    		  "access_main",
			              String.valueOf(minute),
			              minute)
			      .build());
		start_time = System.currentTimeMillis();
        login_util = new LoginUtil(this);
		login_util.pingServer();
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		menu.clear();
		getMenuInflater().inflate(R.menu.main, menu);
		// This is checking for log in status!
		if (!ph.IV().equals(PreferenceHandler.default_IV)){
			menu.removeItem(R.id.action_login);
		} else {
			menu.removeItem(R.id.action_new);
			menu.removeItem(R.id.action_settings);
			menu.removeItem(R.id.action_logout);
		}
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case R.id.action_new:
			newThingDialog();
	        return true;
		case R.id.action_discard:
			Collection to_delete = this.collections.get(
					this.actionBar.getSelectedNavigationIndex() - 5);
			CollectionDataSource c_data = new CollectionDataSource(this);
			c_data.open();
			c_data.deleteCollection(to_delete);
			setUpNavigation();
	        return true;
		case R.id.action_refresh:
			login_util.pingServer();
			return true;
		case R.id.action_logout:
			login_util.clearAllDb();
			this.turnOnActionBarNav(false);
			refresh();
			return true;
		case R.id.action_login:
			login_util.loginDialog();
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
				confirmDialog();
			}
		}
		return false;
	}
	
	private void newThingDialog(){
		fm.beginTransaction()
		.replace(android.R.id.content, new PotFragment(), "pot")
		.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN)
		.addToBackStack("pot").commit();
		turnOnActionBarNav(false);
	}
	
	public void refresh(){
		Log.d(TAG, "Refresh");
		this.user_id = ph.server_id();

		int to_refresh = this.actionBar.getSelectedNavigationIndex();
		if (to_refresh > -1 && this.actionBar.getNavigationItemCount() > to_refresh){
			onNavigationItemSelected(to_refresh, to_refresh);
		}
		
		PlanterFragment planter_frag = (PlanterFragment) getFragmentManager()
				.findFragmentByTag("planter");
		if (planter_frag !=null){
			planter_frag.refresh();
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
				actionBar.setHomeButtonEnabled(false);
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

	public void setUpNavigation(){
        // Spinner title navigation
		actionBar = getActionBar();
		actionBar.setDisplayUseLogoEnabled(true);
        navSpinner = new ArrayList<String>();
        navSpinner.add("All Topics");
        navSpinner.add("Mine");   
        navSpinner.add("Shared with me");   
        navSpinner.add("Archived");   
        navSpinner.add("+ New Collection");   
        
        CollectionDataSource c_data = new CollectionDataSource(this);
        c_data.open();
        collections = c_data.getAllCollections();
        for (Collection c : collections){
        	navSpinner.add("> " + c.name);
        }
        c_data.close();

        adapter = new MainNavigationAdapter(this, navSpinner);
        actionBar.setListNavigationCallbacks(adapter, this);
	}

	@Override
	public boolean onNavigationItemSelected(int itemPosition, long itemId) {
		Log.d(TAG, "Spinner Item :" + itemPosition + " " + itemId);
		Set<String> users = new HashSet<String>();
		UserDataSource userdata = new UserDataSource(this);
		PlanterFragment planter_frag = (PlanterFragment) getFragmentManager()
				.findFragmentByTag("planter");

		if (planter_frag != null) {
			switch (itemPosition) {
			case 0:		// All
				planter_frag.refresh(false);
				userdata.close();
				return true;
			case 1:		// Mine
				users.add(ph.server_id());
				planter_frag.refresh(users);
				userdata.close();
				return true;
			case 2:		// Not mine
				userdata.open();
				for (User u : userdata.getAllUsers()){
					if (!u.server_id.equals(ph.server_id())){
						users.add(u.server_id);
					}
				}
				userdata.close();
				planter_frag.refresh(users);
				return true;
			case 3:		// Archived
				planter_frag.refresh(true);
				userdata.close();
				return true;
			case 4:		// New Collection
				fm.beginTransaction()
				.replace(android.R.id.content, new CollectionFragment(), "collection")
				.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN)
				.addToBackStack("collection").commit();
				this.actionBar.setSelectedNavigationItem(0);
				turnOnActionBarNav(false);
				userdata.close();
				return true;
			default:	// Collections
				Collection selected = this.collections.get(itemPosition-5);
				planter_frag.refresh(selected);
				userdata.close();
				return true;
			}
		}
		userdata.close();
		return false;
	}

	private void confirmDialog(){
		NoteFragment note = (NoteFragment) fm.findFragmentByTag("note");
		PotFragment pot = (PotFragment) fm.findFragmentByTag("pot");
		CollectionFragment collection = (CollectionFragment) fm.findFragmentByTag("collection");
		
		if ((note!= null && note.inProgress()) || 
			(pot!=null && pot.inProgress()) || 
			(collection!=null && collection.inProgress())){
			new AlertDialog.Builder(this)
		    .setTitle(R.string.confirm)
		    .setNegativeButton("Yes", new DialogInterface.OnClickListener() {
		        public void onClick(DialogInterface dialog, int whichButton) {
		        	goBack();
		        }
		    }).setPositiveButton("No", new DialogInterface.OnClickListener() {
		        public void onClick(DialogInterface dialog, int whichButton) {
		            // Don't do anything
		        }
		    }).show();
		} else {
			goBack();
		}
	}
	
	public void goBack(){
		if (fm.getBackStackEntryCount() == 1){
			this.turnOnActionBarNav(true);
			refresh();
		}
		fm.popBackStack();
	}

	@Override
	public void onBackPressed() {
		// check to see if stack is empty
		if (fm.getBackStackEntryCount() > 0) {
			confirmDialog();
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
