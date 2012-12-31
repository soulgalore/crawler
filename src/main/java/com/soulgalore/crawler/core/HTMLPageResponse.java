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
package com.soulgalore.crawler.core;

import java.util.Map;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

/**
 * The response for a html page.
 * 
 */
public class HTMLPageResponse {

	private static final int NO_HTTP_PORT = -1;
	
	private final Document doc;
	private final String encoding;
	private final PageURL url;
	private final int responseCode;
	private final String responseType;

	/**
	 * Create a response.
	 * @param pageUrl the url
	 * @param theResponseCode the response code
	 * @param theHeaders the headers
	 * @param theBody the body
	 * @param theEncoding the encoding
	 * @param theSize the size
	 */
	public HTMLPageResponse(PageURL pageUrl, int theResponseCode,
			Map<String, String> theHeaders, String theBody, String theEncoding,
			long theSize, String theResponseType) {
		encoding = theEncoding;
		url = pageUrl;
		responseCode = theResponseCode;
		responseType = theResponseType;

		// special hack:
		// if the path contains a . (.html etc) then use the full path,
		//
		// relative links using ../ get's confused if the path don't
		// ends with an /

		final String baseUri = pageUrl.getUri().getScheme()
				+ "://"
				+ pageUrl.getUri().getHost()
				+ ((pageUrl.getUri().getPort() != NO_HTTP_PORT) ? ":" + pageUrl
						.getUri().getPort() : "")
				+ ((pageUrl.getUri().getPath().contains(".")) ? pageUrl
						.getUri().getPath() : pageUrl.getUri().getPath()
						+ (pageUrl.getUri().getPath().endsWith("/") ? "" : "/"));
	
		doc = Jsoup.parse(theBody, baseUri);

	}

	public String getEncoding() {
		return encoding;
	}

	public Document getBody() {
		return doc;
	}

	public String getUrl() {
		return url.getUrl();
	}
	
	public String getResponseType() {
		return responseType;
	}
	
	public PageURL getPageUrl() {
		return url;
	}

	public int getResponseCode() {
		return responseCode;
	}

	@Override
	public String toString() {
		// left out the body for now
		return this.getClass().getSimpleName() + "url:" + getUrl() + "responseCode:" + getResponseCode()+ "encoding:"+ encoding + " type:" + responseType;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = super.hashCode();
		result = prime * result + ((doc == null) ? 0 : doc.hashCode());
		result = prime * result
				+ ((encoding == null) ? 0 : encoding.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (!super.equals(obj))
			return false;
		if (getClass() != obj.getClass())
			return false;
		final HTMLPageResponse other = (HTMLPageResponse) obj;
		if (doc == null) {
			if (other.doc != null)
				return false;
		} else if (!doc.equals(other.doc))
			return false;
		if (encoding == null) {
			if (other.encoding != null)
				return false;
		} else if (!encoding.equals(other.encoding))
			return false;
		return true;
	}

}
