package edu.mit.media.inm.data;

import java.util.Collections;
import java.util.List;

import edu.mit.media.inm.R;
import edu.mit.media.inm.util.FileUtil;
import android.app.Activity;
import android.content.Context;
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
	public static final int[] faces = { R.drawable.face_sad,
			R.drawable.face_soso, R.drawable.face_cheer };

	public static final int[] doors = { R.drawable.door_no,
			R.drawable.door_maybe, R.drawable.door_yes };

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
			holder.mood_image = (ImageView) row
					.findViewById(R.id.status_mood_icon);
			holder.avail_image = (ImageView) row
					.findViewById(R.id.status_avail_icon);

			row.setTag(holder);
		} else {
			holder = (StatusHolder) row.getTag();
		}

		Status status = data.get(position);
		holder.name.setText(status.name);
		holder.briefing.setText(status.toString());
		holder.mood_image.setImageBitmap(FileUtil
				.decodeSampledBitmapFromResource(context, faces[status.mood], 150, 150));
		holder.avail_image.setImageBitmap(FileUtil
				.decodeSampledBitmapFromResource(context, doors[status.avail], 150, 150));

		return row;
	}

	public static class StatusHolder {
		TextView name;
		TextView briefing;
		ImageView mood_image;
		ImageView avail_image;

		public String getUser() {
			return name.getText().toString();
		}
	}
}