package edu.mit.media.inm.http;

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;

import org.apache.http.NameValuePair;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.message.BasicNameValuePair;

import android.content.Context;

public class PostNote extends PostThread{

	public PostNote(String uri, int id, Context ctx) {
		super(uri, id, ctx);
	}

	List<NameValuePair> urlParameters = new ArrayList<NameValuePair>();

	@Override
	public void setupParams() throws UnsupportedEncodingException {
		urlParameters.add(new BasicNameValuePair("sn", "C02G8416DRJM"));
		urlParameters.add(new BasicNameValuePair("cn", ""));
		urlParameters.add(new BasicNameValuePair("locale", ""));
		urlParameters.add(new BasicNameValuePair("caller", ""));
		urlParameters.add(new BasicNameValuePair("num", "12345"));
		httpPost.setEntity(new UrlEncodedFormEntity(urlParameters));
	}

	@Override
	public void processRequest(String result) {
		// TODO Auto-generated method stub
		
	}
}