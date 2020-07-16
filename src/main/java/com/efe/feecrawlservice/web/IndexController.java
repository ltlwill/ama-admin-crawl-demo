package com.efe.feecrawlservice.web;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping
public class IndexController {
	
	private static final Logger logger = LoggerFactory.getLogger(IndexController.class);

	
	@Autowired
	
	@RequestMapping
	public String index() {
		return "hello fee crawl service";
	}
	
}
