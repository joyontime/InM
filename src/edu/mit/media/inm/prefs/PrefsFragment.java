package edu.mit.media.inm.prefs;

import android.os.Bundle;
import android.preference.PreferenceFragment;
import edu.mit.media.inm.R;

public class PrefsFragment extends PreferenceFragment {

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		// Load the preferences from an XML resource
		addPreferencesFromResource(R.xml.preferences);
	}
	
	@Override
	public void onResume(){
		super.onResume();
		getActivity().getActionBar().setTitle(R.string.action_settings);
	}
}