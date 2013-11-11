package edu.mit.media.inm.status;
import edu.mit.media.inm.MainActivity;
import edu.mit.media.inm.MajorFragment;
import edu.mit.media.inm.R;
import edu.mit.media.inm.data.Status;
import edu.mit.media.inm.data.StatusAdapter;
import edu.mit.media.inm.data.StatusDataSource;
import edu.mit.media.inm.data.Story;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;

public class UpdateFragment extends MajorFragment {
	private static final String TAG = "TellFragment";

	//TODO Use preferences
	private String username = "joy4luck";
	private StatusDataSource datasource;

	private Button new_status_btn;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		Log.d(TAG, "OnCreateView");

		View rootView = inflater.inflate(R.layout.fragment_update, container,
				false);

		datasource = new StatusDataSource(this.getActivity());
		datasource.open();

		return rootView;
	}

	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);
		new_status_btn = (Button) this.getActivity()
				.findViewById(R.id.new_status);
		new_status_btn.setOnClickListener(new View.OnClickListener() {
			// Initialize a ComposeActivity to write a Status.
			@Override
			public void onClick(View v) {
				Toast.makeText(getActivity(), "Boop.", Toast.LENGTH_SHORT).show();
				// TODO (joyc) image acquire!
				String image = "candle.png";
				Status status = new Status();
				Toast.makeText(getActivity(), "Publishing: " + status.toString(),
						Toast.LENGTH_LONG).show();
				Status s = datasource.createStatus(0, "briefing", System.currentTimeMillis(), 0, username);
				
				((MainActivity) getActivity()).switchToTab(0);
			}
		});
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

	@Override
	public void onClick(View v) {
		//TODO (joyc) maybe handle response to refresh?
	}
}