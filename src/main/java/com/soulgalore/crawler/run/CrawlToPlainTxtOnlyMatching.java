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

import org.apache.commons.cli.Option;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.ParseException;

import com.google.inject.Guice;
import com.google.inject.Injector;
import com.soulgalore.crawler.core.Crawler;
import com.soulgalore.crawler.core.CrawlerResult;
import com.soulgalore.crawler.core.HTMLPageResponse;
import com.soulgalore.crawler.guice.CrawlModule;

/**
 * Crawl and print urls that contains specific keyword in the HTML body.
 * 
 */
public class CrawlToPlainTxtOnlyMatching extends AbstractCrawl {

  private final String keyword;

  CrawlToPlainTxtOnlyMatching(String[] args) throws ParseException {
    super(args);
    keyword = getLine().getOptionValue("keyword");

  }

  /**
   * Run.
   * 
   * @param args the args
   */
  public static void main(String[] args) {

    try {

      final CrawlToPlainTxtOnlyMatching crawl = new CrawlToPlainTxtOnlyMatching(args);
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
    for (HTMLPageResponse response : result.getVerifiedURLResponses()) {

      if (response.getBody().toString().contains(keyword)) {
        System.out.println(response.getUrl());
      }
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

    final Option filenameOption =
        new Option("k", "the keyword to search for in the page  [required]");
    filenameOption.setArgName("KEYWORD");
    filenameOption.setLongOpt("keyword");
    filenameOption.setRequired(true);
    filenameOption.setArgs(1);

    options.addOption(filenameOption);

    return options;

  }
}
