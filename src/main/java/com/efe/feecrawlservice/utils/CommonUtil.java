package com.efe.feecrawlservice.utils;

import java.net.InetAddress;

/**
 * 公共工具
 * @author Administrator
 *
 */
public class CommonUtil {

	public static String getComputerName() {
		InetAddress addr = null;
		String address = "";
		try {
			addr = InetAddress.getLocalHost();
			address = addr.getHostName().toString();
		} catch (Exception e) {
			throw new RuntimeException("获取计算机名失败", e);
		}
		return address;
	}
}
