package edu.mit.media.inm.status;
import java.util.List;
import java.util.Random;

import edu.mit.media.inm.MajorFragment;
import edu.mit.media.inm.R;
import edu.mit.media.inm.data.Status;
import edu.mit.media.inm.data.StatusAdapter;
import edu.mit.media.inm.data.StatusDataSource;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.GridView;

public class StatusFragment extends MajorFragment {
	private static final String TAG = "FeedFragment";
	private StatusDataSource datasource;
	private GridView gridview;
	private StatusAdapter adapter;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		Log.d(TAG, "OnCreateView");

		View rootView = inflater.inflate(R.layout.fragment_status, container,
				false);

		datasource = new StatusDataSource(this.getActivity());
		datasource.open();

		List<Status> values = datasource.getAllStatuses();

		// Populate the gridview
		this.adapter = new StatusAdapter(this.getActivity(), values);

		Log.d(TAG, "OnCreateViewFinished:" + this.adapter.toString());

		return rootView;
	}

	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);
		// Find all stories in db
		gridview = (GridView) this.getActivity().findViewById(R.id.status_grid);
		Log.d(TAG, gridview.toString());
		gridview.setAdapter(adapter);
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
	public void onClick(View v) {
		// TODO Auto-generated method stub

	}
}