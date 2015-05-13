package com.soulgalore.crawler.util;

import java.util.Collections;
import java.util.HashSet;
import java.util.NoSuchElementException;
import java.util.Set;
import java.util.StringTokenizer;

/**
 * Helper class for authentication.
 */
public final class AuthUtil {

  private static final AuthUtil INSTANCE = new AuthUtil();

  /**
   * Create a new utils.
   */
  private AuthUtil() {}

  /**
   * Get the instance.
   * 
   * @return the singleton instance.
   */
  public static AuthUtil getInstance() {
    return INSTANCE;
  }

  /**
   * Create a auth object from a String looking like.
   * 
   * @param authInfo the authinfo in the form of
   * @return a Set of auth
   */
  public Set<Auth> createAuthsFromString(String authInfo) {

    if ("".equals(authInfo) || authInfo == null) return Collections.emptySet();

    String[] parts = authInfo.split(",");

    final Set<Auth> auths = new HashSet<Auth>();

    try {
      for (String auth : parts) {

        StringTokenizer tokenizer = new StringTokenizer(auth, ":");

        while (tokenizer.hasMoreTokens()) {
          auths.add(new Auth(tokenizer.nextToken(), tokenizer.nextToken(), tokenizer.nextToken(),
              tokenizer.nextToken()));
        }

      }

      return auths;
    } catch (NoSuchElementException e) {
      final StringBuilder b = new StringBuilder();
      for (String auth : parts) {
        b.append(auth);
      }
      throw new IllegalArgumentException(
          "Auth configuration is configured wrongly:" + b.toString(), e);
    }
  }

}
