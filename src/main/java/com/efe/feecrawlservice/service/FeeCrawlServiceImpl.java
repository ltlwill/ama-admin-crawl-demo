package com.efe.feecrawlservice.service;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.List;
import java.util.Optional;
import java.util.regex.Pattern;

import javax.transaction.Transactional;

import org.apache.commons.lang3.StringUtils;
import org.openqa.selenium.By;
import org.openqa.selenium.ElementNotInteractableException;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import com.efe.feecrawlservice.config.AppConfiguration;
import com.efe.feecrawlservice.config.AppConfiguration.CrawlerConfig;
import com.efe.feecrawlservice.config.AppConfiguration.WebDriverConfig;
import com.efe.feecrawlservice.domain.FeeCrawlDetail;
import com.efe.feecrawlservice.domain.FeeCrawlTask;
import com.efe.feecrawlservice.domain.FeeCrawlTxtDetail;
import com.efe.feecrawlservice.domain.FeeCrawlVerifyCode;
import com.efe.feecrawlservice.exception.BusinessException;
import com.efe.feecrawlservice.repo.FeeCrawlDetailRepository;
import com.efe.feecrawlservice.repo.FeeCrawlTaskRepository;
import com.efe.feecrawlservice.repo.FeeCrawlTxtDetailRepository;
import com.efe.feecrawlservice.repo.FeeCrawlVerifyCodeRepository;
import com.efe.feecrawlservice.utils.CommonUtil;
import com.efe.feecrawlservice.utils.DateZoneUtil;
import com.efe.feecrawlservice.utils.NumberUtil;
import com.efe.feecrawlservice.webdrivers.WebChromeDriver;
import com.efe.feecrawlservice.webdrivers.WebDriverManager;

/**
 * 
 * @author Tianlong Liu
 * @2020年7月9日 上午11:53:24
 */
@Service
public class FeeCrawlServiceImpl implements FeeCrawlService {

	private static Logger logger = LoggerFactory.getLogger(FeeCrawlServiceImpl.class);
	private static final long DEFAUT_WAIT_SECONDS = 60 * 2; // 页面加载最大等待时间，60s（2分钟）
	private static final long SMS_MAX_VALID_TIMS_DIFF = 1000 * 60 * 60; // 1h 短信最大有效时间
	private static final long DOWNLOAD_MAX_WAIT_MILLISECOND = 1000 * 60 * 5; // 最大下载等待时间(5分钟)
	private static final long LOGIN_MAX_TIMS_DIFF = 1000 * 60 * 30; // 30min 短信登录最大登录时间
	private static final int SKIP_LINES_COUNT = 4; // 跳过前面4行
	private static final String SPIT_CHAR = "\\t"; // 分隔符

	@Autowired
	private AppConfiguration appCfg;

	@Autowired
	private FeeCrawlTaskRepository feeCrawlTaskRepository;

	@Autowired
	private FeeCrawlDetailRepository feeCrawlDetailRepository;
	
	@Autowired
	private FeeCrawlTxtDetailRepository feeCrawlTxtDetailRepository;

	@Autowired
	private FeeCrawlVerifyCodeRepository verifyCodeRepository;

	@Override
	public void startNewTask() throws Exception {
		FeeCrawlTask task = createNewFeeCrawlTask();
		processTask(task);
	}

	@Override
	public void processTask(final FeeCrawlTask task) throws Exception {
		// 保存任务记录数据
		FeeCrawlTask savedTask = feeCrawlTaskRepository.save(task);
		FeeCrawlDetail detail = createFeeCrawDetailWithTask(savedTask);
		asyncStartCrawlData(savedTask, detail);
	}

	@Override
	public void startCrawlData(final FeeCrawlTask task, final FeeCrawlDetail detail) throws Exception {
		doStartCrawl(task, detail);
	}

	@Override
	public void asyncStartCrawlData(final FeeCrawlTask task, final FeeCrawlDetail detail) throws Exception {
		new Thread(() -> doStartCrawl(task, detail)).start();

	}

	@Override
	@Transactional
	public void doStartCrawl(final FeeCrawlTask task, final FeeCrawlDetail detail) {
		task.setStatus(FeeCrawlTask.Status.PROCESSING);
		feeCrawlTaskRepository.save(task);
		try {
			doCrawlDataBusiness(detail);
			task.setStatus(FeeCrawlTask.Status.PROCESSED);
		} catch (Exception e) {
			String msg = e.getMessage();
			msg = msg == null ? "" : (msg.length() > 2000 ? msg.substring(0, 2000) : msg);
			task.setMessage(msg);
			task.setStatus(FeeCrawlTask.Status.PROCESS_EXCEPTION);
		}
		feeCrawlTaskRepository.save(task);
	}

