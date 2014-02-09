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
package com.soulgalore.crawler.core.impl;

import java.util.HashSet;
import java.util.Set;

import org.apache.http.HttpStatus;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import com.soulgalore.crawler.core.PageURLParser;
import com.soulgalore.crawler.core.CrawlerURL;
import com.soulgalore.crawler.core.HTMLPageResponse;

/**
 * The ahref parser, parses a response (HTML body) and fetch all ahref links within this document.
 * 
 * 
 */
public class AhrefPageURLParser implements PageURLParser {

  private static final String AHREF = "a[href]";
  private static final String ABS_HREF = "abs:href";

  private static final String MAIL_TO = "mailto:";
  private static final String IFRAME = "iframe";

  /**
   * Create a parser.
   */
  public AhrefPageURLParser() {

  }

  /**
   * Get all ahref links within this page response.
   * 
   * @param theResponse the response from the request to this page
   * @return the urls.
   */
  public Set<CrawlerURL> get(HTMLPageResponse theResponse) {

    final String url = theResponse.getUrl();

    Set<CrawlerURL> ahrefs = new HashSet<CrawlerURL>();

    // only populate if we have a valid response, else return empty set
    if (theResponse.getResponseCode() == HttpStatus.SC_OK) {
      ahrefs = fetch(AHREF, ABS_HREF, theResponse.getBody(), url);
    }

    return ahrefs;
  }

  private Set<CrawlerURL> fetch(String query, String attributeKey, Document doc, String url) {

    final Set<CrawlerURL> urls = new HashSet<CrawlerURL>();

    final Elements elements = doc.select(query);

    for (Element src : elements) {

      if (src.attr(attributeKey).isEmpty()) continue;

      // don't fetch mailto links
      if (src.attr(attributeKey).startsWith(MAIL_TO))
        continue;

      else if (IFRAME.equals(src.tag().getName()))
        urls.add(new CrawlerURL(src.attr(attributeKey), url));

      else
        urls.add(new CrawlerURL(src.attr(attributeKey), url));

    }

    return urls;

  }
}
