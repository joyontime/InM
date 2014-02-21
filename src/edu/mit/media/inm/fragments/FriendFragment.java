package edu.mit.media.inm.fragments;

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
import edu.mit.media.inm.MainActivity;
import edu.mit.media.inm.R;
import edu.mit.media.inm.handlers.UserDataSource;
import edu.mit.media.inm.types.User;

public class FriendFragment extends Fragment {
	private MainActivity ctx;
	private View rootView;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		// Load the preferences from an XML resource	
	}
	

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		ctx = (MainActivity) this.getActivity();

		rootView = inflater.inflate(R.layout.fragment_friends, container,
				false);
		
		ListView friend_listview = (ListView) rootView.findViewById(R.id.friend_list);
		List<User> friends = ctx.user_ds.getAllUsers();
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
		getActivity().getActionBar().setTitle("My Group");
	}
}