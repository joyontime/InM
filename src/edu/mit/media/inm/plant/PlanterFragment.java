package edu.mit.media.inm.plant;

import java.util.List;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Fragment;
import android.app.FragmentTransaction;
import android.content.DialogInterface;
import android.graphics.Typeface;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.text.Editable;
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
import android.widget.EditText;
import android.widget.HorizontalScrollView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import edu.mit.media.inm.MainActivity;
import edu.mit.media.inm.R;
import edu.mit.media.inm.data.PlantDataSource;
import edu.mit.media.inm.data.UserDataSource;
import edu.mit.media.inm.prefs.PreferenceHandler;

public class PlanterFragment extends Fragment {
	private static final String TAG = "PlanterFragment";

	private MainActivity ctx;
	private PlantDataSource datasource;
	private HorizontalScrollView planter;
	private LinearLayout my_plants;
	private TextView message;
	private int plant_width;
	
	private boolean archived = false;
	

	public static PlanterFragment newInstance(boolean archived) {
        PlanterFragment f = new PlanterFragment();

        Bundle args = new Bundle();
        Plant plant = new Plant();
        plant.archived = archived;
        args.putParcelable("plant", plant);
        f.setArguments(args);

        return f;
    }
	
	@Override
	public void onCreate(Bundle savedInstanceState){
		super.onCreate(savedInstanceState);
		setHasOptionsMenu(true);
		this.archived = (getArguments() != null ? ((Plant) getArguments().get("plant")).archived : false);
		
		BitmapDrawable bd=(BitmapDrawable) this.getResources().getDrawable(R.drawable.plant_0);
		plant_width=bd.getBitmap().getWidth();
		
		Log.d(TAG, "Each plant width: " + plant_width);
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		Log.d(TAG, "OnCreateView");
		ctx = (MainActivity) this.getActivity();

		View rootView = inflater.inflate(R.layout.fragment_planter, container,
				false);

		return rootView;
	}
	
	@Override
	public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
		if (this.archived){
			menu.removeItem(R.id.action_new);
		}
	}

	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);

		planter = (HorizontalScrollView) getView().findViewById(R.id.planter);
		my_plants = (LinearLayout) getView().findViewById(R.id.my_plants);
		message = (TextView) getView().findViewById(R.id.planter_message);
		
		this.refresh();
	}
	
	public void refresh(){
		if (datasource == null){
			datasource = new PlantDataSource(ctx);
		}
		datasource.open();
		Log.d(TAG, "Refreshing all views.");
		List<Plant> values = datasource.getAllPlants();

		// If there are child elements, remove them so we can refresh.
		if (my_plants.getChildAt(0)!=null){
			my_plants.removeAllViews();
		} 

		UserDataSource user_data = new UserDataSource(ctx);
		user_data.open();
		for (Plant p : values){
			if (this.archived ^ p.archived){
				// Don't show archived plants if in archive, etc.
				continue;
			} else if (this.archived && !p.author.equals(ctx.user_id)){
				// Don't show other people's archived plants
				continue;
			}
			// Set up the plant container
			LinearLayout plant = new LinearLayout(ctx);
			plant.setOrientation(LinearLayout.VERTICAL);
			plant.setTag(p);
			plant.setLayoutParams(new LayoutParams(
					this.plant_width,
					LayoutParams.WRAP_CONTENT));
			plant.setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(View v) {
					Plant clicked_plant = (Plant) v.getTag();
	                ctx.getFragmentManager().beginTransaction()
					.replace(android.R.id.content, PlantFragment.newInstance(clicked_plant), "plant")
					.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN)
					.addToBackStack("plant")
					.commit();

			        ctx.getActionBar().setDisplayHomeAsUpEnabled(true);
				}
		    });
			my_plants.addView(plant);
			
			// Label the plant with its topic
			TextView text = new TextView(ctx);
			text.setLines(2);
			text.setText(p.title);
			text.setGravity(Gravity.CENTER);
			text.setLayoutParams(new LayoutParams(
					LayoutParams.MATCH_PARENT,
					LayoutParams.WRAP_CONTENT));
			if (p.shiny){
				text.setTypeface(null, Typeface.BOLD);
				text.setBackgroundResource(R.drawable.glow);
			}
			plant.addView(text);

			// Choose a plant image
			ImageView image = new ImageView(ctx);
			image.setImageResource(Plant.growth[p.status]);
			image.setBackgroundResource(Plant.pots[p.pot]);
			plant.addView(image);

			// Label the plant with its owner
			TextView owner = new TextView(ctx);
			owner.setLines(2);
			owner.setPadding(0, 5, 0, 0);
			owner.setLayoutParams(new LayoutParams(
					LayoutParams.MATCH_PARENT,
					LayoutParams.WRAP_CONTENT));
			owner.setText(user_data.getUserAlias(p.author));
			owner.setGravity(Gravity.CENTER);
			if (p.shiny){
				owner.setTypeface(null, Typeface.BOLD);
				owner.setBackgroundResource(R.drawable.glow);
			}
			plant.addView(owner);
		}

		PreferenceHandler ph = new PreferenceHandler(ctx);

		if (ph.IV().equals(PreferenceHandler.default_IV)){
			message.setText("Welcome to InMind! Please log in.");
		} else {
			if (this.archived){
				message.setText("Archived plants are kept here." +
						"You can\'t do anything with them " +
						"unless you bring them back.");
			} else {
				StringBuilder potd = new StringBuilder();
				potd.append("Consider: \n");
				potd.append(ph.POTD_neut());
				potd.append('\n');
				potd.append(ph.POTD_happy());
				potd.append('\n');
				potd.append(ph.POTD_sad());
				message.setText(potd.toString());
			}
		}

		// If there are no plants to display, don't show the planter.
		if (my_plants.getChildAt(0) == null){
			planter.setVisibility(View.GONE);
		} else {
			planter.setVisibility(View.VISIBLE);
		}

		user_data.close();
	}

	@Override
	public void onResume() {
		Log.d(TAG, "onResume");
		super.onResume();
		refresh();
		if (archived){
			ctx.getActionBar().setTitle("Archived Plants");
		}
		datasource.open();
	}

	@Override
	public void onPause() {
		datasource.close();
		super.onPause();
	}
}
