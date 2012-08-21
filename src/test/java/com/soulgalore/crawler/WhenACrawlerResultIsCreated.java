package com.soulgalore.crawler;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;

import java.util.HashSet;
import java.util.Set;

import org.junit.Before;
import org.junit.Test;

import com.soulgalore.crawler.core.CrawlerResult;
import com.soulgalore.crawler.core.PageURL;

public class WhenACrawlerResultIsCreated {

	private static final String STARTPOINT = "http://www.google.com";
	private final Set<PageURL> workingUrls = new HashSet<PageURL>();
	private final Set<PageURL> nonWorkingUrls = new HashSet<PageURL>();

	private CrawlerResult result;

	@Test
	public void nonWorkingURlsShouldContain() {
		assertThat(result.getNonWorkingUrls().size(), is(1));
	}

	@Before
	public void setup() {
		workingUrls.add(new PageURL("http://www.facebook.com"));
		workingUrls.add(new PageURL("http://www.twitter.com"));
		nonWorkingUrls.add(new PageURL("http://www.facebook2.com"));
		result = new CrawlerResult(STARTPOINT, workingUrls, nonWorkingUrls);
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
