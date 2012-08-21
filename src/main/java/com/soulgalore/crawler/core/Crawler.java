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

/**
 * Interface of a web crawler.
 *
 */
public interface Crawler {

	/**
	 * Get the urls.
	 * 
	 * @param startUrl
	 *            the first url to start crawl
	 * @param maxLevels
	 *            the maximum number of levels to crawl, the max number is
	 *            {@link #MAX_CRAWL_LEVELS}
	 * 
	 * @return the result of the crawl
	 */
	CrawlerResult getUrls(String startUrl, int maxLevels);

	/**
	 * Get the urls.
	 * 
	 * @param startUrl
	 *            the first url to start crawl
	 * @param onlyOnPath
	 *            only fetch files that match the following path. If empty, all
	 *            will match.
	 * @param maxLevels
	 *            the maximum number of levels to crawl, the max number is
	 *            {@link #MAX_CRAWL_LEVELS}
	 * @return the result of the crawl
	 */
	CrawlerResult getUrls(String startUrl, String onlyOnPath,
			int maxLevels);

	/**
	 * Shutdown the crawler and all it's assets.
	 */
	void shutdown();

}
