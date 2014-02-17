package edu.mit.media.inm.fragments;

import com.google.analytics.tracking.android.EasyTracker;
import com.google.analytics.tracking.android.MapBuilder;

import android.app.Fragment;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;
import edu.mit.media.inm.MainActivity;
import edu.mit.media.inm.R;
import edu.mit.media.inm.handlers.NoteDataSource;
import edu.mit.media.inm.handlers.PreferenceHandler;
import edu.mit.media.inm.http.PostNote;
import edu.mit.media.inm.types.Plant;
import edu.mit.media.inm.util.AesUtil;

public class NoteFragment extends Fragment {
	private static final String TAG = "NoteFragment";

	private MainActivity ctx;
	private View rootView;
	private NoteDataSource datasource;
	private Plant plant;
	private TextView note_text;
	private PreferenceHandler ph;
	
	private EasyTracker tracker;
	private long start_time;
	
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
		
		this.ctx = (MainActivity) getActivity();

		tracker = EasyTracker.getInstance(ctx);
		start_time = System.currentTimeMillis();
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		ctx = (MainActivity) this.getActivity();
		ph = new PreferenceHandler(ctx);

		rootView = inflater.inflate(R.layout.mini_fragment_note, container,
				false);

		note_text = (TextView) rootView.findViewById(R.id.note_text);

		datasource = new NoteDataSource(ctx);
		datasource.open();
		
		ImageButton send = (ImageButton) rootView.findViewById(R.id.send_btn);
		send.setOnClickListener(new OnClickListener(){
			@Override
			public void onClick(View v) {
				if (!inProgress()) {
					Toast.makeText(ctx, "You haven't written anything yet!",
							Toast.LENGTH_SHORT).show();
				} else {
	        		String encryptedText = encrypt(note_text.getText().toString());
	    			
	    			PostNote http_client = new PostNote(0, ctx);
	        		http_client.setupParams(encryptedText, plant.server_id);
	                Toast.makeText(getActivity(), "Publishing to server...", Toast.LENGTH_LONG)
	                                .show();
	        		note_text.setText("");
	                http_client.execute();
	        	}
			}
		});

		return rootView;
	}
	
	private String encrypt(String text){
		String IV = ph.IV();
		String pass = plant.passphrase;
		String salt = plant.salt;
		String plain_text = text;

		Log.d(TAG, "IV "+ IV.length());
		
		AesUtil util = new AesUtil();
        String encrypt = util.encrypt(salt, IV, pass, plain_text);
        return encrypt;
	}
	
	/**
	 * Checks if there is a note currently in progress.
	 * @return
	 */
	public boolean inProgress(){
		return !note_text.getText().toString().trim().isEmpty();
	}
	
	@Override
	public void onResume() {
		super.onResume();
		Log.d(TAG, "onResume");
		if (datasource == null){
			datasource = new NoteDataSource(ctx);
		}
		datasource.open();
	}
	
	@Override
	public void onPause() {
		datasource.close();
		note_text.setText("");
		super.onPause();
	}

	@Override
	public void onStop(){
	    tracker.send(MapBuilder
	    	      .createTiming("engagement",
	                      System.currentTimeMillis()-this.start_time, 
	                      "note",
	                      ph.server_id())
	        .build()
	    );
		super.onStop();
	}
}