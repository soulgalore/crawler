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

import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Map.Entry;

import java.util.Set;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Future;

import org.apache.http.HttpStatus;

import com.google.common.util.concurrent.ListeningExecutorService;
import com.google.common.util.concurrent.MoreExecutors;
import com.google.inject.Inject;
import com.soulgalore.crawler.core.Parser;
import com.soulgalore.crawler.core.Crawler;
import com.soulgalore.crawler.core.CrawlerResult;
import com.soulgalore.crawler.core.HTMLPageResponseCallable;
import com.soulgalore.crawler.core.PageURL;
import com.soulgalore.crawler.core.HTMLPageResponse;
import com.soulgalore.crawler.core.HTMLPageResponseFetcher;
import com.soulgalore.crawler.util.StatusCode;

/**
 * Crawl urls within the same domain.
 * 
 */
public class DefaultCrawler implements Crawler {

	/**
	 * The default number of levels to crawl.
	 */
	public static final int DEFAULT_CRAWL_LEVELS = 2;

	private final HTMLPageResponseFetcher responseFetcher;
	private final ListeningExecutorService service;
	private final Parser parser;

	/**
	 * Create a new crawler.
	 * 
	 * @param theResponseFetcher
	 *            the response fetcher to use.
	 * @param theService
	 *            the thread pool.
	 * @param theParser
	 *            the parser.
	 */
	@Inject
	public DefaultCrawler(HTMLPageResponseFetcher theResponseFetcher,
			ExecutorService theService, Parser theParser) {
		service = MoreExecutors.listeningDecorator(theService);
		responseFetcher = theResponseFetcher;
		parser = theParser;
	}

	/**
	 * Shutdown the crawler.
	 */
	public void shutdown() {
		if (service != null)
			service.shutdown();
		if (responseFetcher != null)
			responseFetcher.shutdown();
	}

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
	public CrawlerResult getUrls(String startUrl, int maxLevels) {
		return getUrls(startUrl, "", maxLevels);
	}

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
	public CrawlerResult getUrls(String startUrl, String onlyOnPath,
			int maxLevels) {

		final PageURL pageUrl = verifyInput(startUrl, onlyOnPath);

		int level = 0;

		final Set<PageURL> allUrls = new LinkedHashSet<PageURL>();
		final Set<PageURL> nonWorkingUrls = new LinkedHashSet<PageURL>();

		final String host = pageUrl.getHost();

		// set the start url
		Set<PageURL> nextToFetch = new LinkedHashSet<PageURL>();
		nextToFetch.add(pageUrl);

		while (level < maxLevels) {

			final Map<Future<HTMLPageResponse>, PageURL> futures = new HashMap<Future<HTMLPageResponse>, PageURL>(
					nextToFetch.size());

			for (PageURL testURL : nextToFetch) {
				futures.put(service.submit(new HTMLPageResponseCallable(
						testURL, responseFetcher, true)), testURL);
			}

			nextToFetch = fetchNextLevelLinks(futures, allUrls, nonWorkingUrls,
					host, onlyOnPath);
			level++;
		}
		return new CrawlerResult(startUrl, allUrls, nonWorkingUrls);

	}

	/**
	 * Fetch links to the next level of the crawl.
	 * 
	 * @param responses
	 *            holding bodys where we should fetch the links.
	 * @param allUrls
	 *            every url we have fetched so far
	 * @param nonWorkingUrls
	 *            the urls that didn't work to fetch
	 * @param host
	 *            the host we are working on
	 * @param onlyOnPath
	 *            only fetch files that match the following path. If empty, all
	 *            will match.
	 * @return the next level of links that we should fetch
	 */
	protected Set<PageURL> fetchNextLevelLinks(
			Map<Future<HTMLPageResponse>, PageURL> responses,
			Set<PageURL> allUrls, Set<PageURL> nonWorkingUrls, String host,
			String onlyOnPath) {

		final Set<PageURL> nextLevel = new LinkedHashSet<PageURL>();

		final Iterator<Entry<Future<HTMLPageResponse>, PageURL>> it = responses
				.entrySet().iterator();

		while (it.hasNext()) {

			final Entry<Future<HTMLPageResponse>, PageURL> entry = it.next();

			try {

				final HTMLPageResponse response = entry.getKey().get();
				if (HttpStatus.SC_OK == response.getResponseCode()) {
					final Set<PageURL> allLinks = parser.get(response);

					for (PageURL link : allLinks) {
						// only add if it is the same host
						if (host.equals(link.getHost())
								&& link.getUrl().contains(onlyOnPath)) {
							if (!allUrls.contains(link)) {
								nextLevel.add(link);
								allUrls.add(link);
							}
						}
					}
				}

			} catch (InterruptedException | ExecutionException e) {

				nonWorkingUrls.add(entry.getValue());
			}
		}
		return nextLevel;
	}


	private HTMLPageResponse fetchOnePage(PageURL url) {
		return responseFetcher.get(url, false);

	}

	private PageURL verifyInput(String startUrl, String onlyOnPath) {

		final PageURL pageUrl = new PageURL(startUrl);

		if (pageUrl.isWrongSyntax())
			throw new IllegalArgumentException("The url " + startUrl
					+ " isn't a valid url ");

		// verify that the first url is reachable
		final HTMLPageResponse resp = fetchOnePage(pageUrl);

		if (!StatusCode.getInstance().isResponseCodeOk(resp.getResponseCode()))
			throw new IllegalArgumentException("The start url: " + startUrl
					+ " couldn't be fetched, response code "
					+ resp.getResponseCode());
		return pageUrl;
	}

}
