package edu.mit.media.inm.status;
import java.util.List;
import java.util.Random;

import edu.mit.media.inm.MajorFragment;
import edu.mit.media.inm.R;
import edu.mit.media.inm.data.Status;
import edu.mit.media.inm.data.StatusAdapter;
import edu.mit.media.inm.data.StatusAdapter.StatusHolder;
import edu.mit.media.inm.data.StatusDataSource;
import edu.mit.media.inm.story.ComposeActivity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;
import android.widget.GridView;
import android.widget.Toast;

public class StatusFragment extends MajorFragment {
	private static final String TAG = "FeedFragment";
	private StatusDataSource datasource;
	private GridView gridview;
	private StatusAdapter adapter;

	private Button new_status_btn;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		Log.d(TAG, "OnCreateView");

		View rootView = inflater.inflate(R.layout.fragment_status, container,
				false);

		datasource = new StatusDataSource(this.getActivity());
		datasource.open();
		return rootView;
	}

	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);
		Log.d(TAG, "OnActivityCreated");
		
		new_status_btn = (Button) this.getView().findViewById(R.id.new_status_btn);

		Log.d(TAG, new_status_btn.toString());
		new_status_btn.setOnClickListener(new View.OnClickListener() {
			// Initialize an UpdateActivity
			@Override
			public void onClick(View v) {
				Log.d(TAG, "Button clicked");
			
				Intent intent = new Intent(getActivity(), UpdateActivity.class);
				startActivity(intent);
			}
		});		
		gridview = (GridView) this.getActivity().findViewById(R.id.status_grid);
		Log.d(TAG, gridview.toString());
		
		gridview.setOnItemClickListener(new OnItemClickListener() {
	        public void onItemClick(AdapterView<?> parent, View v, int position, long id) {
	            AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
	            StatusHolder sh = (StatusHolder) v.getTag();
	            String name = sh.getUser();
	            builder.setTitle("Poke " + name + "?");
	            builder.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
	                public void onClick(DialogInterface dialog, int id) {
	                    // TODO Send the poke
	                }
	            });
	            builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
	                public void onClick(DialogInterface dialog, int id) {
	                    // User cancelled the dialog
	                }
	            });
	            builder.show();
	        }
	    });
	}
	
	@Override
	public void onResume() {
		super.onResume();

		datasource.open();
		// Populate the gridview
		List<Status> values = datasource.getAllStatuses();
		this.adapter = new StatusAdapter(this.getActivity(), values);
		gridview.setAdapter(adapter);
	}

	@Override
	public void onPause() {
		datasource.close();
		super.onPause();
	}

	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub

	}
}