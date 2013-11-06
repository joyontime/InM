package edu.mit.media.inm.story;

import java.util.List;

import edu.mit.media.inm.R;
import edu.mit.media.inm.data.Story;
import edu.mit.media.inm.data.StoryDataSource;
import android.app.ActionBar;
import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.view.View.OnKeyListener;

public class ComposeActivity extends Activity {
	private static final String TAG = "ComposeFragment";

	// TODO Use preferences
	private String username = "joy4luck";
	private StoryDataSource datasource;

	private EditText edittext;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		Log.d(TAG, "OnCreate");
		setContentView(R.layout.activity_compose);

		ActionBar actionBar = getActionBar();
		actionBar.setDisplayHomeAsUpEnabled(true);

		datasource = new StoryDataSource(this);
		datasource.open();

		List<Story> values = datasource.getUserStories(username);
	}

	public void addKeyListener() {
		 
		// get edittext component
		edittext = (EditText) findViewById(R.id.editText);
	 
		// add a keylistener to keep track user input
		edittext.setOnKeyListener(new OnKeyListener() {
			@Override
			public boolean onKey(View v, int keyCode, KeyEvent event) {
				 
				// if keydown and "enter" is pressed
				if ((event.getAction() == KeyEvent.ACTION_DOWN)
					&& (keyCode == KeyEvent.KEYCODE_ENTER)) {
					return true;
		 
				} else if ((event.getAction() == KeyEvent.ACTION_DOWN)
					&& (keyCode == KeyEvent.KEYCODE_9)) {
		 
					return true;
				}
		 
				return false;
			}
		});
	}

	public void onActivityCreated(Bundle savedInstanceState) {
		/*
		 * new_story_btn = (Button) this.findViewById(R.id.new_story);
		 * new_story_btn.setOnClickListener(new View.OnClickListener() {
		 * 
		 * @Override public void onClick(View v) { Story Story = null;
		 * Log.d(TAG, "New story button clicked.");
		 * 
		 * // Autogenerate stories. String[] Titles = new String[] { "Cool",
		 * "Very nice", "Hate it" }; String[] Stories = new String[] { "A", "B",
		 * "C" }; String image = "candle.png"; int r = new Random().nextInt(3);
		 * 
		 * // save the new Story to the database Log.d(TAG,
		 * datasource.toString()); Story = datasource.createStory(username,
		 * System.currentTimeMillis(), image, Stories[r], Titles[r]); } });
		 */
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.compose, menu);
		return true;
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
}