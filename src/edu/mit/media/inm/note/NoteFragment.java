package edu.mit.media.inm.note;

import edu.mit.media.inm.R;
import edu.mit.media.inm.data.NoteDataSource;
import edu.mit.media.inm.note.Note;
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
import android.widget.TextView;
import android.widget.Toast;

public class NoteFragment extends Fragment {
	private static final String TAG = "PlantActivity";

	private Activity ctx;
	private View rootView;
	private NoteDataSource datasource;
	private Plant plant;
	private TextView note_text;
	
	private int status;
	
	public static NoteFragment newInstance(Plant p) {
        NoteFragment f = new NoteFragment();

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

		rootView = inflater.inflate(R.layout.mini_fragment_note, container,
				false);
		
		note_text = (TextView) rootView.findViewById(R.id.note_text);
		
		datasource = new NoteDataSource(ctx);
		datasource.open();
		
		return rootView;
	}

	@Override
	public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
		inflater.inflate(R.menu.compose, menu);
	    super.onCreateOptionsMenu(menu, inflater);
	}
	
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case R.id.action_done:
			// TODO send to server!
			Toast.makeText(ctx, "Publishing...", Toast.LENGTH_LONG).show();

			PreferenceHandler ph = new PreferenceHandler(ctx);
			String username = ph.username();
			Note n = datasource.createNote(
					username,
					System.currentTimeMillis(), 
					note_text.getText().toString(),
					plant.server_id);
			
			getFragmentManager().popBackStack();

			return true;
		}
		return false;
	}
	
	@Override
	public void onResume() {
		super.onResume();
		Log.d(TAG, "onResume");
		ctx.getActionBar().setTitle(this.plant.title);
		datasource.open();
	}
	
	@Override
	public void onPause() {
		datasource.close();
		super.onPause();
	}
}