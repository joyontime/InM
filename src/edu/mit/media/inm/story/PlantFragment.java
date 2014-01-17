package edu.mit.media.inm.story;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

import edu.mit.media.inm.MainActivity;
import edu.mit.media.inm.R;
import edu.mit.media.inm.data.Plant;
import edu.mit.media.inm.data.PlantDataSource;
import edu.mit.media.inm.data.PreferenceHandler;
import edu.mit.media.inm.data.Story;
import edu.mit.media.inm.data.StoryDataSource;
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
	private PlantDataSource datasource;
	private Plant plant;
	private ImageView plant_image;
	private Button note;
	private Button water;
	private Button trim;
	private Button archive;
	private TextView show_info;
	private TextView info_text;
	
	
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

		View rootView = inflater.inflate(R.layout.fragment_plant, container,
				false);
		
		datasource = new PlantDataSource(ctx);
		datasource.open();

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
		
		//TODO Buttons

		return rootView;
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