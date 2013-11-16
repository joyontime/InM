package edu.mit.media.inm.status;

import edu.mit.media.inm.MainActivity;
import edu.mit.media.inm.R;
import edu.mit.media.inm.data.Status;
import edu.mit.media.inm.data.StatusAdapter;
import edu.mit.media.inm.data.StatusDataSource;
import edu.mit.media.inm.data.Story;
import android.app.ActionBar;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.NumberPicker;
import android.widget.NumberPicker.OnValueChangeListener;
import android.widget.Toast;

public class UpdateActivity extends Activity {
	private static final String TAG = "TellFragment";

	// TODO Use preferences
	private String username = "joy4luck";
	private StatusDataSource datasource;

	private NumberPicker pick_avail;
	private NumberPicker pick_mood;
	private ImageView icon_avail;
	private ImageView icon_mood;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		Log.d(TAG, "OnCreateView");

		setContentView(R.layout.activity_update);

		datasource = new StatusDataSource(this);
		datasource.open();
		
		ActionBar actionBar = getActionBar();
		actionBar.setDisplayHomeAsUpEnabled(true);
		
		setup();
	}
	
	protected UpdateActivity getActivity(){
		return this;
	}

	public void setup() {
		OnValueChangeListener updateListener = new OnValueChangeListener(){
			@Override
			public void onValueChange(NumberPicker arg0, int arg1, int arg2) {
				updateIcon();
			}
		};
		
		pick_avail = (NumberPicker) this.findViewById(
				R.id.pick_avail);
		pick_avail.setMinValue(0);
		pick_avail.setMaxValue(2);
		pick_avail.setDisplayedValues(new String[] {Status.NO, Status.MAYBE, Status.YES});
		pick_avail.setValue(1);
		pick_avail.setDescendantFocusability(NumberPicker.FOCUS_BLOCK_DESCENDANTS);
		pick_avail.setOnValueChangedListener(updateListener);
		
		pick_mood = (NumberPicker) this.findViewById(
				R.id.pick_mood);
		pick_mood.setMinValue(0);
		pick_mood.setMaxValue(2);
		pick_mood.setDisplayedValues(new String[] {Status.BAD, Status.SOSO, Status.CHEERFUL});
		pick_mood.setValue(1);
		pick_mood.setDescendantFocusability(NumberPicker.FOCUS_BLOCK_DESCENDANTS);
		pick_mood.setOnValueChangedListener(updateListener);
		
		icon_mood = (ImageView) this.findViewById(R.id.update_mood_icon);
		icon_avail = (ImageView) this.findViewById(R.id.update_avail_icon);
		updateIcon();
	}
	
	private void updateIcon(){
		icon_mood.setImageResource(StatusAdapter.faces[pick_mood.getValue()]);
		icon_avail.setImageResource(StatusAdapter.doors[pick_avail.getValue()]);
	}
	
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case R.id.action_done:
			// TODO (joyc) image acquire!
			Status s = datasource.createStatus(pick_avail.getValue(),
					"", System.currentTimeMillis(), pick_mood.getValue(), username);

			Toast.makeText(getActivity(), "Publishing: " + s.toString(), Toast.LENGTH_LONG)
					.show();

			Intent intent = new Intent(getActivity(), MainActivity.class);
			intent.putExtra(Story.NEW_STORY, s);
			startActivity(intent);
			return true;
		case android.R.id.home:
			startActivity(new Intent(this, MainActivity.class));
			return true;
		}
		return false;
	}
	

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.update, menu);
		return true;
	}

	@Override
	public void onResume() {
		super.onResume();
		Log.d(TAG, "onResume");
		datasource.open();
		super.onResume();
	}

	@Override
	public void onPause() {
		datasource.close();
		super.onPause();
	}
}