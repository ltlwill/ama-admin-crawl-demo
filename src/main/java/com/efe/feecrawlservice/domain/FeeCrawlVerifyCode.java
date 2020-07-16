package com.efe.feecrawlservice.domain;

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * amazon费用爬取验证码
 * @author Administrator
 *
 */
@SuppressWarnings("serial")
@Entity
@Table(name = "amazon_fee_verify_code")
@org.hibernate.annotations.Table(appliesTo = "amazon_fee_verify_code",comment = "验证码记录表")
@Getter
@Setter
@NoArgsConstructor
public class FeeCrawlVerifyCode extends SeriEntity{

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;
	@Column(name = "account_id",columnDefinition = "varchar(50) default '' comment '账号ID'")
	private String accountId;
	@Column(name = "account_name",columnDefinition = "varchar(100) default '' comment '账号名称'")
	private String accountName;
	@Column(name = "market_id",columnDefinition = "varchar(50) default '' comment '站点ID'")
	private String marketId;
	@Column(name = "market_name",columnDefinition = "varchar(100) default '' comment '站点名称'")
	private String marketName;
	@Column(columnDefinition = "varchar(100) default '' comment '设备ID'")
	private String deviceid;
	@Column(columnDefinition = "varchar(100) comment '验证码信息'")
	private String code;
	@Column(columnDefinition = "varchar(500) comment '短信内容'")
	private String sms;
	@Column(name = "sms_timestamp",columnDefinition = "bigint comment '接收短信的时间戳'")
	private Long smsTimestamp;
	@Column(name= "create_time",columnDefinition = "datetime comment '创建时间'")
	private Date createTime;
	@Column(columnDefinition = "int(2) comment '状态(0:未使用,1:已使用)'")
	private Integer status = 0;
	
	public static class Status{
		public static final int UN_USE = 0;          // 未使用
		public static final int USED = 1;            // 已使用
	}
}
