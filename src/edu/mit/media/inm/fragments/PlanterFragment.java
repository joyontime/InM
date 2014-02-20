package edu.mit.media.inm.fragments;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Random;
import java.util.Set;

import android.app.Fragment;
import android.app.FragmentTransaction;
import android.graphics.Typeface;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.widget.HorizontalScrollView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import edu.mit.media.inm.MainActivity;
import edu.mit.media.inm.R;
import edu.mit.media.inm.handlers.NoteDataSource;
import edu.mit.media.inm.handlers.PlantDataSource;
import edu.mit.media.inm.handlers.PreferenceHandler;
import edu.mit.media.inm.handlers.UserDataSource;
import edu.mit.media.inm.types.Collection;
import edu.mit.media.inm.types.Note;
import edu.mit.media.inm.types.Plant;

public class PlanterFragment extends Fragment {
	private static final String TAG = "PlanterFragment";

	private MainActivity ctx;
	private PreferenceHandler ph;
	private PlantDataSource datasource;
	private HorizontalScrollView planter;
	private LinearLayout my_plants;
	private TextView message;
	private int plant_width;

	private boolean archived = false;
	private boolean display_collection = false;
	public List<Plant> plants;
	
	public static PlanterFragment newInstance() {
        PlanterFragment f = new PlanterFragment();
        Bundle args = new Bundle();
        Plant plant = new Plant();
        args.putParcelable("plant", plant);
        f.setArguments(args);
        return f;
    }

	@Override
	public void onCreate(Bundle savedInstanceState){
		super.onCreate(savedInstanceState);
		setHasOptionsMenu(true);
		this.archived = (getArguments() != null ? ((Plant) getArguments().get(
				"plant")).archived : false);

		BitmapDrawable bd=(BitmapDrawable) this.getResources().getDrawable(R.drawable.plant_0);
		plant_width=bd.getBitmap().getWidth();
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		Log.d(TAG, "OnCreateView");
		ctx = (MainActivity) this.getActivity();

		View rootView = inflater.inflate(R.layout.fragment_planter, container,
				false);
		
		planter = (HorizontalScrollView) rootView.findViewById(R.id.planter);
		my_plants = (LinearLayout) rootView.findViewById(R.id.my_plants);
		message = (TextView) rootView.findViewById(R.id.planter_message);
		
		ph = new PreferenceHandler(ctx);
		return rootView;
	}
	
