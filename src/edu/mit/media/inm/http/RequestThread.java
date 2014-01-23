package edu.mit.media.inm.http;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.protocol.BasicHttpContext;
import org.apache.http.protocol.HttpContext;
import org.apache.http.util.EntityUtils;

import android.content.Context;
import android.util.Log;

public class RequestThread extends Thread {

	public static String TAG = "RequestThread";
	
    private final HttpClient httpClient;
    private final HttpContext context;
    private final HttpGet httpget;
    private final int id;

    public RequestThread(String uri, int id) {
        this.context = new BasicHttpContext();
        this.httpget = new HttpGet(uri);
        this.id = id;
        this.httpClient = new DefaultHttpClient();
    }

    /**
     * Executes the GetMethod and prints some status information.
     */
    @Override
    public void run() {
        try {
            Log.d(TAG, id + " - about to get something from " + httpget.getURI());
            HttpResponse response = httpClient.execute(httpget, context);
            try {
                Log.d(TAG, id + " - get executed");
                // get the response body as an array of bytes
                HttpEntity entity = response.getEntity();
                if (entity != null) {
                    byte[] bytes = EntityUtils.toByteArray(entity);
                    Log.d(TAG, id + " - " + bytes.length + " bytes read");
                }
            } finally {
                Log.d(TAG, "request finished");
            }
        } catch (Exception e) {
            Log.d(TAG, id + " - error: " + e);
        }
    }
}