	@Override
	@Transactional
	public void doCrawlDataBusiness(final FeeCrawlDetail detail) throws Exception {
		logger.info("开始采集----");
		WebDriver driver = null;
		try {
			driver = WebDriverManager.getInstance().selectDriver();
			doLogin(driver); // 登录
			extraFeeData(driver, detail); // 抽取数据
		} catch (Exception e) {
			logger.error("采集amazon 费用失败", e);
			throw e;
		} finally {
			if (driver != null) {
				driver.close();
			}
		}
		logger.info("结束采集----");
	}

	/**
	 * 从页面抽取数据
	 * 
	 * @param driver
	 * @param detail
	 */
	private void extraFeeData(final WebDriver driver, final FeeCrawlDetail detail) throws Exception {
		WebDriverWait wait = new WebDriverWait(driver, DEFAUT_WAIT_SECONDS);
		String selector = "div#sc-navtabs > div#sc-top-nav > a#sc-top-nav-icon";
		wait.until(ExpectedConditions.presenceOfElementLocated(By.cssSelector(selector)));
		WebElement clickEventEle = driver.findElement(By.cssSelector(selector));
		if (clickEventEle == null) {
			throw new BusinessException("登录成功后获取[显示导航]元素为空，css selector=" + selector);
		}
		if (clickEventEle.isDisplayed()) { // 不显示的元素出发click事件会抛：element not interactable的异常
			clickEventEle.click();
			Thread.sleep(2000);
		}
		String reportSelector = "li#sc-navtab-reports";
		wait.until(ExpectedConditions.presenceOfElementLocated(By.cssSelector(reportSelector)));
		WebElement reportEle = driver.findElement(By.cssSelector(reportSelector));
		if (reportEle == null) {
			throw new BusinessException("登录成功后获取[数据报告]元素为空，css selector=" + reportSelector);
		}
		if (reportEle.isDisplayed()) {
			reportEle.click();
			Thread.sleep(2000);
		}
		selector = "li#sc-navtab-reports > ul.sc-sub-nav > li > a[href*=\"/gp/payments-account\"]";
		wait.until(ExpectedConditions.presenceOfElementLocated(By.cssSelector(selector)));
		clickEventEle = driver.findElement(By.cssSelector(selector));
//		String payXpathExp = "//li[@id=\"sc-navtab-reports\"]/ul[@class=\"sc-sub-nav\"]/li/a[contains=(@href,'/gp/payments-account')]";
//		WebElement payBtn = driver.findElement(By.xpath(payXpathExp));
		if (clickEventEle == null) {
			throw new BusinessException("登录成功后获取[付款]元素为空，css selector=" + selector);
		}
		try {
			reportEle.click();
			Thread.sleep(500);
			clickEventEle.click(); // 进入到 付款页面
		} catch (Exception e) {
			if (e instanceof ElementNotInteractableException) {
				Thread.sleep(5000);
				clickEventEle.click(); // 重试
			} else {
				throw new BusinessException("点击[付款]，失败", e);
			}
		}
		selector = "kat-tab-header[tab-id=\"TRANSACTION_VIEW\"]";
		wait.until(ExpectedConditions.presenceOfElementLocated(By.cssSelector(selector)));
		clickEventEle = driver.findElement(By.cssSelector(selector));
		if (clickEventEle == null) {
			throw new BusinessException("登录成功后获取[交易一览]元素为空");
		}
		clickEventEle.click(); // 进入到 交易一览 页面
		String transTypeselector = "select#eventType";
		wait.until(ExpectedConditions.presenceOfElementLocated(By.cssSelector(transTypeselector)));
		String pageTableSelector = "td#content-main-entities > table.data-display:nth-child(4)"; // 分页表格
		WebElement pageTalble = driver.findElement(By.cssSelector(pageTableSelector));
		WebElement selectEle = pageTalble
				.findElement(By.cssSelector(".list-row-white .data-display-field select[name=\"pageSize\"]"));
		WebElement maxSizeEle = selectEle.findElement(By.cssSelector("option:last-child"));
		if (maxSizeEle != null) {
			maxSizeEle.click(); // 设置分页为最大值
			Thread.sleep(500);
			WebElement switchPageSizeBtn = pageTalble.findElement(By.cssSelector("input[type=\"image\"]"));
			if (switchPageSizeBtn != null) {
				switchPageSizeBtn.click();
				wait.until(ExpectedConditions.presenceOfElementLocated(By.cssSelector(transTypeselector)));
			}
		}
		extraFeeDataByPage(driver, detail, false, true); // 不切換“时间区间” ，需要下载文件
	}

