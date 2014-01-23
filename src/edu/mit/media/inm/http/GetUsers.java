package edu.mit.media.inm.http;

import org.apache.http.params.HttpParams;

public class GetUsers extends GetThread {

	public GetUsers(String uri, int id) {
		super(uri, id);
		// TODO Auto-generated constructor stub
	}

	@Override
	public void setupParams() {
		HttpParams params = null;
		httpget.setParams(params);
	}

	@Override
	public void processRequest(String result) {
		// TODO Auto-generated method stub

	}
}
