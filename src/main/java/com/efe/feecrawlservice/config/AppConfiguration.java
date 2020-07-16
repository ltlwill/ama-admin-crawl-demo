package com.efe.feecrawlservice.config;

import java.io.File;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.UUID;

import org.apache.commons.lang3.StringUtils;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * app 配置
 * 
 * @author Tianlong Liu
 * @2020年7月9日 上午9:56:14
 */
@Configuration
@ConfigurationProperties(prefix = "app")
@Getter
@Setter
@NoArgsConstructor
public class AppConfiguration {

	private CrawlerConfig crawlerConfig;
	private WebDriverConfig webDriver;

	@Getter
	@Setter
	@NoArgsConstructor
	public static class CrawlerConfig {
		private String loginUrl; // 主页地址
		private String accountId; // 账号ID
		private String accountName; // 账号名称
		private String marketId; // 站点ID
		private String marketName; // 站点名称
		private String userName; // amazon用户
		private String password; // amazon登录密码

		public String toString() {
			return "loginUrl=" + loginUrl + ";accountId=" + accountId + ";accountName=" + accountName + ";marketId="
					+ marketId + ";marketName=" + marketName;
		}
	}

	@Getter
	@Setter
	@NoArgsConstructor
	@AllArgsConstructor
	public static class WebDriverConfig {
		private String chromeDriverPath; // chrome 浏览器 路径
		private String geckoDriverPath; // firefox 浏览器驱动路径
		private String phantomjsDriverPath; // phantomjs 浏览器驱动路径
		private DriverType driverType; // 驱动类型
		private String downloadDir;   // 下载文件时的存放的根目录路径
		private List<DriverArgument> arguments; // 驱动参数
		
		@Override
		public String toString() {
			return "chromeDriverPath=" + chromeDriverPath + ";geckoDriverPath=" + geckoDriverPath
					+ ";phantomjsDriverPath = " + phantomjsDriverPath;
		}
	}
	
	@Setter
	@Getter
	@NoArgsConstructor
	@AllArgsConstructor
	public static class DriverArgument{
		private String name;
		private String value;
	}
	
	@Getter
	public static enum DriverType{
		CHROME("chrome"),CHROME_HEADLESS("chrome-headless"),FIREFOX("firefox"),PHANTOMJS("phantomjs");
		
		private DriverType() {
		}
		
		private DriverType(String type) {
			this.type = type;
		}
		
		private String type;
	}
}