	/**
	 * 分页解析数据
	 * 
	 * @param driver
	 * @param detail
	 * @param switchDateRange 是否切换到下一个“时间区间”
	 * @param downloadFile    是否切下载文件（每个“时间区间”下载一次即可）
	 */
	private void extraFeeDataByPage(final WebDriver driver, final FeeCrawlDetail detail, boolean switchDateRange,
			boolean downloadFile) throws Exception {
		String dateRangeSelector = "div#filtertransactions select#groupId";
		String updateRangeSelector = "div#filtertransactions button#UpdateID";
		WebDriverWait wait = new WebDriverWait(driver, DEFAUT_WAIT_SECONDS);
		wait.until(ExpectedConditions.presenceOfElementLocated(By.cssSelector(dateRangeSelector)));
		WebElement dateRangeEle = driver.findElement(By.cssSelector(dateRangeSelector));
		WebElement updateRangeEle = driver.findElement(By.cssSelector(updateRangeSelector));
		if (switchDateRange) { // 是否切换到下个“时间区间”
			WebElement nextOptionEle = findElementByCssSelector(dateRangeEle, "option[selected=\"selected\"] + option");
			if (nextOptionEle == null) {
				return;
			}
			nextOptionEle.click();
			Thread.sleep(200);
			updateRangeEle.click();
			Thread.sleep(1000);
			wait.until(ExpectedConditions.presenceOfElementLocated(By.cssSelector(dateRangeSelector)));
			dateRangeEle = driver.findElement(By.cssSelector(dateRangeSelector));
		}
		String dataTableSelector = "td#content-main-entities > table.data-display:nth-child(3)"; // 数据表格
		WebElement dataTable = driver.findElement(By.cssSelector(dataTableSelector));
		if (dataTable == null) {
			throw new BusinessException("登录后获取费用数据表格元素为空，css selector:" + dataTableSelector);
		}
		List<WebElement> trs = dataTable.findElements(By.cssSelector("tbody > tr"));
		if (trs == null || trs.isEmpty()) {
			return;
		}
		List<FeeCrawlDetail> details = new ArrayList<FeeCrawlDetail>();
		FeeCrawlDetail det = null;
		WebElement currDateRangeEle = findElementByCssSelector(dateRangeEle, "option[selected=\"selected\"]");
		String dateRange = currDateRangeEle == null ? null : currDateRangeEle.getText();
		String transTypeSelector = "select#eventType > option[selected=\"selected\"]";
		WebElement dataTypeEle = findElementByCssSelector(driver, transTypeSelector);
		String dataType = dataTypeEle == null ? null : dataTypeEle.getText();
		// 需要下载文件，每个“结算周期”只需要下载一次并解析，分页时不需要再下载
		if (downloadFile) {
			// 下载文件并解析
			doDownloadAndParseData(driver, detail, dataType, dateRange);
		}
		int size = trs.size();
		Date now = null;
		for (int i = 0; i < size; i++) {
			if (i == 0) { // 跳过表头
				continue;
			}
			now = DateZoneUtil.nowOfChina();
			det = new FeeCrawlDetail();
			BeanUtils.copyProperties(detail, det);
			det.setDataType(dataType);
			det.setDateRange(dateRange);
			det.setCreateTime(now);
			parseTrElementAsDetailData(trs.get(i), det);
			details.add(det);
		}
		// 保存数据
		feeCrawlDetailRepository.saveAll(details);
		// 如果有下一页
		String pageTableSelector = "td#content-main-entities > table.data-display:nth-child(4)"; // 分页表格
		WebElement pageTalble = driver.findElement(By.cssSelector(pageTableSelector));
		WebElement nextPageBtn = findElementByCssSelector(pageTalble,
				".list-row-odd .data-display-field td.right > a:last-child"); // 下一页按钮
		String text = nextPageBtn == null ? null : nextPageBtn.getText();
		if (StringUtils.isBlank(text) || Pattern.compile("\\d+").matcher(text).matches()) { // 如果最后一个a标签的文本内容是数字，则没有下一页
			extraFeeDataByPage(driver, detail, true, true); // 获取下一个“时间期限”的数据，需要下载文件
		} else { // 有下一页
			nextPageBtn.click();
			extraFeeDataByPage(driver, detail, false, false); // 不切换“时间期限”，不需要下载文件
		}
	}

