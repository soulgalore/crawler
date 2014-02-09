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

import java.util.HashSet;
import java.util.Set;

import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import com.soulgalore.crawler.core.CrawlerURL;
import com.soulgalore.crawler.core.assets.AssetsParser;

public class DefaultAssetsParser implements AssetsParser {

  public DefaultAssetsParser() {}

  @Override
  public Set<CrawlerURL> getAssets(Document doc, String referer) {

    Elements media = doc.select("[src]");
    Elements imports = doc.select("link[href]");

    Set<CrawlerURL> urls = new HashSet<CrawlerURL>(media.size() + imports.size());

    for (Element link : imports) {
      urls.add(new CrawlerURL(link.attr("abs:href"), referer));
    }

    for (Element src : media) {
      urls.add(new CrawlerURL(src.attr("abs:src"), referer));
    }

    return urls;
  }

}
