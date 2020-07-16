package com.efe.feecrawlservice.webdrivers;

import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.remote.DesiredCapabilities;

/**
 * 扩展ChromeDriver
 * @author Administrator
 *
 */
public class WebChromeDriver extends ChromeDriver {
	
	private String downloadPath;
	
	public WebChromeDriver() {
	}
	
	public WebChromeDriver(ChromeOptions opts) {
		super(opts);
	}
	
	@SuppressWarnings("deprecation")
	public WebChromeDriver(DesiredCapabilities caps) {
		super(caps);
	}
	
	public WebChromeDriver(ChromeOptions opts,String downloadPath) {
		super(opts);
		this.downloadPath = downloadPath;
	}

	public String getDownloadPath() {
		return downloadPath;
	}

	public void setDownloadPath(String downloadPath) {
		this.downloadPath = downloadPath;
	}

	
}
