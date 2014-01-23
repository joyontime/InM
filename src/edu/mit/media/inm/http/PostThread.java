package edu.mit.media.inm.http;

import java.io.UnsupportedEncodingException;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.protocol.BasicHttpContext;
import org.apache.http.protocol.HttpContext;
import org.apache.http.util.EntityUtils;

import android.util.Log;

public abstract class PostThread extends Thread {

	public static String TAG = "RequestThread";
	
    private final HttpClient httpClient;
    private final HttpContext context;
    protected final HttpPost httpPost;
    private final int id;

    public PostThread(String uri, int id) {
        this.context = new BasicHttpContext();
        this.httpPost = new HttpPost(uri);
        this.id = id;
        this.httpClient = new DefaultHttpClient();
    }
    
    public abstract void setupParams() throws UnsupportedEncodingException ;
    
    /**
     * Executes the PostMethod and prints some status information.
     */
    @Override
    public void run() {
        try {
            Log.d(TAG, id + " - about to Post something to " + httpPost.getURI());
            HttpResponse response = httpClient.execute(httpPost, context);
            try {
                Log.d(TAG, id + " - Post executed");
                // Post the response body as an array of bytes
                HttpEntity entity = response.getEntity();
                if (entity != null) {
                    String result = EntityUtils.toString(entity);
                    Log.d(TAG, "Result: "+ result);
                    this.processRequest(result);
                }
            } finally {
                Log.d(TAG, "request finished");
            }
        } catch (Exception e) {
            Log.d(TAG, id + " - error: " + e);
        }
    }

    public abstract void processRequest(String result);
}