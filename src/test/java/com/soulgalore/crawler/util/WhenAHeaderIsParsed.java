package com.soulgalore.crawler.util;

import static org.junit.Assert.*;
import static org.hamcrest.Matchers.is;

import java.util.Map;
import java.util.Set;

import org.apache.http.Header;
import org.junit.Test;

public class WhenAHeaderIsParsed {

	@Test
	public void allValuesShouldBeFetched() {
		String headersAndValues = "header1:value1@header2:value2";
		Map<String,String> headers = HeaderUtil.getInstance().createHeadersFromString(
				headersAndValues);
		assertThat(headers.size(), is(2));
		// the equals in BasicHeader is different, even though key/value is the
		// same, so no easy way to check the content
	}

	@Test
	public void oneHeaderValueIsFetched() {
		String headersAndValues = "header1:value1";
		Map<String,String> headers = HeaderUtil.getInstance().createHeadersFromString(
				headersAndValues);
		assertThat(headers.size(), is(1));
	}

	@Test
	public void faultyHeadersShouldBreak() {
		String headersAndValues = "header1value1";
		try {
			Map<String,String> headers = HeaderUtil.getInstance()
					.createHeadersFromString(headersAndValues);
			fail("Exception not thrown");
		} catch (IllegalArgumentException e) {
		}

	}

}
