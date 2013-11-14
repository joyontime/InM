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

public class StoryAdapter extends ArrayAdapter<Story> {

	Context context;
	int layoutResourceId;
	List<Story> data;

	public StoryAdapter(Context context, List<Story> data) {
		super(context, R.layout.story_list_item, data);
		this.layoutResourceId = R.layout.story_list_item;
		this.context = context;
		this.data = data;
		Collections.sort(this.data);
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		View row = convertView;
		StoryHolder holder = null;

		if (row == null) {
			LayoutInflater inflater = ((Activity) context).getLayoutInflater();
			row = inflater.inflate(layoutResourceId, parent, false);

			holder = new StoryHolder();
			holder.title = (TextView) row.findViewById(R.id.story_title);
			holder.author = (TextView) row.findViewById(R.id.story_author);
			holder.date = (TextView) row.findViewById(R.id.story_date);
			holder.excerpt = (TextView) row.findViewById(R.id.story_excerpt);
			holder.image = (ImageView) row.findViewById(R.id.story_image);

			row.setTag(holder);
		} else {
			holder = (StoryHolder) row.getTag();
		}

		Story story = data.get(position);
		holder.title.setText(story.title);
		holder.author.setText(story.author);
		
		DateFormat df = new SimpleDateFormat("MM/dd/yyyy HH:mm");
		holder.date.setText(df.format(new Date(story.date)));
		
		//TODO truncate story
		holder.excerpt.setText(story.story);
		try {
			InputStream ims = context.getAssets().open(story.image);
			holder.image.setImageDrawable(Drawable.createFromStream(ims, null));
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		holder.id = story.id;
		return row;
	}

	public static class StoryHolder {
		TextView title;
		TextView author;
		TextView date;
		TextView excerpt;
		ImageView image;
		
		long id;
		
		public long getId(){
			return id;
		}
	}
}