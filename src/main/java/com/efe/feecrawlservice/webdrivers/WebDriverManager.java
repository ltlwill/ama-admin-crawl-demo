package com.efe.feecrawlservice.webdrivers;

import java.io.File;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import javax.annotation.PostConstruct;

import org.apache.commons.lang3.StringUtils;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriverService;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.openqa.selenium.firefox.GeckoDriverService;
import org.openqa.selenium.phantomjs.PhantomJSDriver;
import org.openqa.selenium.phantomjs.PhantomJSDriverService;
import org.openqa.selenium.remote.CapabilityType;
import org.openqa.selenium.remote.DesiredCapabilities;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.efe.feecrawlservice.config.AppConfiguration;
import com.efe.feecrawlservice.config.AppConfiguration.CrawlerConfig;
import com.efe.feecrawlservice.config.AppConfiguration.DriverArgument;
import com.efe.feecrawlservice.config.AppConfiguration.DriverType;
import com.efe.feecrawlservice.config.AppConfiguration.WebDriverConfig;
import com.efe.feecrawlservice.utils.SpringBeanUtil;
import com.gargoylesoftware.htmlunit.BrowserVersion;
import com.gargoylesoftware.htmlunit.NicelyResynchronizingAjaxController;
import com.gargoylesoftware.htmlunit.WebClient;

/**
 * web driver管理
 * 
 * @author Tianlong Liu
 * @2020年7月9日 上午10:01:23
 */
@Component
public class WebDriverManager {

	protected Logger logger = LoggerFactory.getLogger(WebDriverManager.class);

	private static final String CHROME_DOWNLOAD_PATH_ARGS_NAME = "download.default_directory";
	private static final String CHROME_DOWNLOAD_NO_POPUPS = "profile.default_content_settings.popups";

	private static WebDriverManager manager;

	@Autowired
	private AppConfiguration appCfg;

	@PostConstruct
	public void setDriverPath() {
		WebDriverConfig driverCfg = appCfg.getWebDriver();
		if (driverCfg != null) {
			System.setProperty(ChromeDriverService.CHROME_DRIVER_EXE_PROPERTY, driverCfg.getChromeDriverPath());
			System.setProperty(GeckoDriverService.GECKO_DRIVER_EXE_PROPERTY, driverCfg.getGeckoDriverPath());
			System.setProperty(PhantomJSDriverService.PHANTOMJS_EXECUTABLE_PATH_PROPERTY,
					driverCfg.getPhantomjsDriverPath());
		}
	}

	public static WebDriverManager getInstance() {
		if (manager == null) {
			synchronized (WebDriverManager.class) {
				if (manager == null) {
					manager = SpringBeanUtil.getBean(WebDriverManager.class);
				}
			}
		}
		return manager;
	}

	public WebDriver selectDriver() {
		WebDriverConfig cfg = appCfg.getWebDriver();
		CrawlerConfig ccfg = appCfg.getCrawlerConfig();
		DriverType driverType = cfg.getDriverType();
		if (DriverType.FIREFOX.equals(driverType)) {
			return firefoxDriver();
		} else if (DriverType.PHANTOMJS.equals(driverType)) {
			return phantomjsDriver();
		}
		String marketAccountStr = ccfg.getMarketName() + "_" + ccfg.getAccountName();
		String path = generateDownloadPath(cfg.getDownloadDir(), driverType.getType(), marketAccountStr);
		ChromeOptions opts = createChromeOptions(cfg.getArguments(), path);
		DesiredCapabilities cap = DesiredCapabilities.chrome();
		cap.setCapability(ChromeOptions.CAPABILITY, new HashMap<String, Object>());
		cap.setCapability(CapabilityType.ACCEPT_SSL_CERTS, true);
		cap.setCapability(ChromeOptions.CAPABILITY, opts);
//		WebChromeDriver webDriver = DriverType.CHROME_HEADLESS.equals(driverType) ? chromeDriverHeadless(opts)
//				: chromeDriver(opts);
		WebChromeDriver webDriver = DriverType.CHROME_HEADLESS.equals(driverType) ? chromeDriver(cap)
				: chromeDriver(cap);
		webDriver.setDownloadPath(path);
		return webDriver;
	}

