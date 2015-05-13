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
package com.soulgalore.crawler.util;

import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Set;
import java.util.StringTokenizer;

import org.apache.http.Header;
import org.apache.http.message.BasicHeader;

/**
 * Utils class.
 * 
 * 
 */
public final class HeaderUtil {

  private static final HeaderUtil INSTANCE = new HeaderUtil();

  /**
   * Create a new utils.
   */
  private HeaderUtil() {}

  /**
   * Get the instance.
   * 
   * @return the singleton instance.
   */
  public static HeaderUtil getInstance() {
    return INSTANCE;
  }

  /**
   * Create headers from a string.
   * 
   * @param headersAndValues by the header1:value1,header2:value2...
   * @return the Headers as a Set
   */
  public Map<String, String> createHeadersFromString(String headersAndValues) {

    if (headersAndValues == null || headersAndValues.isEmpty()) return Collections.emptyMap();

    final StringTokenizer token = new StringTokenizer(headersAndValues, "@");

    final Map<String, String> theHeaders = new HashMap<String, String>(token.countTokens());

    while (token.hasMoreTokens()) {
      final String headerAndValue = token.nextToken();
      if (!headerAndValue.contains(":"))
        throw new IllegalArgumentException(
            "Request headers wrongly configured, missing separator :" + headersAndValues);

      final String header = headerAndValue.substring(0, headerAndValue.indexOf(":"));
      final String value =
          headerAndValue.substring(headerAndValue.indexOf(":") + 1, headerAndValue.length());
      theHeaders.put(header, value);
    }

    return theHeaders;

  }



}
