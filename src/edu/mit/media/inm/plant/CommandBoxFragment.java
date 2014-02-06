package edu.mit.media.inm.plant;

import java.util.Calendar;

import com.google.analytics.tracking.android.EasyTracker;
import com.google.analytics.tracking.android.MapBuilder;

import android.app.Activity;
import android.app.Fragment;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;
import edu.mit.media.inm.R;
import edu.mit.media.inm.data.PlantDataSource;
import edu.mit.media.inm.http.UpdatePlant;
import edu.mit.media.inm.note.NoteFragment;
import edu.mit.media.inm.prefs.PreferenceHandler;

public class CommandBoxFragment extends Fragment {
	private static final String TAG = "PlantActivity";

	private Activity ctx;
	private View rootView;
	private PlantDataSource datasource;
	private Plant plant;
	
	private ImageView plant_image;

	private Button water;
	private Button trim;
	private Button archive;
	
	private int status_init;
	private int status = 1000;
	
	private EasyTracker tracker;
	
	public static CommandBoxFragment newInstance(Plant p) {
        CommandBoxFragment f = new CommandBoxFragment();

        Bundle args = new Bundle();
        args.putParcelable("plant", p);
        f.setArguments(args);

        return f;
    }
	
	@Override
	public void onCreate(Bundle savedInstanceState){
		super.onCreate(savedInstanceState);
		setHasOptionsMenu(true);
		this.plant = (Plant) (getArguments() != null ? getArguments().get("plant") : 1);

		ctx = this.getActivity();
		tracker = EasyTracker.getInstance(ctx);
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		PreferenceHandler ph = new PreferenceHandler(ctx);
		rootView = inflater.inflate(R.layout.mini_fragment_command_box, container,
				false);

		if (status == 1000){
			status = plant.status;
			status_init = plant.status;
		}
		
		plant_image = (ImageView) rootView.findViewById(R.id.plant_image);
		plant_image.setImageResource(Plant.growth[status]);
		plant_image.setBackgroundResource(Plant.pots[plant.pot]);
		
		//Choose which buttons to turn on and off.
		if (plant.archived){
			disableWater();
			disableTrim();
			if (plant.author.equals(ph.server_id())){
				enableArchive(true);
			} else {
				disableArchive();
			}
		} else {
			if (plant.author.equals(ph.server_id())){
				enableWater();
				enableTrim();
				enableArchive(false);
			} else {
				disableWater();
				disableTrim();
				disableArchive();
			}
		}
		
		datasource = new PlantDataSource(ctx);
		datasource.open();
		
		return rootView;
	}

	private void disableWater() {
		water = (Button) rootView.findViewById(R.id.water_btn);
		water.setEnabled(false);
	}


	private void disableTrim() {
		trim = (Button) rootView.findViewById(R.id.trim_btn);
		trim.setEnabled(false);
	}

	private void disableArchive() {
		archive = (Button) rootView.findViewById(R.id.archive_btn);
		archive.setEnabled(false);
	}

	private void enableWater() {
		water = (Button) rootView.findViewById(R.id.water_btn);
		water.setOnClickListener(new OnClickListener(){
			@Override
			public void onClick(View v) {
				if (status < 8){
					status +=1;
					plant_image.setImageResource(Plant.growth[status]);

				}

			}
		});
	}

	private void enableTrim() {
		trim = (Button) rootView.findViewById(R.id.trim_btn);
		trim.setOnClickListener(new OnClickListener(){
			@Override
			public void onClick(View v) {
				if (status > 0){
					status -=1;
					plant_image.setImageResource(Plant.growth[status]);
				}
			}
		});
	}

	private void enableArchive(final boolean archived) {
		archive = (Button) rootView.findViewById(R.id.archive_btn);
		if (archived){
			archive.setText("Bring Back");
		}
		archive.setOnClickListener(new OnClickListener(){
			@Override
			public void onClick(View v) {
				//Archive the plant.
				archivePlant(!archived);
				//return to planter screen
				getFragmentManager().popBackStack();
			}
		});
	}

	public void updatePlant(){
		if (status_init != status){
			UpdatePlant http_client = new UpdatePlant(0, ctx);
			http_client.setupParams(this.plant.server_id, status, false);
			http_client.execute();
			
			if (status_init < status) {
				Toast.makeText(ctx, "That's great! Keep it up!",
						Toast.LENGTH_SHORT).show();
			} else {
				Toast.makeText(ctx, "You trimmed that down to size!",
						Toast.LENGTH_SHORT).show();
			}

			Calendar cal = Calendar.getInstance();
			Long minute = Long.valueOf(60 * cal.get(Calendar.HOUR_OF_DAY)
					+ cal.get(Calendar.MINUTE));
			PreferenceHandler ph = new PreferenceHandler(ctx);
			tracker.send(MapBuilder
				      .createEvent("ui_action",
				                   "status_changed",
				                   ph.server_id(),
				                   minute).build());
		}
		status_init = status;
	}

	public void archivePlant(boolean archived) {
		UpdatePlant http_client = new UpdatePlant(0, ctx);
		http_client.setupParams(this.plant.server_id, status, archived);
		http_client.execute();
	}
	
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case R.id.action_edit:
			if (plant.archived) {
				Toast.makeText(ctx, "You need to unarchive this plant!",
						Toast.LENGTH_SHORT).show();
			} else {

				updatePlant();
				getFragmentManager().beginTransaction()
				.replace(R.id.control_space, NoteFragment.newInstance(plant), "note")
				.setTransition(0)
				.addToBackStack("note")
				.commit();
			}
			return true;
		}
		return false;
	}
	
	
	@Override
	public void onResume() {
		super.onResume();
		Log.d(TAG, "onResume");
		datasource.open();
	}
	
	@Override
	public void onPause() {
		datasource.close();
		super.onPause();
	}
}