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

/**
 * Interface for the response fetchers.
 * 
 */
public interface HTMLPageResponseFetcher {

  /**
   * Get the response for this url, never fetch the body.
   * 
   * @param url the url to fetch
   * @param fetchBody fetch the body or not
   * @param requestHeaders request headers to add
   * @param followRedirectsToNewDomain if true, follow redirects that lead to a different domain.
   * @return the response
   */
  HTMLPageResponse get(CrawlerURL url, boolean fetchBody,
                       Map<String, String> requestHeaders,
                       boolean followRedirectsToNewDomain);


  /**
   * Shutdown the fetcher and all of it's assets.
   */
  void shutdown();
}
