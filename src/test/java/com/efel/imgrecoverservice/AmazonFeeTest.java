package com.efel.imgrecoverservice;

import org.junit.jupiter.api.Test;
import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeDriverService;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.openqa.selenium.firefox.FirefoxOptions;
import org.openqa.selenium.firefox.GeckoDriverService;
import org.openqa.selenium.phantomjs.PhantomJSDriver;
import org.openqa.selenium.phantomjs.PhantomJSDriverService;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import com.efe.feecrawlservice.webdrivers.WebDriverManager;

public class AmazonFeeTest {
	private static final long DEFAUT_WAIT_SECONDS = 30;
	
	private static final String CHROME_DRIVER_PATH = "D:\\web_drivers\\chromedriver_win32\\chromedriver.exe";
	private static final String FIREFOX_DRIVER_PATH = "D:\\web_drivers\\geckodriver-v0.26.0-win64\\geckodriver.exe";
	private static final String PHANTOMJS_DRIVER_PATH = "D:\\web_drivers\\phantomjs-2.1.1-windows\\phantomjs-2.1.1-windows\\bin\\phantomjs.exe";
	
	@Test
	public void test01() {
//		WebDriver driver = WebDriverManager.chromeDriver();
		init();
		WebDriver driver = chromeDriver(); // chrome
//		WebDriver driver = chromeDriverHeadless(); // chrome
//		WebDriver driver = phantomJSDriver(); // phantomjs
//		driver.get("https://www.amazon.com");
		driver.get("https://sellercentral.amazon.com");
//		driver.get("https://sellercentral.amazon.com/orders-v3/ref=xx_myo_dnav_xx?shipByDate=all&openid.identity=http://specs.openid.net/auth/2.0/identifier_select&openid.assoc_handle=sc_na_amazon_v2&openid.mode=checkid_setup&openid.claimed_id=http://specs.openid.net/auth/2.0/identifier_select&openid.ns=http://specs.openid.net/auth/2.0&ssoResponse=eyJ6aXAiOiJERUYiLCJlbmMiOiJBMjU2R0NNIiwiYWxnIjoiQTI1NktXIn0.j1Zeo7C7tDdAdftf_EsR7Y917eYHVZzNarjoNwbGB9fkUiqx4NfTtQ.5Bqy9yXrxxtCzq3g.C_AYan-ydwHvEKMdTz7dgdFBc8ieHXAa6IxjuJiRkPg8wv38f4juaj-QUxc0RIxtYQLF0umvZBmSd-ZpH10XiYUZ7gmPTxz5OaC0y64ZAJpUoInWPBjhwbwasbv89_ST10hUP9j7rjyTSM5leT9RaBUz8rVOfjI2kjVp9UX5OVkoKTDZc8C0Suq0EgrrdDe-MWDT7wIC-xHHwPS74SjLDjOMqTLwsV4WU8c_9nmzjy2ZePicwWMJOwmzJVLPcWFC5b6h.uSUa6Yxz6vGqfLcuPwRXJQ");
		WebDriverWait wait = new WebDriverWait(driver, DEFAUT_WAIT_SECONDS);
		wait.until(ExpectedConditions.presenceOfElementLocated(By.cssSelector("#nav-link-accountList")));
		WebElement ele = driver.findElement(By.cssSelector("#nav-link-accountList"));
		System.out.println("主界面：" + ele.getText());
		ele.click();
		wait.until(ExpectedConditions.presenceOfElementLocated(By.cssSelector("input#continue")));
		ele = driver.findElement(By.cssSelector("div.a-spacing-base > .a-form-label"));
		System.out.println("登录界面：" + ele.getText());
	}
	
	@Test
	public void test02() throws Exception{
		init();
//		WebDriver driver = chromeDriver(); // chrome
		ChromeOptions opts = new ChromeOptions();
		opts.addArguments("--user-data-dir=C:\\Users\\Administrator\\AppData\\Local\\Google\\Chrome\\User Data");
		WebDriver driver = new ChromeDriver(opts);
		JavascriptExecutor jsExecutor = ((JavascriptExecutor) driver);
//		jsExecutor.executeScript("var myname = 'ttt';console.log(myname)");
		driver.get("https://sellercentral.amazon.com/signin");
//		Thread.sleep(2000);
		String js = "var scrWidth = screen.availWidth;" + 
				"    var scrHeight = screen.availHeight;" + 
				"    window.resizeTo(scrWidth + 9,scrHeight + 9);";
//		jsExecutor.executeScript(js);
	}
	
	private void init() {
		System.setProperty(ChromeDriverService.CHROME_DRIVER_EXE_PROPERTY, CHROME_DRIVER_PATH);
		System.setProperty(GeckoDriverService.GECKO_DRIVER_EXE_PROPERTY, FIREFOX_DRIVER_PATH);
		System.setProperty(PhantomJSDriverService.PHANTOMJS_EXECUTABLE_PATH_PROPERTY,
				PHANTOMJS_DRIVER_PATH);
	}
	
	private ChromeDriver chromeDriver() {
		ChromeOptions opts = new ChromeOptions();
		return new ChromeDriver(opts);
	}
	
	private ChromeDriver chromeDriverHeadless() {
		ChromeOptions opts = new ChromeOptions();
		opts.setHeadless(true);
		return new ChromeDriver(opts);
	}
	
	private FirefoxDriver firefoxDriver() {
		FirefoxOptions opts = new FirefoxOptions();
		return new FirefoxDriver(opts);
	}
	
	private PhantomJSDriver phantomJSDriver() {
		return new PhantomJSDriver();
	}

}
