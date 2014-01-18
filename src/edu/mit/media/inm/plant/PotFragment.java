package edu.mit.media.inm.plant;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import edu.mit.media.inm.R;
import edu.mit.media.inm.data.PlantDataSource;
import edu.mit.media.inm.data.UserDataSource;
import edu.mit.media.inm.prefs.PreferenceHandler;
import edu.mit.media.inm.user.User;
import android.app.Activity;
import android.app.Fragment;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.util.SparseBooleanArray;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnKeyListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.HorizontalScrollView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

public class PotFragment extends Fragment {
	private static final String TAG = "NewPotFragment";

	private String username;
	private PlantDataSource datasource;
	private HorizontalScrollView planter;
	private LinearLayout my_plants;
	private Activity ctx;

	private EditText title_box;
	private ImageView pot_image;
	private ListView friend_list;
	private List<User> friends;
	

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		Log.d(TAG, "OnCreateView");
		ctx = this.getActivity();
		
		PreferenceHandler ph = new PreferenceHandler(ctx);
		username = ph.username();

		View rootView = inflater.inflate(R.layout.fragment_pot, container,
				false);

		datasource = new PlantDataSource(ctx);
		datasource.open();

		setHasOptionsMenu(true);

		return rootView;
	}

	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);
		
		title_box = (EditText) getView().findViewById(R.id.title_box);
		pot_image = (ImageView) getView().findViewById(R.id.pot_image);
		pot_image.setOnClickListener(new OnClickListener(){

			@Override
			public void onClick(View arg0) {
				// TODO Iterate through pot colors!
			}
		});
		
		// Database call for friends

		UserDataSource user_data = new UserDataSource(ctx);
		user_data.open();
		friends = user_data.getAllUsers();
		ArrayList<String> friend_aliases = new ArrayList<String>(); 
		for (User u: friends){
			friend_aliases.add(u.alias);
		}
		
		friend_list = (ListView) getView().findViewById(R.id.friend_list);
		friend_list.setChoiceMode(ListView.CHOICE_MODE_MULTIPLE);
		friend_list.setAdapter(new ArrayAdapter<String>(ctx,
		                android.R.layout.simple_list_item_multiple_choice, friend_aliases));
	}

	@Override
	public void onResume() {
		super.onResume();
		Log.d(TAG, "onResume");
		ctx.getActionBar().setTitle(R.string.pot_fragment);
		datasource.open();
	}
	
	@Override
	public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
	   inflater.inflate(R.menu.pot, menu);
	}

	
	private String randomString(){
		String id =  UUID.randomUUID().toString();
		Log.e(TAG, "New string: " + id);
		return id;
	}
	
	@Override
    public boolean onOptionsItemSelected(MenuItem item) {
            switch (item.getItemId()) {
            case R.id.action_done:
            	//TODO Send to server!
        		SparseBooleanArray checked = friend_list.getCheckedItemPositions();
        		
        		StringBuilder share = new StringBuilder();
        		for (int i =0; i<friends.size(); i++){
        			if (checked.get(i)){
        				share.append(friends.get(i).server_id);
        				share.append(',');
        			}
        		}

                Plant s = datasource.createPlant(
                		username,
                		System.currentTimeMillis(),		// TODO get date from server
                		randomString(),
                		randomString(),
                		randomString(),					// TODO get server id from server
                		share.toString(),
                		0,
                		title_box.getText().toString());

                Toast.makeText(getActivity(), "Publishing: " + s.toString(), Toast.LENGTH_LONG)
                                .show();

                // Send it back to the main screen.
                ctx.onBackPressed();
                return true;
            }
            return false;
    }


	@Override
	public void onPause() {
		datasource.close();
		super.onPause();
	}
}
