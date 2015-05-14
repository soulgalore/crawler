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
import java.util.concurrent.Callable;



/**
 * A callable that fetch a HTTP response code and return response to the caller.
 * 
 */
public class HTMLPageResponseCallable implements Callable<HTMLPageResponse> {

  private final HTMLPageResponseFetcher fetcher;
  private final CrawlerURL url;
  private final boolean fetchPage;
  private final boolean followRedirectsToNewDomain;
  private final Map<String, String> requestHeaders;


  /**
   * Create a new callable.
   * 
   * @param theUrl the url to call.
   * @param theFetcher the fetcher to use
   * @param fetchTheBody if true, the response body is fetched, else not.
   * @param theRequestHeaders request headers to add
   * @param followRedirectsToNewDomain if true, follow redirects that lead to a different domain.
   */
  public HTMLPageResponseCallable(CrawlerURL theUrl, HTMLPageResponseFetcher theFetcher,
      boolean fetchTheBody, Map<String, String> theRequestHeaders, boolean followRedirectsToNewDomain) {

    url = theUrl;
    fetcher = theFetcher;
    fetchPage = fetchTheBody;
    requestHeaders = theRequestHeaders;
    this.followRedirectsToNewDomain = followRedirectsToNewDomain;

  }

  /**
   * Fetch the actual response.
   * 
   * @return the response
   * @throws InterruptedException if it takes longer time than the configured max time to fetch the
   *         response
   */
  public HTMLPageResponse call() throws InterruptedException {
    return fetcher.get(url, fetchPage, requestHeaders, followRedirectsToNewDomain);
  }

  @Override
  public String toString() {
    // TODO add request headers
    return this.getClass().getSimpleName() + " url:" + url;
  }
}
