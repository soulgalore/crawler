package com.soulgalore.crawler;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.endsWith;
import static org.junit.Assert.assertTrue;

import java.io.UnsupportedEncodingException;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URISyntaxException;

import org.junit.Test;

import com.soulgalore.crawler.core.PageURL;


public class WhenAPageURLIsCreated {

	@Test
	public void hasAHost() {
		String theHost = "www.soulgalore.com";
		PageURL asset = new PageURL("http://" + theHost);
		assertThat(asset.getHost(), is(theHost));

	}

	
	@Test
	public void hasAReferer() {
		String url = "http://www.soulgalore.com/test/";
		String referer = "http://www.soulgalore.com/";
		PageURL asset = new PageURL(url, referer);
		assertThat(asset.getReferer(), is(referer));

	}
	
	@Test
	public void hasAUrl() {
		String url = "http://www.soulgalore.com/page/";
		PageURL asset = new PageURL(url);
		assertThat(asset.getUrl(), is(url));

	}
	
	@Test
	public void hasAUri() throws URISyntaxException {
		String url = "http://www.soulgalore.com/page/";
		PageURL asset = new PageURL(url);
		
		assertThat(asset.getUri(), is(new URI(url)));

	}
	
	@Test
	public void theHashIsStrippedFromTheUri() {
		String url = "http://www.soulgalore.com/page/#special";
		PageURL asset = new PageURL(url);
		
		String uri = asset.getUri().toString();
		assertThat("Assert that the # part of the uri is removed",uri,endsWith("/") );

	}
	
	@Test
	public void hashTagShouldBeignoredFromEquals() {
		PageURL asset = new PageURL("http://www.soulislove.com/");
		PageURL asset2 = new PageURL("http://www.soulislove.com/#respond");
		
		assertTrue("The assets shoudln't depend on hashtag", asset.equals(asset2));
	}
	
	@Test
	public void urlsWithWrongSyntaxShouldNotBeValid()
			throws MalformedURLException, URISyntaxException,
			UnsupportedEncodingException {

		PageURL url = new PageURL("http://www.soulgalore.com");
		assertThat(url.isWrongSyntax(), is(false));

		// faulty url
		url = new PageURL("apa");
		assertThat(
				"The url has no wrong syntax, but it should:" + url.getUrl(),
				url.isWrongSyntax(), is(true));

		
		// trying out a couple of special urls
		url = new PageURL(
				"http://b.scorecardresearch.com/b?c1=2&c2=6035308&c3=&c4=&c5=&c6=&c15=&cv=1.3&cj=1");
		assertThat(url.isWrongSyntax(), is(false));

		url = new PageURL(
				"http://fonts.googleapis.com/css?family=Droid+Sans|Vollkorn:bold|Merienda+One");
		assertThat(url.isWrongSyntax(), is(false));

		url = new PageURL(
				"http://adserver.adtech.de/addyn|3.0|506|3067392|407017|-1|ADTECH;loc=100;key=;grp=96905;asfunc=1;cookie=info;size=1x1;misc=1326643451844");
		assertThat(url.isWrongSyntax(), is(false));

	}

}
