package com.efel.imgrecoverservice;

import java.util.regex.Pattern;

import org.junit.jupiter.api.Test;

import com.efe.feecrawlservice.utils.CommonUtil;
import com.efe.feecrawlservice.utils.NumberUtil;

public class AppTest {
	
	@Test
	public void test1() {
		System.out.println(CommonUtil.getComputerName());
	}
	
	@Test
	public void test2() {
//		String str = "-US$39.99";
//		String str = "US$29.50";
//		String str = "US$0.00";
		String str = "US$-12,999.01";
//		String str = "US$-39.99";
		System.out.println(NumberUtil.currencyToBigDecimal(str));
		System.out.println(NumberUtil.getCurrency(str));
	}
	
	@Test
	public void test3() {
		Pattern pattern = Pattern.compile(".txt$", Pattern.CASE_INSENSITIVE);
		System.out.println(pattern.matcher("xxx.txt").matches());
	}

}