	/**
	 * 下载文件并解析
	 * 
	 * @param driver
	 * @param dataType
	 * @param dataRange
	 */
	private void doDownloadAndParseData(final WebDriver driver, final FeeCrawlDetail detail, String dataType,
			String dateRange) throws Exception {
		WebChromeDriver wDriver = (WebChromeDriver) driver;
		File file = doDownloadReportFile(wDriver); // 下载文件
		List<FeeCrawlTxtDetail> txtDetails = parseTextFileAsFeeCrawlTxtDetails(detail,dataType,dateRange,file); // 解析文件
		if(!CollectionUtils.isEmpty(txtDetails)) {
			feeCrawlTxtDetailRepository.saveAll(txtDetails);
		}
	}

	/**
	 * 解析文本文件（TXT）内容为明细数据
	 * 
	 * @param detail
	 * @param dataType
	 * @param dateRange
	 * @param file
	 * @return
	 * @throws Exception
	 */
	private List<FeeCrawlTxtDetail> parseTextFileAsFeeCrawlTxtDetails(FeeCrawlDetail detail, String dataType,
			String dateRange, File file) throws Exception {
		logger.info("开始解析下载后的文件，file path:" + file.getAbsolutePath());
		FeeCrawlTxtDetail pubDetail = new FeeCrawlTxtDetail();
		BeanUtils.copyProperties(detail, pubDetail);
		pubDetail.setDataType(dataType);
		pubDetail.setDateRange(dateRange);
		pubDetail.setCreateTime(DateZoneUtil.nowOfChina());
		pubDetail.setFileName(file.getName());
		pubDetail.setFilePath(file.getAbsolutePath());

		List<String> lines = Files.readAllLines(Paths.get(file.getAbsolutePath()));
		if (lines != null && lines.size() > SKIP_LINES_COUNT) {
			lines = lines.subList(SKIP_LINES_COUNT, lines.size());
		}
		List<FeeCrawlTxtDetail> details = new ArrayList<FeeCrawlTxtDetail>();
		Optional.ofNullable(lines).orElse(Collections.emptyList()).forEach(line -> {
			if (StringUtils.isNotBlank(line)) {
				FeeCrawlTxtDetail textDetail = new FeeCrawlTxtDetail();
				BeanUtils.copyProperties(pubDetail, textDetail);
				parseLineAsDetail(line, textDetail);
				details.add(textDetail);
			}
		});
		return details;
	}

	/**
	 * 将行数据解析成一条明细数据
	 * 
	 * @param line
	 * @param detail
	 * @return
	 */
	private FeeCrawlTxtDetail parseLineAsDetail(String line, FeeCrawlTxtDetail detail) {
		String[] arr = line.split(SPIT_CHAR);
		detail.setTransDate(getValueFromArray(arr, 0));
		detail.setOrderNo(getValueFromArray(arr, 1));
		detail.setSku(getValueFromArray(arr, 2));
		detail.setTransType(getValueFromArray(arr, 3));
		detail.setPayType(getValueFromArray(arr, 4));
		detail.setPayDetail(getValueFromArray(arr, 5));
		detail.setAmount(getValueFromArray(arr, 6));
		detail.setAmountNum(NumberUtil.currencyToBigDecimal(detail.getAmount()));
		detail.setCurrency(NumberUtil.getCurrency(detail.getAmount()));
		String countStr = getValueFromArray(arr, 7);
		detail.setCount(StringUtils.isBlank(countStr) ? null : Integer.valueOf(countStr));
		detail.setProductName(getValueFromArray(arr, 8));
		return detail;
	}

	private String getValueFromArray(String[] arr, int index) {
		String val = "";
		try {
			val = arr[index];
		} catch (Exception e) {
			val = "";
		}
		return val == null ? "" : val.trim();
	}

