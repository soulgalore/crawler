package com.soulgalore.crawler.run;

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
import java.io.File;
import java.io.IOException;
import java.nio.file.FileSystems;
import java.nio.file.Files;
import java.nio.file.StandardOpenOption;

import org.apache.commons.cli.Option;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.ParseException;

import com.google.inject.Guice;
import com.google.inject.Injector;
import com.soulgalore.crawler.core.Crawler;
import com.soulgalore.crawler.core.CrawlerResult;
import com.soulgalore.crawler.core.PageURL;
import com.soulgalore.crawler.guice.CrawlModule;

/**
 * Crawl and output a CSV file.
 *
 */
public class CrawlToCsv extends AbstractCrawl {

	/**
	 * The default file name of the result.
	 */
	public static final String DEFAULT_FILENAME = "result.csv";

	private final String fileName;

	CrawlToCsv(String[] args) throws ParseException {
		super(args);
		fileName = getLine().getOptionValue("filename", DEFAULT_FILENAME);

	}

	/**
	 * Run.
	 * 
	 * @param args
	 *            the args
	 */
	public static void main(String[] args) {

		try {

			final CrawlToCsv crawl = new CrawlToCsv(args);
			crawl.crawl();

		} catch (ParseException e) {
			System.out.print(e.getMessage());
		} catch (IllegalArgumentException e) {
			System.out.println(e.getMessage());
		}

	}

	private void crawl() {
		final Injector injector = Guice.createInjector(new CrawlModule());
		final Crawler crawler = injector.getInstance(Crawler.class);

		final CrawlerResult result = crawler.getUrls(getConfiguration());
	
		final StringBuilder builder = new StringBuilder();
		builder.append("URL,parent\n");

		for (PageURL workingUrl : result.getUrls()) {
			builder.append(workingUrl.getUrl()).append(",")
					.append(workingUrl.getReferer()).append("\n");

		}

		if (result.getNonWorkingUrls().size() > 0)
			builder.append("URL non working,parent\n");

		for (PageURL nonWorkingUrl : result.getNonWorkingUrls()) {
			builder.append(nonWorkingUrl.getUrl()).append(",")
					.append(nonWorkingUrl.getReferer()).append("\n");
		}

		System.out.println("Start storing file " + fileName);

		try {
			Files.write(FileSystems.getDefault().getPath(fileName),
					builder.toString().getBytes("UTF-8"),
					StandardOpenOption.CREATE);
		} catch (IOException e) {
			System.err.println(e);
		}

		crawler.shutdown();
	}

	/**
	 * Get the options.
	 * 
	 * @return the specific CrawlToCsv options
	 */
	@Override
	protected Options getOptions() {
		final Options options = super.getOptions();

		final Option filenameOption = new Option("f",
				"the name of the csv output file, default name is "
						+ DEFAULT_FILENAME + " [optional]");
		filenameOption.setArgName("FILENAME");
		filenameOption.setLongOpt("filename");
		filenameOption.setRequired(false);
		filenameOption.setArgs(1);

		options.addOption(filenameOption);

		return options;

	}
}
