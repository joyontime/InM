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
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

public class PlantFragment extends Fragment {
	private static final String TAG = "PlantActivity";

	private Activity ctx;
	private PlantDataSource datasource;
	private Plant plant;
	
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

		return rootView;

		/*
		story_iv = (ImageView) this.findViewById(R.id.story_full_image);
		
		if (! s.image.equals("None")){
			story_iv.setImageBitmap(FileUtil.decodeSampledBitmapFromFile(
					getApplicationContext(), s.image, 400, 200));
		}
		
		if (s.image.equals("None")){
			story_iv.setImageBitmap(FileUtil.decodeSampledBitmapFromResource(
					getApplicationContext(), R.drawable.candle_small, 400, 200));
		} else {
			story_iv.setImageBitmap(FileUtil.decodeSampledBitmapFromFile(
					getApplicationContext(), s.image, 400, 200));
		}

		author_tv = (TextView) this.findViewById(R.id.story_full_author);
		author_tv.setText(s.author);
		text_tv = (TextView) this.findViewById(R.id.story_full_text);
		text_tv.setText(s.story);
		date_tv = (TextView) this.findViewById(R.id.story_full_date);

		DateFormat df = new SimpleDateFormat("MM/dd/yyyy HH:mm");
		date_tv.setText(df.format(new Date(s.date)));
		*/
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