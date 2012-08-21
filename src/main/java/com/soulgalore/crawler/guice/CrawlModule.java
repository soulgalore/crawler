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
package com.soulgalore.crawler.guice;

import java.util.concurrent.ExecutorService;

import org.apache.http.client.HttpClient;

import com.soulgalore.crawler.core.Parser;
import com.soulgalore.crawler.core.Crawler;
import com.soulgalore.crawler.core.HTMLPageResponseFetcher;
import com.soulgalore.crawler.core.impl.DefaultCrawler;
import com.soulgalore.crawler.core.impl.AhrefParser;
import com.soulgalore.crawler.core.impl.HttpClientResponseFetcher;


/**
 * Module for a crawl.
 *
 */
public class CrawlModule extends AbstractPropertiesModule {

	
	/**
	 * Bind the classes.
	 */
	@Override
	protected void configure() {
		super.configure();
		bind(Crawler.class).to(DefaultCrawler.class);
		bind(ExecutorService.class).toProvider(ExecutorServiceProvider.class);
		bind(HTMLPageResponseFetcher.class).to(HttpClientResponseFetcher.class);
		bind(HttpClient.class).toProvider(HttpClientProvider.class);
		bind(Parser.class).to(AhrefParser.class);

	}
}
