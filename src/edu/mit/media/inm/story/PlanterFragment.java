package edu.mit.media.inm.story;

import java.util.List;
import edu.mit.media.inm.R;
import edu.mit.media.inm.data.PreferenceHandler;
import edu.mit.media.inm.data.Story;
import edu.mit.media.inm.data.StoryAdapter;
import edu.mit.media.inm.data.StoryAdapter.StoryHolder;
import edu.mit.media.inm.data.StoryDataSource;
import android.app.Fragment;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;
import android.widget.AdapterView.OnItemClickListener;

public class PlanterFragment extends Fragment {
	private static final String TAG = "TellFragment";

	private String username;
	private StoryDataSource datasource;
	private ListView listview;
	private StoryAdapter adapter;

	private Button new_story_btn;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		Log.d(TAG, "OnCreateView");
		
		PreferenceHandler ph = new PreferenceHandler(this.getActivity());
		username = ph.username();

		View rootView = inflater.inflate(R.layout.fragment_feed, container,
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
		listview.setOnItemClickListener(new OnItemClickListener() {
	        public void onItemClick(AdapterView<?> parent, View v, int position, long id) {
	            // Get which story was clicked
	            StoryHolder sh = (StoryHolder) v.getTag();
	            final long storyId = sh.getId();
                Story s = datasource.getStory(storyId);
                
	            Intent i = new Intent(getActivity(), PlantActivity.class);
                i.putExtra(Story.OPEN_STORY, s);
                startActivity(i);
	        }
	    });
	}

	@Override
	public void onResume() {
		super.onResume();
		Log.d(TAG, "onResume");
		datasource.open();

		//List<Story> values = datasource.getUserStories(username);
		List<Story> values = datasource.getAllStories();
		this.adapter = new StoryAdapter(this.getActivity(), values);
		listview.setAdapter(adapter);
		
		super.onResume();
	}

	@Override
	public void onPause() {
		datasource.close();
		super.onPause();
	}
}