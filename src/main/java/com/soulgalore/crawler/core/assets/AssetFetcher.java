/******************************************************
 * Web crawler
 * 
 * 
 * Copyright (C) 2013 by Peter Hedenskog (http://peterhedenskog.com)
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
package com.soulgalore.crawler.core.assets;

import java.util.Map;

import com.soulgalore.crawler.core.CrawlerURL;

/**
 * Interface of a asset fetcher.
 * 
 */
public interface AssetFetcher {

  /**
   * Fetch the asset.
   * 
   * @param url the url of the assets.
   * @param requestHeaders request headers for the fetch.
   * @return the asset response
   */
  AssetResponse getAsset(CrawlerURL url, Map<String, String> requestHeaders);

  /**
   * Shutdown the fetcher and all of it's assets.
   */
  void shutdown();

}
