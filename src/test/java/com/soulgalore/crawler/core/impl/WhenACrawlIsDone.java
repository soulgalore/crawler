package com.soulgalore.crawler.core.impl;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.hasItem;
import static org.hamcrest.Matchers.is;
import static org.mockito.Matchers.anyObject;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import static org.mockito.Matchers.eq;

import java.io.IOException;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.Executors;

import org.apache.http.HttpStatus;
import org.junit.Before;
import org.junit.Test;

import com.soulgalore.crawler.core.Crawler;
import com.soulgalore.crawler.core.CrawlerConfiguration;
import com.soulgalore.crawler.core.CrawlerResult;
import com.soulgalore.crawler.core.HTMLPageResponse;
import com.soulgalore.crawler.core.CrawlerURL;
import com.soulgalore.crawler.core.HTMLPageResponseFetcher;
import com.soulgalore.crawler.test.TestFileHelper;

public class WhenACrawlIsDone {

	private Crawler crawler;

	@Before
	public void setUp() throws Exception {

		HTMLPageResponseFetcher fetcher = mock(HTMLPageResponseFetcher.class);

		// the first one is for the url verification
				// verification!
		when(fetcher.get((CrawlerURL) anyObject(), eq(true), (Map<String, String>) anyObject(), eq(true) )).thenReturn(
				getResponse("/crawler/crawler1.html",
						"http://soulislove.com/crawler/crawler1.html"));
		
		when(fetcher.get((CrawlerURL) anyObject(), eq(true), (Map<String, String>) anyObject(), eq(false) )).thenReturn(
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
				Executors.newSingleThreadExecutor(), new AhrefPageURLParser());
	}

	@Test
	public void oneLevelShouldBeFetched() {
		CrawlerConfiguration conf = CrawlerConfiguration.builder()
				.setStartUrl("http://soulislove.com").setVerifyUrls(false)
				.build();
		CrawlerResult result = crawler.getUrls(conf);
		assertThat(result.getUrls().size(), is(1));
		assertThat(result.getNonWorkingUrls().size(), is(0));
		assertThat(result.getUrls(), hasItem(new CrawlerURL(
				"http://soulislove.com/mypath/crawler2.html",
				"http://soulislove.com")));
	}

	@Test
	public void pathThatDoesntExistShouldNotBeFetched() {

		CrawlerConfiguration conf = CrawlerConfiguration.builder()
				.setStartUrl("http://soulislove.com")
				.setOnlyOnPath("this/dont/exist").setMaxLevels(4)
				.setVerifyUrls(false).build();

		assertThat(crawler.getUrls(conf).getUrls().size(), is(0));

	}

	@Test
	public void specificPathsShouldAlwaysBeFetched() {

		CrawlerConfiguration conf = CrawlerConfiguration.builder()
				.setStartUrl("http://soulislove.com").setOnlyOnPath("/mypath/")
				.setMaxLevels(3).setVerifyUrls(false).build();

		CrawlerResult result = crawler.getUrls(conf);
		assertThat(result.getUrls().size(), is(3));
		assertThat(result.getNonWorkingUrls().size(), is(0));
	}

	@Test
	public void specificNoPathShouldNotBeFetched() {

		CrawlerConfiguration conf = CrawlerConfiguration.builder()
				.setStartUrl("http://soulislove.com").setNotOnPath("/mypath/")
				.setMaxLevels(3).setVerifyUrls(false).build();

		CrawlerResult result = crawler.getUrls(conf);
		assertThat(result.getUrls().size(), is(0));
		assertThat(result.getNonWorkingUrls().size(), is(0));
	}

	@Test
	public void threeLevelsShouldBeFetched() {

		CrawlerConfiguration conf = CrawlerConfiguration.builder()
				.setStartUrl("http://soulislove.com").setMaxLevels(3)
				.setVerifyUrls(false).build();

		CrawlerResult result = crawler.getUrls(conf);
		assertThat(result.getUrls().size(), is(10));
		assertThat(result.getNonWorkingUrls().size(), is(0));
		assertThat(result.getUrls(), hasItem(new CrawlerURL(
				"http://soulislove.com/mypath/crawler2.html")));
		assertThat(result.getUrls(), hasItem(new CrawlerURL(
				"http://soulislove.com/mypath/crawler3.html")));
		assertThat(result.getUrls(), hasItem(new CrawlerURL(
				"http://soulislove.com/crawler4.html")));
		assertThat(result.getUrls(), hasItem(new CrawlerURL(
				"http://soulislove.com/crawler5.html")));
		assertThat(result.getUrls(), hasItem(new CrawlerURL(
				"http://soulislove.com/crawler6.html")));
		assertThat(result.getUrls(), hasItem(new CrawlerURL(
				"http://soulislove.com/mypath/crawler7.html")));

	}

	@Test
	public void twoLevelsShouldBeFetched() {

		CrawlerConfiguration conf = CrawlerConfiguration.builder()
				.setStartUrl("http://soulislove.com").setMaxLevels(2)
				.setVerifyUrls(false).build();
		CrawlerResult result = crawler.getUrls(conf);
		System.out.println(result.getUrls());
		assertThat(result.getUrls().size(), is(3));
		assertThat(result.getNonWorkingUrls().size(), is(0));
		assertThat(result.getUrls(), hasItem(new CrawlerURL(
				"http://soulislove.com/mypath/crawler2.html")));
		assertThat(result.getUrls(), hasItem(new CrawlerURL(
				"http://soulislove.com/mypath/crawler3.html")));
		assertThat(result.getUrls(), hasItem(new CrawlerURL(
				"http://soulislove.com/crawler4.html")));
	}

	private HTMLPageResponse getResponse(String file, String url)
			throws IOException {
		String html = TestFileHelper.fetchFileFromClasspathAsString(file);

		return new HTMLPageResponse(new CrawlerURL(url), HttpStatus.SC_OK,
				new HashMap<String, String>(), html, "UTF-8", html.length(),"text/html", 1283);

	}

}
