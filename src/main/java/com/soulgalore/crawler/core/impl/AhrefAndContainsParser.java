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
package com.soulgalore.crawler.core.impl;

import java.util.Set;

import com.soulgalore.crawler.core.PageURL;
import com.soulgalore.crawler.core.HTMLPageResponse;


/**
 * The ahref parser, parses a response (HTML body) and if it holds any 
 * occurenses o the search key, the page url is printed to system out.
 * 
 * 
 */
public class AhrefAndContainsParser extends AhrefParser {

	private final String key;
	
	/**
	 * Create the parser.
	 * @param searchKey the String that each page needs to contain to be logged.
	 */
	public AhrefAndContainsParser(String searchKey) {
		super();
		key = searchKey;
	}

	/**
	 * Get all the links.
	 * 
	 * @param theResponse
	 *            the response from the request to this page
	 *  @return the urls          
	 */
	public Set<PageURL> get(HTMLPageResponse theResponse) {
		final Set<PageURL> urls = super.get(theResponse);
		
		if (theResponse.getBody().toString().contains(key)) {
			System.out.println(theResponse.getUrl());
		}
			
		return urls;
	}

}
