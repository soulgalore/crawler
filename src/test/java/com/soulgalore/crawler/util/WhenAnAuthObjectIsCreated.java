package com.soulgalore.crawler.util;

import static org.junit.Assert.*;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.hasItem;
import static org.hamcrest.Matchers.notNullValue;

import java.util.Set;

import org.junit.Test;

public class WhenAnAuthObjectIsCreated {

	private AuthUtil util = AuthUtil.getInstance();

	@Test
	public void authFromFaultyStringShouldNotWork() {
		try {
			util.createAuthsFromString("soulislove.com:80:name1password1");
			fail("If the auth are wrongly configured, an exception should be thrown");
		} catch (IllegalArgumentException e) {
		}
	}

	@Test
	public void emptyAuthShouldNotWork() {
		Set<Auth> auth = util
				.createAuthsFromString("");
		assertThat(auth, notNullValue());
		assertThat(auth.size(), equalTo(0));
	}

	@Test
	public void oneAuthsStringShouldBeOneAuthObject() {
		Set<Auth> auth = util
				.createAuthsFromString("soulislove.com:80:name1:password1") ;
		assertThat(auth, notNullValue());
		assertThat(auth.size(), equalTo(1));
		assertThat(auth, hasItem((new Auth("soulislove.com", "80",
				"name1", "password1"))));
	}

	@Test
	public void twoAuthsStringShouldBeTwoAuthObject() {
		Set<Auth> auth = util.createAuthsFromString("soulislove.com:80:name1:password1,soulgalore.com:81:name2:password2");
		assertThat(auth, notNullValue());
		assertThat(auth.size(), equalTo(2));
		assertThat(auth, hasItem(new Auth("soulislove.com", "80",
				"name1", "password1")));
		assertThat(auth, hasItem(new Auth("soulgalore.com", "81",
				"name2", "password2")));

	}
}