	/**
	 * 下载文件
	 * @param driver
	 * @return
	 * @throws Exception
	 */
	private File doDownloadReportFile(final WebChromeDriver driver) throws Exception {
		logger.info("开始下载文件...");
		String cssSelector = "#content-main-entities table.titlebar a.buttonImage";
		WebElement downloadBtn = findElementByCssSelector(driver, cssSelector);
		if (downloadBtn == null) {
			throw new BusinessException("没有找到下载文件的按钮元素，css selector:" + cssSelector);
		}
		CrawlerConfig cfg = appCfg.getCrawlerConfig();
		Date startTime = new Date();
		downloadBtn.click();
		Thread.sleep(2000);
		String dir = driver.getDownloadPath();
		File pFile = null;
		File file = null;
		File[] files = null;
		long t1 = System.currentTimeMillis();
		long maxWait = DOWNLOAD_MAX_WAIT_MILLISECOND; // 最大等待下载时间
		String msg = "";
		String pwdSelector = "form[name=\"signIn\"] input#ap_password";
		String siginSelector = "form[name=\"signIn\"] input#signInSubmit";
		WebElement pwdEle = null;
		WebElement siginEle = null;
		WebDriverWait wait = new WebDriverWait(driver, DEFAUT_WAIT_SECONDS);
		while (true) {
			long t2 = System.currentTimeMillis();
			if ((t2 - t1) > maxWait) {
				msg = "下载文件超时，限制在 " + maxWait + " ms";
				logger.error(msg);
				throw new BusinessException(msg);
			}
			pwdEle = findElementByCssSelector(driver, pwdSelector);
			siginEle = findElementByCssSelector(driver, siginSelector);
			if (pwdEle != null && siginEle != null) { // 需要登录
				pwdEle.sendKeys(cfg.getPassword());
				siginEle.click();
				Thread.sleep(1000);
				try {
					wait.until(ExpectedConditions.presenceOfElementLocated(By.cssSelector(cssSelector)));
					downloadBtn = findElementByCssSelector(driver, cssSelector);
					startTime = new Date();
					downloadBtn.click();
					Thread.sleep(2000);
				} catch (Exception e) {
					logger.error("下载文件时需要重新登录，登录后获取下载按钮失败，css selector:" + cssSelector, e);
					throw new BusinessException(e);
				}
				continue;
			}
			pFile = new File(dir);
			if (pFile == null || !pFile.exists()) {
				Thread.sleep(2000);
				continue;
			}
			files = pFile.listFiles();
			if (files == null || files.length == 0) {
				Thread.sleep(2000);
				continue;
			}
			List<File> list = Arrays.asList(files);
			list.sort(Comparator.comparing(File::lastModified).reversed()); // lastModified 降序
			int size = list.size();
			for (int i = 0; i < size; i++) {
				file = list.get(i);
				if (!file.exists() || file.isDirectory()) {
					continue;
				}
				long time = file.lastModified();
				String lname = file.getName().toLowerCase();
				Date createTime = new Date(time);
				// 如果文件的创建时间在下载按钮点击之后，且 以 report开头 且 以 .text 结尾，则是下载后的数据文件
				if (createTime.after(startTime) && lname.startsWith("report") && lname.endsWith(".txt")) {
					return file;
				}
			}
			Thread.sleep(2000);
		}
	}

	/**
	 * 解析每一行的数据为明细对象
	 * 
	 * @param tr
	 * @param detail
	 */
	private void parseTrElementAsDetailData(final WebElement tr, final FeeCrawlDetail detail) {
		List<WebElement> tds = findElementsByCssSelector(tr, "td");
		if (tds == null || tds.isEmpty()) {
			return;
		}
		String text = null;
		text = getElementText(tds, 0); // 日期
		detail.setTransDate(text);
		text = getElementText(tds, 1); // 交易类型
		detail.setTransType(text);
		text = getElementText(tds, 2); // 订单编号
		detail.setOrderNo(text);
		text = getElementText(tds, 3); // 商品详情
		detail.setProductName(text);
		text = getElementText(tds, 4); // 商品价格总额
		detail.setProductPrice(text);
		detail.setProductPriceNum(NumberUtil.currencyToBigDecimal(detail.getProductPrice()));
		text = getElementText(tds, 5); // 促销返点总额
		detail.setPromotionFee(text);
		detail.setPromotionFeeNum(NumberUtil.currencyToBigDecimal(detail.getPromotionFee()));
		text = getElementText(tds, 6); // 亚马逊所收费用
		detail.setAmazonFee(text);
		detail.setAmazonFeeNum(NumberUtil.currencyToBigDecimal(detail.getAmazonFee()));
		text = getElementText(tds, 7); // 其他
		detail.setOtherFee(text);
		detail.setOtherFeeNum(NumberUtil.currencyToBigDecimal(detail.getOtherFee()));
		text = getElementText(tds, 8); // 总计
		detail.setTotalIncome(text);
		detail.setTotalIncomeNum(NumberUtil.currencyToBigDecimal(detail.getTotalIncome()));
	}

	private String getElementText(List<WebElement> eles, int idx) {
		WebElement ele = null;
		try {
			ele = eles.get(idx);
		} catch (Exception e) {
			logger.error(e.getMessage());
		}
		return getElementText(ele);
	}

	private String getElementText(WebElement ele) {
		String text = ele == null ? null : ele.getText();
		return text == null ? null : text.trim();
	}

	/**
	 * 登录
	 * 
	 * @param driver
	 * @return
	 * @throws Exception
	 */
	private WebDriver doLogin(WebDriver driver) throws Exception {
		return doLogin(driver, false);
	}

