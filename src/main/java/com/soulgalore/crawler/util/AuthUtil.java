package com.soulgalore.crawler.util;

import java.util.Collections;
import java.util.HashSet;
import java.util.Iterator;
import java.util.NoSuchElementException;
import java.util.Set;

import com.google.common.base.Splitter;

/**
 * Helper class for authentication.
 */
public final class AuthUtil {

	private static final AuthUtil INSTANCE = new AuthUtil();

	/**
	 * Create a new utils.
	 */
	private AuthUtil() {
	}

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
	 * @param authInfo
	 *            the authinfo in the form of
	 * @return a Set of auth
	 */
	public Set<Auth> createAuthsFromString(String authInfo) {

		if ("".equals(authInfo) || authInfo == null)
			return Collections.emptySet();

		String[] parts = authInfo.split("\\,");

		final Set<Auth> auths = new HashSet<Auth>();

		try {
			for (String auth : parts) {
				final Iterable<String> allAuths = Splitter.on(':')
						.trimResults().split(auth);

				final Iterator<String> authItems = allAuths.iterator();

				auths.add(new Auth(authItems.next(), authItems.next(),
						authItems.next(), authItems.next()));

			}

			return auths;
		} catch (NoSuchElementException e) {
			final StringBuilder b = new StringBuilder();
			for (String auth : parts) {
				b.append(auth);
			}
			throw new IllegalArgumentException(
					"Auth configuration is configured wrongly:" + b.toString(),
					e);
		}
	}

}
