package com.efe.feecrawlservice.service;

import com.efe.feecrawlservice.domain.FeeCrawlDetail;
import com.efe.feecrawlservice.domain.FeeCrawlTask;
import com.efe.feecrawlservice.domain.FeeCrawlVerifyCode;

/**
 * 
 * @author Tianlong Liu
 * @2020年7月9日 上午11:51:33
 */
public interface FeeCrawlService {

	/**
	 * 新建采集任务
	 * 
	 * @throws Exception
	 */
	void startNewTask() throws Exception;

	/**
	 * 开始处理采集任务
	 * 
	 * @param driver
	 * @param task
	 * @throws Exception
	 */
	void processTask(final FeeCrawlTask task) throws Exception;

	/**
	 * 开始采集数据
	 * 
	 * @param driver
	 * @param task
	 * @param detail
	 * @throws Exception
	 */
	void startCrawlData(final FeeCrawlTask task, final FeeCrawlDetail detail) throws Exception;

	/**
	 * 异步开始采集数据
	 * 
	 * @param driver
	 * @param task
	 * @param detail
	 * @throws Exception
	 */
	void asyncStartCrawlData(final FeeCrawlTask task, final FeeCrawlDetail detail) throws Exception;

	/**
	 * 开始采集
	 * 
	 * @param task
	 * @param detail
	 */
	void doStartCrawl(final FeeCrawlTask task, final FeeCrawlDetail detail);

	/**
	 * 开始采集
	 * 
	 * @param detail
	 * @throws Exception
	 */
	void doCrawlDataBusiness(final FeeCrawlDetail detail) throws Exception;

	/**
	 * 保存验证码
	 * 
	 * @param code
	 * @param sms
	 */
	void saveVerifyCode(String code, String sms);

	/**
	 * 获取有效的验证码
	 * 
	 * @param accountId
	 * @param marketId
	 * @param deviceid
	 * @return
	 */
	String getValidVerifyCode(FeeCrawlVerifyCode entity);

}
