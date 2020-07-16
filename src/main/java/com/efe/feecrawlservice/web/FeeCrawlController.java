package com.efe.feecrawlservice.web;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.efe.feecrawlservice.service.FeeCrawlService;
import com.efe.feecrawlservice.vo.BusinessResult;

@RestController
@RequestMapping
public class FeeCrawlController {
	
	private static final Logger logger = LoggerFactory.getLogger(FeeCrawlController.class);

	
	@Autowired
	private FeeCrawlService feeCrawlService;
	
	@RequestMapping("/amazon/crawl")
	public BusinessResult crawl() throws Exception{
		feeCrawlService.startNewTask();
		return BusinessResult.success();
	}
	
	@RequestMapping("/sms")
	public BusinessResult sms(String captcha,String text) {
		logger.info("captcha=" + captcha + ";text=" + text);
		feeCrawlService.saveVerifyCode(captcha, text);
		return BusinessResult.success();
	}

}
