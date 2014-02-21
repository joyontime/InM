package edu.mit.media.inm.fragments;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

import com.google.analytics.tracking.android.EasyTracker;
import com.google.analytics.tracking.android.MapBuilder;

import android.app.Fragment;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import edu.mit.media.inm.MainActivity;
import edu.mit.media.inm.R;
import edu.mit.media.inm.handlers.PreferenceHandler;
import edu.mit.media.inm.http.PostNote;
import edu.mit.media.inm.http.UpdatePlant;
import edu.mit.media.inm.types.Plant;
import edu.mit.media.inm.util.AesUtil;

public class CommandBoxFragment extends Fragment {
	private static final String TAG = "PlantActivity";

	private MainActivity ctx;
	private View rootView;
	private PreferenceHandler ph;
	private Plant plant;

	private TextView info_text;
	private ImageView plant_image;

	private ImageButton smile;
	private ImageButton water;
	private ImageButton trim;
	private Button archive;
	
	private int status_init;
	private int status = 1000;
	private int smiles;
	
	private int MAX_GROWTH;
	private int[] GROWTH_IMAGES;
	
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

		ctx = (MainActivity) this.getActivity();
		tracker = EasyTracker.getInstance(ctx);
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		ph = new PreferenceHandler(ctx);
		rootView = inflater.inflate(R.layout.mini_fragment_command_box, container,
				false);

		if (status == 1000){
			status = plant.status;
			status_init = plant.status;
		}
		
		smiles = plant.smiles;
		
		plant_image = (ImageView) rootView.findViewById(R.id.plant_image);
		
