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
  private final CrawlerURL url;
  private final int responseCode;
  private final String responseType;
  private final Map<String, String> headers;
  private final long fetchTime;

  /**
   * Create a response.
   * 
   * @param pageUrl the url
   * @param theResponseCode the response code
   * @param theHeaders the headers
   * @param theBody the body
   * @param theEncoding the encoding
   * @param theSize the size
   * @param theResponseType the response mime type
   * @param theFetchTime the time it took to fetch the response
   */
  public HTMLPageResponse(CrawlerURL pageUrl, int theResponseCode, Map<String, String> theHeaders,
      String theBody, String theEncoding, long theSize, String theResponseType, long theFetchTime) {
    encoding = theEncoding;
    url = pageUrl;
    responseCode = theResponseCode;
    responseType = theResponseType;
    headers = theHeaders;
    fetchTime = theFetchTime;

    // special hack:
    // if the path contains a . (.html etc) then use the full path,
    //
    // relative links using ../ get's confused if the path don't
    // ends with an /

    if (!pageUrl.isWrongSyntax()) {
      final String baseUri =
          pageUrl.getUri().getScheme()
              + "://"
              + pageUrl.getUri().getHost()
              + ((pageUrl.getUri().getPort() != NO_HTTP_PORT)
                  ? ":" + pageUrl.getUri().getPort()
                  : "")
              + ((pageUrl.getUri().getPath().contains(".")) ? pageUrl.getUri().getPath() : pageUrl
                  .getUri().getPath() + (pageUrl.getUri().getPath().endsWith("/") ? "" : "/"));

      // OK, here's a true story, There are some plugins (WP?) that create a href tags with a return instead 
      // of a space between the a and the href. Lets catch them in this ugly way
      if (theBody.contains("<ahref")) {
        theBody = theBody.replaceAll("<ahref", "<a href");
      }
        
      doc = Jsoup.parse(theBody, baseUri);
    } else {
      doc = null;
    }

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

  public CrawlerURL getPageUrl() {
    return url;
  }

  public int getResponseCode() {
    return responseCode;
  }

  public Map<String, String> getResponseHeaders() {
    return headers;
  }

  public String getHeaderValue(String key) {
    return headers.get(key);

  }

  public long getFetchTime() {
    return fetchTime;
  }

  @Override
  public String toString() {
    // left out the body & headers for now
    return this.getClass().getSimpleName() + "url:" + getUrl() + "responseCode:"
        + getResponseCode() + "encoding:" + encoding + " type:" + responseType;
  }

  @Override
  public int hashCode() {
    final int prime = 31;
    int result = 1;
    result = prime * result + responseCode;
    result = prime * result + ((responseType == null) ? 0 : responseType.hashCode());
    result = prime * result + ((url == null) ? 0 : url.hashCode());
    return result;
  }

  @Override
  public boolean equals(Object obj) {
    if (this == obj) return true;
    if (obj == null) return false;
    if (getClass() != obj.getClass()) return false;
    HTMLPageResponse other = (HTMLPageResponse) obj;
    if (responseCode != other.responseCode) return false;
    if (responseType == null) {
      if (other.responseType != null) return false;
    } else if (!responseType.equals(other.responseType)) return false;
    if (url == null) {
      if (other.url != null) return false;
    } else if (!url.equals(other.url)) return false;
    return true;
  }



}