	public static String generateDownloadPath(String downloadDir, String driverType, String contextPath) {
		if (StringUtils.isBlank(downloadDir)) {
			return null;
		}
		LocalDateTime now = LocalDateTime.now();
		String dateFmt = now.format(DateTimeFormatter.ofPattern("yyyyMMdd"));
		return downloadDir + File.separator + driverType + File.separator
//				+ dateFmt + File.separator + UUID.randomUUID().toString();
				+ contextPath + File.separator + dateFmt + File.separator + System.currentTimeMillis();
	}

	private static ChromeOptions createChromeOptions(List<DriverArgument> args, String downloadPath) {
		final ChromeOptions opts = new ChromeOptions();
		if (StringUtils.isNotBlank(downloadPath)) {
			Map<String, Object> chromePrefs = new HashMap<String, Object>();
			chromePrefs.put(CHROME_DOWNLOAD_NO_POPUPS, 0);
			chromePrefs.put(CHROME_DOWNLOAD_PATH_ARGS_NAME, downloadPath);
			opts.setExperimentalOption("prefs", chromePrefs);
		}
		Optional.ofNullable(args).orElse(Collections.emptyList()).forEach(arg -> {
			opts.addArguments(arg.getName() + (StringUtils.isNotBlank(arg.getValue()) ? "=" + arg.getValue() : ""));
		});
		return opts;
	}

	/**
	 * chrome 浏览器驱动
	 * 
	 * @return
	 */
	public static WebChromeDriver chromeDriver() {
//		return new ChromeDriver(); // chrome
		return new WebChromeDriver();
	}

	public static WebChromeDriver chromeDriver(ChromeOptions opts) {
//		return new ChromeDriver(opts); // chrome
		return new WebChromeDriver(opts);
	}

	public static WebChromeDriver chromeDriver(DesiredCapabilities caps) {
		return new WebChromeDriver(caps);
	}

	/**
	 * chrome headless模式驱动
	 * 
	 * @return
	 */
	public static WebChromeDriver chromeDriverHeadless() {
		ChromeOptions opts = new ChromeOptions();
//		opts.addArguments("--headless");
		opts.setHeadless(true);
//		return new ChromeDriver(opts); // chrome --headless模式（无界面模式）
		return new WebChromeDriver(opts);
	}

	public static WebChromeDriver chromeDriverHeadless(ChromeOptions opts) {
		opts = opts == null ? new ChromeOptions() : opts;
//		opts.addArguments("--headless");
		opts.setHeadless(true);
//		return new ChromeDriver(opts); // chrome --headless模式（无界面模式）
		return new WebChromeDriver(opts);
	}

	/**
	 * firefox浏览器驱动
	 * 
	 * @return
	 */
	public static WebDriver firefoxDriver() {
		return new FirefoxDriver();
	}

	/**
	 * phantomjs 无界面浏览器驱动
	 * 
	 * @return
	 */
	public static WebDriver phantomjsDriver() {
		return new PhantomJSDriver();
	}

	/**
	 * 创建htmlunit WebClient
	 * 
	 * @return
	 */
	public static WebClient htmlUnitWebClient() {
		WebClient webClient = new WebClient(BrowserVersion.CHROME);
		webClient.getOptions().setCssEnabled(false); // 无界面，不需要开启CSS
		webClient.getOptions().setDownloadImages(false); // 不需要加载图片
		webClient.getOptions().setJavaScriptEnabled(true); // 有ajax请求，需要开启
		webClient.getOptions().setActiveXNative(false);
		webClient.getOptions().setThrowExceptionOnScriptError(false); // 当JS执行出错的时候是否抛出异常, 这里选择不需要
		webClient.getOptions().setThrowExceptionOnFailingStatusCode(false); // 当HTTP的状态非200时是否抛出异常, 这里选择不需要
		webClient.setAjaxController(new NicelyResynchronizingAjaxController()); // 设置支持AJAX
		return webClient;
	}

}