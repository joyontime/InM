package edu.mit.media.inm.plant;

import java.util.List;

import edu.mit.media.inm.R;
import edu.mit.media.inm.data.PlantDataSource;
import edu.mit.media.inm.data.UserDataSource;
import edu.mit.media.inm.prefs.PreferenceHandler;
import edu.mit.media.inm.prefs.PrefsFragment;
import android.app.Activity;
import android.app.Fragment;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Typeface;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.widget.Button;
import android.widget.HorizontalScrollView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

public class PlanterFragment extends Fragment {
	private static final String TAG = "TellFragment";

	private Activity ctx;
	private PlantDataSource datasource;
	private HorizontalScrollView planter;
	private LinearLayout my_plants;
	private TextView message;
	private int plant_width;
	
	private boolean archived = false;

	private Button new_plant_btn;
	

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
		this.archived = (getArguments() != null ? ((Plant) getArguments().get("plant")).archived : false);
		
		BitmapDrawable bd=(BitmapDrawable) this.getResources().getDrawable(R.drawable.plant_0);
		plant_width=bd.getBitmap().getWidth();
		
		Log.d(TAG, "Each plant width: " + plant_width);
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		Log.d(TAG, "OnCreateView");

		ctx = this.getActivity();

		View rootView = inflater.inflate(R.layout.fragment_planter, container,
				false);

		datasource = new PlantDataSource(ctx);
		datasource.open();

		return rootView;
	}

	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);

		new_plant_btn = (Button) getView().findViewById(R.id.new_plant);
		if (this.archived){
			new_plant_btn.setVisibility(View.GONE);
		} else {
			new_plant_btn.setVisibility(View.VISIBLE);
			new_plant_btn.setOnClickListener(new View.OnClickListener() {
				// Switch to pot fragment
				@Override
				public void onClick(View v) {
					getFragmentManager().beginTransaction()
					.replace(android.R.id.content, new PotFragment())
					.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN)
					.addToBackStack("pot").commit();
			        ctx.getActionBar().setDisplayHomeAsUpEnabled(true);
				}
			});
		}

		planter = (HorizontalScrollView) getView().findViewById(R.id.planter);
		my_plants = (LinearLayout) getView().findViewById(R.id.my_plants);
		message = (TextView) getView().findViewById(R.id.planter_message);
		
		this.refresh();
	}
	
	public void refresh(){
		datasource.open();
		Log.d(TAG, "Refreshing");
		List<Plant> values = datasource.getAllPlants();

		// If there are child elements, remove them so we can refresh.
		if (my_plants.getChildAt(0)!=null){
			my_plants.removeAllViews();
		} 

		UserDataSource user_data = new UserDataSource(ctx);
		user_data.open();
		for (Plant p : values){
			if (this.archived ^ p.archived){
				// Don't show archived plants.
				continue;
			}
			// Set up the plant container
			LinearLayout plant = new LinearLayout(ctx);
			plant.setOrientation(LinearLayout.VERTICAL);
			plant.setTag(p);
			plant.setLayoutParams(new LayoutParams(this.plant_width,
					LayoutParams.WRAP_CONTENT));
			plant.setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(View v) {
					Plant clicked_plant = (Plant) v.getTag();
	                ctx.getFragmentManager().beginTransaction()
					.replace(android.R.id.content, PlantFragment.newInstance(clicked_plant), "plant")
					.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN)
					.addToBackStack("plant").commit();

			        ctx.getActionBar().setDisplayHomeAsUpEnabled(true);
				}
		    });
			my_plants.addView(plant);
			
			// Label the plant with its topic
			TextView text = new TextView(ctx);
			text.setPadding(10, 10, 10, 10);
			text.setMaxLines(2);
			text.setMinLines(2);
			text.setText(p.title);
			text.setGravity(Gravity.CENTER_HORIZONTAL);
			text.setLayoutParams(new LayoutParams(LayoutParams.MATCH_PARENT,
					LayoutParams.WRAP_CONTENT));
			if (p.updated){
				text.setTypeface(null, Typeface.BOLD);
			}
			plant.addView(text);

			// Choose a plant image
			ImageView image = new ImageView(ctx);
			image.setImageResource(Plant.growth[p.status]);
			image.setBackgroundResource(Plant.pots[p.pot]);
			plant.addView(image);

			// Label the plant with its owner
			TextView owner = new TextView(ctx);
			owner.setPadding(10, 10, 10, 10);
			owner.setMaxLines(1);
			owner.setLayoutParams(new LayoutParams(LayoutParams.MATCH_PARENT,
					LayoutParams.WRAP_CONTENT));
			owner.setText(user_data.getUserAlias(p.author));
			owner.setGravity(Gravity.CENTER_HORIZONTAL);
			if (p.updated){
				owner.setTypeface(null, Typeface.BOLD);
			}
			plant.addView(owner);
		}

		PreferenceHandler ph = new PreferenceHandler(ctx);
		

		if (ph.IV().equals(PreferenceHandler.default_IV)){
			message.setText("Welcome to InMind! Please log in under settings.");
		} else {
			if (this.archived){
				message.setText("Archived plants are kept here." +
						"You can\'t do anything with them " +
						"unless you bring them back.");
			} else {
				StringBuilder potd = new StringBuilder();
				potd.append("Some thoughts to consider today: \n");
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
		super.onResume();

		if (archived){
			ctx.getActionBar().setTitle("Archived Plants");
		}
		Log.d(TAG, "onResume");
		datasource.open();
	}

	@Override
	public void onPause() {
		datasource.close();
		super.onPause();
	}
}
