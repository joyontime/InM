package edu.mit.media.inm.http;

import java.util.ArrayList;
import java.util.List;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;

import android.content.Context;

public class PostNote extends PostThread{

	public PostNote(int id, Context ctx) {
		super(id, ctx);
	}

	List<NameValuePair> urlParameters = new ArrayList<NameValuePair>();


	public void setupParams() {
		params.add(new BasicNameValuePair("firstParam", "one"));
		params.add(new BasicNameValuePair("secondParam", "two"));
		params.add(new BasicNameValuePair("thirdParam", "three"));
	}

	@Override
	protected void onPostExecute(String result) {
		// TODO Auto-generated method stub
	}
}