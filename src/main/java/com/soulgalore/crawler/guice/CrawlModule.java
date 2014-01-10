/******************************************************
 * Web crawler
 * 
 * 
 * Copyright (C) 2012 by Peter Hedenskog (http://peterhedenskog.com)
 * 
 ****************************************************** 
 * 
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except
 * in compliance with the License. You may obtain a copy of the License at
 * 
 * 
 * http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software distributed under the License
 * is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express
 * or implied. See the License for the specific language governing permissions and limitations under
 * the License.
 * 
 ******************************************************* 
 */
package com.soulgalore.crawler.guice;

import java.util.concurrent.ExecutorService;

import org.apache.http.client.HttpClient;

import com.soulgalore.crawler.core.PageURLParser;
import com.soulgalore.crawler.core.Crawler;
import com.soulgalore.crawler.core.HTMLPageResponseFetcher;
import com.soulgalore.crawler.core.assets.AssetFetcher;
import com.soulgalore.crawler.core.assets.AssetsParser;
import com.soulgalore.crawler.core.assets.AssetsVerifier;
import com.soulgalore.crawler.core.assets.impl.DefaultAssetsParser;
import com.soulgalore.crawler.core.assets.impl.DefaultAssetsVerifier;
import com.soulgalore.crawler.core.assets.impl.HTTPClientAssetFetcher;
import com.soulgalore.crawler.core.impl.DefaultCrawler;
import com.soulgalore.crawler.core.impl.AhrefPageURLParser;
import com.soulgalore.crawler.core.impl.HTTPClientResponseFetcher;


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
    bind(HTMLPageResponseFetcher.class).to(HTTPClientResponseFetcher.class);
    bind(HttpClient.class).toProvider(HttpClientProvider.class);
    bind(PageURLParser.class).to(AhrefPageURLParser.class);

    // For parsing assets
    bind(AssetsParser.class).to(DefaultAssetsParser.class);
    bind(AssetsVerifier.class).to(DefaultAssetsVerifier.class);
    bind(AssetFetcher.class).to(HTTPClientAssetFetcher.class);

  }
}
