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
package com.soulgalore.crawler.run;

import org.apache.commons.cli.ParseException;

import com.google.inject.Guice;
import com.google.inject.Injector;
import com.soulgalore.crawler.core.Crawler;
import com.soulgalore.crawler.core.CrawlerResult;
import com.soulgalore.crawler.core.CrawlerURL;
import com.soulgalore.crawler.guice.CrawlModule;

public class CrawlToSystemOut extends AbstractCrawl {

  CrawlToSystemOut(String[] args) throws ParseException {
    super(args);

  }

  /**
   * Run.
   * 
   * @param args the args
   */
  public static void main(String[] args) {

    try {
      final CrawlToSystemOut crawl = new CrawlToSystemOut(args);
      crawl.crawl();

    } catch (ParseException e) {
      System.err.print(e.getMessage());
    } catch (IllegalArgumentException e) {
      System.err.println(e.getMessage());
    }

  }

  private void crawl() {
    final Injector injector = Guice.createInjector(new CrawlModule());
    final Crawler crawler = injector.getInstance(Crawler.class);

    final CrawlerResult result = crawler.getUrls(getConfiguration());


    for (CrawlerURL workingUrls : result.getUrls()) {
      System.out.println(workingUrls.getUrl());
    }


    crawler.shutdown();
  }
}