	/**
	 * 登录，可重试
	 * 
	 * @param driver
	 * @return
	 */
	private WebDriver doLogin(WebDriver driver, boolean isErrorRetry) throws Exception {
		logger.info("打开登录页面");
		CrawlerConfig cfg = appCfg.getCrawlerConfig();
		driver.get(cfg.getLoginUrl()); // 打开登录页
		WebDriverWait wait = new WebDriverWait(driver, DEFAUT_WAIT_SECONDS);
		// 1. 用户名、密码登录界面
		String siginSelector = "input#signInSubmit";
		String userSelector = "input#ap_email";
		String pwdSelector = "input#ap_password";
		String keepSignSelector = "input[name=\"rememberMe\"]";
		// 2. 已经记录账号，账号选择界面
		String switchAccSelector = "div#ap-account-switcher-container";
		wait.until(ExpectedConditions.or(ExpectedConditions.presenceOfElementLocated(By.cssSelector(siginSelector)),
				ExpectedConditions.presenceOfElementLocated(By.cssSelector(switchAccSelector))));
//		wait.until(ExpectedConditions.presenceOfElementLocated(By.cssSelector(siginSelector)));

		WebElement signBtn = findElementByCssSelector(driver, siginSelector); // 登录按钮
		WebElement userInp = findElementByCssSelector(driver, userSelector); // 账号输入框
		WebElement pwdInp = findElementByCssSelector(driver, pwdSelector); // 密码输入框
		WebElement keepSignInp = findElementByCssSelector(driver, keepSignSelector); // 密码输入框
		WebElement switchAccEle = findElementByCssSelector(driver, switchAccSelector); // 切换账号
		if (switchAccEle != null) { // 切换账号方式
			switchAccEle.click();
			Thread.sleep(2000);
			String switchAccBtnSelector = "div[action^=\"/ap/switchaccount\"] > a[data-name=\"switch_account_request\"]";
			WebElement switchAccBtn = findElementByCssSelector(switchAccEle, switchAccBtnSelector);
//			switchAccBtn = findElementByCssSelector(switchAccEle, "div[action^=\"/ap/switchaccount\"]");
			if (switchAccBtn == null) {
				throw new BusinessException("进入到选择账号登录页面时获取切换按钮失败,css selector:" + switchAccBtnSelector);
			}
			switchAccBtn.click();
			String singFormSelector = "div#authportal-center-section form[name=\"signIn\"]";
			wait.until(ExpectedConditions.presenceOfElementLocated(By.cssSelector(singFormSelector)));
			WebElement signFormEle = findElementByCssSelector(driver, singFormSelector); // 登录表单
			pwdInp = findElementByCssSelector(signFormEle, pwdSelector); // 密码输入框
			if (pwdInp == null) {
				throw new BusinessException("登录界面选择账号时获取密码输入框元素失败，css selector:" + pwdSelector);
			}
			pwdInp.sendKeys(cfg.getPassword());
			signBtn = findElementByCssSelector(driver, siginSelector); // 登录按钮
			if (signBtn == null) {
				throw new BusinessException("登录界面选择账号时获取登录按钮元素失败，css selector:" + siginSelector);
			}
			signBtn.click();
			wait.until(ExpectedConditions.presenceOfElementLocated(By.cssSelector("li#sc-quicklink-settings")));
		} else { // 用户名、密码登录方式
			userInp.sendKeys(cfg.getUserName());
			pwdInp.sendKeys(cfg.getPassword());
			if (keepSignInp != null && keepSignInp.isDisplayed()) {
				keepSignInp.click();
			}
			signBtn.click(); // 点击登录
			boolean flag = doLoginForVerifyCode(driver, isErrorRetry); // 验证码登录
			if (!flag) {
				throw new BusinessException("登录失败");
			}
		}
		return driver;
	}

