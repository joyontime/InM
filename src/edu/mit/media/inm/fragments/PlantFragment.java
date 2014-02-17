package edu.mit.media.inm.fragments;

import java.util.List;

import com.google.analytics.tracking.android.EasyTracker;
import com.google.analytics.tracking.android.MapBuilder;

import android.app.Activity;
import android.app.Fragment;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import edu.mit.media.inm.R;
import edu.mit.media.inm.handlers.NoteAdapter;
import edu.mit.media.inm.handlers.NoteDataSource;
import edu.mit.media.inm.handlers.PlantDataSource;
import edu.mit.media.inm.handlers.PreferenceHandler;
import edu.mit.media.inm.handlers.UserDataSource;
import edu.mit.media.inm.types.Note;
import edu.mit.media.inm.types.Plant;

public class PlantFragment extends Fragment {
	private static final String TAG = "PlantActivity";

	private Activity ctx;
	private View rootView;
	private PlantDataSource datasource;
	private String server_id;
	private Plant plant;
	private Button show_info;
	private LinearLayout info_view;
	
	private ListView notes_view;
	
	private CommandBoxFragment cmd_box_frag;
	
	private EasyTracker tracker;
	private long start_time;
	
	public static PlantFragment newInstance(Plant p) {
        PlantFragment f = new PlantFragment();
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

		tracker = EasyTracker.getInstance(ctx);
		start_time = System.currentTimeMillis();
	}

	@Override
	public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
		Log.d(TAG, "Plant Create Menu");
		menu.clear();
	    super.onCreateOptionsMenu(menu, inflater);
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		ctx = this.getActivity();
		rootView = inflater.inflate(R.layout.fragment_plant, container,
				false);
		
		datasource = new PlantDataSource(ctx);
		datasource.open();
		datasource.setPlantShiny(plant.server_id, false);
		
		server_id = new PreferenceHandler(ctx).server_id();

		setupInfoView();
		setupNotes();

		cmd_box_frag = CommandBoxFragment.newInstance(plant);
		if (plant.archived){
			getFragmentManager().beginTransaction()
				.replace(R.id.info, cmd_box_frag, "command")
				.setTransition(0)
				.commit();
		} else {
			getFragmentManager().beginTransaction()
				.replace(R.id.info, cmd_box_frag, "command")
				.replace(R.id.note_space, NoteFragment.newInstance(plant), "note")
				.setTransition(0)
				.commit();
		}
		
		checkOld();
		
		return rootView;
	}

	private void setupInfoView(){
		// Toggle visibility of plant data
		OnClickListener listener = new OnClickListener(){
			@Override
			public void onClick(View arg0) {
				info_view.setVisibility(info_view.isShown()
                        ? View.GONE
                        : View.VISIBLE );
				if (info_view.isShown()){
					show_info.setText(R.string.hide_info);
					show_info.setVisibility(View.GONE);
				} else{
					show_info.setText(R.string.show_info);
					show_info.setVisibility(View.VISIBLE);
				}
			}
		};
		LinearLayout toggle = (LinearLayout) rootView.findViewById(R.id.toggle);
		toggle.setOnClickListener(listener);
		show_info = (Button) rootView.findViewById(R.id.show_info);
		show_info.setOnClickListener(listener);
		info_view = (LinearLayout) rootView.findViewById(R.id.info);
		info_view.setOnClickListener(listener);
	}
	

	
	private void checkOld(){
		TextView alert_text = (TextView) rootView.findViewById(R.id.alert_text);
		if (!plant.archived && plant.author.equals(server_id)){
			NoteDataSource nds = new NoteDataSource(ctx);
			nds.open();
			List<Note> notes = nds.getPlantNotes(plant.server_id);
			nds.close();
			if (notes.size() > 0
					&& notes.get(notes.size() - 1).date < System
							.currentTimeMillis() - (1000 * 60 * 60 * 48)) {
				alert_text.setVisibility(View.VISIBLE);
			} else {
				alert_text.setVisibility(View.GONE);
			}
		}
	}
	
	private void setupNotes(){
		notes_view = (ListView) rootView.findViewById(R.id.notes);
		NoteDataSource nds = new NoteDataSource(ctx);
		nds.open();
		List<Note> notes = nds.getPlantNotes(plant.server_id);
		nds.close();
		
		NoteAdapter note_adapter = new NoteAdapter(ctx, notes, plant);
		notes_view.setAdapter(note_adapter);
	}
	
	public void refresh(){
		setupNotes();
		checkOld();
	}

	@Override
	public void onResume() {
		super.onResume();
		Log.d(TAG, "onResume");
		UserDataSource userdata = new UserDataSource(ctx);
		userdata.open();
		ctx.getActionBar().setTitle(
				userdata.getUserAlias(this.plant.author)
				+ "\'s "
				+ this.plant.title);
		userdata.close();
		datasource.open();
	}
	
	@Override
	public void onPause() {
		datasource.close();
		super.onPause();
	}

	@Override
	  public void onStop() {
	    super.onStop();
	    PreferenceHandler ph = new PreferenceHandler(ctx);
	    tracker.send(MapBuilder
	    	      .createTiming("engagement",
	                      System.currentTimeMillis()-this.start_time, 
	                      "plant",
	                      ph.server_id())
	        .build()
	    );
	  }
}