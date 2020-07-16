//package com.efe.feecrawlservice.service;
//
//import java.util.regex.Pattern;
//
//import org.slf4j.Logger;
//import org.slf4j.LoggerFactory;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.stereotype.Service;
//
//import com.efe.feecrawlservice.config.AppConfiguration;
//import com.efe.feecrawlservice.repo.ImageRepository;
//import com.efe.feecrawlservice.repo.RecoverLogRepository;
//import com.efe.feecrawlservice.web.IndexController;
//
//@Service
//public class ImageServiceImpl implements ImageService{
//	
//	private static final Logger logger = LoggerFactory.getLogger(IndexController.class);
//	
//	private static final String CONTEXT_PATH = "/iupload/";
//	private static final String COMPRESS_DIR = "small";
//	private static final String WALMART_ACCOUNT_ID = "1121";
//	private static final String WALMART_ACCOUNT_NAME = "walmart_iefiel";
//	private static final String WALMART_ACCOUNT_PLATFORM = "WALMART";
//	private static final String WALMART_FLAG = "wal";
//	private static final Pattern SKU_PATTERN = Pattern.compile("^[0-9]{8}");
//
//	@Autowired
//	private AppConfiguration appCfg;
//	
//	@Autowired
//	private ImageRepository imageRepository;
//	
//	@Autowired
//	private RecoverLogRepository recoverLogRepository;
//
//}