	/**
	 * 第二步验证码登录
	 * 
	 * @param driver
	 * @param codeInp
	 * @param authBtn
	 */
	private boolean doLoginForVerifyCode(WebDriver driver, boolean isErrorRetry) throws Exception {
		logger.info("判断是否进入主页或需要是否进行验证码登录");
		String verifyCodeSelector = "input#auth-mfa-otpcode";
		String authSelector = "input#auth-signin-button";
		String authErrSelector = "div#auth-error-message-box";
		String authSuccSelector = "li#sc-quicklink-settings";
		WebElement settingBtn = null;
		WebElement verifyCode = null;
		WebDriverWait wait = new WebDriverWait(driver, DEFAUT_WAIT_SECONDS);
		try {
//			wait.until(ExpectedConditions.presenceOfElementLocated(By.cssSelector(verifyCodeSelector)));
			wait.until(ExpectedConditions.or(
					ExpectedConditions.presenceOfElementLocated(By.cssSelector(verifyCodeSelector)),
					ExpectedConditions.presenceOfElementLocated(By.cssSelector(authSuccSelector))));
			settingBtn = findElementByCssSelector(driver, authSuccSelector);
			if (settingBtn != null) { // 已经登录成功，不需要手机验证码
				logger.info("用户名密码登录后直接到主页，没有验证码登录");
				return true;
			}
			verifyCode = findElementByCssSelector(driver, verifyCodeSelector);
			if (verifyCode == null) {
				throw new BusinessException("验证码登录时获取验证码输入框失败，css selector:" + verifyCodeSelector);
			}
		} catch (Exception e) {
			String errorReturnSeletor = "div#ap_error_return_home";
			WebElement errorReturnEle = findElementByCssSelector(driver, errorReturnSeletor);
			WebElement aEle = findElementByCssSelector(errorReturnEle, "p > a");
			if (!isErrorRetry && aEle != null) {
				aEle.click();
				Thread.sleep(1000 * 10); // 10s
				doLogin(driver, true);
			} else {
				throw new BusinessException("用户密码登录后进入验证码或主页超时", e);
			}
		}
		WebElement verifyInp = null;
		WebElement authBtn = null;
		CrawlerConfig cfg = appCfg.getCrawlerConfig();
		long t1 = System.currentTimeMillis();
		boolean needClickSubBtn = true; // 是否需要点击提交按钮
		int errTimes = 0; // 失败次数
		int maxRetryTimes = 100; // 最大重试次数
		String msg = "";
		while (true) {
			long t2 = System.currentTimeMillis();
			if ((t2 - t1) > LOGIN_MAX_TIMS_DIFF) {
				msg = "验证码登录超时，限制在 " + LOGIN_MAX_TIMS_DIFF + " ms";
				logger.error(msg);
				throw new BusinessException(msg);
			}
			if (errTimes > maxRetryTimes) {
				msg = "验证码登录失败，超过重试次数：" + maxRetryTimes;
				logger.error(msg);
				throw new BusinessException(msg);
			}
			// 获取短信验证码
			List<FeeCrawlVerifyCode> list = verifyCodeRepository.getNewestValidByParams(cfg.getAccountId(),
					cfg.getMarketId(), CommonUtil.getComputerName());
			FeeCrawlVerifyCode entity = list == null || list.isEmpty() ? null : list.get(0);
			String smsCode = getValidVerifyCode(entity);
			logger.info("获取到短信验证码：" + smsCode);
			if (StringUtils.isBlank(smsCode) || entity == null) {
				Thread.sleep(1000 * 10); // 10s
				continue;
			}
			if (needClickSubBtn) {
				verifyInp = driver.findElement(By.cssSelector(verifyCodeSelector)); // 验证码输入框
				authBtn = driver.findElement(By.cssSelector(authSelector)); // 提交按钮
				verifyInp.sendKeys(smsCode); // 设置验证码
				authBtn.click(); // 开始验证码登录
				needClickSubBtn = false;
			}
			Thread.sleep(1000 * 10); // 10s
			WebElement errEle = findElementByCssSelector(driver, authErrSelector);
			WebElement succEle = findElementByCssSelector(driver, authSuccSelector);
			if (errEle == null && succEle == null) { // 页面未加载完毕
				long tt1 = System.currentTimeMillis();
				while (true) {
					long tt2 = System.currentTimeMillis();
					if ((tt2 - tt1) > (1000 * 60 * 3)) { // 3 分钟没有加载完页面
						throw new BusinessException("验证码登录页面加载超时");
					}
					Thread.sleep(1000 * 3);
					errEle = findElementByCssSelector(driver, authErrSelector);
					succEle = findElementByCssSelector(driver, authSuccSelector);
					if (errEle == null && succEle == null) {
						continue;
					} else {
						break;
					}
				}
			}
			if (errEle != null) { // 验证失败
				logger.error("验证码登录失败，当前重试次数：" + errTimes);
				errTimes++;
				needClickSubBtn = true;
				Thread.sleep(1000 * 30); // 30s
				continue;
			}
			if (succEle != null) { // 登录成功
				logger.info("验证码登录成功");
				// 更新验证码为 已使用 状态
				entity.setStatus(FeeCrawlVerifyCode.Status.USED);
				verifyCodeRepository.save(entity);
				return true;
			}
		}
	}

	private FeeCrawlTask createNewFeeCrawlTask() {
		CrawlerConfig cfg = appCfg.getCrawlerConfig();
		WebDriverConfig driverCfg = appCfg.getWebDriver();
		FeeCrawlTask task = new FeeCrawlTask();
		task.setAccountId(cfg.getAccountId());
		task.setAccountName(cfg.getAccountName());
		task.setMarketId(cfg.getMarketId());
		task.setMarketName(cfg.getMarketName());
		task.setCreateTime(DateZoneUtil.nowOfChina());
		task.setDriverType(driverCfg.getDriverType().toString());
		return task;
	}

