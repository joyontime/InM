package edu.mit.media.inm.story;

import java.util.List;
import java.util.Random;

import edu.mit.media.inm.MajorFragment;
import edu.mit.media.inm.R;
import edu.mit.media.inm.data.Story;
import edu.mit.media.inm.data.StoryAdapter;
import edu.mit.media.inm.data.StoryDataSource;
import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;

public class TellFragment extends MajorFragment {
	private static final String TAG = "TellFragment";

	//TODO Use preferences
	private String username = "joy4luck";
	private StoryDataSource datasource;
	private ListView listview;
	private StoryAdapter adapter;

	private Button new_story_btn;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		Log.d(TAG, "OnCreateView");

		View rootView = inflater.inflate(R.layout.fragment_tell, container,
				false);

		datasource = new StoryDataSource(this.getActivity());
		datasource.open();

		List<Story> values = datasource.getUserStories(username);

		// Populate a listview
		this.adapter = new StoryAdapter(this.getActivity(), values);

		Log.d(TAG, "OnCreateViewFinished:" + this.adapter.toString());

		return rootView;
	}

	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);

		new_story_btn = (Button) this.getActivity()
				.findViewById(R.id.new_story);
		new_story_btn.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				Story Story = null;
				Log.d(TAG, "New story button clicked.");

				// Autogenerate stories.
				String[] Titles = new String[] { "Cool", "Very nice", "Hate it" };
				String[] Stories = new String[] { "A", "B", "C" };
				String image = "candle.png";
				int r = new Random().nextInt(3);

				// save the new Story to the database
				Log.d(TAG, datasource.toString());
				Story = datasource.createStory(username,
						System.currentTimeMillis(), image, Stories[r],
						Titles[r]);
				adapter.add(Story);
				adapter.notifyDataSetChanged();
			}
		});

		listview = (ListView) this.getActivity().findViewById(R.id.my_stories);
		Log.d(TAG, listview.toString());
		listview.setAdapter(adapter);
	}

	@Override
	public void onResume() {
		datasource.open();
		super.onResume();
	}

	@Override
	public void onPause() {
		datasource.close();
		super.onPause();
	}

	@Override
	public void onClick(View v) {
	}
}