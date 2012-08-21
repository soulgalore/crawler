/******************************************************
 * Web crawler
 * 
 *
 * Copyright (C) 2012 by Peter Hedenskog (http://peterhedenskog.com)
 *
 ******************************************************
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except in 
 * compliance with the License. You may obtain a copy of the License at
 * 
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software distributed under the License is 
 * distributed  on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.   
 * See the License for the specific language governing permissions and limitations under the License.
 *
 *******************************************************
 */
package com.soulgalore.crawler.util;

import org.apache.http.HttpStatus;

/**
 * Specific status codes.
 *
 */
public final class StatusCode {

	/**
	 * Server time out.
	 */
	public static final int SC_SERVER_RESPONSE_TIMEOUT = 580;

	/**
	 * Unknown response.
	 */
	public static final int SC_SERVER_RESPONSE_UNKNOWN = 581;

	/**
	 * Malformed URI.
	 */
	public static final int SC_MALFORMED_URI = 582;

	/**
	 * Wrong content type aka not text/html.
	 */
	public static final int SC_WRONG_CONTENT_TYPE = 583;

	private static final StatusCode INSTANCE = new StatusCode();

	private StatusCode() {
	}

	/**
	 * Get the instance.
	 * @return the status code
	 */
	public static StatusCode getInstance() {
		return INSTANCE;
	}

	/**
	 * Is a status code ok?
	 * @param responseCode the code
	 * @return true if it is ok
	 */
	public boolean isResponseCodeOk(Integer responseCode) {

		if (responseCode >= HttpStatus.SC_BAD_REQUEST)
			return false;
		return true;
	}
}
