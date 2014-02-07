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

import org.apache.http.HttpStatus;

/**
 * Specific status codes.
 * 
 */
public enum StatusCode {

  SC_SERVER_RESPONSE_TIMEOUT(580, "Response timed out"), SC_SERVER_RESPONSE_UNKNOWN(581,
      "Unknown error"), SC_MALFORMED_URI(582, "Malformed url"), SC_WRONG_CONTENT_TYPE(583,
      "Wrong content type"),SC_SERVER_REDIRECT_TO_NEW_DOMAIN(308, "Redirected to new domain");

  private final int code;
  private final String friendlyName;

  StatusCode(int theCode, String theFriendlyName) {
    code = theCode;
    friendlyName = theFriendlyName;
  }

  public int getCode() {
    return code;
  }

  public String getFriendlyName() {
    return friendlyName;
  }

  public static String toFriendlyName(int code) {
    for (StatusCode s : StatusCode.values()) {
      if (s.getCode() == code) return s.getFriendlyName();
    }
    return String.valueOf(code);
  }

  /**
   * Is a status code ok?
   * 
   * @param responseCode the code
   * @return true if it is ok
   */
  public static boolean isResponseCodeOk(Integer responseCode) {

    if (responseCode >= HttpStatus.SC_BAD_REQUEST) return false;
    return true;
  }
}
