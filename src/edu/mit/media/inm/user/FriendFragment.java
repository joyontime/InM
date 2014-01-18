package edu.mit.media.inm.user;

import edu.mit.media.inm.R;
import edu.mit.media.inm.data.PlantDataSource;
import edu.mit.media.inm.prefs.PreferenceHandler;
import android.app.Activity;
import android.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

public class FriendFragment extends Fragment {
	private Activity ctx;
	private View rootView;
	private UserDataSource datasource;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		// Load the preferences from an XML resource	
	}
	

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		ctx = this.getActivity();
		PreferenceHandler ph = new PreferenceHandler(ctx);

		rootView = inflater.inflate(R.layout.fragment_friends, container,
				false);
		
		datasource = new UserDataSource(ctx);
		datasource.open();

		setupInfoView();
		setupButtons();
		
		return rootView;
	}
	
	@Override
	public void onResume(){
		super.onResume();
		getActivity().getActionBar().setTitle(R.string.action_friends);
	}
}