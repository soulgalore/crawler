package com.soulgalore.crawler;

import static org.junit.Assert.assertThat;
import static org.hamcrest.Matchers.is;

import org.apache.http.HttpStatus;
import org.junit.Test;

import com.soulgalore.crawler.util.StatusCode;

public class WhenAStatusCodeIsChecked {

	@Test
	public void test() {
		StatusCode statusCode = StatusCode.getInstance();
		assertThat(statusCode.isResponseCodeOk(HttpStatus.SC_OK), is(true));
		assertThat(statusCode.isResponseCodeOk(HttpStatus.SC_FORBIDDEN), is(false));
		assertThat(statusCode.isResponseCodeOk(HttpStatus.SC_INTERNAL_SERVER_ERROR), is(false));
	}

}
