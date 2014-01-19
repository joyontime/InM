package edu.mit.media.inm.plant;

import edu.mit.media.inm.R;
import edu.mit.media.inm.data.NoteDataSource;
import edu.mit.media.inm.data.PlantDataSource;
import edu.mit.media.inm.note.Note;
import edu.mit.media.inm.note.NoteFragment;
import edu.mit.media.inm.plant.Plant;
import edu.mit.media.inm.prefs.PreferenceHandler;
import android.app.Activity;
import android.app.Fragment;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

public class CommandBoxFragment extends Fragment {
	private static final String TAG = "PlantActivity";

	private Activity ctx;
	private View rootView;
	private PlantDataSource datasource;
	private Plant plant;
	
	private ImageView plant_image;

	private Button note;
	private Button water;
	private Button trim;
	private Button archive;
	
	private int status;
	
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
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		ctx = this.getActivity();
		PreferenceHandler ph = new PreferenceHandler(ctx);

		rootView = inflater.inflate(R.layout.mini_fragment_command_box, container,
				false);

		status = plant.status;
		
		plant_image = (ImageView) rootView.findViewById(R.id.plant_image);
		plant_image.setImageResource(Plant.growth[plant.status]);
		plant_image.setBackgroundResource(Plant.pots[plant.pot]);
		
		setupButtons();
		
		datasource = new PlantDataSource(ctx);
		datasource.open();
		
		return rootView;
	}
	
	
	private void setupButtons(){
		// Buttons
		water = (Button) rootView.findViewById(R.id.water_btn);
		water.setOnClickListener(new OnClickListener(){
			@Override
			public void onClick(View v) {
				if (status < 8){
					status +=1;
					plant_image.setImageResource(Plant.growth[status]);
					Toast.makeText(ctx, "Plant watered. It grew!", Toast.LENGTH_SHORT).show();
				} else {
					Toast.makeText(ctx, "Plant watered.", Toast.LENGTH_SHORT).show();
				}
			}
		});
		trim = (Button) rootView.findViewById(R.id.trim_btn);
		trim.setOnClickListener(new OnClickListener(){
			@Override
			public void onClick(View v) {
				if (status > 0){
					status -=1;
					plant_image.setImageResource(Plant.growth[status]);
					Toast.makeText(ctx, "Plant trimmed. It's smaller now.", Toast.LENGTH_SHORT).show();
				} else {
					Toast.makeText(ctx, "Plant trimmed.", Toast.LENGTH_SHORT).show();
				}
			}
		});
		note = (Button) rootView.findViewById(R.id.note_btn);
		note.setOnClickListener(new OnClickListener(){
			@Override
			public void onClick(View v) {
				// Swap out the contents of the plant view and controls
				// Push onto backstack so back button handles correctly.
				getFragmentManager().beginTransaction()
					.replace(R.id.control_space, NoteFragment.newInstance(plant))
					.setTransition(0)
					.addToBackStack("note")
					.commit();
			}
		});
		archive = (Button) rootView.findViewById(R.id.archive_btn);

	}
	

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case android.R.id.home:
			Toast.makeText(ctx, "Pop!", Toast.LENGTH_SHORT).show();
			getFragmentManager().popBackStack();
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
		// TODO send to server!
		datasource.updatePlant(plant.server_id, this.status);
		datasource.close();
		super.onPause();
	}
}