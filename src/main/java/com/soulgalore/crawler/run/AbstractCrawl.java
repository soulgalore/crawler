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
package com.soulgalore.crawler.run;

import org.apache.commons.cli.Option;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.ParseException;

import com.soulgalore.crawler.core.CrawlerConfiguration;

/**
 * Abstract crawl class, used to setup default args options.
 * 
 * 
 */
public abstract class AbstractCrawl extends AbstractRunner {

	private static final String URL = "url";
	private static final String LEVEL = "level";
	private static final String FOLLOW_PATH = "followPath";
	private static final String NO_FOLLOW_PATH = "notFollowPath";
	private static final String VERIFY = "verify";
	private final String url;
	private final int level;
	private final String path;
	private final String noPath;
	private final boolean shouldVerify;
	private final CrawlerConfiguration configuration = new CrawlerConfiguration();
	

	/**
	 * Create a crawl object, will fetch the args that is needed.
	 * 
	 * @param args
	 *            containing needed args
	 * @throws ParseException
	 *             if the input parameter couldn't be parsed
	 */
	public AbstractCrawl(String[] args) throws ParseException {
		super(args);
		url = getLine().getOptionValue(URL);

		level = Integer.parseInt(getLine().getOptionValue(LEVEL, "1"));
		path = getLine().getOptionValue(FOLLOW_PATH, "");
		noPath = getLine().getOptionValue(NO_FOLLOW_PATH, "");
		shouldVerify = Boolean.parseBoolean(getLine().getOptionValue(VERIFY,"true"));
		
		configuration.setMaxLevels(level);
		configuration.setStartUrl(url);
		configuration.setNotOnPath(noPath);
		configuration.setOnlyOnPath(path);
		configuration.setVerifyUrls(shouldVerify);
	}

	/**
	 * Get the level of the crawl.
	 * 
	 * @return level
	 */
	protected int getLevel() {
		return level;
	}

	/**
	 * Get hold of the default options.
	 * 
	 * @return the options that needs to run a crawl
	 */
	@Override
	protected Options getOptions() {
		final Options options = super.getOptions();

		final Option urlOption = new Option(
				"u",
				"the page that is the startpoint of the crawl, examle http://mydomain.com/mypage");
		urlOption.setLongOpt(URL);
		urlOption.setArgName("URL");
		urlOption.setRequired(true);
		urlOption.setArgs(1);

		options.addOption(urlOption);

		final Option levelOption = new Option("l",
				"how deep the crawl should be done, default is " + 1
						+ " [optional]");
		levelOption.setArgName("LEVEL");
		levelOption.setLongOpt(LEVEL);
		levelOption.setRequired(false);
		levelOption.setArgs(1);
		options.addOption(levelOption);

		final Option followOption = new Option("p",
				"stay on this path when crawling [optional]");
		followOption.setArgName("PATH");
		followOption.setLongOpt(FOLLOW_PATH);
		followOption.setRequired(false);
		followOption.setArgs(1);
		options.addOption(followOption);
		
		final Option noFollowOption = new Option("np",
				"no url:s on this path will be crawled [optional]");
		noFollowOption.setArgName("NOPATH");
		noFollowOption.setLongOpt(NO_FOLLOW_PATH);
		noFollowOption.setRequired(false);
		noFollowOption.setArgs(1);
		options.addOption(noFollowOption);
		
		final Option verifyOption = new Option("v",
				"verify that all links are returning 200, default is set to true [optional]");
		verifyOption.setArgName("VERIFY");
		verifyOption.setLongOpt(VERIFY);
		verifyOption.setRequired(false);
		verifyOption.setArgs(1);
		options.addOption(verifyOption);
		
		return options;
	}

	/**
	 * Get the path the crawl will follow. If empty all paths on the domain
	 * will be followed.
	 * 
	 * @return the path
	 */
	protected String getPath() {
		return path;
	}

	/**
	 * Urls on this path will not be fetched. If empty all paths on the domain
	 * will be followed.
	 * 
	 * @return the path
	 */
	protected String getNoPath() {
		return noPath;
	}
	
	/**
	 * Get the url (startpage) this crawler will use.
	 * 
	 * @return the url
	 */
	protected String getUrl() {
		return url;
	}

	/**
	 * Get to know if all url:s should be verified or not.
	 * @return the verify
	 */
	protected boolean getShouldVerify() {
		return shouldVerify;
	}
	
	protected CrawlerConfiguration getConfiguration() {
		return configuration;
	}
	
}
