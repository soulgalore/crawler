package com.soulgalore.crawler;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;

import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

import org.junit.Before;
import org.junit.Test;

import com.soulgalore.crawler.core.CrawlerResult;
import com.soulgalore.crawler.core.HTMLPageResponse;
import com.soulgalore.crawler.core.CrawlerURL;
import com.soulgalore.crawler.util.StatusCode;

public class WhenACrawlerResultIsCreated {

	private static final String STARTPOINT = "http://www.google.com";
	private final Set<CrawlerURL> allUrls = new HashSet<CrawlerURL>();
	private final Set<HTMLPageResponse> nonWorkingUrls = new HashSet<HTMLPageResponse>();
	private final Set<HTMLPageResponse> verifiedUrls = new HashSet<HTMLPageResponse>();
	
	private CrawlerResult result;

	@Test
	public void nonWorkingURlsShouldContain() {
		assertThat(result.getNonWorkingUrls().size(), is(1));
	}

	@Before
	public void setup() {
		allUrls.add(new CrawlerURL("http://www.facebook.com"));
		allUrls.add(new CrawlerURL("http://www.twitter.com"));
		nonWorkingUrls.add(new HTMLPageResponse(new CrawlerURL("http://www.facebook2.com"), StatusCode.SC_SERVER_RESPONSE_TIMEOUT.getCode(), Collections.EMPTY_MAP, "", "", 0,"",1) );
		verifiedUrls.add(new HTMLPageResponse(new CrawlerURL("http://www.facebook.com"),200, Collections.EMPTY_MAP, "", "", 0,"",21212) );
		verifiedUrls.add(new HTMLPageResponse(new CrawlerURL("http://www.twitter.com"),200, Collections.EMPTY_MAP, "", "", 0,"",212) );
		result = new CrawlerResult(STARTPOINT, allUrls, verifiedUrls, nonWorkingUrls);
	}

	@Test
	public void startingPoingShouldBeRight() {
		assertThat(result.getTheStartPoint(), is(STARTPOINT));
	}

	@Test
	public void workingUrlsShouldContain() {
		assertThat(result.getUrls().size(), is(2));
	}

}
