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

import java.io.IOException;
import java.net.SocketTimeoutException;
import java.util.Map;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.conn.ConnectTimeoutException;
import org.apache.http.util.EntityUtils;

import com.google.inject.Inject;
import com.soulgalore.crawler.core.CrawlerURL;
import com.soulgalore.crawler.core.assets.AssetFetcher;
import com.soulgalore.crawler.core.assets.AssetResponse;
import com.soulgalore.crawler.util.StatusCode;

public class HTTPClientAssetFetcher implements AssetFetcher {

  private final HttpClient httpClient;

  @Inject
  public HTTPClientAssetFetcher(HttpClient client) {
    httpClient = client;
  }

  /**
   * Shutdown the client.
   */
  public void shutdown() {
    httpClient.getConnectionManager().shutdown();
  }

  @Override
  public AssetResponse getAsset(CrawlerURL url, Map<String, String> requestHeaders) {

    final HttpGet get = new HttpGet(url.getUri());

    for (String key : requestHeaders.keySet()) {
      get.setHeader(key, requestHeaders.get(key));
    }

    HttpEntity entity = null;

    final long start = System.currentTimeMillis();
    try {

      final HttpResponse resp = httpClient.execute(get);
      final long time = System.currentTimeMillis() - start;
      final int sc = resp.getStatusLine().getStatusCode();
      entity = resp.getEntity();
      EntityUtils.consume(entity);
      return new AssetResponse(url.getUrl(), url.getReferer(), sc, time);

    } catch (ConnectTimeoutException e) {
      return new AssetResponse(url.getUrl(), url.getReferer(), StatusCode.SC_SERVER_RESPONSE_TIMEOUT.getCode(),
          System.currentTimeMillis() - start);
    } catch (SocketTimeoutException e) {
      return new AssetResponse(url.getUrl(), url.getReferer(), StatusCode.SC_SERVER_RESPONSE_TIMEOUT.getCode(),
          System.currentTimeMillis() - start);
    } catch (ClientProtocolException e) {
      e.printStackTrace();
      return new AssetResponse(url.getUrl(), url.getReferer(), StatusCode.SC_SERVER_RESPONSE_UNKNOWN.getCode(), -1);
    } catch (IOException e) {
      e.printStackTrace();
      return new AssetResponse(url.getUrl(), url.getReferer(),  StatusCode.SC_SERVER_RESPONSE_UNKNOWN.getCode(), -1);
    }

    finally {

      get.releaseConnection();

    }

  }

}
