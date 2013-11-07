package edu.mit.media.inm.story;

import java.util.List;
import java.util.Random;

import edu.mit.media.inm.MajorFragment;
import edu.mit.media.inm.R;
import edu.mit.media.inm.data.Story;
import edu.mit.media.inm.data.StoryAdapter;
import edu.mit.media.inm.data.StoryDataSource;
import android.support.v4.app.Fragment;
import android.content.Intent;
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

		return rootView;
	}

	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);

		new_story_btn = (Button) this.getActivity()
				.findViewById(R.id.new_story);
		new_story_btn.setOnClickListener(new View.OnClickListener() {
			// Initialize a ComposeActivity to write a story.
			@Override
			public void onClick(View v) {
				Intent intent = new Intent(getActivity(), ComposeActivity.class);
				startActivity(intent);
			}
		});

		listview = (ListView) this.getActivity().findViewById(R.id.my_stories);
		Log.d(TAG, listview.toString());
	}

	@Override
	public void onResume() {
		super.onResume();
		Log.d(TAG, "onResume");
		datasource.open();

		List<Story> values = datasource.getUserStories(username);
		this.adapter = new StoryAdapter(this.getActivity(), values);
		listview.setAdapter(adapter);
		
		super.onResume();
	}

	@Override
	public void onPause() {
		datasource.close();
		super.onPause();
	}

	@Override
	public void onClick(View v) {
		//TODO (joyc) maybe handle response to refresh?
	}
}