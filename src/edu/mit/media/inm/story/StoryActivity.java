package edu.mit.media.inm.story;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

import edu.mit.media.inm.MainActivity;
import edu.mit.media.inm.R;
import edu.mit.media.inm.data.Story;
import edu.mit.media.inm.data.StoryDataSource;
import android.app.ActionBar;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.widget.TextView;
import android.widget.Toast;

public class StoryActivity extends Activity{
	private static final String TAG = "StoryActivity";
	
	// TODO Use preferences
	private String username = "joy4luck";
	private StoryDataSource datasource;
	TextView author_tv;
	TextView text_tv;
	TextView date_tv;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		Log.d(TAG, "OnCreate");
		setContentView(R.layout.activity_story);

		datasource = new StoryDataSource(this);
		datasource.open();
		
		Intent i = this.getIntent();
		Story s = i.getParcelableExtra(Story.OPEN_STORY);
		Toast.makeText(this, s.toString(), Toast.LENGTH_SHORT).show();

		ActionBar actionBar = getActionBar();
		actionBar.setDisplayHomeAsUpEnabled(true);
		actionBar.setTitle(s.title);
		
		author_tv = (TextView) this.findViewById(R.id.story_full_author);
		author_tv.setText(s.author);
		text_tv = (TextView) this.findViewById(R.id.story_full_text);
		text_tv.setText(s.story);
		date_tv = (TextView) this.findViewById(R.id.story_full_date);

		DateFormat df = new SimpleDateFormat("MM/dd/yyyy HH:mm");
		date_tv.setText(df.format(new Date(s.date)));
	}
	
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case android.R.id.home:
			startActivity(new Intent(this, MainActivity.class));
			return true;
		}
		return false;
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