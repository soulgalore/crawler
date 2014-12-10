/******************************************************
 * Web crawler
 * 
 * 
 * Copyright (C) 2012 by Peter Hedenskog (http://peterhedenskog.com)
 * 
 ****************************************************** 
 * 
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except
 * in compliance with the License. You may obtain a copy of the License at
 * 
 * 
 * http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software distributed under the License
 * is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express
 * or implied. See the License for the specific language governing permissions and limitations under
 * the License.
 * 
 ******************************************************* 
 */
package com.soulgalore.crawler.guice;

import java.util.Set;
import java.util.StringTokenizer;

import org.apache.http.Header;
import org.apache.http.HttpHost;
import org.apache.http.auth.AuthScope;
import org.apache.http.auth.UsernamePasswordCredentials;
import org.apache.http.client.HttpClient;
import org.apache.http.client.params.ClientPNames;
import org.apache.http.client.params.CookiePolicy;
import org.apache.http.client.protocol.RequestAcceptEncoding;
import org.apache.http.client.protocol.ResponseContentEncoding;
import org.apache.http.conn.params.ConnRoutePNames;
import org.apache.http.cookie.Cookie;
import org.apache.http.cookie.CookieSpec;
import org.apache.http.cookie.CookieSpecFactory;
import org.apache.http.cookie.MalformedCookieException;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.cookie.CookieOrigin;
import org.apache.http.impl.conn.tsccm.ThreadSafeClientConnManager;
import org.apache.http.impl.cookie.BestMatchSpec;
import org.apache.http.impl.cookie.BrowserCompatSpec;
import org.apache.http.params.HttpParams;
import org.apache.http.params.HttpProtocolParams;

import com.google.inject.Inject;
import com.google.inject.Provider;
import com.google.inject.name.Named;
import com.soulgalore.crawler.core.CrawlerConfiguration;
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
   * The number of threads used in the HTTP Client Manager, meaning we can have this number of HTTP
   * connections open at the same time.
   */
  private final int nrOfThreads;

  /**
   * The number of connections that can be open to the same route. Setting this to the same number
   * as the number of HTTP threads, will ensure that we will use all the thread, even if we only are
   * using one route.
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

  private final Set<Auth> auths;

  private final String proxy;


  /**
   * Create a provider.
   * 
   * @param maxNrOfThreads the max number of threads in the client
   * @param theSocketTimeout the socket timeout time
   * @param theConnectionTimeout the connection timeout time
   * @param authAsString the auth string
   * @param theProxy the proxy
   */
  @Inject
  public HttpClientProvider(
      @Named(CrawlerConfiguration.MAX_THREADS_PROPERTY_NAME) int maxNrOfThreads,
      @Named(CrawlerConfiguration.SOCKET_TIMEOUT_PROPERTY_NAME) int theSocketTimeout,
      @Named(CrawlerConfiguration.CONNECTION_TIMEOUT_PROPERTY_NAME) int theConnectionTimeout,
      @Named(CrawlerConfiguration.AUTH_PROPERTY_NAME) String authAsString,
      @Named(CrawlerConfiguration.PROXY_PROPERTY_NAME) String theProxy) {
    nrOfThreads = maxNrOfThreads;
    maxToRoute = maxNrOfThreads;
    connectionTimeout = theConnectionTimeout;
    socketTimeout = theSocketTimeout;
    auths = AuthUtil.getInstance().createAuthsFromString(authAsString);
    proxy = theProxy;
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
    client.getParams().setParameter("http.connection.timeout", connectionTimeout);

    client.addRequestInterceptor(new RequestAcceptEncoding());
    client.addResponseInterceptor(new ResponseContentEncoding());
    
    CookieSpecFactory csf = new CookieSpecFactory() {
      public CookieSpec newInstance(HttpParams params) {
        return new BestMatchSpecWithURLErrorLog();
      }
    };

    client.getCookieSpecs().register("bestmatchwithurl", csf);
    client.getParams().setParameter(ClientPNames.COOKIE_POLICY, "bestmatchwithurl");

    if (!"".equals(proxy)) {
      StringTokenizer token = new StringTokenizer(proxy, ":");

      if (token.countTokens() == 3) {
        String proxyProtocol = token.nextToken();
        String proxyHost = token.nextToken();
        int proxyPort = Integer.parseInt(token.nextToken());

        HttpHost proxy = new HttpHost(proxyHost, proxyPort, proxyProtocol);
        client.getParams().setParameter(ConnRoutePNames.DEFAULT_PROXY, proxy);
      } else
        System.err.println("Invalid proxy configuration: " + proxy);
    }

    if (auths.size() > 0) {

      for (Auth authObject : auths) {
        client.getCredentialsProvider().setCredentials(
            new AuthScope(authObject.getScope(), authObject.getPort()),
            new UsernamePasswordCredentials(authObject.getUserName(), authObject.getPassword()));
      }
    }

    return client;
  }

  private class BestMatchSpecWithURLErrorLog extends BestMatchSpec {
    @Override
    public void validate(Cookie cookie, CookieOrigin origin) throws MalformedCookieException {
      try {
        super.validate(cookie, origin);
      } catch (MalformedCookieException e) {
        System.err.println("Cookie rejected for url: " + origin.getHost()
            + (origin.getPort() != 80 ? ":" + origin.getPort() : "") + origin.getPath()
            + " the error:" + e.getMessage() + " for cookie:" + cookie.toString());
        throw e;
      }
    }
  }
}
