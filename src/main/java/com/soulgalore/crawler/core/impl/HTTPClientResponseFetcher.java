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
package com.soulgalore.crawler.core.impl;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.SocketTimeoutException;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import org.apache.http.Header;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.util.EntityUtils;

import com.google.inject.Inject;
import com.soulgalore.crawler.core.CrawlerConfiguration;
import com.soulgalore.crawler.core.PageURL;
import com.soulgalore.crawler.core.HTMLPageResponse;
import com.soulgalore.crawler.core.HTMLPageResponseFetcher;
import com.soulgalore.crawler.util.StatusCode;

/**
 * Fetch urls by a HTTPClient. Note: Will only fetch response headers for
 * resources that fails and for pages (meaning where the body of the response is
 * fetched).
 * 
 * 
 */
public class HttpClientResponseFetcher implements HTMLPageResponseFetcher {

	private final HttpClient httpClient;

	/**
	 * Create a new fetcher.
	 * 
	 * @param client
	 *            the client to use
	 */
	@Inject
	public HttpClientResponseFetcher(HttpClient client) {
		httpClient = client;
	}

	/**
	 * Shutdown the client.
	 */
	public void shutdown() {
		httpClient.getConnectionManager().shutdown();
	}

	/**
	 * Get a response.
	 * @param url the url
	 * @param getPage the body of the page or not
	 * @return the response
	 */
	public HTMLPageResponse get(PageURL url, boolean getPage, Map<String,String> requestHeaders) {

		if (url.isWrongSyntax()) {
			return new HTMLPageResponse(url, StatusCode.SC_MALFORMED_URI.getCode(),
					Collections.<String, String>emptyMap(), "", "", 0, "");
		}
	
		final HttpGet get = new HttpGet(url.getUri());
		
		for (String key : requestHeaders.keySet()) {
			get.setHeader(key, requestHeaders.get(key));
		}

		HttpEntity entity = null;
		
		try {

			final HttpResponse resp = httpClient.execute(get);
			entity = resp.getEntity();

			// this is a hack to minimize the amount of memory used
			// should make this configurable maybe
			// don't fetch headers for request that don't fetch the body and
			// response isn't 200
			// these headers will not be shown in the results
			final Map<String, String> headersAndValues = getPage
					|| !StatusCode.isResponseCodeOk(
							resp.getStatusLine().getStatusCode()) ? getHeaders(resp)
					: Collections.<String, String>emptyMap();

			final String encoding = entity.getContentEncoding() != null ? entity
					.getContentEncoding().getValue() : "";

			final String body = getPage ? getBody(entity, "".equals(encoding)?"UTF-8":encoding) : "";
			final long size = entity.getContentLength();
			// TODO add log when null
			final String type = (entity.getContentType() !=null) ? entity.getContentType().getValue() : "";
			
			return new HTMLPageResponse(url, resp.getStatusLine()
					.getStatusCode(), headersAndValues, body, encoding, size, type);

		} catch (SocketTimeoutException e) {
			System.err.println(e);
			return new HTMLPageResponse(url,
					StatusCode.SC_SERVER_RESPONSE_TIMEOUT.getCode(),
					Collections.<String, String>emptyMap(), "", "", 0, "");
			}

		catch (IOException e) {
			System.err.println(e);
			return new HTMLPageResponse(url,
					StatusCode.SC_SERVER_RESPONSE_UNKNOWN.getCode(),
					Collections.<String, String>emptyMap(), "", "", 0, "");
		}
		finally {
			try {
				EntityUtils.consume(entity);
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		

	}

	/**
	 * Get the body.
	 * 
	 * @param entity
	 *            the http entity from the response
	 * @param enc
	 *            the encoding
	 * @return the body as a String
	 * @throws IOException
	 *             if the body couldn't be fetched
	 */

	protected String getBody(HttpEntity entity, String enc) throws IOException {
		final StringBuilder body = new StringBuilder();
		String buffer = "";
		if (entity != null) {
			final BufferedReader reader = new BufferedReader(
					new InputStreamReader(entity.getContent(), enc));
			while ((buffer = reader.readLine()) != null) {
				body.append(buffer);
			}

			reader.close();
		}
		return body.toString();
	}

	/**
	 * Get the headers from the response.
	 * 
	 * @param resp
	 *            the response
	 * @return the headers as a key/value map.
	 */
	protected Map<String, String> getHeaders(HttpResponse resp) {
		final Map<String, String> headersAndValues = new HashMap<String, String>();

		final Header[] httpHeaders = resp.getAllHeaders();
		for (Header header : httpHeaders) {
			headersAndValues.put(header.getName(), header.getValue());
		}
		return headersAndValues;
	}
}
