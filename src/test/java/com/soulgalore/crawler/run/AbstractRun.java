package com.soulgalore.crawler.run;

import org.apache.commons.cli.ParseException;

public abstract class AbstractRun {


	/**
	 * Fetch the test that class that are under test
	 * 
	 * @param args
	 *            the args that should be used
	 * @return the test that should run
	 * @throws ParseException
	 */
	public abstract AbstractRunner fetchRunner(String[] args)
			throws ParseException;

	/**
	 * Get the default arg for this test. For a crawler it can be the url etc.
	 * 
	 * @return the default args needed.
	 */
	public abstract String getDefaultArg();

}
