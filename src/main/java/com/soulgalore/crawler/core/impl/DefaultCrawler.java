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

import java.util.Collections;
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
import com.soulgalore.crawler.core.CrawlerConfiguration;
import com.soulgalore.crawler.core.PageURLParser;
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
	private final PageURLParser parser;

	
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
			ExecutorService theService, PageURLParser theParser) {
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
	 * @param configuration
	 *            how to perform the crawl
	 * @return the result of the crawl
	 */
	public CrawlerResult getUrls(CrawlerConfiguration configuration) {

		final Map<String,String> requestHeaders = configuration.getRequestHeadersMap();
		final HTMLPageResponse resp = verifyInput(configuration.getStartUrl(),
				configuration.getOnlyOnPath(), requestHeaders);

		int level = 0;

		final Set<PageURL> allUrls = new LinkedHashSet<PageURL>();
		final Set<HTMLPageResponse> verifiedUrls = new LinkedHashSet<HTMLPageResponse>();
		final Set<HTMLPageResponse> nonWorkingResponses = new LinkedHashSet<HTMLPageResponse>();
		
		verifiedUrls.add(resp);
		
		final String host = resp.getPageUrl().getHost();

		if (configuration.getMaxLevels() > 0) {

			// set the start url
			Set<PageURL> nextToFetch = new LinkedHashSet<PageURL>();
			nextToFetch.add(resp.getPageUrl());

			while (level < configuration.getMaxLevels()) {

				final Map<Future<HTMLPageResponse>, PageURL> futures = new HashMap<Future<HTMLPageResponse>, PageURL>(
						nextToFetch.size());

				for (PageURL testURL : nextToFetch) {
					futures.put(service.submit(new HTMLPageResponseCallable(
							testURL, responseFetcher, true, requestHeaders)), testURL);
				}

				nextToFetch = fetchNextLevelLinks(futures, allUrls,
						nonWorkingResponses, verifiedUrls, host,
						configuration.getOnlyOnPath(),
						configuration.getNotOnPath());
				level++;
			}
		} else {
			allUrls.add(resp.getPageUrl());
		}

		if (configuration.isVerifyUrls())
			verifyUrls(allUrls, verifiedUrls, nonWorkingResponses, requestHeaders);

		Set<PageURL> workingUrls = new LinkedHashSet<PageURL>();
		for (HTMLPageResponse workingResponses : verifiedUrls) {
			workingUrls.add(workingResponses.getPageUrl());
		}
		
		return new CrawlerResult(configuration.getStartUrl(),
				configuration.isVerifyUrls()? workingUrls: allUrls, verifiedUrls,
				nonWorkingResponses);

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
	 * @param don
	 *            't collect/follow urls that contains this text in the url
	 * @return the next level of links that we should fetch
	 */
	protected Set<PageURL> fetchNextLevelLinks(
			Map<Future<HTMLPageResponse>, PageURL> responses,
			Set<PageURL> allUrls, Set<HTMLPageResponse> nonWorkingUrls,
			Set<HTMLPageResponse> verifiedUrls, String host, String onlyOnPath,
			String notOnPath) {

		final Set<PageURL> nextLevel = new LinkedHashSet<PageURL>();

		final Iterator<Entry<Future<HTMLPageResponse>, PageURL>> it = responses
				.entrySet().iterator();

		while (it.hasNext()) {

			final Entry<Future<HTMLPageResponse>, PageURL> entry = it.next();

			try {

				final HTMLPageResponse response = entry.getKey().get();
				if (HttpStatus.SC_OK == response.getResponseCode()) {
					// we know that this links work
					verifiedUrls.add(response);
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
					nonWorkingUrls.add(response);
				}

			} catch (InterruptedException e) {
				nonWorkingUrls.add(new HTMLPageResponse(entry.getValue(),
						StatusCode.SC_SERVER_RESPONSE_UNKNOWN.getCode(), Collections
								.<String, String> emptyMap(), "", "", 0,"",-1));
			} catch (ExecutionException e) {
				nonWorkingUrls.add(new HTMLPageResponse(entry.getValue(),
						StatusCode.SC_SERVER_RESPONSE_UNKNOWN.getCode(), Collections
								.<String, String> emptyMap(), "", "", 0,"",-1));
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
	private void verifyUrls(Set<PageURL> allUrls, Set<HTMLPageResponse> verifiedUrls,
			Set<HTMLPageResponse> nonWorkingUrls, Map<String,String> requestHeaders) {

		Set<PageURL> urlsThatNeedsVerification = new LinkedHashSet<PageURL>(
				allUrls);
		
		urlsThatNeedsVerification.removeAll(verifiedUrls);

		final Set<Callable<HTMLPageResponse>> tasks = new HashSet<Callable<HTMLPageResponse>>(
				urlsThatNeedsVerification.size());

		for (PageURL testURL : urlsThatNeedsVerification) {
			tasks.add(new HTMLPageResponseCallable(testURL, responseFetcher,
					true,requestHeaders ));
		}

		try {
			// wait for all urls to verify
			List<Future<HTMLPageResponse>> responses = service.invokeAll(tasks);

			for (Future<HTMLPageResponse> future : responses) {
				if (!future.isCancelled()) {
					HTMLPageResponse response = future.get();
					if (response.getResponseCode() == HttpStatus.SC_OK && response.getResponseType().indexOf("html")>0) {						
						// remove, way of catching interrupted / execution e
						urlsThatNeedsVerification.remove(response.getPageUrl());
						verifiedUrls.add(response);
					}
					else if (response.getResponseCode() == HttpStatus.SC_OK) {
						urlsThatNeedsVerification.remove(response.getPageUrl());
					}
					else nonWorkingUrls.add(response);
				}
			}

		} catch (InterruptedException e1) {
			// TODO add some logging
			e1.printStackTrace();
		} catch (ExecutionException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		// TODO: We can have a delta here if the exception occur
		
	}

	private HTMLPageResponse fetchOnePage(PageURL url, Map<String,String> requestHeaders) {
		return responseFetcher.get(url, true, requestHeaders);

	}

	private HTMLPageResponse verifyInput(String startUrl, String onlyOnPath, Map<String,String> requestHeaders) {

		final PageURL pageUrl = new PageURL(startUrl);

		if (pageUrl.isWrongSyntax())
			throw new IllegalArgumentException("The url " + startUrl
					+ " isn't a valid url ");

		// verify that the first url is reachable
		final HTMLPageResponse resp = fetchOnePage(pageUrl, requestHeaders);

		if (!StatusCode.isResponseCodeOk(resp.getResponseCode()))
			throw new IllegalArgumentException("The start url: " + startUrl
					+ " couldn't be fetched, response code "
					+ StatusCode.toFriendlyName(resp.getResponseCode()));
		return resp;
	}

}
