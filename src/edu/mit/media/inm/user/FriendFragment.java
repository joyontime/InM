package edu.mit.media.inm.user;

import java.util.ArrayList;
import java.util.List;

import android.app.Activity;
import android.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import edu.mit.media.inm.R;
import edu.mit.media.inm.data.UserDataSource;
import edu.mit.media.inm.prefs.PreferenceHandler;

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
		
		ListView friend_listview = (ListView) rootView.findViewById(R.id.friend_list);
		List<User> friends = datasource.getAllUsers();
		ArrayList<String> friend_aliases = new ArrayList<String>(); 
		for (User u: friends){
			friend_aliases.add(u.alias);
		}
		
		friend_listview.setAdapter(
				new ArrayAdapter<String>(ctx, R.layout.friend_row_item, friend_aliases));
		
		return rootView;
	}
	
	@Override
	public void onResume(){
		super.onResume();
		getActivity().getActionBar().setTitle(R.string.action_friends);
	}
}