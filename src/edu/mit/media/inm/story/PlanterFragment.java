package edu.mit.media.inm.story;

import java.util.List;
import edu.mit.media.inm.R;
import edu.mit.media.inm.data.PlantDataSource;
import edu.mit.media.inm.data.PreferenceHandler;
import edu.mit.media.inm.data.Plant;
import android.app.Fragment;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.HorizontalScrollView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

public class PlanterFragment extends Fragment {
	private static final String TAG = "TellFragment";

	private String username;
	private PlantDataSource datasource;
	private HorizontalScrollView planter;
	private LinearLayout my_plants;

	private Button new_plant_btn;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		Log.d(TAG, "OnCreateView");
		
		PreferenceHandler ph = new PreferenceHandler(this.getActivity());
		username = ph.username();

		View rootView = inflater.inflate(R.layout.fragment_feed, container,
				false);

		datasource = new PlantDataSource(this.getActivity());
		datasource.open();

		return rootView;
	}

	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);

		new_plant_btn = (Button) this.getActivity()
				.findViewById(R.id.new_plant);
		new_plant_btn.setOnClickListener(new View.OnClickListener() {
			// Initialize a ComposeActivity to write a plant.
			@Override
			public void onClick(View v) {
				Intent intent = new Intent(getActivity(), ComposeActivity.class);
				startActivity(intent);
			}
		});

		planter = (HorizontalScrollView) this.getActivity().findViewById(R.id.planter);
		//planter.setBackgroundResource(R.drawable.cloud_bg);
		
		my_plants = (LinearLayout) this.getActivity().findViewById(R.id.my_plants);
		
	}

	@Override
	public void onResume() {
		super.onResume();
		Log.d(TAG, "onResume");
		datasource.open();

		List<Plant> values = datasource.getAllStories();
		
		if (values.size() == 0){
			// If there are no plants to display, show a message instead.
			planter.setVisibility(View.INVISIBLE);

			planter.setVisibility(View.INVISIBLE);
		} else if (my_plants.getChildAt(0)!=null){
			// If there are child elements, remove them so we can refresh.
			my_plants.removeAllViews();
		}
		for (Plant p : values){
			//View plant = View.inflate(getActivity(), R.layout.plant_list_item, my_plants);

			// Set up the plant container
			LinearLayout plant = new LinearLayout(getActivity());
			plant.setOrientation(LinearLayout.VERTICAL);
			plant.setTag(p);
			plant.setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(View v) {
					Plant clicked_plant = (Plant) v.getTag();
	                
		            Intent i = new Intent(getActivity(), PlantActivity.class);
	                i.putExtra(Plant.OPEN_STORY, clicked_plant);
	                startActivity(i);
				}
		    });
			my_plants.addView(plant);

			// Choose a plant image
			ImageView image = new ImageView(getActivity());
			image.setImageResource(R.drawable.demo_plant);
			plant.addView(image);
			
			// Label the plant with its topic
			TextView text = new TextView(getActivity());
			text.setText("Topic " + p.title);
			text.setGravity(Gravity.CENTER_HORIZONTAL);
			plant.addView(text);
			
			
			Log.d(TAG, "VIEWS!" + plant.getId());
		}
		super.onResume();
	}

	@Override
	public void onPause() {
		datasource.close();
		super.onPause();
	}
}
