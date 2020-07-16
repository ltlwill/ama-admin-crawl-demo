package com.efe.feecrawlservice.vo;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

/**
 * 
 * <p>业务处理结果: </p> 
 * @author Liu TianLong
 * 2019年1月10日 下午3:30:49
 */
@SuppressWarnings("serial")
public class BusinessResult implements Serializable {
	private int resultCode;
	private String message;
	private Map<String, Object> messages = new HashMap<String, Object>();
	private Map<String, Object> datas = new HashMap<String, Object>();
	
	public static final class ResultCode {
		public final static int SUCCESS = 1; // 成功
		public final static int FAIL = 0;    // 失败
	}

	public BusinessResult() {
	}

	public BusinessResult(int resultCode) {
		this.resultCode = resultCode;
	}

	public BusinessResult(int resultCode, String message) {
		this.message = message;
	}

	public BusinessResult(int resultCode, Map<String, Object> messages) {
		this.resultCode = resultCode;
		this.messages = messages;
	}

	public BusinessResult addMessage(String key, Object msg) {
		this.messages.put(key, msg);
		return this;
	}
	public BusinessResult addData(String key, Object data) {
		this.datas.put(key, data);
		return this;
	}

	public int getResultCode() {
		return resultCode;
	}

	public void setResultCode(int resultCode) {
		this.resultCode = resultCode;
	}

	public Map<String, Object> getMessages() {
		return messages;
	}

	public void setMessages(Map<String, Object> messages) {
		this.messages = messages;
	}

	public boolean isSuccess() {
		return this.resultCode == ResultCode.SUCCESS;
	}

	public String getMessage() {
		return message;
	}

	public void setMessage(String message) {
		this.message = message;
	}

	public Map<String, Object> getDatas() {
		return datas;
	}

	public void setDatas(Map<String, Object> datas) {
		this.datas = datas;
	}
	
	public static BusinessResult success(){
		return new BusinessResult(BusinessResult.ResultCode.SUCCESS);
	}
	public static BusinessResult success(String message){
		return new BusinessResult(BusinessResult.ResultCode.SUCCESS,message);
	}
	
	public static BusinessResult fail(){
		return new BusinessResult(BusinessResult.ResultCode.FAIL);
	}
	public static BusinessResult fail(String message){
		return new BusinessResult(BusinessResult.ResultCode.FAIL,message);
	}
}