	@Override
	public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
		if (this.archived || this.display_collection){
			menu.removeItem(R.id.action_new);
			menu.removeItem(R.id.action_settings);
			menu.removeItem(R.id.action_about);
			menu.removeItem(R.id.action_logout);
		}
		if (!this.display_collection){
			menu.removeItem(R.id.action_discard);
		}
	}
	
	public void refresh(){
		setupPrompt();
		if (datasource == null){
			datasource = new PlantDataSource(ctx);
		}
		datasource.open();
		if (this.plants == null){
			List<Plant> all_plants = datasource.getAllPlants();
			this.plants = new ArrayList<Plant>();

			for (Plant p : all_plants){
				if (p.archived){
					// Don't show archived plants
					continue;
				} else {
					plants.add(p);
				}
			}
		} else {
			List<Plant> refreshed_plants = new ArrayList<Plant>();
			for (Plant p: this.plants){
				refreshed_plants.add(datasource.getPlantByServerID(p.server_id));
			}
			this.plants = refreshed_plants;
		}
		
		displayPlants(this.plants);
	}
	
	/**
	 * For showing All or Archived views.
	 * @param archived
	 */
	public void refresh(boolean archived){
		Log.d(TAG, "Showing: " + archived);
		this.archived = archived;
		this.display_collection = false;
		if (datasource == null){
			datasource = new PlantDataSource(ctx);
		}
		datasource.open();
		List<Plant> all_plants = datasource.getAllPlants();
		List<Plant> to_display = new ArrayList<Plant>();

		for (Plant p : all_plants){
			//Log.d(TAG, "Main Display: " + p.title);
			if (this.archived ^ p.archived){
				// Don't show archived plants if in archive, etc.
				continue;
			} else if (this.archived && !p.author.equalsIgnoreCase(ctx.user_id)){
				// Don't show other people's archived plants
				continue;
			} else {
				to_display.add(p);
			}
		}
		displayPlants(to_display);
	}
	
	/**
	 * For showing user filtered views.
	 * @param users
	 */
	public void refresh(Set<String> users){
		this.archived = false;
		this.display_collection = false;
		if (datasource == null){
			datasource = new PlantDataSource(ctx);
		}
		datasource.open();
		List<Plant> all_plants = datasource.getAllPlants();
		List<Plant> to_display = new ArrayList<Plant>();

		for (Plant p : all_plants){
			if (p.archived){
				// Don't show archived plants
				continue;
			} else if (!users.contains(p.author)){
				// Don't show plants not owned by one of users
				continue;
			} else {
				to_display.add(p);
			}
		}
		displayPlants(to_display);
	}

	/**
	 * For showing a collection.
	 * @param collection
	 */
	public void refresh(Collection collection){
		this.archived = false;
		this.display_collection = true;
		
		HashSet<String> plants_in_collection = new HashSet<String>();
		for (String s: collection.plant_list){
			plants_in_collection.add(s);
		}
		
		if (datasource == null){
			datasource = new PlantDataSource(ctx);
		}
		datasource.open();
		
		List<Plant> all_plants = datasource.getAllPlants();
		List<Plant> to_display = new ArrayList<Plant>();

		for (Plant p : all_plants){
			if (p.archived){
				// Don't show archived plants
				continue;
			} else if (!plants_in_collection.contains(p.server_id)){
				// Don't show plants not owned by one of users
				continue;
			} else {
				to_display.add(p);
			}
		}
		displayPlants(to_display);
	}
	
	public void displayPlants(List<Plant> plants) {
		this.plants = plants;
		ctx.invalidateOptionsMenu();
		// If there are child elements, remove them so we can refresh.
		if (my_plants.getChildAt(0) != null) {
			my_plants.removeAllViews();
		}

		UserDataSource user_data = new UserDataSource(ctx);
		user_data.open();
		for (Plant p: this.plants){
			addPlant(user_data, p);
		}
		user_data.close();
		checkPlanterEmpty();
	}
	
	private void addPlant(UserDataSource user_data, Plant p){
		// Set up the plant container
		LinearLayout plant = new LinearLayout(ctx);
		plant.setOrientation(LinearLayout.VERTICAL);
		plant.setTag(p);
		plant.setLayoutParams(new LayoutParams(
				this.plant_width,
				LayoutParams.WRAP_CONTENT));
		if (p.shiny){
			plant.setBackgroundResource(R.drawable.glow);
		} else if (!p.archived && p.author.equals(ph.server_id())){
			NoteDataSource nds = new NoteDataSource(ctx);
			nds.open();
			List<Note> notes = nds.getPlantNotes(p.server_id);
			nds.close();
			if (notes.size() > 0
					&& notes.get(notes.size() - 1).date < System
							.currentTimeMillis() - (1000 * 60 * 60 * 48)) {
				plant.setBackgroundResource(R.drawable.red_glow);
			}
		}
		plant.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				Plant clicked_plant = (Plant) v.getTag();
                ctx.getFragmentManager().beginTransaction()
				.replace(android.R.id.content, PlantFragment.newInstance(clicked_plant), "plant")
				.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN)
				.addToBackStack("plant")
				.commit();

		        ctx.turnOnActionBarNav(false);
			}
	    });
		my_plants.addView(plant);
		
		// Choose a plant image
		ImageView image = new ImageView(ctx);
		if (p.type.equals("plant")){
			image.setImageResource(Plant.growth[p.status]);
			image.setBackgroundResource(Plant.pots[p.pot]);
		} else if (p.type.equals("bird")){
			image.setImageResource(Plant.birds[p.status]);
			image.setBackgroundResource(Plant.water[p.pot]);
		} else if (p.type.equals("ham")){
			image.setImageResource(Plant.ham[p.status]);
			image.setBackgroundResource(Plant.wheel[p.pot]);
		} else {
			Log.d(TAG, p.title + " TYPE:" + p.type);
		}
		plant.addView(image);
		
		// Label the plant with its topic
		TextView text = new TextView(ctx);
		text.setLines(2);
		text.setText(p.title);
		text.setGravity(Gravity.CENTER);
		text.setTypeface(null, Typeface.BOLD);
		text.setLayoutParams(new LayoutParams(
				LayoutParams.MATCH_PARENT,
				LayoutParams.WRAP_CONTENT));
		plant.addView(text);

		// Label the plant with its owner
		TextView owner = new TextView(ctx);
		owner.setLines(1);
		owner.setLayoutParams(new LayoutParams(
				LayoutParams.MATCH_PARENT,
				LayoutParams.WRAP_CONTENT));
		owner.setText(user_data.getUserAlias(p.author));
		owner.setGravity(Gravity.CENTER);
		plant.addView(owner);
	}
	
	private void checkPlanterEmpty(){
		// If there are no plants to display, don't show the planter.
		if (my_plants.getChildAt(0) == null){
			planter.setVisibility(View.GONE);
		} else {
			planter.setVisibility(View.VISIBLE);
		}
	}
	
	private void setupPrompt(){
		if (ph.IV().equals(PreferenceHandler.default_IV)){
			message.setText("Welcome to InMind! Please log in.");
		} else {
			if (this.archived){
				message.setText("Archived plants are kept here." +
						"You can\'t do anything with them " +
						"unless you bring them back.");
			} else {
				Random random = new Random();
				int prompt = random.nextInt(6);
				if (prompt == 0) {
					message.setText(ph.POTD_neut());
				} else if (prompt == 1) {
					message.setText(ph.POTD_happy());
				} else if (prompt == 2) {
					message.setText(ph.POTD_sad());
				} else {
					message.setText("Hello! What's on your mind?");
				}
			}
		}
	}
	
	@Override
	public void onResume() {
		Log.d(TAG, "onResume");
		super.onResume();
		refresh();
		ctx.turnOnActionBarNav(true);
	}

	@Override
	public void onPause() {
		datasource.close();
		super.onPause();
	}
}
