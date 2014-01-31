package edu.mit.media.inm.http;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;
import java.net.URL;
import java.net.URLEncoder;
import java.security.KeyManagementException;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.cert.Certificate;
import java.security.cert.CertificateException;
import java.security.cert.CertificateFactory;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSession;
import javax.net.ssl.TrustManagerFactory;

import org.apache.http.NameValuePair;

import edu.mit.media.inm.prefs.PreferenceHandler;

import android.content.Context;
import android.os.AsyncTask;
import android.util.Base64;
import android.util.Log;

public abstract class PostThread extends AsyncTask<Void, Void, String> {
	public static String TAG = "PostThread";

	private SSLContext context;
	private final int id;
	protected String uri;
	protected static String charset = "UTF-8";
	protected final Context ctx;
	protected final int TIMEOUT = 1000;
	protected final PreferenceHandler ph;
	
	protected final List<NameValuePair> params;

	public PostThread(int id, Context ctx) {
		this.id = id;
		this.ctx = ctx;
		this.ph = new PreferenceHandler(ctx);
		this.params = new ArrayList<NameValuePair>();

		InputStream caInput;
		try {
			caInput = new BufferedInputStream(ctx.getAssets()
					.open("server.crt"));

			// Load CAs from an InputStream
			CertificateFactory cf = CertificateFactory.getInstance("X.509");
			Certificate ca = cf.generateCertificate(caInput);

			// Create a KeyStore containing our trusted CAs
			String keyStoreType = KeyStore.getDefaultType();
			KeyStore keyStore = KeyStore.getInstance(keyStoreType);
			keyStore.load(null, null);
			keyStore.setCertificateEntry("ca", ca);

			// Create a TrustManager that trusts the CAs in our KeyStore
			String tmfAlgorithm = TrustManagerFactory.getDefaultAlgorithm();
			TrustManagerFactory tmf = TrustManagerFactory
					.getInstance(tmfAlgorithm);
			tmf.init(keyStore);

			// Create an SSLContext that uses our TrustManager
			context = SSLContext.getInstance("TLS");
			context.init(null, tmf.getTrustManagers(), null);

			caInput.close();

		} catch (CertificateException e) {
			e.printStackTrace();
		} catch (KeyManagementException e) {
			e.printStackTrace();
		} catch (NoSuchAlgorithmException e) {
			e.printStackTrace();
		} catch (KeyStoreException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	protected abstract void onPostExecute(String result);

	/**
	 * Executes the GetMethod and prints some status information.
	 */
	protected String doInBackground(Void... arg0) {
		try {
			URL url = new URL(this.uri);
			Log.d(TAG, this.id + " - Posting to " + url.toString());
			HttpsURLConnection
					.setDefaultHostnameVerifier(new NullHostNameVerifier());
			HttpsURLConnection conn = (HttpsURLConnection) url.openConnection();

			// Set up the POST call + authenticate
			conn.setSSLSocketFactory(context.getSocketFactory());
			conn.setRequestMethod("POST");
			conn.setDoInput(true);
			conn.setDoOutput(true);
			conn.setRequestProperty("Accept", "text/html");
			conn.setRequestProperty("Accept-Charset", charset);
			conn.setRequestProperty(
					"Authorization",
					"Basic " + Base64.encodeToString((ph.username() + ":" + ph
									.password()).getBytes(), Base64.NO_WRAP));

			conn.setConnectTimeout(TIMEOUT);
			
			// Set up parameters in output stream
			OutputStream os = conn.getOutputStream();
			BufferedWriter writer = new BufferedWriter(
			        new OutputStreamWriter(os, "UTF-8"));
			writer.write(getQuery(this.params));
			writer.flush();
			writer.close();
			os.close();

			// Actually make the call to the server.
			InputStream in = new BufferedInputStream(conn.getInputStream());

			BufferedReader r = new BufferedReader(new InputStreamReader(in));
			StringBuilder total = new StringBuilder();
			String line;
			while ((line = r.readLine()) != null) {
				total.append(line);
			}
			conn.disconnect();

			return total.toString();
		} catch (FileNotFoundException e1) {
			e1.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return "Failed.";
	}

	public class NullHostNameVerifier implements HostnameVerifier {
		@Override
		public boolean verify(String hostname, SSLSession arg1) {
			Log.i("RestUtilImpl", "Approving certificate for " + hostname);
			return true;
		}
	}
	
	protected String getQuery(List<NameValuePair> params) throws UnsupportedEncodingException {
	    StringBuilder result = new StringBuilder();
	    boolean first = true;

	    for (NameValuePair pair : params)
	    {
	        if (first) first = false;
	        else
	            result.append("&");
	        result.append(URLEncoder.encode(pair.getName(), "UTF-8"));
	        result.append("=");
	        result.append(URLEncoder.encode(pair.getValue(), "UTF-8"));
	    }

	    return result.toString();
	}
}