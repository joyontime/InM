package edu.mit.media.inm.data;

import java.util.List;

import edu.mit.media.inm.R;
import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

public class StoryAdapter extends ArrayAdapter<Story> {

	Context context;
	int layoutResourceId;
	List<Story> data;

	public StoryAdapter(Context context, int layoutResourceId, List<Story> data) {
		super(context, layoutResourceId, data);
		this.layoutResourceId = layoutResourceId;
		this.context = context;
		this.data = data;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		View row = convertView;
		StoryHolder holder = null;

		if (row == null) {
			LayoutInflater inflater = ((Activity) context).getLayoutInflater();
			row = inflater.inflate(layoutResourceId, parent, false);

			holder = new StoryHolder();
			holder.txtTitle = (TextView) row.findViewById(R.id.story_title);

			row.setTag(holder);
		} else {
			holder = (StoryHolder) row.getTag();
		}

		Story Story = data.get(position);
		holder.txtTitle.setText(Story.title);

		return row;
	}

	static class StoryHolder {
		TextView txtTitle;
	}
}