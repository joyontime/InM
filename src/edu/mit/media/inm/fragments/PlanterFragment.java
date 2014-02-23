package edu.mit.media.inm.fragments;

import java.util.ArrayList;
import java.util.Collections;
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
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.HorizontalScrollView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import edu.mit.media.inm.MainActivity;
import edu.mit.media.inm.R;
import edu.mit.media.inm.handlers.PreferenceHandler;
import edu.mit.media.inm.types.Collection;
import edu.mit.media.inm.types.Note;
import edu.mit.media.inm.types.Plant;

public class PlanterFragment extends Fragment {
	private static final String TAG = "PlanterFragment";

	private MainActivity ctx;
	private PreferenceHandler ph;
	private HorizontalScrollView planter;
	private LinearLayout my_plants;
	private Button message;
	private int plant_width;
	
	private ProgressBar progress_spinner;

	private boolean archived = false;
	private boolean display_collection = false;
	public List<Plant> plants;
	
	public boolean visible = false;
	
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
		message = (Button) rootView.findViewById(R.id.planter_message);
		progress_spinner = (ProgressBar) rootView.findViewById(R.id.progress_bar);
		Log.d(TAG, progress_spinner.toString());
		
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
	
	public void showSpinner(){
		if (progress_spinner != null){
			progress_spinner.setVisibility(View.VISIBLE);
		}
	}
	
	public void refresh(){
		if (!visible){
			return;
		}
		setupPrompt();
		progress_spinner.setVisibility(View.GONE);
		if (this.plants == null){
			List<Plant> all_plants = ctx.plant_ds.getAllPlants();
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
				refreshed_plants.add(ctx.plant_ds.getPlantByServerID(p.server_id));
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
		if (!visible){
			return;
		}
		Log.d(TAG, "Showing: " + archived);
		this.archived = archived;
		this.display_collection = false;
		List<Plant> all_plants = ctx.plant_ds.getAllPlants();
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
		if (!visible){
			return;
		}
		this.archived = false;
		this.display_collection = false;
		List<Plant> all_plants = ctx.plant_ds.getAllPlants();
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
		if (!visible){
			return;
		}
		this.archived = false;
		this.display_collection = true;
		
		HashSet<String> plants_in_collection = new HashSet<String>();
		for (String s: collection.plant_list){
			plants_in_collection.add(s);
		}
		
		List<Plant> all_plants = ctx.plant_ds.getAllPlants();
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
		Collections.sort(this.plants);
		ctx.invalidateOptionsMenu();
		// If there are child elements, remove them so we can refresh.
		if (my_plants.getChildAt(0) != null) {
			my_plants.removeAllViews();
		}

		for (Plant p: this.plants){
			addPlant(p);
		}
		checkPlanterEmpty();
	}
	
	private void addPlant(Plant p){
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
			List<Note> notes = ctx.note_ds.getPlantNotes(p.server_id);
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
			}
	    });
		my_plants.addView(plant);
		
		// Choose a plant image
		FrameLayout frame = new FrameLayout(ctx);
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
		ImageView smiles = new ImageView(ctx);
		if (p.smiles == 1){
			smiles.setImageResource(R.drawable.smile_1);
		} else if (p.smiles == 2){
			smiles.setImageResource(R.drawable.smile_2);
		} else if (p.smiles == 3){
			smiles.setImageResource(R.drawable.smile_3);
		} else if (p.smiles >3){
			smiles.setImageResource(R.drawable.smile_lots);
		}
		frame.addView(image);
		frame.addView(smiles);
		plant.addView(frame);
		
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
		owner.setText(ctx.user_ds.getUserAlias(p.author));
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
	
	public void setupPrompt(){
		message.setClickable(true);
		message.setOnClickListener(new OnClickListener(){
			@Override
			public void onClick(View arg0) {
				ctx.promptDialog();
			}
		});
		if (ph.username().isEmpty()){
			message.setText("Welcome to InMind! Please log in.");
		} else if (ph.IV().equals(PreferenceHandler.default_IV)){
			message.setText(R.string.waiting_on_server);
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
		ctx.turnOnActionBarNav(true);
		
		this.visible = true;
		refresh();
	}

	@Override
	public void onPause() {
		this.visible = false;
		super.onPause();
	}
}
