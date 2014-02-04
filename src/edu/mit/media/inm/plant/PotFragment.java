package edu.mit.media.inm.plant;

import java.util.ArrayList;
import java.util.List;

import android.app.Activity;
import android.app.Fragment;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.util.Log;
import android.util.SparseBooleanArray;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.inputmethod.InputMethodManager;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.Toast;
import edu.mit.media.inm.R;
import edu.mit.media.inm.data.UserDataSource;
import edu.mit.media.inm.http.PostPlant;
import edu.mit.media.inm.prefs.PreferenceHandler;
import edu.mit.media.inm.user.User;

public class PotFragment extends Fragment {
	private static final String TAG = "NewPotFragment";

	private String username;
	private Activity ctx;
	private PreferenceHandler ph;

	private EditText title_box;
	private ImageView pot_image;
	private ListView friend_list;
	private List<User> friends;

	private InputMethodManager imm;
	
	private int selected_color = 0;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		Log.d(TAG, "OnCreateView");
		ctx = this.getActivity();
		
		ph = new PreferenceHandler(ctx);
		username = ph.server_id();

		View rootView = inflater.inflate(R.layout.fragment_pot, container,
				false);

		setHasOptionsMenu(true);

        imm = (InputMethodManager) ctx.getSystemService(Context.INPUT_METHOD_SERVICE);
		imm.toggleSoftInput(InputMethodManager.SHOW_FORCED,0);

		return rootView;
	}

	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);
		
		int num_pots =  Plant.pots.length;
		final Bitmap[] only_pots = new Bitmap[num_pots];
		for (int i = 0; i <num_pots ; i++){
			Bitmap original = BitmapFactory.decodeResource(getResources(),
					Plant.pots[i]);
			int START_X = 0;
			int START_Y = original.getHeight()/2;
			int WIDTH_PX = original.getWidth();
			int HEIGHT_PX = original.getHeight()/2;

			// Crop bitmap
			Bitmap cropped_pot = Bitmap.createBitmap(original, START_X,
					START_Y, WIDTH_PX, HEIGHT_PX, null, false);
			
			only_pots[i] = cropped_pot;
		}
		
		title_box = (EditText) getView().findViewById(R.id.title_box);
		pot_image = (ImageView) getView().findViewById(R.id.pot_image);
		pot_image.setImageBitmap(only_pots[selected_color]);
		
		LinearLayout pot_list = (LinearLayout) getView().findViewById(R.id.pot_list);
		for (int i = 0; i < num_pots; i++){
			ImageView pot = new ImageView(ctx);
			pot.setImageBitmap(only_pots[i]);
			pot.setMaxHeight(only_pots[i].getHeight()/2);
			final int pot_color = i;

			pot.setOnClickListener(new OnClickListener(){
				@Override
				public void onClick(View arg0) {
					pot_image.setImageBitmap(only_pots[pot_color]);
					selected_color = pot_color;
				}
			});
			pot_list.addView(pot);
		}
		
		// Database call for friends
		UserDataSource user_data = new UserDataSource(ctx);
		user_data.open();
		friends = user_data.getAllUsers();
		ArrayList<String> friend_aliases = new ArrayList<String>(); 
		for (User u: friends){
			if (u.server_id.equals(ph.server_id())){
				friends.remove(u);
				break;
			}
		}
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
	}
	
	@Override
	public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
		menu.clear();
		inflater.inflate(R.menu.pot, menu);
	}

	@Override
    public boolean onOptionsItemSelected(MenuItem item) {
            switch (item.getItemId()) {
            case R.id.action_done:
        		SparseBooleanArray checked = friend_list.getCheckedItemPositions();
        		
        		StringBuilder share_server = new StringBuilder();
        		share_server.append('[');
        		share_server.append('"' + ph.server_id() + '"');
        		for (int i =0; i<friends.size(); i++){
        			if (checked.get(i)){
        				share_server.append(',');
        				share_server.append('"');
        				share_server.append(friends.get(i).server_id);
        				share_server.append('"');
        			}
        		}
        		share_server.append(']');
        		
        		StringBuilder share_local = new StringBuilder();
        		for (int i =0; i<friends.size(); i++){
        			if (checked.get(i)){
        				share_local.append(friends.get(i).server_id);
        				share_local.append(',');
        			}
        		}
                imm.hideSoftInputFromWindow(title_box.getWindowToken(), 0);
        		
        		PostPlant http_client = new PostPlant(0, ctx);
        		http_client.setupParams(username, selected_color, share_server.toString(),
        				share_local.toString(), title_box.getText().toString());

                Toast.makeText(getActivity(), "Publishing to server...", Toast.LENGTH_LONG)
                                .show();
                http_client.execute();

                // Send it back to the main screen.
                ctx.onBackPressed();
                return true;
            }
            return false;
    }


	@Override
	public void onPause() {
		super.onPause();
	}
}
