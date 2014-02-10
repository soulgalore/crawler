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

import java.io.BufferedWriter;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.Writer;

import org.apache.commons.cli.ParseException;

import com.google.inject.Guice;
import com.google.inject.Injector;
import com.soulgalore.crawler.core.Crawler;
import com.soulgalore.crawler.core.CrawlerResult;
import com.soulgalore.crawler.core.assets.AssetResponse;
import com.soulgalore.crawler.core.assets.AssetsVerificationResult;
import com.soulgalore.crawler.core.assets.AssetsVerifier;
import com.soulgalore.crawler.guice.CrawlModule;
import com.soulgalore.crawler.util.StatusCode;

public class CrawlAndVerifyAssetsToCsv extends AbstractCrawl {

  /**
   * The default file name of the result.
   */
  public static final String DEFAULT_FILENAME = "errorassets.csv";

  private final String fileName;
  
  CrawlAndVerifyAssetsToCsv(String[] args) throws ParseException {
    super(args);
    fileName = getLine().getOptionValue("filename", DEFAULT_FILENAME);
  }

  /**
   * Run.
   * 
   * @param args the args
   */
  public static void main(String[] args) {

    try {
      final CrawlAndVerifyAssetsToCsv crawl = new CrawlAndVerifyAssetsToCsv(args);
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

    final StringBuilder builder = new StringBuilder();
    builder.append("URL,parent,error\n");
    
    final CrawlerResult result = crawler.getUrls(getConfiguration());
    System.out.println("Crawled  " + result.getVerifiedURLResponses().size() + " pages");

    System.out.println("Start verify assets ...");
    AssetsVerifier verifier = injector.getInstance(AssetsVerifier.class);
    AssetsVerificationResult assetsResult =
        verifier.verify(result.getVerifiedURLResponses(), getConfiguration());

    System.out.println(assetsResult.getWorkingAssets().size() + " assets is ok, "
        + assetsResult.getNonWorkingAssets().size() + " is not");

    for (AssetResponse resp : assetsResult.getNonWorkingAssets()) {
      builder.append(resp.getUrl()).append(",").append(resp.getReferer()).append(",")
          .append(StatusCode.toFriendlyName(resp.getResponseCode())).append("\n");
    }
    
    Writer out = null;
    try {
      out = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(fileName), "UTF-8"));
      out.write(builder.toString());
    } catch (Exception e) {
      // TODO Auto-generated catch block
      System.err.println(e);
    } finally {
      if (out != null) try {
        out.close();
      } catch (IOException e) {
        // TODO Auto-generated catch block
        System.err.println(e);
      }
    }

    crawler.shutdown();
    verifier.shutdown();
  }
}