	private FeeCrawlDetail createFeeCrawDetailWithTask(FeeCrawlTask task) {
		FeeCrawlDetail detail = new FeeCrawlDetail();
		BeanUtils.copyProperties(task, detail);
		detail.setId(null);
		detail.setTaskId(task.getId());
		return detail;
	}

	@Transactional
	@Override
	public void saveVerifyCode(String code, String sms) {
		if (StringUtils.isBlank(code) || StringUtils.isBlank(sms)) {
			throw new RuntimeException("参数无效");
		}
		FeeCrawlVerifyCode verifyCode = createNewFeeCrawlVerifyCode();
		verifyCode.setCode(code);
		verifyCode.setSms(sms);
		verifyCodeRepository.save(verifyCode);
	}

	private FeeCrawlVerifyCode createNewFeeCrawlVerifyCode() {
		CrawlerConfig cfg = appCfg.getCrawlerConfig();
		FeeCrawlVerifyCode verifyCode = new FeeCrawlVerifyCode();
		verifyCode.setAccountId(cfg.getAccountId());
		verifyCode.setAccountName(cfg.getAccountName());
		verifyCode.setMarketId(cfg.getMarketId());
		verifyCode.setMarketName(cfg.getMarketName());
		verifyCode.setDeviceid(CommonUtil.getComputerName());
		verifyCode.setSmsTimestamp(System.currentTimeMillis());
		verifyCode.setCreateTime(DateZoneUtil.nowOfChina());
		return verifyCode;
	}

	@Override
	public String getValidVerifyCode(FeeCrawlVerifyCode entity) {
//		FeeCrawlVerifyCode entity = verifyCodeRepository.getNewestByParams(accountId, marketId, deviceid);
		if (entity == null || StringUtils.isBlank(entity.getCode())) {
			return null;
		}
		String code = entity.getCode();
		Long tims = entity.getSmsTimestamp();
		Long currTims = System.currentTimeMillis();
		Pattern pattern = Pattern.compile("[0-9]{6}");
		// 如果不是6位数字 或 不在有效时间内的验证码视为无效
		if (!pattern.matcher(code).matches() || (currTims - tims) > SMS_MAX_VALID_TIMS_DIFF) {
			return null;
		}
		return code;
	}

	@SuppressWarnings("unused")
	private void setValidVerifyCodeStatus(String accountId, String marketId, String deviceid, int status) {
		List<FeeCrawlVerifyCode> list = verifyCodeRepository.getNewestValidByParams(accountId, marketId, deviceid);
		if (list != null && !list.isEmpty()) {
			for (FeeCrawlVerifyCode entity : list) {
				entity.setStatus(status);
				verifyCodeRepository.save(entity);
			}
		}
	}

	private WebElement findElementByCssSelector(WebDriver driver, String cssSelector) {
		if (driver == null || StringUtils.isBlank(cssSelector)) {
			return null;
		}
		try {
			return driver.findElement(By.cssSelector(cssSelector));
		} catch (Exception e) {
			logger.error("driver.findElement(By.cssSelector) error,cssSelector=" + cssSelector);
		}
		return null;
	}

	@SuppressWarnings("unused")
	private List<WebElement> findElementsByCssSelector(WebDriver driver, String cssSelector) {
		if (driver == null || StringUtils.isBlank(cssSelector)) {
			return null;
		}
		try {
			return driver.findElements(By.cssSelector(cssSelector));
		} catch (Exception e) {
			logger.error("driver.findElement(By.cssSelector) error,cssSelector=" + cssSelector);
		}
		return null;
	}

	private WebElement findElementByCssSelector(WebElement ele, String cssSelector) {
		if (ele == null || StringUtils.isBlank(cssSelector)) {
			return null;
		}
		try {
			return ele.findElement(By.cssSelector(cssSelector));
		} catch (Exception e) {
			logger.error("driver.findElement(By.cssSelector) error,cssSelector=" + cssSelector);
		}
		return null;
	}

	private List<WebElement> findElementsByCssSelector(WebElement ele, String cssSelector) {
		if (ele == null || StringUtils.isBlank(cssSelector)) {
			return null;
		}
		try {
			return ele.findElements(By.cssSelector(cssSelector));
		} catch (Exception e) {
			logger.error("driver.findElement(By.cssSelector) error,cssSelector=" + cssSelector);
		}
		return null;
	}

}
