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

		List<Story> values = datasource.getAllStories();

		// Populate a listview
		this.adapter = new StoryAdapter(this.getActivity(),
				R.layout.story_list_item, values);

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
				Log.d(TAG, "OnClick");

				// Autogenerate stories.
				String[] Titles = new String[] { "Cool", "Very nice", "Hate it" };
				String author = "joy4luck";
				String[] Stories = new String[] { "A", "B", "C" };
				int r = new Random().nextInt(3);

				// save the new Story to the database
				Log.d(TAG, datasource.toString());
				Story = datasource.createStory(author,
						System.currentTimeMillis(), "drawable/ic_launcher",
						Stories[r], Titles[r]);
				adapter.add(Story);
				Log.d(TAG, "OnClick:" + adapter.toString());
				adapter.notifyDataSetChanged();
			}
		});

		// Find all stories in db
		// TODO Only get stories by this author
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
		// TODO Auto-generated method stub
		
	}
}