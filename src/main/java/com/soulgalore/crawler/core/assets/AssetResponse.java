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
package com.soulgalore.crawler.core.assets;

/**
 * The response of an asset fetch.
 * 
 */
public class AssetResponse {

  private final String url;
  private final String referer;
  private final int responseCode;
  private final long fetchTime;

  public AssetResponse(String url, String referer, int responseCode, long fetchTime) {
    super();
    this.url = url;
    this.responseCode = responseCode;
    this.fetchTime = fetchTime;
    this.referer = referer;
  }

  /**
   * Get the URL of the asset.
   * 
   * @return the url
   */
  public String getUrl() {
    return url;
  }

  /**
   * The response code when the asset was fetched.
   * 
   * @return the response code
   */
  public int getResponseCode() {
    return responseCode;
  }

  public long getFetchTime() {
    return fetchTime;
  }

  public String getReferer() {
    return referer;
  }
  
  
  @Override
  public int hashCode() {
    final int prime = 31;
    int result = 1;
    result = prime * result + responseCode;
    result = prime * result + ((url == null) ? 0 : url.hashCode());
    return result;
  }

  @Override
  public boolean equals(Object obj) {
    if (this == obj) return true;
    if (obj == null) return false;
    if (getClass() != obj.getClass()) return false;
    AssetResponse other = (AssetResponse) obj;
    if (responseCode != other.responseCode) return false;
    if (url == null) {
      if (other.url != null) return false;
    } else if (!url.equals(other.url)) return false;
    return true;
  }



}
