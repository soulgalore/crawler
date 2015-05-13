/******************************************************
 * Web crawler
 * 
 * 
 * Copyright (C) 2013 by Peter Hedenskog (http://peterhedenskog.com)
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
package com.soulgalore.crawler.core.assets.impl;

import com.google.inject.Inject;
import com.soulgalore.crawler.core.CrawlerConfiguration;
import com.soulgalore.crawler.core.CrawlerURL;
import com.soulgalore.crawler.core.HTMLPageResponse;
import com.soulgalore.crawler.core.assets.*;
import com.soulgalore.crawler.util.StatusCode;
import org.jsoup.nodes.Document;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Future;

public class DefaultAssetsVerifier implements AssetsVerifier {

  private final ExecutorService service;
  private final AssetFetcher responseCodeGetter;
  private final AssetsParser parser;

  @Inject
  public DefaultAssetsVerifier(ExecutorService theService, AssetFetcher getter,
      AssetsParser theParser) {
    service = theService;
    responseCodeGetter = getter;
    parser = theParser;

  }

  @Override
  public AssetsVerificationResult verify(Set<HTMLPageResponse> responses,
      CrawlerConfiguration configuration) {

    final Map<String, String> requestHeaders = configuration.getRequestHeadersMap();

    Set<CrawlerURL> urls = new HashSet<CrawlerURL>();

    final Set<Future<Set<CrawlerURL>>> fut = new HashSet<Future<Set<CrawlerURL>>>();

    for (HTMLPageResponse response : responses) {
      fut.add(service.submit(new AssetsParserCallable(response.getBody(), parser, response.getUrl())));
    }

    for (Future<Set<CrawlerURL>> future : fut) {
      try {
        urls.addAll(future.get());
      } catch (InterruptedException e) {
        // TODO Auto-generated catch block
        e.printStackTrace();
      } catch (ExecutionException e) {
        // TODO Auto-generated catch block
        e.printStackTrace();
      }
    }

    Set<AssetResponse> working = new HashSet<AssetResponse>();
    Set<AssetResponse> nonWorking = new HashSet<AssetResponse>();

    final Map<Future<AssetResponse>, String> futures =
        new HashMap<Future<AssetResponse>, String>(urls.size());

    for (CrawlerURL url : urls) {
      futures.put(
          service.submit(new AssetResponseCallable(url.getUrl(), responseCodeGetter, requestHeaders, url.getReferer())), url.getUrl());

    }

    for (Entry<Future<AssetResponse>, String> entry : futures.entrySet()) {

      try {
        AssetResponse assetResponse = entry.getKey().get();
        if (StatusCode.isResponseCodeOk(assetResponse.getResponseCode()))
          working.add(assetResponse);
        else
          nonWorking.add(assetResponse);

      } catch (InterruptedException e) {
        nonWorking.add(new AssetResponse(entry.getValue(), "", StatusCode.SC_SERVER_RESPONSE_UNKNOWN
                .getCode(), -1));
      } catch (ExecutionException e) {
        System.err.println(e.getMessage());
        nonWorking.add(new AssetResponse(entry.getValue(), "", StatusCode.SC_SERVER_RESPONSE_UNKNOWN
                .getCode(), -1));
      }

    }

    return new AssetsVerificationResult(working, nonWorking);
  }

  @Override
  public void shutdown() {
    service.shutdown();

  }

  private static class AssetsParserCallable implements Callable<Set<CrawlerURL>> {

    private final Document doc;
    private final AssetsParser parser;
    private final String referer;

    private AssetsParserCallable(Document theDoc, AssetsParser theParsers, String theReferer) {
      doc = theDoc;
      parser = theParsers;
      referer = theReferer;
    }

    @Override
    public Set<CrawlerURL> call() throws Exception {
      return parser.getAssets(doc, referer);
    }

  }
}
