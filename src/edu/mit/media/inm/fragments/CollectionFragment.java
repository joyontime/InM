package edu.mit.media.inm.fragments;

import java.util.ArrayList;
import java.util.List;

import android.app.Fragment;
import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.util.SparseBooleanArray;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;
import edu.mit.media.inm.MainActivity;
import edu.mit.media.inm.R;
import edu.mit.media.inm.handlers.CollectionDataSource;
import edu.mit.media.inm.handlers.PlantDataSource;
import edu.mit.media.inm.handlers.UserDataSource;
import edu.mit.media.inm.types.Plant;

public class CollectionFragment extends Fragment {
	private static final String TAG = "CollectionFragment";

	private MainActivity ctx;

	private EditText title_box;
	private ListView plant_list;
	private List<Plant> plants;

	private InputMethodManager imm;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		ctx = (MainActivity) this.getActivity();

		View rootView = inflater.inflate(R.layout.fragment_collection, container,
				false);
		setHasOptionsMenu(true);

        imm = (InputMethodManager) ctx.getSystemService(Context.INPUT_METHOD_SERVICE);
		imm.toggleSoftInput(InputMethodManager.SHOW_FORCED,0);

		return rootView;
	}

	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);		
		title_box = (EditText) getView().findViewById(R.id.title_box);
		
		// Database call for plants
		UserDataSource user_data = new UserDataSource(ctx);
		user_data.open();
		PlantDataSource plant_data = new PlantDataSource(ctx);
		plant_data.open();
		plants = plant_data.getAllPlants();
		ArrayList<String> plant_aliases = new ArrayList<String>(); 
		for (Plant p: plants){
			plant_aliases.add(user_data.getUserAlias(p.author) + "'s " + p.title);
		}
		
		plant_list = (ListView) getView().findViewById(R.id.friend_list);
		plant_list.setChoiceMode(ListView.CHOICE_MODE_MULTIPLE);
		plant_list.setAdapter(new ArrayAdapter<String>(ctx,
		                android.R.layout.simple_list_item_multiple_choice, plant_aliases));
	}

	@Override
	public void onResume() {
		super.onResume();
		Log.d(TAG, "onResume");
		ctx.getActionBar().setTitle(R.string.collection_fragment);
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
            	if (!inProgress()){
            		Toast.makeText(ctx, "Name your collection!", Toast.LENGTH_SHORT).show();
            	} else {
            		SparseBooleanArray checked = plant_list.getCheckedItemPositions();
            		
            		StringBuilder plants_local = new StringBuilder();
            		for (int i =0; i<plants.size(); i++){
            			if (checked.get(i)){
            				plants_local.append(plants.get(i).server_id);
            				plants_local.append(',');
            			}
            		}
                    imm.hideSoftInputFromWindow(title_box.getWindowToken(), 0);

                    CollectionDataSource collection_data = new CollectionDataSource(ctx);
                    collection_data.open();
                    collection_data.createCollection("0",
                    		title_box.getText().toString(),
                    		plants_local.toString());

                    /*
            		PostPlant http_client = new PostPlant(0, ctx);
            		http_client.setupParams(username, selected_color, share_local.toString(),
            				share_local.toString(), title_box.getText().toString());
                    http_client.execute();
                    */

                    // Send it back to the main screen.

            		ctx.setUpNavigation();
                    ctx.goBack();
            	}
                return true;
            }
            return false;
    }

	public boolean inProgress(){
		return !title_box.getText().toString().trim().isEmpty();
	}

	@Override
	public void onPause() {
        imm.hideSoftInputFromWindow(title_box.getWindowToken(), 0);
		super.onPause();
	}
}
