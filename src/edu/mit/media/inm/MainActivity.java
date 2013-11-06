package edu.mit.media.inm;

import java.util.List;

import android.R.color;
import android.os.Bundle;
import android.app.ActionBar;
import android.app.ActionBar.Tab;
import android.app.Activity;
import android.app.FragmentTransaction;
import android.support.v4.app.FragmentActivity;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.app.ActionBar.TabListener;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import edu.mit.media.inm.adapter.*;
import edu.mit.media.inm.data.Story;
import edu.mit.media.inm.data.StoryDataSource;

public class MainActivity extends FragmentActivity implements TabListener {
	private ViewPager viewPager;
	private TabsPagerAdapter mAdapter;
	private ActionBar actionBar;
	// Tab titles
	private String[] tabs = { "Status", "Feed", "Update", "Tell" };

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);

		// Initilization
		viewPager = (ViewPager) findViewById(R.id.pager);
		actionBar = getActionBar();
		mAdapter = new TabsPagerAdapter(getSupportFragmentManager());

		viewPager.setAdapter(mAdapter);
		// actionBar.setHomeButtonEnabled(false);
		actionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_TABS);

		int[] tabimages = { R.drawable.door, R.drawable.bookmark,
				R.drawable.smiley, R.drawable.pencil };
		// Adding Tabs
		for (int tab_name : tabimages) {
			/*
			// Alternative ways to set appearance and text of a tab.
			actionBar.addTab(actionBar.newTab().setText(tab_name).setTabListener(this));
			View v = LayoutInflater.from(this).inflate(R.layout.main_tab, null);
			v.setBackgroundResource(tab_name);
			*/
			
			actionBar.addTab(actionBar.newTab().setIcon(tab_name)
					.setTabListener(this));
		}

		/**
		 * on swiping the viewpager make respective tab selected
		 * */
		viewPager.setOnPageChangeListener(new ViewPager.OnPageChangeListener() {

			@Override
			public void onPageSelected(int position) {
				// on changing the page
				// make respected tab selected
				actionBar.setSelectedNavigationItem(position);
			}

			@Override
			public void onPageScrolled(int arg0, float arg1, int arg2) {
			}

			@Override
			public void onPageScrollStateChanged(int arg0) {
			}
		});

	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case R.id.action_settings:
			return true;
		case R.id.action_about:
			// TODO Convenience function that delete all stories
			StoryDataSource datasource = new StoryDataSource(this);
			datasource.open();
			List<Story> stories = datasource.getAllStories();
			for (Story s : stories) {
				datasource.deleteStory(s);
			}
			return true;
		}
		return false;
	}

	@Override
	public void onTabReselected(Tab arg0, FragmentTransaction arg1) {

	}

	@Override
	public void onTabSelected(Tab tab, FragmentTransaction ft) {
		viewPager.setCurrentItem(tab.getPosition());
	}

	@Override
	public void onTabUnselected(Tab tab, FragmentTransaction ft) {
	}
}
