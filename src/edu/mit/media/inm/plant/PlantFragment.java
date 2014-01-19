package edu.mit.media.inm.plant;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

import edu.mit.media.inm.MainActivity;
import edu.mit.media.inm.R;
import edu.mit.media.inm.data.PlantDataSource;
import edu.mit.media.inm.data.StoryDataSource;
import edu.mit.media.inm.data.UserDataSource;
import edu.mit.media.inm.note.ComposeActivity;
import edu.mit.media.inm.note.Story;
import edu.mit.media.inm.prefs.PreferenceHandler;
import edu.mit.media.inm.util.FileUtil;
import android.app.ActionBar;
import android.app.Activity;
import android.app.Fragment;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

public class PlantFragment extends Fragment {
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
	private TextView show_info;
	private TextView info_text;
	
	private int status;
	
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
		this.plant = (Plant) (getArguments() != null ? getArguments().get("plant") : 1);
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		ctx = this.getActivity();
		PreferenceHandler ph = new PreferenceHandler(ctx);

		rootView = inflater.inflate(R.layout.fragment_plant, container,
				false);
		
		datasource = new PlantDataSource(ctx);
		datasource.open();

		setupInfoView();
		setupButtons();
		
		return rootView;
	}

	private void setupInfoView(){

		plant_image = (ImageView) rootView.findViewById(R.id.plant_image);
		plant_image.setImageResource(Plant.growth[plant.status]);
		plant_image.setBackgroundResource(Plant.pots[plant.pot]);
		
		// Toggle visibility of plant data
		OnClickListener listener = new OnClickListener(){
			@Override
			public void onClick(View arg0) {
				info_text.setVisibility(info_text.isShown()
                        ? View.GONE
                        : View.VISIBLE );
				if (info_text.isShown()){
					show_info.setText(R.string.hide_info);
				} else{
					show_info.setText(R.string.show_info);
				}
			}
		};
		info_text = (TextView) rootView.findViewById(R.id.info_text);
		info_text.setOnClickListener(listener);
		show_info = (TextView) rootView.findViewById(R.id.show_info);
		show_info.setOnClickListener(listener);
		
		// Load plant data.
		StringBuilder info_string = new StringBuilder();

		// Pretty Print date
		info_string.append("Created at: ");
		DateFormat df = new SimpleDateFormat("MM/dd/yyyy HH:mm");
		info_string.append(df.format(new Date(plant.date)));
		info_string.append("\n");

		// Pretty Print friends shared with
		info_string.append("Shared with: ");
		UserDataSource user_data = new UserDataSource(ctx);
		user_data.open();
		for (String s: plant.shared_with.split(",")){
			if (!s.trim().isEmpty()){
				info_string.append("\n\t");
				info_string.append(user_data.getUserAlias(s));	
			}		
		}

		info_text.setText(info_string.toString());
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
				Intent intent = new Intent(ctx, ComposeActivity.class);
                startActivity(intent);
			}
		});
		archive = (Button) rootView.findViewById(R.id.archive_btn);

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
		datasource.updatePlant(plant.server_id, this.status);
		datasource.close();
		super.onPause();
	}
}