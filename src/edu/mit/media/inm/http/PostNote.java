package edu.mit.media.inm.http;

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;

import org.apache.http.NameValuePair;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.message.BasicNameValuePair;

import android.content.Context;

public class PostNote extends PostThread{

	public PostNote(int id, Context ctx) {
		super(id, ctx);
	}

	List<NameValuePair> urlParameters = new ArrayList<NameValuePair>();

	@Override
	public void setupParams() {
		// TODO Auto-generated method stub
		
	}


	@Override
	protected void onPostExecute(String result) {
		// TODO Auto-generated method stub
		
	}
}