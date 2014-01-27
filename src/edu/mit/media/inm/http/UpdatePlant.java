package edu.mit.media.inm.http;

import java.util.ArrayList;
import java.util.List;
import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;

import edu.mit.media.inm.MainActivity;
import edu.mit.media.inm.R;
import edu.mit.media.inm.data.PlantDataSource;
import android.content.Context;
import android.util.Log;

public class UpdatePlant extends PostThread{
	private PlantDataSource datasource;
	
	private String plant_id;
	private int status;
	private boolean archived;
	
	private MainActivity main;

	public UpdatePlant(int id, Context ctx) {
		super(id, ctx);
		datasource = new PlantDataSource(ctx);
		datasource.open();
		
		main = (MainActivity) ctx;

		String server = ctx.getResources().getString(R.string.url_server);
		String plants = ctx.getResources().getString(R.string.uri_plants);
		String update = ctx.getResources().getString(R.string.uri_update);
		this.uri = server + "/" + plants + '/' + update;
		
		Log.d(TAG, "URI to ping: " + this.uri);
	}

	List<NameValuePair> urlParameters = new ArrayList<NameValuePair>();
	
	public void setupParams(String plant_id, int status, boolean archived){
		this.plant_id = plant_id;
		this.status = status;
		this.archived = archived;
		
		params.add(new BasicNameValuePair("state", String.valueOf(this.status)));
		params.add(new BasicNameValuePair("id", this.plant_id));
		params.add(new BasicNameValuePair("archived", String.valueOf(this.archived)));
	}

	@Override
	protected void onPostExecute(String result) {
		// Save the plant locally
		datasource.updatePlant(plant_id, this.status);
		datasource.close();
		main.refresh();
	}
}