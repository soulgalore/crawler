package com.soulgalore.crawler.core;

/**
 * Configuration for a crawl.
 * 
 */
public class CrawlerConfiguration {

	private String startUrl;
	private String onlyOnPath = "";
	private int maxLevels = 1;
	private String notOnPath = "";
	private boolean verifyUrls = true;

	private CrawlerConfiguration() {

	}

	public String getStartUrl() {
		return startUrl;
	}

	private void setStartUrl(String startUrl) {
		this.startUrl = startUrl;
	}

	public String getOnlyOnPath() {
		return onlyOnPath;
	}

	private void setOnlyOnPath(String onlyOnPath) {
		this.onlyOnPath = onlyOnPath;
	}

	public int getMaxLevels() {
		return maxLevels;
	}

	private void setMaxLevels(int maxLevels) {
		this.maxLevels = maxLevels;
	}

	public String getNotOnPath() {
		return notOnPath;
	}

	private void setNotOnPath(String notOnPath) {
		this.notOnPath = notOnPath;
	}

	public boolean isVerifyUrls() {
		return verifyUrls;
	}

	private void setVerifyUrls(boolean verifyUrls) {
		this.verifyUrls = verifyUrls;
	}

	private CrawlerConfiguration copy() {
		CrawlerConfiguration conf = new CrawlerConfiguration();
		conf.setMaxLevels(getMaxLevels());
		conf.setNotOnPath(new String(getNotOnPath()));
		conf.setOnlyOnPath(new String(getOnlyOnPath()));
		conf.setStartUrl(new String(getStartUrl()));
		conf.setVerifyUrls(isVerifyUrls());
		return conf;

	}

	public static Builder builder() {
		return new Builder();
	}

	public static class Builder {
		private final CrawlerConfiguration configuration = new CrawlerConfiguration();

		public Builder() {
		}

		public Builder setStartUrl(String startUrl) {
			configuration.setStartUrl(startUrl);
			return this;
		}

		public Builder setVerifyUrls(boolean verifyUrls) {
			configuration.setVerifyUrls(verifyUrls);
			return this;
		}

		public Builder setOnlyOnPath(String onlyOnPath) {
			configuration.setOnlyOnPath(onlyOnPath);
			return this;
		}

		public Builder setMaxLevels(int maxLevels) {
			configuration.setMaxLevels(maxLevels);
			return this;
		}

		public Builder setNotOnPath(String notOnPath) {
			configuration.setNotOnPath(notOnPath);
			return this;
		}

		public CrawlerConfiguration build() {
			return configuration.copy();
		}
	}
}
