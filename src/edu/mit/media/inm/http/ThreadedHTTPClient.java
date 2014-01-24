/*
 * ====================================================================
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 * ====================================================================
 *
 * This software consists of voluntary contributions made by many
 * individuals on behalf of the Apache Software Foundation.  For more
 * information on the Apache Software Foundation, please see
 * <http://www.apache.org/>.
 *
 */
package edu.mit.media.inm.http;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.Authenticator;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.PasswordAuthentication;
import java.net.URL;

import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;

import edu.mit.media.inm.R;
import edu.mit.media.inm.prefs.PreferenceHandler;

import android.content.Context;
import android.content.res.Resources.NotFoundException;
import android.os.AsyncTask;
import android.util.Log;

/**
 * An example that performs GETs from multiple threads.
 * 
 */
public class ThreadedHTTPClient {
	private static String TAG = "ThreadedHTTPClient";
	// private final PoolingHttpClientConnectionManager cm;
	// private final CloseableHttpClient httpclient;
	// HttpClient httpclient;

	private final Context ctx;

	/*
	 * public ThreadedHTTPClient(Context ctx){ // Create an HttpClient with the
	 * ThreadSafeClientConnManager. // This connection manager must be used if
	 * more than one thread will // be using the HttpClient. cm = new
	 * PoolingHttpClientConnectionManager(); cm.setMaxTotal(100);
	 * 
	 * httpclient = HttpClients.custom() .setConnectionManager(cm) .build();
	 * 
	 * this.ctx = ctx; }
	 */

	public ThreadedHTTPClient(Context ctx) {
		this.ctx = ctx;
	}

	/**
	 * Contact the server to update everything.
	 * 
	 * @throws IOException
	 */
	public void updateAll() throws IOException {
		int THREAD_COUNT = 3;

		Log.d(TAG, "Starting update.");

		// create a thread for each URI
		GetThread[] threads = new GetThread[THREAD_COUNT];

		threads[0] = new GetPlants(0, ctx);
		threads[1] = new GetUsers(1, ctx);
		threads[2] = new GetNotes(2, ctx);

		// start the threads
		for (int j = 0; j < THREAD_COUNT; j++) {
			Log.d(TAG, "Executing " + j);
			threads[j].execute();
		}
	}

	public void pingServer() throws IOException {
		final PreferenceHandler ph = new PreferenceHandler(ctx);

		Authenticator.setDefault(new Authenticator() {
			protected PasswordAuthentication getPasswordAuthentication() {
				return new PasswordAuthentication(ph.username(), ph.password()
						.toCharArray());
			}
		});

		URL url;
		try {
			url = new URL(ctx.getResources().getString(R.string.url_server));

			HttpURLConnection urlConnection = (HttpURLConnection) url
					.openConnection();
			InputStream in = new BufferedInputStream(
					urlConnection.getInputStream());

			urlConnection.disconnect();
			java.util.Scanner s = new java.util.Scanner(in).useDelimiter("\\A");
			Log.d(TAG, s.hasNext() ? s.next() : "");
		} catch (MalformedURLException e) {
			e.printStackTrace();
		} catch (NotFoundException e) {
			e.printStackTrace();
		}
	}

}