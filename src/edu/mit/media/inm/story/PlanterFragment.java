package edu.mit.media.inm.story;

import java.util.List;
import edu.mit.media.inm.R;
import edu.mit.media.inm.data.PlantDataSource;
import edu.mit.media.inm.data.PreferenceHandler;
import edu.mit.media.inm.data.Plant;
import edu.mit.media.inm.data.PlantAdapter;
import edu.mit.media.inm.data.PlantAdapter.PlantHolder;
import edu.mit.media.inm.data.PlantDataSource;
import android.app.Fragment;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.HorizontalScrollView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.AdapterView.OnItemClickListener;

public class PlanterFragment extends Fragment {
	private static final String TAG = "TellFragment";

	private String username;
	private PlantDataSource datasource;
	private HorizontalScrollView planter;
	private LinearLayout my_plants;
	private PlantAdapter adapter;

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
		my_plants.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				 PlantHolder sh = (PlantHolder) v.getTag();
		            final long plantId = sh.getId();
	                Plant s = datasource.getPlant(plantId);
	                
		            Intent i = new Intent(getActivity(), PlantActivity.class);
	                i.putExtra(Plant.OPEN_STORY, s);
	                startActivity(i);
			}
	    });
	}

	@Override
	public void onResume() {
		super.onResume();
		Log.d(TAG, "onResume");
		datasource.open();

		List<Plant> values = datasource.getAllStories();
		this.adapter = new PlantAdapter(this.getActivity(), values);
		for (int i = 0; i <10; i++){
			View plant = View.inflate(getActivity(), R.layout.plant_list_item, my_plants);

			Log.d(TAG, "VIEWS!" + plant.getId());
			
			ImageView image = (ImageView) plant.findViewById(R.id.plant_image);
			image.setImageResource(R.drawable.door_maybe);
		}
		super.onResume();
	}

	@Override
	public void onPause() {
		datasource.close();
		super.onPause();
	}
}
