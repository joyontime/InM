package edu.mit.media.inm.story;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import edu.mit.media.inm.MainActivity;
import edu.mit.media.inm.R;
import edu.mit.media.inm.data.Story;
import edu.mit.media.inm.data.StoryDataSource;
import edu.mit.media.inm.util.FileUtil;
import android.app.ActionBar;
import android.app.ActionBar.OnNavigationListener;
import android.app.Activity;
import android.content.ComponentName;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.database.Cursor;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.Parcelable;
import android.provider.MediaStore;
import android.util.Log;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.SpinnerAdapter;
import android.widget.Toast;
import android.view.View.OnKeyListener;

public class ComposeActivity extends Activity {
	private static final String TAG = "ComposeFragment";
	private static final int SELECT_PICTURE_RC = 1;

	// TODO Use preferences
	private String username = "joy4luck";
	private StoryDataSource datasource;
	private OnNavigationListener mOnNavigationListener;

	private String currentShareStatus = Story.EVERYONE;
	String[] share = { Story.EVERYONE, Story.INNER, username };
	private EditText compose_title;
	private EditText editStory;
	private ImageView photo;

	private Uri photoUri;
	private File photo_file;
	private boolean photo_added = false;
	private boolean story_saved = false;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		Log.d(TAG, "OnCreate");
		setContentView(R.layout.activity_compose);

		datasource = new StoryDataSource(this);
		datasource.open();

		initPhotoSelect();

		initSpinner();

		initEditTexts();
	}

	private void initPhotoSelect() {
		this.photo = (ImageView) findViewById(R.id.compose_picture);
		photo.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View arg0) {
				openImageIntent();
			}
		});
		
		// Determine Uri of camera image to save.
				final File root = new File(getApplicationContext().getExternalFilesDir(null)
						+ File.separator + "InM_photos");
				root.mkdirs();
				Log.d(TAG, root.getAbsolutePath());
				
				try {
					photo_file = File.createTempFile("photo_", ".bmp", root);
				} catch (IOException e) {
					Log.d(TAG, e.getMessage());
					photo_file = new File(root.getAbsolutePath()
							+ "/temp.png");
				}
				Log.d(TAG, "Temp file" + photo_file.getAbsolutePath());
				photoUri = Uri.fromFile(photo_file);
	}

	private void openImageIntent() {
		// Camera.
		final List<Intent> cameraIntents = new ArrayList<Intent>();
		final Intent captureIntent = new Intent(
				android.provider.MediaStore.ACTION_IMAGE_CAPTURE);
		final PackageManager packageManager = getPackageManager();
		final List<ResolveInfo> listCam = packageManager.queryIntentActivities(
				captureIntent, 0);
		for (ResolveInfo res : listCam) {
			final String packageName = res.activityInfo.packageName;
			final Intent intent = new Intent(captureIntent);
			intent.setComponent(new ComponentName(res.activityInfo.packageName,
					res.activityInfo.name));
			intent.setPackage(packageName);
			intent.putExtra(MediaStore.EXTRA_OUTPUT, photoUri);
			cameraIntents.add(intent);
		}

		// Filesystem.
		final Intent galleryIntent = new Intent();
		galleryIntent.setType("image/*");
		galleryIntent.setAction(Intent.ACTION_GET_CONTENT);

		// Chooser of filesystem options.
		final Intent chooserIntent = Intent.createChooser(galleryIntent,
				"Select Source");

		// Add the camera options.
		chooserIntent.putExtra(Intent.EXTRA_INITIAL_INTENTS,
				cameraIntents.toArray(new Parcelable[] {}));

		startActivityForResult(chooserIntent, SELECT_PICTURE_RC);
	}
	
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		if (resultCode == RESULT_OK) {
			if (requestCode == SELECT_PICTURE_RC) {
				final boolean isCamera;
				if (data == null) {
					isCamera = true;
				} else {
					final String action = data.getAction();
					if (action == null) {
						isCamera = false;
					} else {
						isCamera = action
								.equals(android.provider.MediaStore.ACTION_IMAGE_CAPTURE);
					}
				}

				if (!isCamera) {
					Uri gallery_image = data == null ? null : data.getData();
					FileUtil.copyFile(this.getApplicationContext(), gallery_image, photoUri);

				}
				Drawable photo;
				try {
					InputStream inputStream = getContentResolver()
							.openInputStream(photoUri);
					photo = Drawable.createFromStream(inputStream,
							photoUri.toString());
				} catch (FileNotFoundException e) {
					photo = getResources().getDrawable(R.drawable.ic_launcher);
				}
				this.photo.setImageDrawable(photo);
				this.photo_added = true;
			}
		}
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
		compose_title = (EditText) findViewById(R.id.compose_title);
		editStory = (EditText) findViewById(R.id.compose_story);

		compose_title.setOnKeyListener(new OnKeyListener() {
			@Override
			public boolean onKey(View v, int keyCode, KeyEvent event) {
				if ((event.getAction() == KeyEvent.ACTION_DOWN)
						&& (keyCode == KeyEvent.KEYCODE_ENTER)) {
					// editStory.requestFocus();
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
			if (storyValid()) {
				Toast.makeText(this,
						"Publishing: " + compose_title.getText().toString(),
						Toast.LENGTH_LONG).show();
				Story s = datasource.createStory(username, System
						.currentTimeMillis(), photoUri.getLastPathSegment(), this.currentShareStatus,
						editStory.getText().toString(), compose_title.getText()
								.toString());
				Intent intent = new Intent(this, MainActivity.class);
				intent.putExtra(Story.NEW_STORY, s);
				story_saved = true;
				startActivity(intent);
			} else {
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

	private boolean storyValid() {
		return compose_title.getText().toString().length() > 0;
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

	@Override
	public void onDestroy() {
		super.onDestroy();
		if (!(photo_added && story_saved)){
			photo_file.delete();
		}
	}
}