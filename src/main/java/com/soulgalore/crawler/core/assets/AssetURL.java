package com.soulgalore.crawler.core.assets;

import java.net.URI;
import java.net.URL;
import java.net.URLDecoder;


public class AssetURL {

  private final String url;
  private final String referer;
  private final URI uri;
  
  public AssetURL(String url, String referer) {
    this.url = url;
    this.referer = referer;
    URI tmpURI = null;
    try {
      URL u = null;

      // sometimes the urls are encoded
      // but how do we handle the problem with url:s that are
      // encoded but not the + sign?
      // better to check if it contains faulty characters
      // if (url.matches("@^[a-zA-Z0-9%+-_]*$@"))
      if (url.contains("%"))
        u = new URL(URLDecoder.decode(url, "UTF-8"));
      else
        u = new URL(url);

      // skipping the segment part, since the # is only for the browser
      tmpURI =
          new URI(u.getProtocol(), u.getUserInfo(), u.getHost(), u.getPort(), u.getPath(),
              u.getQuery(), null);

    } catch (Exception e) {
      // an ugly catch all, we should act on it somehow
    }
    uri = tmpURI;
  }
  
  public String getURL() {
    return url;
  }
  
  public String getReferer() {
    return referer;
  }
  
  public URI getURI() {
    return uri;
  }

  @Override
  public int hashCode() {
    final int prime = 31;
    int result = 1;
    result = prime * result + ((url == null) ? 0 : url.hashCode());
    return result;
  }

  @Override
  public boolean equals(Object obj) {
    if (this == obj) return true;
    if (obj == null) return false;
    if (getClass() != obj.getClass()) return false;
    AssetURL other = (AssetURL) obj;
    if (url == null) {
      if (other.url != null) return false;
    } else if (!url.equals(other.url)) return false;
    return true;
  }
  
  
}
