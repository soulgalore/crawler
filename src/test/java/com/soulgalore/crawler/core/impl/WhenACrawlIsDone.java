package com.soulgalore.crawler.core.impl;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.hasItem;
import static org.hamcrest.Matchers.is;
import static org.mockito.Matchers.anyObject;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import static org.mockito.Matchers.eq;

import java.io.IOException;
import java.util.HashMap;
import java.util.concurrent.Executors;

import org.apache.http.HttpStatus;
import org.junit.Before;
import org.junit.Test;

import com.soulgalore.crawler.core.Crawler;
import com.soulgalore.crawler.core.CrawlerResult;
import com.soulgalore.crawler.core.HTMLPageResponse;
import com.soulgalore.crawler.core.PageURL;
import com.soulgalore.crawler.core.HTMLPageResponseFetcher;
import com.soulgalore.crawler.test.TestFileHelper;

public class WhenACrawlIsDone {

	private Crawler crawler;

	@Before
	public void setUp() throws Exception {

		HTMLPageResponseFetcher fetcher = mock(HTMLPageResponseFetcher.class);

		// the first one is for the url verification
		// verification!
		when(fetcher.get((PageURL) anyObject(), eq(false))).thenReturn(
				getResponse("/crawler/crawler1.html",
						"http://soulislove.com/crawler/crawler1.html"));

		when(fetcher.get((PageURL) anyObject(), eq(true))).thenReturn(
				getResponse("/crawler/crawler1.html",
						"http://soulislove.com/crawler/crawler1.html"),
				getResponse("/crawler/crawler2.html",
						"http://soulislove.com/crawler/crawler2.html"),
				getResponse("/crawler/crawler3.html",
						"http://soulislove.com/crawler/crawler3.html"),
				getResponse("/crawler/crawler4.html",
						"http://soulislove.com/crawler/crawler4.html"),
				getResponse("/crawler/crawler5.html",
						"http://soulislove.com/crawler/crawler5.html"));

		crawler = new DefaultCrawler(fetcher,
				Executors.newSingleThreadExecutor(), new AhrefParser());
	}

	@Test
	public void oneLevelShouldBeFetched() {

		CrawlerResult result = crawler.getUrls("http://soulislove.com", 1, false);
		assertThat(result.getUrls().size(), is(1));
		assertThat(result.getNonWorkingUrls().size(), is(0));
		assertThat(result.getUrls(), hasItem(new PageURL(
				"http://soulislove.com/mypath/crawler2.html",
				"http://soulislove.com")));
	}

	@Test
	public void pathThatDoesntExistShouldNotBeFetched() {
		assertThat(
				crawler.getUrls("http://soulislove.com", "this/dont/exist", 4, false).getUrls().size(), is(0));

	}

	@Test
	public void specificPathsShouldAlwaysBeFetched() {
		CrawlerResult result = crawler.getUrls("http://soulislove.com",
				"/mypath/", 3, false);
		assertThat(result.getUrls().size(), is(3));
		assertThat(result.getNonWorkingUrls().size(), is(0));
	}

	@Test
	public void threeLevelsShouldBeFetched() {

		CrawlerResult result = crawler.getUrls("http://soulislove.com", 3, false);
		assertThat(result.getUrls().size(), is(10));
		assertThat(result.getNonWorkingUrls().size(), is(0));
		assertThat(result.getUrls(), hasItem(new PageURL(
				"http://soulislove.com/mypath/crawler2.html")));
		assertThat(result.getUrls(), hasItem(new PageURL(
				"http://soulislove.com/mypath/crawler3.html")));
		assertThat(result.getUrls(), hasItem(new PageURL(
				"http://soulislove.com/crawler4.html")));
		assertThat(result.getUrls(), hasItem(new PageURL(
				"http://soulislove.com/crawler5.html")));
		assertThat(result.getUrls(), hasItem(new PageURL(
				"http://soulislove.com/crawler6.html")));
		assertThat(result.getUrls(), hasItem(new PageURL(
				"http://soulislove.com/mypath/crawler7.html")));

	}

	@Test
	public void twoLevelsShouldBeFetched() {

		CrawlerResult result = crawler.getUrls("http://soulislove.com", 2, false);
		System.out.println(result.getUrls());
		assertThat(result.getUrls().size(), is(3));
		assertThat(result.getNonWorkingUrls().size(), is(0));
		assertThat(result.getUrls(), hasItem(new PageURL(
				"http://soulislove.com/mypath/crawler2.html")));
		assertThat(result.getUrls(), hasItem(new PageURL(
				"http://soulislove.com/mypath/crawler3.html")));
		assertThat(result.getUrls(), hasItem(new PageURL(
				"http://soulislove.com/crawler4.html")));
	}

	private HTMLPageResponse getResponse(String file, String url)
			throws IOException {
		String html = TestFileHelper.fetchFileFromClasspathAsString(file);

		return new HTMLPageResponse(new PageURL(url), HttpStatus.SC_OK,
				new HashMap<String, String>(), html, "UTF-8", html.length());

	}

}
