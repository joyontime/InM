package edu.mit.media.inm.story;

import edu.mit.media.inm.MainActivity;
import edu.mit.media.inm.R;
import edu.mit.media.inm.data.Story;
import edu.mit.media.inm.data.StoryDataSource;
import android.app.ActionBar;
import android.app.ActionBar.OnNavigationListener;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.SpinnerAdapter;
import android.widget.Toast;
import android.view.View.OnKeyListener;

public class ComposeActivity extends Activity {
	private static final String TAG = "ComposeFragment";

	// TODO Use preferences
	private String username = "joy4luck";
	private StoryDataSource datasource;
	private OnNavigationListener mOnNavigationListener;

	private String currentShareStatus = Story.EVERYONE;
	String[] share = { Story.EVERYONE, Story.INNER, username };
	private EditText editTitle;
	private EditText editStory;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		Log.d(TAG, "OnCreate");
		setContentView(R.layout.activity_compose);

		datasource = new StoryDataSource(this);
		datasource.open();

		initSpinner();

		initEditTexts();
	}

	private void initSpinner() {
		ActionBar actionBar = getActionBar();
		actionBar.setDisplayHomeAsUpEnabled(true);
		actionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_LIST);

		SpinnerAdapter mSpinnerAdapter = ArrayAdapter.createFromResource(this,
				R.array.share_list,
				android.R.layout.simple_spinner_dropdown_item);

		mOnNavigationListener = new OnNavigationListener() {
			@Override
			public boolean onNavigationItemSelected(int position, long itemId) {
				currentShareStatus = share[position];
				return true;
			}
		};
		actionBar.setListNavigationCallbacks(mSpinnerAdapter,
				mOnNavigationListener);
	}

	private void initEditTexts() {
		// get edittext component
		editTitle = (EditText) findViewById(R.id.editTitle);
		editStory = (EditText) findViewById(R.id.editStory);

		editTitle.setOnKeyListener(new OnKeyListener() {
			@Override
			public boolean onKey(View v, int keyCode, KeyEvent event) {
				if ((event.getAction() == KeyEvent.ACTION_DOWN)
						&& (keyCode == KeyEvent.KEYCODE_ENTER)) {
					//editStory.requestFocus();
					return true;
				}
				return false;
			}
		});
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case R.id.action_done:
			// TODO (joyc) image acquire!
			String image = "candle.png";
			if (storyValid()){
				Toast.makeText(this, "Publishing: " + editTitle.getText().toString(),
						Toast.LENGTH_LONG).show();
				Story s = datasource.createStory(username, System.currentTimeMillis(), image,
						this.currentShareStatus, editStory.getText().toString(),
						editTitle.getText().toString());
				Intent intent = new Intent(this, MainActivity.class);
				intent.putExtra(Story.NEW_STORY, s);
				startActivity(intent);
			} else{
				String needTitle = "Your story needs a title.";
				Toast.makeText(this, needTitle, Toast.LENGTH_LONG).show();
			}
			return true;
		case android.R.id.home:
			startActivity(new Intent(this, MainActivity.class));
			return true;
		}
		return false;
	}
	
	private boolean storyValid(){
		return editTitle.getText().toString().length() > 0 ;
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