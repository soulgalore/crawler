/******************************************************
 * Web crawler
 * 
 *
 * Copyright (C) 2012 by Peter Hedenskog (http://peterhedenskog.com)
 *
 ******************************************************
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in 
 * compliance with the License. You may obtain a copy of the License at
 * 
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software distributed under the License is 
 * distributed  on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.   
 * See the License for the specific language governing permissions and limitations under the License.
 *
 *******************************************************
 */
package com.soulgalore.crawler.guice;

import java.util.Set;

import org.apache.http.Header;
import org.apache.http.HttpHost;
import org.apache.http.auth.AuthScope;
import org.apache.http.auth.UsernamePasswordCredentials;
import org.apache.http.client.HttpClient;
import org.apache.http.client.params.ClientPNames;
import org.apache.http.conn.params.ConnRoutePNames;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.impl.conn.tsccm.ThreadSafeClientConnManager;

import com.google.inject.Inject;
import com.google.inject.Provider;
import com.google.inject.name.Named;
import com.soulgalore.crawler.util.Auth;
import com.soulgalore.crawler.util.AuthUtil;
import com.soulgalore.crawler.util.HTTPSFaker;
import com.soulgalore.crawler.util.HeaderUtil;

/**
 * Provide a HTTPClient.
 * 
 * 
 */
public class HttpClientProvider implements Provider<HttpClient> {

	/**
	 * The number of threads used in the HTTP Client Manager, meaning we can
	 * have this number of HTTP connections open at the same time.
	 */
	private final int nrOfThreads;

	/**
	 * The number of connections that can be open to the same route. Setting
	 * this to the same number as the number of HTTP threads, will ensure that
	 * we will use all the thread, even if we only are using one route.
	 */
	private final int maxToRoute;

	/**
	 * The number in ms before a socket timeout.
	 */
	private final int socketTimeout;

	/**
	 * The number in ms before a connection timeout.
	 */
	private final int connectionTimeout;

	/**
	 * The default request headers.
	 */
	private final Set<Header> headers;

	private final Set<Auth> auths;
	
	private final String proxyHost;
	
	private final String proxyProtocol;
	
	private final int proxyPort;

	/**
	 * Create a provider.
	 * 
	 * @param maxNrOfThreads
	 *            the max number of threads in the client
	 * @param theSocketTimeout
	 *            the socket timeout time
	 * @param theConnectionTimeout
	 *            the connection timeout time
	 * @param headersAsString
	 *            the request headers, in the form at of ...
	 * @param auth
	 *            the auth string
	 * @param theProxyHost
	 * 			  the host of the proxy
	 * @param theProxyPort
	 * 			  the proxy port
	 * @param theProxyProtocol 
	 * 			  the proxy protocol            
	 */
	@Inject
	public HttpClientProvider(
			@Named("com.soulgalore.crawler.nrofhttpthreads") int maxNrOfThreads,
			@Named("com.soulgalore.crawler.http.socket.timeout") int theSocketTimeout,
			@Named("com.soulgalore.crawler.http.connection.timeout") int theConnectionTimeout,
			@Named("com.soulgalore.crawler.requestheaders") String headersAsString,
			@Named("com.soulgalore.crawler.auth") String authAsString,
			@Named("com.soulgalore.crawler.proxyhost") String theProxyHost,
			@Named("com.soulgalore.crawler.proxyport") int theProxyPort,
			@Named("com.soulgalore.crawler.proxyprotocol") String theProxyProtocol){
		nrOfThreads = maxNrOfThreads;
		maxToRoute = maxNrOfThreads;
		connectionTimeout = theConnectionTimeout;
		socketTimeout = theSocketTimeout;
		headers = HeaderUtil.getInstance().createHeadersFromString(
				headersAsString);
		auths = AuthUtil.getInstance().createAuthsFromString(authAsString);
		proxyHost = theProxyHost;
		proxyPort = theProxyPort;
		proxyProtocol = "".equals(theProxyProtocol)?"http":theProxyProtocol;
		
	}

	/**
	 * Get the client.
	 * 
	 * @return the client
	 */
	public HttpClient get() {
		final ThreadSafeClientConnManager cm = new ThreadSafeClientConnManager();
		cm.setMaxTotal(nrOfThreads);
		cm.setDefaultMaxPerRoute(maxToRoute);
		final DefaultHttpClient client = HTTPSFaker.getClientThatAllowAnyHTTPS(cm);
		
		client.getParams().setParameter("http.socket.timeout", socketTimeout);
		client.getParams().setParameter("http.connection.timeout",
				connectionTimeout);
		client.getParams().setParameter(ClientPNames.DEFAULT_HEADERS, headers);
		
		if (!"".equals(proxyHost)) {
			HttpHost proxy = new HttpHost(proxyHost, proxyPort, proxyProtocol);
			client.getParams().setParameter(ConnRoutePNames.DEFAULT_PROXY,
                    proxy);
		}
			
		if (auths.size() > 0) {
	
			for (Auth authObject : auths) {
					client.getCredentialsProvider().setCredentials(
						new AuthScope(authObject.getScope(),
								authObject.getPort()),
						new UsernamePasswordCredentials(authObject
								.getUserName(), authObject.getPassword()));
			}
		}
		
		return client;
	}
}
