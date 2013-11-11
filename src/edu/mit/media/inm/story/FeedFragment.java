package edu.mit.media.inm.story;
import java.util.List;

import edu.mit.media.inm.MajorFragment;
import edu.mit.media.inm.R;
import edu.mit.media.inm.data.Story;
import edu.mit.media.inm.data.StoryAdapter;
import edu.mit.media.inm.data.StoryDataSource;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
 
public class FeedFragment extends MajorFragment {
	private static final String TAG = "FeedFragment";
	private StoryDataSource datasource;
	private ListView listview;
	private StoryAdapter adapter;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		Log.d(TAG, "OnCreateView");

		View rootView = inflater.inflate(R.layout.fragment_feed, container,
				false);

		datasource = new StoryDataSource(this.getActivity());
		datasource.open();

		List<Story> values = datasource.getAllStories();

		// Populate a listview
		this.adapter = new StoryAdapter(this.getActivity(), values);

		Log.d(TAG, "OnCreateViewFinished:" + this.adapter.toString());

		return rootView;
	}

	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);
		// Find all stories in db
		listview = (ListView) this.getActivity().findViewById(R.id.all_stories);
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