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
	
	public CrawlerConfiguration() {
		
	}
	
	
	public String getStartUrl() {
		return startUrl;
	}
	public void setStartUrl(String startUrl) {
		this.startUrl = startUrl;
	}
	public String getOnlyOnPath() {
		return onlyOnPath;
	}
	public void setOnlyOnPath(String onlyOnPath) {
		this.onlyOnPath = onlyOnPath;
	}
	public int getMaxLevels() {
		return maxLevels;
	}
	public void setMaxLevels(int maxLevels) {
		this.maxLevels = maxLevels;
	}
	public String getNotOnPath() {
		return notOnPath;
	}
	public void setNotOnPath(String notOnPath) {
		this.notOnPath = notOnPath;
	}
	public boolean isVerifyUrls() {
		return verifyUrls;
	}
	public void setVerifyUrls(boolean verifyUrls) {
		this.verifyUrls = verifyUrls;
	}
	
	
}
