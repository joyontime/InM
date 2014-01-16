package edu.mit.media.inm.data;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Collections;
import java.util.Date;
import java.util.List;

import edu.mit.media.inm.R;
import edu.mit.media.inm.util.FileUtil;
import android.app.Activity;
import android.content.Context;
import android.graphics.BitmapFactory;
import android.graphics.drawable.Drawable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

public class PlantAdapter extends ArrayAdapter<Plant> {
	private static final String TAG = "PlantAdapter";

	Context context;
	int layoutResourceId;
	List<Plant> data;

	public PlantAdapter(Context context, List<Plant> data) {
		super(context, R.layout.plant_list_item, data);
		this.layoutResourceId = R.layout.plant_list_item;
		this.context = context;
		this.data = data;
		Collections.sort(this.data);
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		View row = convertView;
		PlantHolder holder = null;

		if (row == null) {
			LayoutInflater inflater = ((Activity) context).getLayoutInflater();
			row = inflater.inflate(layoutResourceId, parent, false);

			holder = new PlantHolder();
			/*
			holder.title = (TextView) row.findViewById(R.id.plant_title);
			holder.author = (TextView) row.findViewById(R.id.plant_author);
			holder.date = (TextView) row.findViewById(R.id.plant_date);
			holder.excerpt = (TextView) row.findViewById(R.id.plant_excerpt);
			*/
			holder.image = (ImageView) row.findViewById(R.id.plant_image);

			row.setTag(holder);
		} else {
			holder = (PlantHolder) row.getTag();
		}

		Plant plant = data.get(position);
		holder.title.setText(plant.title);
		holder.author.setText(plant.author);

		DateFormat df = new SimpleDateFormat("MM/dd/yyyy HH:mm");
		holder.date.setText(df.format(new Date(plant.date)));

		holder.excerpt.setText(plant.plant);

		if (plant.image.equals("None")){
			holder.image.setImageBitmap(FileUtil.decodeSampledBitmapFromResource(
					context, R.drawable.candle_small, 100, 100));
		} else {
			holder.image.setImageBitmap(FileUtil.decodeSampledBitmapFromFile(
					context, plant.image, 100, 100));
		}
		
		holder.id = plant.id;
		return row;
	}

	public static class PlantHolder {
		TextView title;
		TextView author;
		TextView date;
		TextView excerpt;
		ImageView image;

		long id;

		public long getId() {
			return id;
		}
	}
}
