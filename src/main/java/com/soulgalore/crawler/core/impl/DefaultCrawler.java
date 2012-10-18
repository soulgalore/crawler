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
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import java.util.Set;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Future;

import org.apache.http.HttpStatus;

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

	private final HTMLPageResponseFetcher responseFetcher;
	private final ExecutorService service;
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
		service = theService;
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
	public CrawlerResult getUrls(String startUrl, int maxLevels,
			boolean verifyUrls) {
		return getUrls(startUrl, "", maxLevels, verifyUrls);
	}

	
	/**
	 * Get the urls.
	 * 
	 * @param startUrl
	 *            the first url to start crawl
	 * @param maxLevels
	 *            the maximum number of levels to crawl, the max number is
	 *            {@link #MAX_CRAWL_LEVELS}
	 * @param don't collect/follow urls that contains this text in the url       
	 * 
	 * @return the result of the crawl
	 */
	public CrawlerResult getUrls(String startUrl, int maxLevels, String notOnPath,
			boolean verifyUrls) {
		return getUrls(startUrl, "", maxLevels, notOnPath, verifyUrls );
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
	public CrawlerResult getUrls(String startUrl, String onlyOnPath,
			int maxLevels,  boolean verifyUrls) {
		return getUrls(startUrl,onlyOnPath, maxLevels,"", verifyUrls);
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
	 * @param don't collect/follow urls that contains this text in the url               
	 * @return the result of the crawl
	 */
	public CrawlerResult getUrls(String startUrl, String onlyOnPath,
			int maxLevels, String notOnPath, boolean verifyUrls) {

		final PageURL pageUrl = verifyInput(startUrl, onlyOnPath);

		int level = 0;

		final Set<PageURL> allUrls = new LinkedHashSet<PageURL>();
		final Set<PageURL> verifiedUrls = new LinkedHashSet<PageURL>();
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
					verifiedUrls, host, onlyOnPath, notOnPath);
			level++;
		}

		if (verifyUrls)
			verifyUrls(allUrls, verifiedUrls, nonWorkingUrls);

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
	 * @param don't collect/follow urls that contains this text in the url                
	 * @return the next level of links that we should fetch
	 */
	protected Set<PageURL> fetchNextLevelLinks(
			Map<Future<HTMLPageResponse>, PageURL> responses,
			Set<PageURL> allUrls, Set<PageURL> nonWorkingUrls,
			Set<PageURL> verifiedUrls, String host, String onlyOnPath, String notOnPath) {

		final Set<PageURL> nextLevel = new LinkedHashSet<PageURL>();

		final Iterator<Entry<Future<HTMLPageResponse>, PageURL>> it = responses
				.entrySet().iterator();

		while (it.hasNext()) {

			final Entry<Future<HTMLPageResponse>, PageURL> entry = it.next();

			try {

				final HTMLPageResponse response = entry.getKey().get();
				if (HttpStatus.SC_OK == response.getResponseCode()) {
					// we know that this links work
					verifiedUrls.add(entry.getValue());
					final Set<PageURL> allLinks = parser.get(response);

					for (PageURL link : allLinks) {
						// only add if it is the same host
						if (host.equals(link.getHost())
								&& link.getUrl().contains(onlyOnPath)
								&& (notOnPath.equals("") ? true : (!link
										.getUrl().contains(notOnPath)))) {
							if (!allUrls.contains(link)) {
								nextLevel.add(link);
								allUrls.add(link);
							}
						}
					}
				} else {
					allUrls.remove(entry.getValue());
					nonWorkingUrls.add(entry.getValue());
				}

			} catch (InterruptedException | ExecutionException e) {
				nonWorkingUrls.add(entry.getValue());
			}
		}
		return nextLevel;
	}

	/**
	 * Verify that all urls in allUrls returns 200. If not, they will be removed
	 * from that set and instead added to the nonworking list.
	 * 
	 * @param allUrls
	 *            all the links that has been fetched
	 * @param nonWorkingUrls
	 *            links that are not working
	 */
	private void verifyUrls(Set<PageURL> allUrls, Set<PageURL> verifiedUrls,
			Set<PageURL> nonWorkingUrls) {

		// Only test the once that hasn't been verified
		Set<PageURL> urlsThatNeedsVerification = new LinkedHashSet<PageURL>(
				allUrls);
		urlsThatNeedsVerification.removeAll(verifiedUrls);

		final Set<Callable<HTMLPageResponse>> tasks = new HashSet<Callable<HTMLPageResponse>>(
				urlsThatNeedsVerification.size());

		for (PageURL testURL : urlsThatNeedsVerification) {
			tasks.add(new HTMLPageResponseCallable(testURL, responseFetcher,
					true));
		}

		try {
			// wait for all urls to verify
			List<Future<HTMLPageResponse>> responses = service.invokeAll(tasks);

			for (Future<HTMLPageResponse> future : responses) {
				if (!future.isCancelled()) {
					HTMLPageResponse response = future.get();
					if (response.getResponseCode() == HttpStatus.SC_OK)
						urlsThatNeedsVerification.remove(response.getPageUrl());
				}

			}

		} catch (InterruptedException | ExecutionException e1) {
			// TODO add some logging
			e1.printStackTrace();
		}

		// The one kept in urlsThatNeedsVerification are not working urls ...
		allUrls.removeAll(urlsThatNeedsVerification);
		nonWorkingUrls.addAll(urlsThatNeedsVerification);
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
