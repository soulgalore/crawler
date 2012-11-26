package com.soulgalore.crawler.run;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;

import org.apache.commons.cli.ParseException;
import org.junit.Test;

public class WhenCrawlToPlainTxtRun extends AbstractRun {

	private static final String IN_PARAMETER_URL = "http://soulislove.com";

	@Override
	public AbstractRunner fetchRunner(String[] args) throws ParseException {
		return new CrawlToSystemOut(args);
	}

	@Override
	public String getDefaultArg() {
		return "-u=" + IN_PARAMETER_URL;
	}

	@Test
	public void levelParameterShouldBeFetched() throws ParseException {
		int inParameterLevel = 2;
		String[] args = { getDefaultArg(), "-l=" + inParameterLevel };
		CrawlToSystemOut test = (CrawlToSystemOut) fetchRunner(args);
		assertThat(test.getConfiguration().getMaxLevels(), is(inParameterLevel));
	}

	@Test
	public void pathParameterShouldBeFetched() throws ParseException {
		String inParameterPath = "/cool/path/";
		String[] args = { getDefaultArg(), "-p=" + inParameterPath };
		CrawlToSystemOut test = (CrawlToSystemOut) fetchRunner(args);
		assertThat(test.getConfiguration().getOnlyOnPath(), is(inParameterPath));
	}

	@Test
	public void urlParameterShouldBeFetched() throws ParseException {
		String[] args = { getDefaultArg() };
		CrawlToSystemOut test = (CrawlToSystemOut) fetchRunner(args);
		assertThat(test.getConfiguration().getStartUrl(), is(IN_PARAMETER_URL));
	}

}
