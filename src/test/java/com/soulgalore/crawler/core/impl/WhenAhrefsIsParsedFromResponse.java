package com.soulgalore.crawler.core.impl;

import static org.junit.Assert.*;
import static org.hamcrest.Matchers.is;


import java.io.IOException;
import java.util.HashMap;
import java.util.Set;

import org.apache.http.HttpStatus;
import org.junit.Test;

import com.soulgalore.crawler.core.HTMLPageResponse;
import com.soulgalore.crawler.core.CrawlerURL;
import com.soulgalore.crawler.test.TestFileHelper;


public class WhenAhrefsIsParsedFromResponse {

	@Test
	public void allLinksShouldBeFetched() throws IOException {
		String html = TestFileHelper.fetchFileFromClasspathAsString("/crawler/pageToParse.html");
		CrawlerURL pageUrl = new CrawlerURL("http://soulislove.com");
		
		HTMLPageResponse response = new HTMLPageResponse(pageUrl, HttpStatus.SC_OK, new HashMap<String,String>(), html, "UTF-8", html.length(),"text/html",211);
		AhrefPageURLParser parser = new AhrefPageURLParser();
		Set<CrawlerURL> urls = parser.get(response);
		assertThat(urls.size(), is(10));
	}

	@Test
	public void mailToLinksShouldNotBeFetched() throws IOException {
		String html = TestFileHelper.fetchFileFromClasspathAsString("/crawler/pageWithMailToLinks.html");
		CrawlerURL pageUrl = new CrawlerURL("http://soulislove.com");
		
		HTMLPageResponse response = new HTMLPageResponse(pageUrl, HttpStatus.SC_OK, new HashMap<String,String>(), html, "UTF-8", html.length(),"text/html",473);
		AhrefPageURLParser parser = new AhrefPageURLParser();
		Set<CrawlerURL> urls = parser.get(response);
		assertThat(urls.size(), is(3));
	}
}
