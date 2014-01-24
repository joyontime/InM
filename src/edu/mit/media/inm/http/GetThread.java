package edu.mit.media.inm.http;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.StringWriter;
import java.net.Authenticator;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.PasswordAuthentication;
import java.net.URL;
import java.security.KeyStore;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManagerFactory;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.auth.Credentials;
import org.apache.http.auth.UsernamePasswordCredentials;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.params.HttpParams;
import org.apache.http.protocol.BasicHttpContext;
import org.apache.http.protocol.HttpContext;
import org.apache.http.util.EntityUtils;

import edu.mit.media.inm.prefs.PreferenceHandler;

import android.content.Context;
import android.os.AsyncTask;
import android.util.Base64;
import android.util.Log;

public abstract class GetThread extends AsyncTask<Void, Void, String> {

	public static String TAG = "RequestThread";

	//private final HttpClient httpClient;
	//private final SSLContext context;
	protected final HttpGet httpget;
	private final int id;
	protected final String uri;

	protected final Context ctx;
	
	protected final int TIMEOUT = 1000;

	public GetThread(String uri, int id, Context ctx) {
		this.httpget = new HttpGet(uri);
		this.id = id;
		//this.httpClient = new DefaultHttpClient();
		this.ctx = ctx;
		this.uri = uri;

		
		
		/*
		 * KeyStore ks = KeyStore.getInstance(KeyStore.getDefaultType());
		 * 
		 * // get user password and file input stream char[] password = new
		 * char[0];
		 * 
		 * java.io.FileInputStream fis = null; try { fis = new
		 * java.io.FileInputStream("keyStoreName"); ks.load(fis, password); }
		 * finally { if (fis != null) { fis.close(); } }
		 * 
		 * String algorithm = TrustManagerFactory.getDefaultAlgorithm();
		 * TrustManagerFactory tmf = TrustManagerFactory.getInstance(algorithm);
		 * tmf.init(keyStore);
		 * 
		 * context = SSLContext.getInstance("TLS"); context.init(null,
		 * tmf.getTrustManagers(), null);
		 */

	}

	public abstract void setupParams();

	/**
	 * Executes the GetMethod and prints some status information.
	 */
	protected String doInBackground(Void... arg0) {
		try {
			/*
			 * } HttpsURLConnection urlConnection = (HttpsURLConnection) url
			 * .openConnection();
			 * urlConnection.setSSLSocketFactory(context.getSocketFactory());
			 * InputStream in; in = urlConnection.getInputStream();
			 */
			final PreferenceHandler ph = new PreferenceHandler(ctx);

			Authenticator.setDefault(new Authenticator() {
				protected PasswordAuthentication getPasswordAuthentication() {
					return new PasswordAuthentication(ph.username(), ph.password()
							.toCharArray());
				}
			});

			URL url = new URL("https://sodiio.media.mit.edu:3050/");
			//URL url = new URL("https://medium.com");
			//URL url = new URL("http://18.111.19.200:8000/stories/test");
			Log.d(TAG, this.id + " - Getting from " + url.toString());
			HttpURLConnection urlConnection = (HttpURLConnection) url
					.openConnection();
			Log.d(TAG, "boop");
			urlConnection.setUseCaches(false);
			urlConnection.setRequestProperty("Authorization", "basic " +
			        Base64.encode((ph.username() + ":" + ph.password()).getBytes(), Base64.NO_WRAP));
			urlConnection.setDoInput(true);
			urlConnection.setConnectTimeout(TIMEOUT);
			Log.d(TAG, "boop");

			Map<String, List<String>> hdrs = urlConnection.getRequestProperties();
		    Set<String> hdrKeys = hdrs.keySet();
		    for (String k : hdrKeys)
		      Log.d(TAG, "Key: " + k + "  Value: " + hdrs.get(k));
		    
		    
			Log.d(TAG, "boop");
			InputStream in = new BufferedInputStream(
					urlConnection.getInputStream());

			BufferedReader r = new BufferedReader(new InputStreamReader(in));
			StringBuilder total = new StringBuilder();
			String line;
			while ((line = r.readLine()) != null) {
			    total.append(line);
			}
			Log.d(TAG, "boop");
			
			urlConnection.disconnect();
			
			return total.toString();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return "Failed.";
	}

	protected abstract void onPostExecute(String result);
}