		if (plant.type.equals(Plant.PLANT)){
			this.GROWTH_IMAGES = Plant.growth;
			this.MAX_GROWTH = 7;
			plant_image.setBackgroundResource(Plant.pots[plant.pot]);
		} else if (plant.type.equals(Plant.BIRD)){
			this.GROWTH_IMAGES = Plant.birds;
			this.MAX_GROWTH = 3;
			plant_image.setBackgroundResource(Plant.water[plant.pot]);
		} else if (plant.type.equals(Plant.HAMSTER)){
			this.GROWTH_IMAGES = Plant.ham;
			this.MAX_GROWTH = 5;
			plant_image.setBackgroundResource(Plant.wheel[plant.pot]);
		}
		plant_image.setImageResource(this.GROWTH_IMAGES[status]);
		
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
			} else {		// Normal view of plant you don't own.
				disableWater();
				disableTrim();
				disableArchive();
				enableSmile();
			}
		}
		this.setupInfo();
		
		return rootView;
	}
	
	private void setupInfo(){
		info_text = (TextView) rootView.findViewById(R.id.info_text);
		// Load plant data.
		StringBuilder info_string = new StringBuilder();

		// Pretty Print date
				info_string.append("Owned by: \n\t");
				info_string.append(ctx.user_ds.getUserAlias(plant.author));
				info_string.append("\n\n");

		// Pretty Print date
		info_string.append("Created at: \n\t");
		DateFormat df = new SimpleDateFormat("HH:mm \n MM/dd/yy ");
		info_string.append(df.format(new Date(plant.date)));
		info_string.append("\n\n");

		// Pretty Print friends shared with
		info_string.append("Shared with: \n\t");
		for (String s: plant.shared_with.split(",")){
			if (!s.trim().isEmpty() && !s.equals(plant.author)){
				info_string.append(ctx.user_ds.getUserAlias(s) + ", ");	
			}
		}
		info_text.setText(info_string.toString());
	}

	private void disableWater() {
		water = (ImageButton) rootView.findViewById(R.id.water_btn);
		water.setVisibility(View.GONE);
	}


	private void disableTrim() {
		trim = (ImageButton) rootView.findViewById(R.id.trim_btn);
		trim.setVisibility(View.GONE);
	}

	private void disableArchive() {
		archive = (Button) rootView.findViewById(R.id.archive_btn);
		archive.setVisibility(View.GONE);
	}

	private void enableWater() {
		water = (ImageButton) rootView.findViewById(R.id.water_btn);
		if (status  > MAX_GROWTH){
			water.setEnabled(false);
		}
		water.setOnClickListener(new OnClickListener(){
			@Override
			public void onClick(View v) {
				if (status < MAX_GROWTH + 1){
					status +=1;
					if (status > MAX_GROWTH){
						water.setEnabled(false);
						Toast.makeText(ctx, "Congrats! That's as good as it gets!",
								Toast.LENGTH_SHORT).show();
					} else if (status > status_init) {
						Toast.makeText(ctx, "Awesome! Good going!",
								Toast.LENGTH_SHORT).show();
					} else{
						Toast.makeText(ctx, "Keep it up!",
								Toast.LENGTH_SHORT).show();
					}
					trim.setEnabled(true);
					plant_image.setImageResource(GROWTH_IMAGES[status]);

					PreferenceHandler ph = new PreferenceHandler(ctx);
					Long minute = getMinute();
					tracker.send(MapBuilder
						      .createEvent(
					                   ph.server_id(),
					                   "status_up",
					                   String.valueOf(minute),
						               minute).build());
					updatePlant();
				}
			}
		});
	}

	private void enableTrim() {
		trim = (ImageButton) rootView.findViewById(R.id.trim_btn);
		if (status == 0){
			trim.setEnabled(false);
		}
		trim.setOnClickListener(new OnClickListener(){
			@Override
			public void onClick(View v) {
				if (status > 0){
					status -=1;
					if (status < 1){
						trim.setEnabled(false);
						Toast.makeText(ctx, "You've hit the bottom! You can only go up.",
								Toast.LENGTH_SHORT).show();
					} else {
						Toast.makeText(ctx, "Goin' down.",
								Toast.LENGTH_SHORT).show();
					}
					water.setEnabled(true);
					plant_image.setImageResource(GROWTH_IMAGES[status]);

					PreferenceHandler ph = new PreferenceHandler(ctx);
					Long minute = getMinute();
					tracker.send(MapBuilder
						      .createEvent(
					                   ph.server_id(),
					                   "status_down",
					                   String.valueOf(minute),
						               minute).build());
					updatePlant();
				} 
			}
		});
	}
	
	private void enableSmile() {
		smile = (ImageButton) rootView.findViewById(R.id.smile_btn);
		smile.setVisibility(View.VISIBLE);
		smile.setOnClickListener(new OnClickListener(){
			@Override
			public void onClick(View v) {
				smile.setEnabled(false);
				Toast.makeText(ctx, ":)",
						Toast.LENGTH_SHORT).show();
				
				
				
				PreferenceHandler ph = new PreferenceHandler(ctx);
				Long minute = getMinute();
				tracker.send(MapBuilder
					      .createEvent(
				                   ph.server_id(),
				                   "smile",
				                   String.valueOf(minute),
					               minute).build());
				smiles ++;
				sendSmile();
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
		UpdatePlant update_plant = new UpdatePlant(0, ctx);
		update_plant.setupParams(this.plant.server_id, smiles, status, false);
		update_plant.execute();
		
		String update_text = "* changed topic state to level: " + status + " *";
		PostNote post_note = new PostNote(0, ctx);
		post_note.setupParams(encrypt(update_text), plant.server_id);
        post_note.execute();
	}
	
	public void sendSmile(){
		UpdatePlant update_plant = new UpdatePlant(0, ctx);
		update_plant.setupParams(this.plant.server_id, smiles, status, false);
		update_plant.execute();
		
		String update_text = "* :) *";
		PostNote post_note = new PostNote(0, ctx);
		post_note.setupParams(encrypt(update_text), plant.server_id);
        post_note.execute();
	}
	
	private Long getMinute(){
		Calendar cal = Calendar.getInstance();
		return Long.valueOf(60 * cal.get(Calendar.HOUR_OF_DAY)
				+ cal.get(Calendar.MINUTE));
	}
	
	private String encrypt(String text){
		String IV = new PreferenceHandler(ctx).IV();
		String pass = plant.passphrase;
		String salt = plant.salt;
		String plain_text = text;

		AesUtil util = new AesUtil();
        String encrypt = util.encrypt(salt, IV, pass, plain_text);
        return encrypt;
	}

	public void archivePlant(boolean archived) {
		UpdatePlant http_client = new UpdatePlant(0, ctx);
		http_client.setupParams(this.plant.server_id, smiles, status, archived);
		http_client.execute();
		
		String update_text = "* brought back from archive *";
		if (archived){
			update_text = "* archived *";
		}
		PostNote post_note = new PostNote(0, ctx);
		post_note.setupParams(encrypt(update_text), plant.server_id);
        post_note.execute();
	}	
	
	@Override
	public void onResume() {
		super.onResume();
		Log.d(TAG, "onResume");
	}
	
	@Override
	public void onPause() {
		super.onPause();
	}
}