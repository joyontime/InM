package edu.mit.media.inm.data;

import java.io.IOException;
import java.io.InputStream;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Collections;
import java.util.Date;
import java.util.List;

import edu.mit.media.inm.R;
import android.app.Activity;
import android.content.Context;
import android.graphics.BitmapFactory;
import android.graphics.drawable.Drawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

public class StatusAdapter extends ArrayAdapter<Status> {

	Context context;
	int layoutResourceId;
	List<Status> data;

	public StatusAdapter(Context context, List<Status> data) {
		super(context, R.layout.status_grid_item, data);
		this.layoutResourceId = R.layout.status_grid_item;
		this.context = context;
		this.data = data;
		Collections.sort(this.data);
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		View row = convertView;
		StatusHolder holder = null;

		if (row == null) {
			LayoutInflater inflater = ((Activity) context).getLayoutInflater();
			row = inflater.inflate(layoutResourceId, parent, false);

			holder = new StatusHolder();
			holder.name = (TextView) row.findViewById(R.id.status_name);
			holder.briefing = (TextView) row.findViewById(R.id.status_briefing);
			holder.mood_image = (ImageView) row.findViewById(R.id.status_mood_icon);
			holder.avail_image = (ImageView) row.findViewById(R.id.status_avail_icon);

			row.setTag(holder);
		} else {
			holder = (StatusHolder) row.getTag();
		}

		Status Status = data.get(position);
		holder.name.setText(Status.name);
		
		holder.briefing.setText(Status.briefing);
		/*
		try {
			InputStream ims = context.getAssets().open(Status.image);
			holder.avail_image.setImageDrawable(Drawable.createFromStream(ims, null));
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		*/
		return row;
	}

	static class StatusHolder {
		TextView name;
		TextView briefing;
		ImageView mood_image;
		ImageView avail_image;
	}
}